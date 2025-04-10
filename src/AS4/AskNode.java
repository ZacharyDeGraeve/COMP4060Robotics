package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class AskNode extends LeafTask<BedtimeBot> {
    @Override
    public Status execute() {
        getObject().askUser(BedtimeBot.AskQuestion.ANOTHER);
        if (getObject().waitForYesOrNo("confirmation_keywords.txt")) {
            return Status.SUCCEEDED;  // Returns to StorySelectionNode
        } else {
            return Status.FAILED;  // Causes system shutdown
        }
    }

    @Override
    protected Task<BedtimeBot> copyTo(Task<BedtimeBot> task) {
        return task;
    }
}