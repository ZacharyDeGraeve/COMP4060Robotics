package AS3;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class AS3_3 {
	static final String TAG = "AS3_3";
	static final int HZ = 10;

	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);
		
		CRobotMem mem = new CRobotMem();
		CSotaMotion motion = new CSotaMotion(mem);
		
		if(mem.Connect()){
			motion.InitRobot_Sota();
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());
			
            // Load the servo ranges from servo_ranges.dat
            ServoRangeTool servoRangeTool = ServoRangeTool.Load();
            if (servoRangeTool == null) {
                CRobotUtil.Log(TAG, "Error: Could not load file");
                return;
            }

			// clear screen and move cursor to top left
			System.out.print("\033[H\033[2J"); System.out.flush();

			while (!motion.isButton_Power()) {  // stop when power button pressed
				System.out.print("\033[H"); // move cursor to top left before redrawing

				CRobotPose pose = motion.getReadPose();

                // Print the motor positions
                CRobotUtil.Log(TAG, "Loaded servo positions:");
				servoRangeTool.printMotorRanges(servoRangeTool.calcAngles(pose).toArray());

				System.out.flush();  // force stdout flush before waiting to avoid tearing / flicker.
				CRobotUtil.wait(1000 / HZ);
			}
			
			CRobotUtil.Log(TAG, "Servo Off");
			motion.ServoOff();
		}

	}
}
