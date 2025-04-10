package AS4;

import jp.vstone.RobotLib.*;

public class Gesture {
    // Reference to the motion controller
    private CSotaMotion motion;
    // Default transition time for movements in milliseconds
    private int defaultTransitionTime = 1000;
    
    public Gesture(CSotaMotion motion) {
        this.motion = motion;
    }
    
    public void setDefaultTransitionTime(int time) {
        this.defaultTransitionTime = time;
    }
    
    public void neutral() {
        CRobotPose pose = new CRobotPose();
        pose.SetPose(new Byte[] {1, 2, 3, 4, 5, 6, 7, 8},
                    new Short[]{0, -800, -250, 800, 250, 0, 0, 0});
        motion.play(pose, defaultTransitionTime);
        motion.waitEndinterpAll();
    }
    
    public void wavingHi() {
        CRobotPose pose1 = new CRobotPose();
        pose1.SetPose(new Byte[] {
                CSotaMotion.SV_HEAD_R,
                CSotaMotion.SV_L_SHOULDER
            },
            new Short[]{
                -300,
                600
            });
        motion.play(pose1, defaultTransitionTime/2);
        motion.waitEndinterpAll();

        for (int i = 0; i < 3; i++) {
            // Wave forward
            CRobotPose pose2 = new CRobotPose();
            pose2.SetPose(new Byte[] {CSotaMotion.SV_L_ELBOW},
                         new Short[]{-300});
            motion.play(pose2, defaultTransitionTime/4);
            motion.waitEndinterpAll();
            
            // Wave backward
            CRobotPose pose3 = new CRobotPose();
            pose3.SetPose(new Byte[] {CSotaMotion.SV_L_ELBOW},
                         new Short[]{100});
            motion.play(pose3, defaultTransitionTime/4);
            motion.waitEndinterpAll();
        }
        
        neutral();
    }

    public void thinking() {
        CRobotPose pose = new CRobotPose();
        pose.SetPose(new Byte[] {
                CSotaMotion.SV_HEAD_R,
                CSotaMotion.SV_HEAD_P,
                CSotaMotion.SV_R_SHOULDER,
                CSotaMotion.SV_R_ELBOW
            },
            new Short[]{
                -300,
                300,
                -50,
                900
            });
        motion.play(pose, defaultTransitionTime);
        motion.waitEndinterpAll();
        
        CRobotUtil.wait(1500);
        
        neutral();
    }

    public void sleeping() {
        CRobotPose pose = new CRobotPose();
        pose.SetPose(new Byte[] {
                CSotaMotion.SV_HEAD_P,
            },
            new Short[]{
                500,
            });
        motion.play(pose, defaultTransitionTime);
        motion.waitEndinterpAll();
    }
}