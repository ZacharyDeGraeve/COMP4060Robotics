package AS3;

public class Frames {

    enum FrameKeys{  
        L_HAND(0, 1, 2),
        R_HAND(0, 3, 4),
        HEAD(0, 5, 6, 7); 
        
        // store the motor indices that contribute to each frame here for use later.
        // hint: use IDtoIndex and the CSotaMotion. constants to make this easy to do.
        public int[] motorindices;
        FrameKeys(int... motorindices){
            this.motorindices = motorindices;            
        }
    }
}