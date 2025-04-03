package sample4060;

import java.time.Duration;
import java.time.Instant;

import javax.sound.sampled.*;

import pocketsphinx.*;

/// general language processing, transcription, is not working well at all because of Sota's
/// limited processing. If you want to try this there are things you can poke at to improve:
///   - modify the dictionary. I generated a smaller dictionary based on the top 1000 words
///     (loosely) and it is faster. The default dictionary is huge
///   - use a thread. The Sphinx processing takes a long time, while the mic isn't listening.
///     synchronized threads could let the mic still listen while processing, and, leverage 
///     both cores.    

public class PocketSphinxlm {
    
    // longer windows requires less spinning but makes it less responsive.
    // shorter windows avoid the slow processing of sphinx making the recording choppy.
    // default = 128 
    final static int WINDOW_ms = 64;  

    final static String SPHINX_ROOT = "/home/root/sotaprograms/resources/sphinxmodel/";
    final static int SAMPLERATE = 16000; // 8000, 16000, 22050, 44100,  stick to 16k unless you know what you are doing
    final static int BITRATE = 16; // keep at 16
    final static int BUFFER_SIZE = WINDOW_ms*SAMPLERATE*2/1000; // Samplerate x 2 bytes per sample (16 bit) /1000 ms per second
    final static int CHANNELS = 1; // mono

    final static int LISTEN_TIME = 5;  // seconds
    final static int UPDATE_RATE_ms = 1000; // how often to update the procssing live
    final static int UPDATE_RATE_TICKS = UPDATE_RATE_ms / WINDOW_ms;

    public static void main(String[] args) {

        PocketSphinx sphinx = new PocketSphinx();

        // Initialize PocketSphinx
        long decoderPtr = sphinx.initialize_lm(SPHINX_ROOT+"en-us/en-us", SPHINX_ROOT+"en-us/en-us.lm.bin", SPHINX_ROOT+"en-us/cmudict-en-us.dict");
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

        byte[] buffer = new byte[BUFFER_SIZE]; 
        Instant startTime = Instant.now();
        int updateTick = 0;

        while (Duration.between(startTime, Instant.now()).getSeconds() < LISTEN_TIME) {
            int bytesRead = microphone.read(buffer, 0, buffer.length);
            if (bytesRead > 0)
                sphinx.processAudio(decoderPtr, buffer, bytesRead);
            
            if (updateTick >= UPDATE_RATE_TICKS ) {
                RecognitionResult result = sphinx.getRecognitionHypothesis(decoderPtr);
                if (!result.result.isEmpty()) 
                    System.out.println("Partial: " + result.result+" "+result.score);
                updateTick = 0;
            }
            updateTick++;
        }

        // Stop listening
        microphone.stop();
        sphinx.stopListening(decoderPtr); // call get recognition after stop listening again to do final result
        RecognitionResult result = sphinx.getRecognitionHypothesis(decoderPtr);
        System.out.println("Final: " + result.result+" "+result.score);

        // Cleanup
        sphinx.cleanup(decoderPtr);
        
        microphone.close();
    }
}

