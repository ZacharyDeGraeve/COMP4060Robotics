package sample4060;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import jp.vstone.RobotLib.CPlayWave;

import javax.sound.sampled.AudioFileFormat;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.util.Locale;

public class marysample {
    
    public static void main(String[] args) {
        try {

            System.out.println("Loading the Mary Interface");
            LocalMaryInterface mary = new LocalMaryInterface();
            System.out.println("Interface Loaded");

            MaryInterface marytts = new LocalMaryInterface();
            System.out.println("I currently have " + marytts.getAvailableVoices() + " voices in "
                + marytts.getAvailableLocales() + " languages available.");
            System.out.println("Out of these, " + marytts.getAvailableVoices(Locale.US) + " are for US English.");

            System.out.println("Available voices: " + mary.getAvailableVoices());
            // Set voice (must be installed)
            mary.setVoice("cmu-slt-hsmm"); 
            // mary.setVoice("dfki-prudence-hsmm"); 
            
            
            System.out.println("Generating Voice");
            System.out.println(mary.getAudioEffects());

            // effects can be used to shape your voice. 
            // section 3 of this site has the old docs for it: https://myrobotlab.org/service/MarySpeech
            
            // mary.setAudioEffects("FIRFilter(type:3;fc1:500.0;fc2:2000.0)");   // finite impulse response (FIR) filter, advanced
            // mary.setAudioEffects("f0Add(f0add:-100)");
            // mary.setAudioEffects("f0Add(f0add:100)");
            // mary.setAudioEffects("TractScaler(amount:.5)");
            // mary.setAudioEffects("f0Scale(f0scale:2)");  // flatness
            // mary.setAudioEffects("Lowpass(cutoff:500.0)");
            // mary.setAudioEffects("Reverb(reverbAmount:0.5)");
            // mary.setAudioEffects("Whisper(amount:100)");
            // mary.setAudioEffects("Stadium(amount:100)");
            // mary.setAudioEffects("Chorus(amount:100)");
            // mary.setAudioEffects("JetPilot(amount:100)");
            // mary.setAudioEffects("Rate(durScale:1.5)"); // doesn't seem to work with the default voice
 
            // mary.setAudioEffects("Volume(amount:2.0)+Rate(durScale:1.5)+F0Scale(f0Scale:1.2)"); // you can combine effects using the syntax here in this example
            // mary.setAudioEffects("Volume(amount:1.0)+Robot(amount:100)+TractScaler(amount:.5)");
            mary.setAudioEffects("f0Scale(f0scale:2)+TractScaler(amount:1.2)");
            // Generate speech from text
            AudioInputStream audio = mary.generateAudio("It's a trap!");

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            System.out.println("Generation Done");

            AudioSystem.write(audio, AudioFileFormat.Type.WAVE, byteStream);  // not needed if saving to file, converts to bytestream for direct playing
            CPlayWave.PlayWave_wait(byteStream.toByteArray());
            CPlayWave.PlayWave_wait(byteStream.toByteArray());
            CPlayWave.PlayWave_wait(byteStream.toByteArray());
            
            // // Save to a WAV file
            // File outputFile = new File("output.wav");
            // AudioSystem.write(audio, Type.WAVE, outputFile);
            // CPlayWave.PlayWave_wait("output.wav");
            // CPlayWave.PlayWave_wait("output.wav");
            // CPlayWave.PlayWave_wait("output.wav");

            // CPlayWave.PlayWave("output.wav"); // doesn't wait


        } catch (MaryConfigurationException | SynthesisException | IOException e) {
            e.printStackTrace();
        }
    }
}