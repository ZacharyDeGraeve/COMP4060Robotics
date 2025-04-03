package AS4;

import javax.sound.sampled.*;
import pocketsphinx.PocketSphinx;
import pocketsphinx.RecognitionResult;

public class PocketSphinxHelper {
    private final static String SPHINX_ROOT = "/home/root/sotaprograms/resources/sphinxmodel/";
    final static int ms = 128;  // window size to process at a time. longer makes more delay but less processing. 
    final static int SAMPLERATE = 16000; // 8000, 16000, 22050, 44100, stick to 16k unless you know what you are doing
    final static int BITRATE = 16; // keep at 16
    final static int BUFFER_SIZE = ms*SAMPLERATE*2/1000; // Samplerate x 2 bytes per sample (16 bit) /1000 ms per second
    final static int CHANNELS = 1; // mono
    private PocketSphinx sphinx;
    private long decoderPtr;
    private TargetDataLine microphone;

    public PocketSphinxHelper() {
        sphinx = new PocketSphinx();
        setupMicrophone();
    }

    private void setupMicrophone() {
        try {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize microphone", e);
        }
    }

    public void loadKeywordFile(String keywordFile) {
        if (decoderPtr != 0) {
            sphinx.cleanup(decoderPtr);
        }
        decoderPtr = sphinx.initialize_kws(
                SPHINX_ROOT + "en-us/en-us",
                SPHINX_ROOT + keywordFile,
                SPHINX_ROOT + "en-us/cmudict-en-us.dict"
        );
        if (decoderPtr == 0) {
            throw new RuntimeException("Failed to load keyword file: " + keywordFile);
        }
    }

    public boolean detectKeyword() {
        sphinx.startListening(decoderPtr);
        byte[] buffer = new byte[BUFFER_SIZE];

        while (true) {
            int bytesRead = microphone.read(buffer, 0, buffer.length);
            sphinx.processAudio(decoderPtr, buffer, bytesRead);

            RecognitionResult result = sphinx.getRecognitionHypothesis(decoderPtr);
            if (!result.result.isEmpty()) {
                sphinx.stopListening(decoderPtr);
                System.out.println("Final: " + result.result);
                return true;
            }
        }
    }

    public void cleanup() {
        sphinx.cleanup(decoderPtr);
        microphone.close();
    }
}
