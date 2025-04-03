package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class TellStoryNode extends LeafTask<BedtimeBot> {
    private boolean interrupted = false; // Flag to indicate interruption
    private Thread interruptListener;

    @Override
    public void start() {
        try {
            getObject().playStory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start interrupt listener on another thread
        interruptListener = new Thread(() -> {
            if (getObject().waitForKeyword("interrupts.txt")) {
                interrupted = true; // Mark interrupted
            }
        });
        interruptListener.setDaemon(true);
        interruptListener.start();
    }

    @Override
    public Status execute() {
        if (interrupted) {
            System.out.println("Handling interruption...");
            getObject().pauseStory();
            getObject().handleInterrupt();
            getObject().resumeStory();
            interrupted = false;
        }

        if (getObject().isStoryFinished()) {
            return Status.SUCCEEDED; // Moves to AskNode after finishing the story
        }

        return Status.RUNNING;
    }

    @Override
    protected Task<BedtimeBot> copyTo(Task<BedtimeBot> task) {
        return task;
    }
}
