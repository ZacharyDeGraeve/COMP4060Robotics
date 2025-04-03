package sample4060;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import jp.vstone.RobotLib.CPlayWave;

import javax.sound.sampled.AudioFileFormat.Type;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

import java.io.BufferedReader;

import java.io.IOException;
import java.util.Locale;
import java.io.File;
import java.io.FileReader;

public class WriteWavFiles {
    
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

            // Read text file line by line
            File inputFile = new File("TheLittleMatchGirl.txt");  // Change to your file
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {  // Skip empty lines
                    System.out.println("Generating audio for: " + line);

                    // Generate speech from the line
                    AudioInputStream audio = mary.generateAudio(line);

                    // Save to a WAV file (one file per line)
                    File outputFile = new File("TLMG_" + lineNumber + ".wav");
                    AudioSystem.write(audio, Type.WAVE, outputFile);
                    
                    System.out.println("Saved: " + outputFile.getName());

                    lineNumber++;
                }
            }

            reader.close();
            System.out.println("Processing complete.");

        } catch (MaryConfigurationException | SynthesisException | IOException e) {
            e.printStackTrace();
        }
    }
}