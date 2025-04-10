package AS4;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import javax.sound.sampled.AudioFileFormat;
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
            // // String line = "Would you like to listen to this story?";
            // // String line = "Would you like to listen to another story?";
            // // String line = "Hi there!";
            // // String line = "Ok! Let me pick a story.";
            // String line = "I have picked";
            // // String line = "Do you want to hear another story?";
            // // String line = "Goodnight! Sleep tight.";

            // // Generate speech from the line
            // AudioInputStream audio = mary.generateAudio(line);

            // // Save to a WAV file
            // // File outputFile = new File("./resources/sound/SelectStory.wav");
            // // File outputFile = new File("./resources/sound/Another.wav");
            // // File outputFile = new File("./resources/sound/Hi.wav");
            // // File outputFile = new File ("./resources/sound/LetMePick.wav");
            // File outputFile = new File ("./resources/sound/StoryConfirmation.wav");
            // // File outputFile = new File ("./resources/sound/AnotherStory.wav");
            // // File outputFile = new File("./resources/sound/Goodnight.wav");
            // AudioSystem.write(audio, Type.WAVE, outputFile);

            // System.out.println("Saved: " + outputFile.getName());



            /************************ CREATE WAVE FILE FOR EACH LINE READ FROM A TEXT FILE ************************/

            // Read text file line by line
            File inputFile = new File("TheLittleMatchGirl.txt");
            // File inputFile = new File("LittleRedRidingHood.txt");
            // File inputFile = new File("PussInBoots.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            String line;
            String title = null;
            int lineNumber = 1;

            // Get first non-empty line as title
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    title = line.trim().substring(3); // Start at 3 because weird extra characters were picked up
                    break;
                }
            }

            // Read the rest of the file and generate audio for each line
            while ((line = reader.readLine()) != null && lineNumber < 30) {
                if (!line.trim().isEmpty()) {
                    System.out.println("Generating audio for line " + lineNumber + ": " + line);
                    AudioInputStream lineAudio = mary.generateAudio(line);
                    File lineFile = new File("./resources/sound/TLMG_story_" + lineNumber + ".wav");
                    // File lineFile = new File("./resources/sound/LRRH_story_" + lineNumber + ".wav");
                    // File lineFile = new File("./resources/sound/PIB_story_" + lineNumber + ".wav");
                    AudioSystem.write(lineAudio, AudioFileFormat.Type.WAVE, lineFile);
                    System.out.println("Saved: " + lineFile.getName());
                    lineNumber++;  // Increment the line number for the next file
                }
            }

            reader.close();

            if (title != null) {
                // Generate and save title audio
                System.out.println("Generating audio for title: " + title);
                AudioInputStream titleAudio = mary.generateAudio(title);
                File titleFile = new File("./resources/sound/TLMG_title.wav");
                // File titleFile = new File("./resources/sound/LRRH_title.wav");
                // File titleFile = new File("./resources/sound/PIB_title.wav");
                AudioSystem.write(titleAudio, AudioFileFormat.Type.WAVE, titleFile);
                System.out.println("Saved title: " + titleFile.getName());
            }

            System.out.println("Processing complete.");
        } catch (MaryConfigurationException | SynthesisException | IOException e) {
            e.printStackTrace();
        }
    }
}