package AS4;

import com.badlogic.gdx.math.RandomXS128;
import jp.vstone.RobotLib.CPlayWave;

public class BedtimeBot {
    private final static String WAV_ROOT = "/home/root/sotaprograms/resources/sound/";
    private final static String[] STORY_LIBRARY = {WAV_ROOT + "TheLittleMatchGirl.wav"};

    private boolean sleeping = false;
    private boolean finished = false;
    private PocketSphinxHelper sphinxHelper;
    private RandomXS128 random;
    private String story;

    public BedtimeBot() {
        sphinxHelper = new PocketSphinxHelper();
        random = new RandomXS128();
        story = WAV_ROOT + "error.wav";
    }

    public boolean waitForKeyword(String keywordFile) {
        sphinxHelper.loadKeywordFile(keywordFile);
        return sphinxHelper.detectKeyword();
    }

    public void selectStory() {
        System.out.println("Selecting a story...");
        //story = STORY_LIBRARY[random.nextInt(STORY_LIBRARY.length)];
        CPlayWave.PlayWave_wait(story);
    }

    public void askUser(String question) {
        System.out.println(question);
    }

    public void playStory() {
        finished = false;
        System.out.println("Playing story...");
        // Implement WAV file playback
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
    }

    public boolean isSleeping() {
        return sleeping;
    }
}
