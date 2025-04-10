package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import AS4.BedtimeBot.AskQuestion;

public class StorySelectionNode extends LeafTask<BedtimeBot> {
    @Override
    public Status execute() {
        try {
            getObject().selectStory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        getObject().askUser(BedtimeBot.AskQuestion.SELECT);
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<BedtimeBot> copyTo(Task<BedtimeBot> task) {
        return task;
    }
}
