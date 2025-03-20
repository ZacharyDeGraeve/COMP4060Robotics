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
        
        //======== setup Transformation matrices
        // Base
        RealMatrix _base_to_origin = MatrixUtils.createRealIdentityMatrix(4);

        // Body: apply rotation z then translate to origin xyz = "0 0 .005"
        RealMatrix _body_to_base = MatrixHelp.T(MatrixHelp.rotZ(angles.getEntry(0)), 0, 0, 0.005);

        // Head
        // Head Y: apply rotation z then translate to origin xyz = "0 0 .190"
        RealMatrix _head_y_to_body = MatrixHelp.T(MatrixHelp.rotZ(angles.getEntry(5)), 0, 0, 0.190);
        // Head R: apply rotation y then translate to origin xyz = "0 0 0"
        RealMatrix _head_r_to_head_y  = MatrixHelp.T(MatrixHelp.rotY(angles.getEntry(7)), 0, 0, 0);
        // Head P: apply rotation x then translate to origin xyz = "0 0 0"
        RealMatrix _head_p_to_head_r = MatrixHelp.T(MatrixHelp.rotX(angles.getEntry(6)), 0, 0, 0);

        // HAND to ELBOW distance
        double handDistance = 0.0523875;
        
        // Left Arm
        // Left Shoulder: apply rotation x then translate to origin xyz = ".039 0 .1415"
        RealMatrix _left_shoulder_to_body = MatrixHelp.T(MatrixHelp.rotX(angles.getEntry(1)), 0.039, 0, 0.1415);
        // Left Elbow: apply rotation on axis xyz = "0.6258053 0.329192519 0.707106769" then translate to origin xyz = ".0225 -.03897 0"
        RealMatrix _left_elbow_to_left_shoulder = MatrixHelp.T(MatrixHelp.rotRodrigues(0.6258053, 0.329192519, 0.707106769, angles.getEntry(2)), 0.0225, -0.03897, 0);
        // Left Hand: rotate with Left Elbow so only translate
        RealVector leftElbowVector = MatrixUtils.createRealVector(new double[]{0.0225, -0.03897, 0, 1});
        RealVector leftElbowUnitVector = MatrixHelp.normalizeH(leftElbowVector);

        double leftHandX = leftElbowUnitVector.getEntry(0) * handDistance;
        double leftHandY = leftElbowUnitVector.getEntry(1) * handDistance;
        double leftHandZ = leftElbowUnitVector.getEntry(2) * handDistance;

        RealMatrix _left_hand_to_left_elbow = MatrixHelp.trans(leftHandX, leftHandY, leftHandZ);

        // Right Arm
        // Right Shoulder: apply rotation x then translate to origin xyz = "-.039 0 .1415"
        RealMatrix _right_shoulder_to_body = MatrixHelp.T(MatrixHelp.rotX(angles.getEntry(3)), -0.039, 0, 0.1415);
        // Right Elbow: apply rotation on axis xyz = "-0.6258053 0.329192519 0.707106769" then translate to origin xyz = "-.0225 -.03897 0"
        RealMatrix _right_elbow_to_right_shoulder = MatrixHelp.T(MatrixHelp.rotRodrigues(-0.6258053, 0.329192519, 0.707106769, angles.getEntry(4)), -0.0225, -0.03897, 0);
        // Right Hand: rotate with Right Elbow so only translate
        RealVector rightElbowVector = MatrixUtils.createRealVector(new double[]{-0.0225, -0.03897, 0, 1});
        RealVector rightElbowUnitVector = MatrixHelp.normalizeH(rightElbowVector);

        double rightHandX = rightElbowUnitVector.getEntry(0) * handDistance;
        double rightHandY = rightElbowUnitVector.getEntry(1) * handDistance;
        double rightHandZ = rightElbowUnitVector.getEntry(2) * handDistance;

        RealMatrix _right_hand_to_right_elbow = MatrixHelp.trans(rightHandX, rightHandY, rightHandZ);


        //========== precalculate combined chains
        RealMatrix _body_to_origin = _base_to_origin.multiply(_body_to_base);
        // Head Chain
        RealMatrix _head_p_to_origin = _body_to_origin.multiply(_head_y_to_body).multiply(_head_r_to_head_y).multiply(_head_p_to_head_r);
        // Left Arm Chain
        RealMatrix _left_hand_to_origin = _body_to_origin.multiply(_left_shoulder_to_body).multiply(_left_elbow_to_left_shoulder).multiply(_left_hand_to_left_elbow);
        // Right Arm Chain
        RealMatrix _right_hand_to_origin = _body_to_origin.multiply(_right_shoulder_to_body).multiply(_right_elbow_to_right_shoulder).multiply(_right_hand_to_right_elbow);

        // Store frames
        frames.put(FrameKeys.HEAD, _head_p_to_origin);
        frames.put(FrameKeys.L_HAND, _left_hand_to_origin);
        frames.put(FrameKeys.R_HAND, _right_hand_to_origin);
    }
}