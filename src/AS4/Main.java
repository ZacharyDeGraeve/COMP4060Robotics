package AS4;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.btree.decorator.UntilSuccess;

public class Main {
    public static void main(String[] args) {
        BedtimeBot bedtimeBot = new BedtimeBot();

        Sequence<BedtimeBot> root = new Sequence<>();
        root.addChild(new WaitNode());
        root.addChild(new StorySelectionNode());
        UntilSuccess<BedtimeBot> confirmationLoop = new UntilSuccess<>();
        confirmationLoop.addChild(new ConfirmationNode());
        root.addChild(confirmationLoop);
        
        Sequence<BedtimeBot> tellStorySequence = new Sequence<>();
        tellStorySequence.addChild(new TellStoryNode());
        UntilSuccess<BedtimeBot> askLoop = new UntilSuccess<>();
        askLoop.addChild(new AskNode());
        tellStorySequence.addChild(askLoop);
        
        root.addChild(tellStorySequence);

        BehaviorTree<BedtimeBot> behaviorTree = new BehaviorTree<>(root);
        behaviorTree.setObject(bedtimeBot);

        // Start face detection in a separate thread
        Thread faceDetectionThread = new Thread(new FaceDetectionThread(bedtimeBot));
        faceDetectionThread.setDaemon(true);
        faceDetectionThread.start();

        while (true) {
            behaviorTree.step();

            if (bedtimeBot.isSleeping()) {
                System.out.println("User is asleep. Shutting down...");
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("System shut down.");
    }
}
