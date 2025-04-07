package AS4;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import jp.vstone.RobotLib.CPlayWave;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MaryTTSHelper {
    private final MaryInterface mary;
    private String currentVoice;
    private String currentEffects;
    private final Map<String, String> predefinedEffects;
    private final Map<String, byte[]> audioCache;
    private static final int MAX_CACHE_SIZE = 100;

    public MaryTTSHelper() throws MaryConfigurationException {
        mary = new LocalMaryInterface();
        currentVoice = "cmu-slt-hsmm"; // Default voice
        mary.setVoice(currentVoice);
        currentEffects = "";
        audioCache = new ConcurrentHashMap<>(MAX_CACHE_SIZE);
        
        // Initialize predefined effects
        predefinedEffects = new HashMap<>();
        predefinedEffects.put("robot", "Robot(amount:100)");
        predefinedEffects.put("storyteller", "F0Scale(f0Scale:2.2)+F0Add(f0Add:25)+TractScaler(amount:0.5)+Rate(durScale:1.2)");

        System.out.println("[Info] MaryTTS loaded");
    }
    
    public String listAvailableVoices() {
        return "Available voices: " + mary.getAvailableVoices() + 
               "\nUS English voices: " + mary.getAvailableVoices(Locale.US);
    }
    
    public String listEffectPresets() {
        return "Available effect presets: " + String.join(", ", predefinedEffects.keySet());
    }
    
    public boolean setVoice(String voice) {
        if (mary.getAvailableVoices().contains(voice)) {
            mary.setVoice(voice);
            currentVoice = voice;
            audioCache.clear(); // Clear cache when voice changes
            return true;
        }
        return false;
    }

    public boolean setEffectPreset(String presetName) {
        if (predefinedEffects.containsKey(presetName.toLowerCase())) {
            String effects = predefinedEffects.get(presetName.toLowerCase());
            mary.setAudioEffects(effects);
            currentEffects = effects;
            audioCache.clear(); // Clear cache when effects change
            return true;
        }
        return false;
    }
    
    public void setCustomEffects(String effects) {
        mary.setAudioEffects(effects);
        currentEffects = effects;
        audioCache.clear(); // Clear cache when effects change
    }
    
    public void speak(String text) throws SynthesisException, IOException {
        byte[] audioData = getAudioData(text);
        CPlayWave.PlayWave_wait(audioData);
    }
    
    // public void speakAsync(String text) throws SynthesisException, IOException {
    //     byte[] audioData = getAudioData(text);
    //     CPlayWave.PlayWave(audioData);
    // }
    
    private byte[] getAudioData(String text) throws SynthesisException, IOException {
        // Create cache key based on text, voice, and effects
        String cacheKey = text + "|" + currentVoice + "|" + currentEffects;
        
        // Check if audio is already in cache
        if (audioCache.containsKey(cacheKey)) {
            return audioCache.get(cacheKey);
        }
        
        // Generate new audio data
        AudioInputStream audio = mary.generateAudio(text);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        AudioSystem.write(audio, AudioFileFormat.Type.WAVE, byteStream);
        byte[] audioData = byteStream.toByteArray();
        
        // Add to cache if not too large (avoid caching long speeches)
        if (text.length() < 200 && audioCache.size() < MAX_CACHE_SIZE) {
            audioCache.put(cacheKey, audioData);
            
            // If cache is full, remove oldest entry (simple implementation)
            if (audioCache.size() >= MAX_CACHE_SIZE) {
                String oldestKey = audioCache.keySet().iterator().next();
                audioCache.remove(oldestKey);
            }
        }
        
        return audioData;
    }
    
    public String getCurrentVoice() {
        return currentVoice;
    }
    
    public String getCurrentEffects() {
        return currentEffects.isEmpty() ? "none" : currentEffects;
    }
}