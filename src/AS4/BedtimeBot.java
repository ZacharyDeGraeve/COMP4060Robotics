package AS4;

import java.awt.Color;

import AS4.SleepingDetector;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import marytts.exceptions.MaryConfigurationException;

public class BedtimeBot {
    private final static String WAV_ROOT = "/home/root/sotaprograms/resources/sound/";
    private final static String[][] STORY_LIBRARY = {
        {"The Little Match Girl", 
            WAV_ROOT + "TLMG_title.wav", 
            WAV_ROOT + "TLMG_story_1.wav", WAV_ROOT + "TLMG_story_2.wav", WAV_ROOT + "TLMG_story_3.wav", 
            WAV_ROOT + "TLMG_story_4.wav", WAV_ROOT + "TLMG_story_5.wav", WAV_ROOT + "TLMG_story_6.wav",
            WAV_ROOT + "TLMG_story_7.wav", WAV_ROOT + "TLMG_story_8.wav", WAV_ROOT + "TLMG_story_9.wav",
            WAV_ROOT + "TLMG_story_10.wav", WAV_ROOT + "TLMG_story_11.wav", WAV_ROOT + "TLMG_story_12.wav",
            WAV_ROOT + "TLMG_story_13.wav", WAV_ROOT + "TLMG_story_14.wav", WAV_ROOT + "TLMG_story_15.wav",
            WAV_ROOT + "TLMG_story_16.wav", WAV_ROOT + "TLMG_story_17.wav", WAV_ROOT + "TLMG_story_18.wav",
            WAV_ROOT + "TLMG_story_19.wav", WAV_ROOT + "TLMG_story_20.wav", WAV_ROOT + "TLMG_story_21.wav",
            WAV_ROOT + "TLMG_story_22.wav", WAV_ROOT + "TLMG_story_23.wav", WAV_ROOT + "TLMG_story_24.wav",
            WAV_ROOT + "TLMG_story_25.wav", WAV_ROOT + "TLMG_story_26.wav", WAV_ROOT + "TLMG_story_27.wav",
            WAV_ROOT + "TLMG_story_28.wav", WAV_ROOT + "TLMG_story_29.wav"
        },
        {"Puss-In-Boots", 
            WAV_ROOT + "PIB_title.wav", 
            WAV_ROOT + "PIB_story_1.wav", WAV_ROOT + "PIB_story_2.wav", WAV_ROOT + "PIB_story_3.wav", 
            WAV_ROOT + "PIB_story_4.wav", WAV_ROOT + "PIB_story_5.wav", WAV_ROOT + "PIB_story_6.wav",
            WAV_ROOT + "PIB_story_7.wav", WAV_ROOT + "PIB_story_8.wav", WAV_ROOT + "PIB_story_9.wav",
            WAV_ROOT + "PIB_story_10.wav", WAV_ROOT + "PIB_story_11.wav", WAV_ROOT + "PIB_story_12.wav",
            WAV_ROOT + "PIB_story_13.wav", WAV_ROOT + "PIB_story_14.wav", WAV_ROOT + "PIB_story_15.wav",
            WAV_ROOT + "PIB_story_16.wav", WAV_ROOT + "PIB_story_17.wav", WAV_ROOT + "PIB_story_18.wav",
            WAV_ROOT + "PIB_story_19.wav", WAV_ROOT + "PIB_story_20.wav", WAV_ROOT + "PIB_story_21.wav",
            WAV_ROOT + "PIB_story_22.wav", WAV_ROOT + "PIB_story_23.wav", WAV_ROOT + "PIB_story_24.wav",
            WAV_ROOT + "PIB_story_25.wav", WAV_ROOT + "PIB_story_26.wav", WAV_ROOT + "PIB_story_27.wav",
            WAV_ROOT + "PIB_story_28.wav", WAV_ROOT + "PIB_story_29.wav"
        },
        {"Little Red Riding Hood", 
            WAV_ROOT + "LRRH_title.wav", 
            WAV_ROOT + "LRRH_story_1.wav", WAV_ROOT + "LRRH_story_2.wav", WAV_ROOT + "LRRH_story_3.wav", 
            WAV_ROOT + "LRRH_story_4.wav", WAV_ROOT + "LRRH_story_5.wav", WAV_ROOT + "LRRH_story_6.wav",
            WAV_ROOT + "LRRH_story_7.wav", WAV_ROOT + "LRRH_story_8.wav", WAV_ROOT + "LRRH_story_9.wav",
            WAV_ROOT + "LRRH_story_10.wav", WAV_ROOT + "LRRH_story_11.wav", WAV_ROOT + "LRRH_story_12.wav",
            WAV_ROOT + "LRRH_story_13.wav", WAV_ROOT + "LRRH_story_14.wav", WAV_ROOT + "LRRH_story_15.wav",
            WAV_ROOT + "LRRH_story_16.wav", WAV_ROOT + "LRRH_story_17.wav", WAV_ROOT + "LRRH_story_18.wav",
            WAV_ROOT + "LRRH_story_19.wav", WAV_ROOT + "LRRH_story_20.wav", WAV_ROOT + "LRRH_story_21.wav",
            WAV_ROOT + "LRRH_story_22.wav", WAV_ROOT + "LRRH_story_23.wav", WAV_ROOT + "LRRH_story_24.wav",
            WAV_ROOT + "LRRH_story_25.wav", WAV_ROOT + "LRRH_story_26.wav", WAV_ROOT + "LRRH_story_27.wav",
            WAV_ROOT + "LRRH_story_28.wav", WAV_ROOT + "LRRH_story_29.wav"
        }
    };

