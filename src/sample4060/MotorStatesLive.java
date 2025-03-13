package sample4060;
import jp.vstone.RobotLib.*;

///// BIG HINT to help you speed up debugging.
/// -- use the code in this example to learn how to print live results in a clean way to the console
///    this clears the screen and uses flushing to show live updates in a way that is human readable
///    instead of just scrolling. Works in Putty, may not work in all terminals.

public class MotorStatesLive {
	static final String TAG = "MotorStatesLive";
	static final int HZ = 10;

	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);
		
		CRobotMem mem = new CRobotMem();
		CSotaMotion motion = new CSotaMotion(mem);
		
		if(mem.Connect()){
			motion.InitRobot_Sota();
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());
			

			// clear screen and move cursor to top left
			System.out.print("\033[H\033[2J"); System.out.flush();

			while (!motion.isButton_Power()) {  // stop when power button pressed
				System.out.print("\033[H"); // move cursor to top left before redrawing

				CRobotPose pose = motion.getReadPose();

				CRobotUtil.Log(TAG, "-------------");
				CRobotUtil.Log(TAG, "Head R: "+pose.getServoAngle(CSotaMotion.SV_HEAD_R)+"          ");  // add spaces since we are not clearing screen
				CRobotUtil.Log(TAG, "Head P: "+pose.getServoAngle(CSotaMotion.SV_HEAD_P)+"          ");
				CRobotUtil.Log(TAG, "Head Y: "+pose.getServoAngle(CSotaMotion.SV_HEAD_Y)+"          ");
				CRobotUtil.Log(TAG, "Body Y: "+pose.getServoAngle(CSotaMotion.SV_BODY_Y)+"          ");
				CRobotUtil.Log(TAG, "L Elbow: "+pose.getServoAngle(CSotaMotion.SV_L_ELBOW)+"          ");
				CRobotUtil.Log(TAG, "L Shoulder: "+pose.getServoAngle(CSotaMotion.SV_L_SHOULDER)+"          ");
				CRobotUtil.Log(TAG, "R Elbow: "+pose.getServoAngle(CSotaMotion.SV_R_ELBOW)+"          ");
				CRobotUtil.Log(TAG, "R Shoulder: "+pose.getServoAngle(CSotaMotion.SV_R_SHOULDER)+"          ");

				System.out.flush();  // force stdout flush before waiting to avoid tearing / flicker.
				CRobotUtil.wait(1000 / HZ);
			}
			
			CRobotUtil.Log(TAG, "Servo Off");
			motion.ServoOff();
		}

	}
}