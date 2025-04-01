package AS3;

import jp.vstone.RobotLib.CSotaMotion;

public class Frames {

    enum FrameKeys{  
        L_HAND(CSotaMotion.SV_BODY_Y, CSotaMotion.SV_L_SHOULDER, CSotaMotion.SV_L_ELBOW),
        R_HAND(CSotaMotion.SV_BODY_Y, CSotaMotion.SV_R_SHOULDER, CSotaMotion.SV_R_ELBOW),
        HEAD(CSotaMotion.SV_BODY_Y, CSotaMotion.SV_HEAD_Y, CSotaMotion.SV_HEAD_P, CSotaMotion.SV_HEAD_R); 
        
        // store the motor indices that contribute to each frame here for use later.
        // hint: use IDtoIndex and the CSotaMotion. constants to make this easy to do.
        public int[] motorindices;
        FrameKeys(int... motorindices){
            this.motorindices = new int[motorindices.length];
            for (int i = 0; i < motorindices.length; i++) {
                this.motorindices[i] = motorindices[i] - 1; // subtract 1 since IDtoIndex is minus 1
            }         
        }
    }
}