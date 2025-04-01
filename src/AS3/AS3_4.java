package AS3;

import AS3.Frames.FrameKeys;
import jp.vstone.RobotLib.*;

public class AS3_4 {
	static final String TAG = "AS3_4";   // set this to support the Sota logging system

	// private variables
	CRobotPose _sotaPose = new CRobotPose();
	CRobotMem _sotaMem = new CRobotMem();
	CSotaMotion _sotaMotion = new CSotaMotion(_sotaMem);

	AS3_4() {
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

		ServoRangeTool ranges = ServoRangeTool.Load();
		CRobotUtil.Log(TAG, "Servo Ranges Loaded");
		ranges.printMotorRanges(_sotaMotion.getReadpos());

		CRobotUtil.Log(TAG, "Servos Off"); // initialize in off state
		_sotaMotion.ServoOff();
			
		// clear screen and move to origin.
		System.out.print("\033[H\033[2J"); System.out.flush();

		while (!_sotaMotion.isButton_Power()) {
			System.out.print("\033[H");  // move to origin

			SotaForwardK FK = new SotaForwardK(ranges.calcAngles(_sotaMotion.getReadPose()));
			ranges.printMotorRanges(_sotaMotion.getReadpos());

			MatrixHelp.printFrame("head", FK.frames.get(FrameKeys.HEAD));
			MatrixHelp.printFrame("lhand", FK.frames.get(FrameKeys.L_HAND));
			MatrixHelp.printFrame("rhand", FK.frames.get(FrameKeys.R_HAND));

			double dist = MatrixHelp.getTrans(FK.frames.get(FrameKeys.L_HAND))
					      .getDistance(MatrixHelp.getTrans(FK.frames.get(FrameKeys.R_HAND)));
			System.out.println("Distance between hands: "+dist*100);
			
			System.out.flush();
			CRobotUtil.wait(100);
		}
	}

	public static void main(String args[]){
		AS3_4 sota = new AS3_4();
		if (!sota.connect())
			return;
		CRobotUtil.Log(TAG, "Startup Successful");
		sota.run();
			
		CRobotUtil.Log(TAG, "Program End Reached");
	}
}

