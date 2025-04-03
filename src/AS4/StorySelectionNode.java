package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class StorySelectionNode extends LeafTask<BedtimeBot> {
    @Override
    public Status execute() {
        try {
            getObject().selectStory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        getObject().askUser("Would you like to hear this story?");
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<BedtimeBot> copyTo(Task<BedtimeBot> task) {
        return task;
    }
}
