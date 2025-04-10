package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class TellStoryNode extends LeafTask<BedtimeBot> {
    // private boolean interrupted = false;
    // private Thread interruptListener;

    @Override
    public void start() {
        System.out.println("> Starting to tell story...");

        /*
        interruptListener = new Thread(() -> {
            if (getObject().waitForKeyword("interrupts.txt")) {
                interrupted = true;
            }
        });
        interruptListener.setDaemon(true);
        interruptListener.start();
        */
    }

    @Override
    public Status execute() {
        /*
        if (interrupted) {
            System.out.println("Handling interruption...");
            getObject().pauseStory();
            getObject().handleInterrupt();
            getObject().resumeStory();
            interrupted = false;
        }
        */
            
        // Check if the story is still playing
        if (!getObject().playStory()) {
            System.out.println("> Story playback has finished");
            return Status.SUCCEEDED; // Story is done, move to the next node
        }

        // If we reach here, the story is still playing
        return Status.RUNNING;
    }

    @Override
    protected Task<BedtimeBot> copyTo(Task<BedtimeBot> task) {
        TellStoryNode node = (TellStoryNode) task;
        return node;
    }
}