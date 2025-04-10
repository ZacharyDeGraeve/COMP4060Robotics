package AS4;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.btree.decorator.UntilSuccess;

import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotMotion;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.camera.CRoboCamera;

import com.badlogic.gdx.ai.btree.branch.Selector;

public class Main {
    static final String TAG = "BedtimeBot";

    public static void main(String[] args) {
        BedtimeBot bedtimeBot = new BedtimeBot();

        Sequence<BedtimeBot> root = new Sequence<>();
        root.addChild(new GreetingNode());
        root.addChild(new WaitNode());
        
        // Create a loop for story selection and confirmation
        UntilSuccess<BedtimeBot> storySelectionLoop = new UntilSuccess<>();
        
        // Create a sequence for story selection and confirmation
        Sequence<BedtimeBot> storySelectionAndConfirmation = new Sequence<>();
        storySelectionAndConfirmation.addChild(new StorySelectionNode());
        storySelectionAndConfirmation.addChild(new ConfirmationNode());
        
        // Add the sequence to the loop
        storySelectionLoop.addChild(storySelectionAndConfirmation);
        
        // Add the story selection loop to the root
        root.addChild(storySelectionLoop);
        
        // Create a selector for the story telling and goodnight paths
        Selector<BedtimeBot> storyOrGoodnight = new Selector<>();
        
        // The tell story sequence - first choice in the selector
        Sequence<BedtimeBot> tellStorySequence = new Sequence<>();
        tellStorySequence.addChild(new TellStoryNode());
        tellStorySequence.addChild(new AskNode());
        
        // Add tell story sequence as first option
        storyOrGoodnight.addChild(tellStorySequence);
        
        // Add goodnight node as fallback option when story sequence fails
        storyOrGoodnight.addChild(new GoodnightNode());
        
        // Add the selector to the root
        root.addChild(storyOrGoodnight);

        BehaviorTree<BedtimeBot> behaviorTree = new BehaviorTree<>(root);
        behaviorTree.setObject(bedtimeBot);

        try {
            long startTime = System.currentTimeMillis();
            boolean isSleeping = false;

            while (true) {

                if (behaviorTree.getObject().isSleeping()) {
                    CRobotUtil.Log(TAG, "SLEEP DETECTED! Shutting down...");
                    break;
                }

                if (behaviorTree.getObject().isGoodnightExecuted()) {
                    CRobotUtil.Log(TAG, "Shutting down...");
                    break;
                }

                behaviorTree.step();
            }
        } finally {
            behaviorTree.getObject().cleanup();
            System.out.println("System shut down.");
        }
    }
}