package sample4060;

import javax.sound.sampled.*;

import pocketsphinx.PocketSphinx;
import pocketsphinx.RecognitionResult;

public class PocketSphinxkws {

    final static String SPHINX_ROOT = "/home/root/sotaprograms/resources/sphinxmodel/";
    final static int ms = 128;  // window size to process at a time. longer makes more delay but less processing. 
    final static int SAMPLERATE = 16000; // 8000, 16000, 22050, 44100, stick to 16k unless you know what you are doing
    final static int BITRATE = 16; // keep at 16
    final static int BUFFER_SIZE = ms*SAMPLERATE*2/1000; // Samplerate x 2 bytes per sample (16 bit) /1000 ms per second
    final static int CHANNELS = 1; // mono

    public static void main(String[] args) {
        PocketSphinx sphinx = new PocketSphinx();

        // Initialize PocketSphinx
        long decoderPtr = sphinx.initialize_kws(SPHINX_ROOT+"en-us/en-us", SPHINX_ROOT+"keyphrases.txt", SPHINX_ROOT+"en-us/cmudict-en-us.dict");
        if (decoderPtr == 0) {
            System.err.println("Failed to initialize PocketSphinx");
            return;
        }

        // Configure microphone
        TargetDataLine microphone = null;
        try {
            AudioFormat format = new AudioFormat(SAMPLERATE, BITRATE, CHANNELS, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
            System.out.println("Microphone started... Speak now!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize Microphone");
            return;
        }

        // Start listening -- you call start and stop listening each time you want to reset and try a new utterance
        sphinx.startListening(decoderPtr);
        System.out.println("Decoder Started");

        boolean keywordDetected = false;
        byte[] buffer = new byte[BUFFER_SIZE]; 
        while (!keywordDetected) {
        
            int bytesRead = microphone.read(buffer, 0, buffer.length);
            sphinx.processAudio(decoderPtr, buffer, bytesRead);
            
            // NOTE: in KWS mode, sphinx does not return a confidence score since it already
            //  uses the threshold specified in your keyphrases.txt file. It is always 0
            RecognitionResult result = sphinx.getRecognitionHypothesis(decoderPtr);
            if (!result.result.isEmpty()) {
                keywordDetected = true;
            }     
        }     

        // Stop listening
        microphone.stop();
        sphinx.stopListening(decoderPtr);  // call get recognition after stop listening again to do final result
        RecognitionResult result = sphinx.getRecognitionHypothesis(decoderPtr);
        System.out.println("Final: " + result.result);

        // Cleanup
        sphinx.cleanup(decoderPtr);
        microphone.close();
    }
}

