package AS4;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.btree.decorator.UntilSuccess;

import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.camera.CRoboCamera;

import com.badlogic.gdx.ai.btree.branch.Selector;

public class Main {
    // public static void main(String[] args) {
    //     BedtimeBot bedtimeBot = new BedtimeBot();

    //     Sequence<BedtimeBot> root = new Sequence<>();
    //     root.addChild(new GreetingNode());
    //     root.addChild(new WaitNode());
        
    //     // Create a loop for story selection and confirmation
    //     UntilSuccess<BedtimeBot> storySelectionLoop = new UntilSuccess<>();
        
    //     // Create a sequence for story selection and confirmation
    //     Sequence<BedtimeBot> storySelectionAndConfirmation = new Sequence<>();
    //     storySelectionAndConfirmation.addChild(new StorySelectionNode());
    //     storySelectionAndConfirmation.addChild(new ConfirmationNode());
        
    //     // Add the sequence to the loop
    //     storySelectionLoop.addChild(storySelectionAndConfirmation);
        
    //     // Add the story selection loop to the root
    //     root.addChild(storySelectionLoop);
        
    //     // Create a selector for the story telling and goodnight paths
    //     Selector<BedtimeBot> storyOrGoodnight = new Selector<>();
        
    //     // The tell story sequence - first choice in the selector
    //     Sequence<BedtimeBot> tellStorySequence = new Sequence<>();
    //     tellStorySequence.addChild(new TellStoryNode());
    //     tellStorySequence.addChild(new AskNode());
        
    //     // Add tell story sequence as first option
    //     storyOrGoodnight.addChild(tellStorySequence);
        
    //     // Add goodnight node as fallback option when story sequence fails
    //     storyOrGoodnight.addChild(new GoodnightNode());
        
    //     // Add the selector to the root
    //     root.addChild(storyOrGoodnight);

    //     BehaviorTree<BedtimeBot> behaviorTree = new BehaviorTree<>(root);
    //     behaviorTree.setObject(bedtimeBot);

    //     // // Start face detection in a separate thread
    //     // Thread faceDetectionThread = new Thread(new FaceDetectionThread(bedtimeBot));
    //     // faceDetectionThread.setDaemon(true);
    //     // faceDetectionThread.start();

    //     while (true) {
    //         behaviorTree.step();

    //         if (bedtimeBot.isSleeping()) {
    //             System.out.println("Shutting down...");
    //             break;
    //         }

    //         try {
    //             Thread.sleep(100);
    //         } catch (InterruptedException e) {
    //             e.printStackTrace();
    //         }
    //     }

    //     System.out.println("System shut down.");
    // }

    static final String TAG = "SleepingDetectorTest";
    
    public static void main(String[] args) {
        CRobotUtil.Log(TAG, "Starting SleepingDetector test");
        SleepingDetector detector = new SleepingDetector();
        
        try {
            long startTime = System.currentTimeMillis();
            boolean isSleeping = false;
            
            while (System.currentTimeMillis() - startTime < 10000 && !isSleeping) {
                isSleeping = detector.isPersonSleeping();
                
                if (isSleeping) {
                    CRobotUtil.Log(TAG, "SLEEP DETECTED! Person is sleeping.");
                    break;
                }
                
                // Check every 500ms
                CRobotUtil.wait(500);
            }
            
            System.out.println("Test result: Person sleeping = " + isSleeping);
            
        } finally {
            // Clean up
            detector.cleanup();
        }
    }
}