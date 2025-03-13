package AS3;
import java.util.Arrays;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import AS3.Frames.FrameKeys;
import AS3.SotaInverseK.JType;
import jp.vstone.RobotLib.*;

public class AS3_5 {
	static final String TAG = "AS3_5";   // set this to support the Sota logging system

	final boolean DEBUG_PRINT = false; // turn on to get debug output
	enum TestMode {
		EASY,
		MED,
		HARD,
		EXTREME;
	}
	final TestMode TEST_MODE=TestMode.EXTREME;

	// private variables
	CRobotPose _sotaPose = new CRobotPose();
	CRobotMem _sotaMem = new CRobotMem();
	CSotaMotion _sotaMotion = new CSotaMotion(_sotaMem);

	AS3_5() {
		CRobotUtil.Log(TAG, "Start " + TAG);
	}

	boolean connect() {		
		if(!_sotaMem.Connect()) { // connect to the robot's subsystem
			CRobotUtil.Log(TAG, "Sota connection failure " + TAG);
			return false;
		}

		CRobotUtil.Log(TAG, "connected " + TAG);
		_sotaMotion.InitRobot_Sota();  // initialize the Sota VSMD			
		CRobotUtil.Log(TAG, "Rev. " + _sotaMem.FirmwareRev.get());
		return true;
	}

	void run() {

		double RADIUS = 0; // m
		double ROTATION_SPEED = 0;  // rad / tick
		double l_angle = 0;
		double r_angle = 0; 
		double h_angle = 0; // head angle.
		double h_speed = 0.05;

		switch (this.TEST_MODE) {
			case EASY:
				RADIUS = .015; // mm
				ROTATION_SPEED = 0.05; // radians each time.
				break;

			case MED:
				RADIUS = .025; // mm
				ROTATION_SPEED = 0.15; // radians each time.
				break;

			case EXTREME:
				// no break on purpose

			case HARD:
				RADIUS = .035; // mm
				ROTATION_SPEED = 0.5; // radians each time.
				break;
		}
		
		ServoRangeTool ranges = ServoRangeTool.Load();
		CRobotUtil.Log(TAG, "Servo Ranges Loaded");
		
		_sotaMotion.ServoOn();
		CRobotUtil.Log(TAG, "Servos On"); // initialize in on state
		
		Short[] startPose = new Short[]{-20, -261, -568, 261, 568, 31, -95, 26};  // a middle-stance for waxon/waxoff
		CRobotPose pose = new CRobotPose();
		pose.SetPose(_sotaMotion.getDefaultIDs(), startPose);
		_sotaMotion.play(pose, 1000);
		_sotaMotion.waitEndinterpAll();

		SotaForwardK FK = new SotaForwardK(ranges.calcAngles(_sotaMotion.getReadPose()));
		double[] leftCenter = MatrixHelp.getTrans(FK.frames.get(FrameKeys.L_HAND)).toArray();
		double[] rightCenter = MatrixHelp.getTrans(FK.frames.get(FrameKeys.R_HAND)).toArray();
		double[] headCenter = MatrixHelp.getTrans(FK.frames.get(FrameKeys.HEAD)).toArray();

		if (DEBUG_PRINT) System.out.println("center motors: "+Arrays.toString(leftCenter));

		boolean first = true;
		while (!_sotaMotion.isButton_Power()) {  // stop when the power button is pressed
			RealVector currentAngles = ranges.calcAngles(_sotaMotion.getReadPose());
			if (DEBUG_PRINT) MatrixHelp.printVector("angles", currentAngles);

			// calculate new left/right hand positions as offset from their home position.
			double[] left = 	new double[]{leftCenter[0] 	+ Math.cos(l_angle)*RADIUS, leftCenter[1], leftCenter[2]+Math.sin(l_angle)*RADIUS};
			double[] right = 	new double[]{rightCenter[0] + Math.cos(r_angle)*RADIUS, rightCenter[1], rightCenter[2]+Math.sin(r_angle)*RADIUS};
			double[] head = 	new double[]{headCenter[0]	+ Math.sin(h_angle), headCenter[1]+(h_angle*3)%1.5, headCenter[2]+Math.sin(h_angle)};

			// do IK to solve for motor positions, do incrementally
			RealVector theta = SotaInverseK.solve(FrameKeys.L_HAND, JType.O, MatrixUtils.createRealVector(left), currentAngles); 
			theta = SotaInverseK.solve(FrameKeys.R_HAND, JType.O, MatrixUtils.createRealVector(right), theta);
			if (this.TEST_MODE == TestMode.EXTREME)
				theta = SotaInverseK.solve(FrameKeys.HEAD, JType.R, MatrixUtils.createRealVector(head), theta); ; 
			
			pose = ranges.calcMotorValues(theta);
			
			if (DEBUG_PRINT) {
				MatrixHelp.printVector("post-IK angles ", theta);
				System.out.println("motors pre move: "+Arrays.toString(_sotaMotion.getReadpos()) );
				System.out.println("motors target  : "+Arrays.toString(pose.getServoAngles(_sotaMotion.getDefaultIDs())) );
			}

			if (first) {
				_sotaMotion.play(pose, 500); // make move to first angle smooth
				_sotaMotion.waitEndinterpAll();
				first = false;	
			} else
				_sotaMotion.play(pose, 100);   /// if robot doesn't move, increase this to 30, 40, 50, etc.

			// _sotaMotion.waitEndinterpAll();  // not needed but can turn it on to help with debugging.
			if (DEBUG_PRINT) System.out.println("motors after:  "+Arrays.toString(_sotaMotion.getReadpos()) );

			// rotate angles
			l_angle = (l_angle-ROTATION_SPEED) % (Math.PI*2);
			r_angle = (r_angle+ROTATION_SPEED) % (Math.PI*2);
			h_angle += h_speed;
			if (h_angle > 1) h_speed *= -1;
			if (h_angle < -1) h_speed *= -1;

			CRobotUtil.wait(20); // increasa to a high value can help with debugging.
		}
	}

	public static void main(String args[]){
		AS3_5 sota = new AS3_5();
		if (!sota.connect())
			return;
		CRobotUtil.Log(TAG, "Startup Successful");
		sota.run();
			
		CRobotUtil.Log(TAG, "Program End Reached");
	}
}

