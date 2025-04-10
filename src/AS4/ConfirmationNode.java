package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class ConfirmationNode extends LeafTask<BedtimeBot> {
    @Override
    public Status execute() {
        System.out.println("Waiting for confirmation...");
        if (getObject().waitForYesOrNo("confirmation_keywords.txt")) {
            return Status.SUCCEEDED; // Moves on to TellStory
        } else {
            return Status.FAILED;  // Returns to StorySelectionNode
        }
    }

    @Override
    protected Task<BedtimeBot> copyTo(Task<BedtimeBot> task) {
        return task;
    }
}
