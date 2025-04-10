package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class GoodnightNode extends LeafTask<BedtimeBot> {
    @Override
    public Status execute() {
        getObject().onLaunch(BedtimeBot.Greeting.GOODNIGHT);
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<BedtimeBot> copyTo(Task<BedtimeBot> task) {
        return task;
    }
}