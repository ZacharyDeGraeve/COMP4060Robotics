package AS3;

import java.util.TreeMap;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import AS3.Frames.FrameKeys;
import AS3.SotaForwardK;

public class SotaInverseK {

    private static double NUMERICAL_DELTA_rad = 1e-10;
    private static double DISTANCE_THRESH = 1e-3; // 1mm
    private static int MAX_ITERATIONS = 50;

    enum JType {  // We separate the jacobians into origin and rotation components to simplify the problem
        O, // origin
        R; // rotation / orientation
        
        public static final int OUT_DIM = 3; // each has 3 outputs
    }

    public TreeMap<FrameKeys, RealMatrix>[] J;
    public TreeMap<FrameKeys, RealMatrix>[] Jinv;

    @SuppressWarnings("unchecked")
    SotaInverseK(RealVector currentAngles, FrameKeys frameType) {
        J = new TreeMap[JType.values().length];
        Jinv = new TreeMap[JType.values().length];
        for (int j=0; j < JType.values().length; j++) {
            J[j] = new TreeMap<FrameKeys, RealMatrix>();
            Jinv[j] = new TreeMap<FrameKeys, RealMatrix>();
        }
       makeJacobian(currentAngles, frameType);
    }

    // Makes both the jacobian and inverse from the current configuration for the
    // given frame type. Creates both JTypes.
    private void makeJacobian(RealVector currentAngles, FrameKeys frameType) {
        // Get the motor indices specific to this frame type (L_HAND, R_HAND or HEAD)
        int[] motorIndices = frameType.motorindices;
        int numJoints = frameType.motorindices.length;
        
        // Create Jacobian matrices for position (O) and orientation (R)
        // Each (L_HAND, R_HAND or HEAD) has 3 output dimensions (x,y,z or roll,pitch,yaw) and columns for relevant joints only
        RealMatrix jacobianO = MatrixUtils.createRealMatrix(JType.OUT_DIM, numJoints);
        RealMatrix jacobianR = MatrixUtils.createRealMatrix(JType.OUT_DIM, numJoints);
        
        // Calculate FK for the current angles to get the base state
        SotaForwardK baseFk = new SotaForwardK(currentAngles);
        
        // Extract the current position and orientation for the specified frame
        RealVector basePos = MatrixHelp.getTrans(baseFk.frames.get(frameType));
        RealVector baseOrientation = MatrixHelp.getYPRVec(baseFk.frames.get(frameType));
        
        // For each relevant joint, calculate its column in the Jacobian
        for (int j = 0; j < numJoints; j++) {
            int jointIdx = motorIndices[j];  // Get the actual motor index
            
            // Create a copy of current angles to perturb
            RealVector perturbedAngles = currentAngles.copy();
            
            // Apply a small perturbation to this joint only
            perturbedAngles.setEntry(jointIdx, perturbedAngles.getEntry(jointIdx) + NUMERICAL_DELTA_rad);
            
            // Calculate FK with the perturbed angle
            SotaForwardK perturbedFk = new SotaForwardK(perturbedAngles);
            
            // Get the perturbed position and orientation
            RealVector perturbedPos = MatrixHelp.getTrans(perturbedFk.frames.get(frameType));
            RealVector perturbedOrientation = MatrixHelp.getYPRVec(perturbedFk.frames.get(frameType));
            
            // Calculate the difference (this approximates the partial derivative)
            RealVector posDiff = perturbedPos.subtract(basePos);
            RealVector orientationDiff = perturbedOrientation.subtract(baseOrientation);
            
            // Normalize by the perturbation amount to get the derivative
            posDiff = posDiff.mapDivide(NUMERICAL_DELTA_rad);
            orientationDiff = orientationDiff.mapDivide(NUMERICAL_DELTA_rad);
            
            // Set this column in the Jacobian matrices
            for (int i = 0; i < JType.OUT_DIM; i++) {
                jacobianO.setEntry(i, j, posDiff.getEntry(i));
                jacobianR.setEntry(i, j, orientationDiff.getEntry(i));
            }
        }
        
        // Store the Jacobians
        J[JType.O.ordinal()].put(frameType, jacobianO);
        J[JType.R.ordinal()].put(frameType, jacobianR);
        
        // Calculate and store the pseudo-inverses
        Jinv[JType.O.ordinal()].put(frameType, MatrixHelp.pseudoInverse(jacobianO));
        Jinv[JType.R.ordinal()].put(frameType, MatrixHelp.pseudoInverse(jacobianR));
    }
    
    // calculates the target absolute pose from the current pose, plus the given delta
    // using FK before calling solve.
    static public RealVector solveDelta(FrameKeys frameType, JType jtype, RealVector deltaEndPose, RealVector curMotorAngles) {
        //TODO if needed
        return solve(frameType, jtype, null, curMotorAngles);
    }

    // solves for the target pose on the given frame and type, starting at the current angle configuration.
    static public RealVector solve(FrameKeys frameType, JType jtype, RealVector targetPose, RealVector curMotorAngles) {
        RealVector solution = curMotorAngles.copy();
        RealVector theta_i = curMotorAngles.copy();
        SotaInverseK IK = null;
        SotaForwardK FK = new SotaForwardK(theta_i);
        RealVector FK_solved = null;

        // set FK to be based on the correct JType
        if (jtype == JType.O) {
            FK_solved = MatrixHelp.getTrans(FK.frames.get(frameType));
        }
        else if (jtype == JType.R) {
            FK_solved = MatrixHelp.getYPRVec(FK.frames.get(frameType));
        }

        // Create a frame-specific theta
        RealVector frameTheta = new ArrayRealVector(frameType.motorindices.length);

        // Map the full theta_i to the frame-specific theta for the affected motors
        for (int i = 0; i < frameType.motorindices.length; i++) {
            frameTheta.setEntry(i, theta_i.getEntry(frameType.motorindices[i]));
        }

        // Calculate starting error
        RealVector error = targetPose.subtract(FK_solved.getSubVector(0,3));
        RealVector min_error = error.copy();

        for (int i = 0; i < MAX_ITERATIONS && error.getNorm() > DISTANCE_THRESH; i++) {
            IK = new SotaInverseK(theta_i, frameType);
            IK.makeJacobian(theta_i, frameType);
            frameTheta = frameTheta.add(IK.Jinv[jtype.ordinal()].get(frameType).operate(error));

            // Update the full theta_i with the new values
            for (int j = 0; j < frameType.motorindices.length; j++) {
                theta_i.setEntry(frameType.motorindices[j], frameTheta.getEntry(j));
            }

            FK = new SotaForwardK(theta_i);
            if (jtype == JType.O) {
                FK_solved = MatrixHelp.getTrans(FK.frames.get(frameType));
            }
            else if (jtype == JType.R) {
                FK_solved = MatrixHelp.getYPRVec(FK.frames.get(frameType));
            }

            error = targetPose.subtract(FK_solved.getSubVector(0,3));

            if (error.getNorm() > DISTANCE_THRESH) {
                return theta_i;
            }
            else if (error.getNorm() < min_error.getNorm()) {
                min_error = error.copy();
                solution = theta_i.copy();
            }
        }
        
        return solution; // Return the best solution found
    }   
}