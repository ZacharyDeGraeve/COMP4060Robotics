package AS3;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;

public class AS3_2 {
    static final String TAG = "AS3_2";   // set this to support the Sota logging system

    // private variables
	CRobotPose _sotaPose = new CRobotPose();
	CRobotMem _sotaMem = new CRobotMem();
	CSotaMotion _sotaMotion = new CSotaMotion(_sotaMem);

    AS3_2() {
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
        Byte[] servoIDs = _sotaMotion.getDefaultIDs();

        // Load the servo ranges from servo_ranges.dat
        ServoRangeTool servoRangeTool = ServoRangeTool.Load();
        if (servoRangeTool == null) {
            CRobotUtil.Log(TAG, "Error: Could not load file");
            return;
        }

        // Print the motor ranges
        CRobotUtil.Log(TAG, "Loaded servo ranges:");
        servoRangeTool.printMotorRanges();
        // Enable the motors
        CRobotUtil.Log(TAG, "Enabling motors");
        _sotaMotion.ServoOn();

        // Move to mid/center position
        CRobotUtil.Log(TAG, "Moving to mid position");
        CRobotPose midPose = servoRangeTool.getMidPose();
        _sotaMotion.play(midPose, 1000);
        // wait until motion has finished
        _sotaMotion.waitEndinterpAll(); // also async public boolean isEndInterpAll()
        CRobotUtil.wait(1000); //pause the program / current thread

        // Go through each joint: move to min -> max -> mid

        // Warning: don’t try to move to the min or max positions because, if all motors do this at the same time, 
        // they collide, and you can damage the robot 
        // After the robot reaches the mid point, go through each joint in turn (following the order in 
        // getServoAngles is fine). For each, move the joint to the min, then the max, then back to the mid, and 
        // then move on to the next joint.

        // TODO
        moveJoint(servoRangeTool, CSotaMotion.SV_R_ELBOW, "Right Elbow");
        moveJoint(servoRangeTool, CSotaMotion.SV_R_SHOULDER, "Right Shoulder");
        moveJoint(servoRangeTool, CSotaMotion.SV_L_ELBOW, "Left Elbow");
        moveJoint(servoRangeTool, CSotaMotion.SV_L_SHOULDER, "Left Shoulder");
        moveJoint(servoRangeTool, CSotaMotion.SV_HEAD_Y, "Head Yaw");
        moveJoint(servoRangeTool, CSotaMotion.SV_HEAD_R, "Head Roll");
        moveJoint(servoRangeTool, CSotaMotion.SV_HEAD_P, "Head Pitch");
        moveJoint(servoRangeTool, CSotaMotion.SV_BODY_Y, "Body");

        // Turn off motors
        CRobotUtil.Log(TAG, "Turning off motors");
        _sotaMotion.ServoOff();
    }

    private void moveJoint(ServoRangeTool servoRangeTool, Byte servoID, String jointName) {
        CRobotUtil.Log(TAG, "Moving" + jointName);

        // Get all poses: min, max, mid
        CRobotPose midPose = servoRangeTool.getMidPose();
 
        CRobotPose minPose = new CRobotPose();
        minPose.SetPose(_sotaMotion.getDefaultIDs(), midPose.getServoAngles(_sotaMotion.getDefaultIDs()));
        Short minValue = servoRangeTool.getMinPose().getServoAngle(servoID); // Set just this joint to min value
        minPose.SetPose(new Byte[]{servoID}, new Short[]{minValue});

        CRobotPose maxPose = new CRobotPose();
        maxPose.SetPose(_sotaMotion.getDefaultIDs(), midPose.getServoAngles(_sotaMotion.getDefaultIDs()));
        Short maxValue = servoRangeTool.getMaxPose().getServoAngle(servoID); // Set just this joint to max value
        maxPose.SetPose(new Byte[]{servoID}, new Short[]{maxValue});

        _sotaMotion.play(minPose, 1000);
        _sotaMotion.waitEndinterpAll(); // also async public boolean isEndInterpAll()
        CRobotUtil.wait(1000); //pause the program / current thread

        _sotaMotion.play(maxPose, 1000);
        _sotaMotion.waitEndinterpAll(); // also async public boolean isEndInterpAll()
        CRobotUtil.wait(500); //pause the program / current thread

        _sotaMotion.play(midPose, 1000);
        _sotaMotion.waitEndinterpAll(); // also async public boolean isEndInterpAll()
        CRobotUtil.wait(1000); //pause the program / current thread
    }
    
    public static void main(String args[]) {
        AS3_2 sota = new AS3_2();
        if (!sota.connect())
            return;
        CRobotUtil.Log(TAG, "Startup Successful");
        sota.run();

        CRobotUtil.Log(TAG, "Program End Reached");
    }
}
