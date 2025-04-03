package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class WaitNode extends LeafTask<BedtimeBot> {
    @Override
    public Status execute() {
        System.out.println("Waiting for starting keyword...");
        if (getObject().waitForKeyword("start_keywords.txt")) {
            return Status.SUCCEEDED;
        }
        return Status.RUNNING;
    }

    @Override
    protected Task<BedtimeBot> copyTo(Task<BedtimeBot> task) {
        return task;
    }
}
