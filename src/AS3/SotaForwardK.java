package AS3;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.linear.*;
import AS3.Frames.FrameKeys;

public class SotaForwardK {

    public final Map<FrameKeys, RealMatrix> frames = new HashMap<>();

    public RealVector endEffectorState = null; // a single vector representing the combined state of the end effector. needs to be in the same order as in the IK

    public SotaForwardK(double[] angles) { this(MatrixUtils.createRealVector(angles)); }
    public SotaForwardK(RealVector angles) {
        // TODO
        // constructs all the frame matrices and stores them in a Map that maps
        //  a frame type (FrameKey) to the frame matrix.
    }
}