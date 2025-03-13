package AS3;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;

public class AS3_1 {
    static final String TAG = "AS3_1";   // set this to support the Sota logging system

    // private variables
	CRobotPose _sotaPose = new CRobotPose();
	CRobotMem _sotaMem = new CRobotMem();
	CSotaMotion _sotaMotion = new CSotaMotion(_sotaMem);

    AS3_1() {
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
        ServoRangeTool _servoRangeTool = new ServoRangeTool(servoIDs);
        CRobotUtil.Log(TAG, "Servo Range Tool created");

        Short[] pos = _sotaMotion.getReadpos();
        _servoRangeTool.register(pos);
        _servoRangeTool.printMotorRanges();

        CRobotUtil.Log(TAG, "Turning off motors. Manually move the robot joints.");
        _sotaMotion.ServoOff();

        while (!_sotaMotion.isButton_Power()) {
            pos = _sotaMotion.getReadpos();
            _servoRangeTool.register(pos);
            CRobotUtil.wait(100);
        }

        _servoRangeTool.save();
        CRobotUtil.Log(TAG, "Motor range saved");
        _servoRangeTool.printMotorRanges();
    }

    public static void main(String args[]) {
        AS3_1 sota = new AS3_1();
        if (!sota.connect())
            return;
        CRobotUtil.Log(TAG, "Startup Successful");
        sota.run();
            
        CRobotUtil.Log(TAG, "Program End Reached");
    }
}