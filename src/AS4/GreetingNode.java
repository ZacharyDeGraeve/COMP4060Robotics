package AS4;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

public class GreetingNode extends LeafTask<BedtimeBot> {
    private boolean hasGreeted = false;
    
    @Override
    public Status execute() {
        if (!hasGreeted) {
            getObject().turnOnEyes();
            getObject().onLaunch(BedtimeBot.Greeting.HI);
            hasGreeted = true;
        }
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<BedtimeBot> copyTo(Task<BedtimeBot> task) {
        GreetingNode node = (GreetingNode) task;
        node.hasGreeted = this.hasGreeted;
        return task;
    }
}