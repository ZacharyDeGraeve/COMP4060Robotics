package pocketsphinx;

public class PocketSphinx {
    static {
        System.loadLibrary("pocketsphinx_jni");
    }

    // Native methods
    public native long initialize_lm(String hmmPath, String lmPath, String dictPath);
    public native long initialize_kws(String hmmPath, String lmPath, String dictPath);
    public native void startListening(long decoderPtr);
    public native void processAudio(long decoderPtr, byte[] audioData, int length);
    public native RecognitionResult getRecognitionHypothesis(long decoderPtr);
    public native void stopListening(long decoderPtr);
    public native void cleanup(long decoderPtr);
}

