package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class WaitNode extends LeafTask<BedtimeBot> {
    private boolean hasWaited = false;

    @Override
    public Status execute() {
        if (!hasWaited) {
            System.out.println("> Waiting for you to tell Sota to tell a story");
            if (getObject().waitForKeyword("start_keywords.txt")) {
                hasWaited = true;
            }
            return Status.RUNNING;
        }
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<BedtimeBot> copyTo(Task<BedtimeBot> task) {
        return task;
    }
}
