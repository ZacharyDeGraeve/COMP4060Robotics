package AS4;

import com.badlogic.gdx.math.RandomXS128;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import marytts.exceptions.MaryConfigurationException;

public class BedtimeBot {
    private final static String WAV_ROOT = "/home/root/sotaprograms/resources/sound/";
    private final static String[] STORY_LIBRARY = {WAV_ROOT + "TheLittleMatchGirl.wav"};

    private boolean sleeping = false;
    private boolean finished = false;
    private PocketSphinxHelper sphinxHelper;
    private RandomXS128 random;
    private String story;
    private MaryTTSHelper ttsHelper;
    private Gesture gesture;
    private CSotaMotion motion;
    private CPlayWave player;

    public BedtimeBot() {
        System.out.println("[Info] Loading...");

        sphinxHelper = new PocketSphinxHelper();
        story = WAV_ROOT + "start_cam_test.wav";

        // Initialize the CSotaMotion
        CRobotMem mem = new CRobotMem();
        motion = new CSotaMotion(mem);
        
        // Only initialize the gesture system if we can connect to the robot
        if (mem.Connect()) {
            motion.InitRobot_Sota();
            motion.ServoOn();
            gesture = new Gesture(motion);
        }
        if (gesture != null) {
            try {
                gesture.neutral();
            } catch (Exception e) {
                System.err.println("Error performing gesture: " + e.getMessage());
            }
        }

        // initialize TTS helper
        try {
            ttsHelper = new MaryTTSHelper();
            ttsHelper.setCustomEffects("storyteller");
        } catch (MaryConfigurationException e) {
            System.err.println("Error initilizing MaryTTS: " + e.getMessage());
            ttsHelper = null;
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
        if (ttsHelper != null) {
            try {
                ttsHelper.speak("Ok. Let me pick a story");
                System.out.println("SOTA: Ok. Let me pick a story");

                if (gesture != null) {
                    try {
                        gesture.thinking();
                    } catch (Exception e) {
                        System.err.println("Error performing gesture: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error speaking: " + e.getMessage());
            }
        }
        System.out.println("> Selecting a story...");
        
        //story = STORY_LIBRARY[random.nextInt(STORY_LIBRARY.length)];
        // CPlayWave.PlayWave_wait(story);

        if (ttsHelper != null) {
            try {
                ttsHelper.speak("I picked Snow White");
                System.out.println("SOTA: I picked Snow White");
            } catch (Exception e) {
                System.err.println("Error speaking: " + e.getMessage());
            }
        }
    }

    public void onLaunch(String greeting) {
        if (ttsHelper != null) {
            try {
                ttsHelper.speak(greeting);
                System.out.println("SOTA: " + greeting);

                if (gesture != null) {
                    try {
                        gesture.wavingHi();
                    } catch (Exception e) {
                        System.err.println("Error performing gesture: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error speaking: " + e.getMessage());
            }
        }
    }

    public void askUser(String question) {
        if (ttsHelper != null) {
            try {
                ttsHelper.speak(question);
                System.out.println("SOTA: " + question);
            } catch (Exception e) {
                System.err.println("Error speaking: " + e.getMessage());
            }
        }
    }

    public void playStory() {
        finished = false;
        System.out.println("> Playing story...");
        player = CPlayWave.PlayWave_wait(story);
    }

    public boolean isStoryPlaying() {
        return player != null && player.isPlaying();
    }

    public boolean isStoryFinished() {
        return finished;
    }

    public void pauseStory() {
        System.out.println("Pausing story...");
    }

    public void handleInterrupt() {
        System.out.println("Handling user question...");
        // Implement interruption handling logic
    }

    public void resumeStory() {
        System.out.println("Resuming story...");
    }

    public boolean isUserAsleep() {
        // Use face detection API to determine if the user is asleep
        return false;
    }

    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
        if (gesture != null) {
            try {
                gesture.sleeping();
            } catch (Exception e) {
                System.err.println("Error performing gesture: " + e.getMessage());
            }
        }
    }

    public boolean isSleeping() {
        return sleeping;
    }

    public void cleanup() {        
        if (motion != null) {
            // Return to neutral pose before shutting down
            if (gesture != null) {
                gesture.neutral();
            }
            motion.ServoOff();
        }
    }
}
