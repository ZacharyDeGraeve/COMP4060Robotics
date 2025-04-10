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
            mary.setAudioEffects("f0Scale(f0scale:0.9)+TractScaler(amount:1)+Rate(durScale:0.3)+Robot(amount:0.0)+Volume(amount:1.3)");



            /******************************** CREATE ONE LINE OF OUR WRITTEN SCRIPT *******************************/

            // // Change line to what we are trying to make
            // String line = "Do you want to hear...";
            // // String line = "Do you want to hear another story?";

            // // Generate speech from the line
            // AudioInputStream audio = mary.generateAudio(line);

            // // Save to a WAV file
            // File outputFile = new File ("./resources/sound/StoryConfirmation.wav");
            // // File outputFile = new File ("./resources/sound/AnotherStory.wav");
            // AudioSystem.write(audio, Type.WAVE, outputFile);

            // System.out.println("Saved: " + outputFile.getName());



            /************************ CREATE WAVE FILE FOR EACH LINE READ FROM A TEXT FILE ************************/

            // Read text file line by line
            // File inputFile = new File("TheLittleMatchGirl.txt");
            // File inputFile = new File("LittleRedRidingHood.txt");
            File inputFile = new File("PussInBoots.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {  // Skip empty lines
                    System.out.println("Generating audio for: " + line);

                    // Generate speech from the line
                    AudioInputStream audio = mary.generateAudio(line);

                    // Save to a WAV file (one file per line)
                    // File outputFile = new File("./resources/sound/TLMG_" + lineNumber + ".wav");
                    // File outputFile = new File("./resources/sound/LRRH_" + lineNumber + ".wav");
                    File outputFile = new File("./resources/sound/PIB_" + lineNumber + ".wav");
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