    public enum Greeting { HI, GOODNIGHT }
    public enum AskQuestion { SELECT, ANOTHER }

    private int storySelect;
    private PocketSphinxHelper sphinxHelper;
    private String[] story;
    private Gesture gesture;
    private CSotaMotion motion;
    private Thread storyThread;
    private int playingLine;
    private boolean isGoodnightExecuted;
    private SleepingDetector detector;
    private CRobotPose pose;

    public BedtimeBot() {
        System.out.println("[Info] Loading...");
        isGoodnightExecuted = false;

        sphinxHelper = new PocketSphinxHelper();
        storySelect = 0;

        // Initialize the CSotaMotion
        CRobotMem mem = new CRobotMem();
        pose = new CRobotPose();
        motion = new CSotaMotion(mem);
        
        // Only initialize the gesture system if we can connect to the robot
        if (mem.Connect()) {
            motion.InitRobot_Sota();
            motion.ServoOn();
            gesture = new Gesture(motion);
            detector = new SleepingDetector(motion);
        }
        if (gesture != null) {
            try {
                gesture.neutral();
            } catch (Exception e) {
                System.err.println("Error performing gesture: " + e.getMessage());
            }
        }

        System.out.println("[Info] Completed!");
    }

    public boolean waitForKeyword(String keywordFile) {
        sphinxHelper.loadKeywordFile(keywordFile);
        return sphinxHelper.detectKeyword();
    }

    public boolean waitForYesOrNo(String keywordFile) {
        sphinxHelper.loadKeywordFile(keywordFile);
        return sphinxHelper.yesOrNo();
    }

    public void selectStory() {
        CPlayWave.PlayWave(WAV_ROOT + "LetMePick.wav");
        if (gesture != null) {
            try {
                gesture.thinking();
            } catch (Exception e) {
                System.err.println("Error performing gesture: " + e.getMessage());
            }
        }
        System.out.println("> Selecting a story...");
        
        story = STORY_LIBRARY[storySelect];
        storySelect = (storySelect + 1) % 3;
        playingLine = 2;

        CPlayWave.PlayWave_wait(WAV_ROOT + "StoryConfirmation.wav");
        CPlayWave.PlayWave_wait(story[1]);
        System.out.println("SOTA: I have picked " + story[0]);
    }

    public void onLaunch(Greeting greeting) {
        switch (greeting) {
            case HI:
                CPlayWave.PlayWave(WAV_ROOT + "Hi.wav");
                System.out.println("SOTA: Hi there!");
                break;

            case GOODNIGHT:
                CPlayWave.PlayWave(WAV_ROOT + "Goodnight.wav");
                System.out.println("SOTA: Goodnight! Sleep tight.");
                isGoodnightExecuted = true;
                break;
        }

        if (gesture != null) {
            try {
                gesture.wavingHi();
            } catch (Exception e) {
                System.err.println("Error performing gesture: " + e.getMessage());
            }
        }
    }

    public void askUser(AskQuestion question) {
        switch (question) {
            case SELECT:
                CPlayWave.PlayWave(WAV_ROOT + "SelectStory.wav");
                System.out.println("SOTA: Would you like to listen to this story?");
                break;

            case ANOTHER:
                CPlayWave.PlayWave(WAV_ROOT + "Another.wav");
                System.out.println("SOTA: Would you like to listen to another story?");
                break;
        }
    }

    public boolean playStory() {
        System.out.println("> Playing story line " + (playingLine-1));
        CPlayWave.PlayWave_wait(story[playingLine]);
        playingLine += 1;
        return playingLine < story.length;
    }

    public boolean isGoodnightExecuted() {
        return isGoodnightExecuted;
    }

    public boolean isSleeping() {
        if (detector != null) {
            return detector.isPersonSleeping();
        }
        else {
            return false;
        }
    } 

    public void turnOnEyes() {
        pose.setLED_Sota(Color.BLUE, Color.BLUE, 255, Color.BLUE);
        motion.play(pose, 100);
    }

    public void cleanup() {  
        if (motion != null) {
            // Return to neutral pose before shutting down
            if (gesture != null) {
                try {
                    gesture.sleeping();
                } catch (Exception e) {
                    System.err.println("Error performing gesture: " + e.getMessage());
                }
            }
            if (detector != null) {
                detector.cleanup();
            }

            pose.setLED_Sota(Color.BLACK, Color.BLACK, 255, Color.BLACK);
            motion.play(pose, 100);
            motion.ServoOff();
        }
    }
}
