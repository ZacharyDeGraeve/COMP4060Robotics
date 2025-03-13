package sample4060;

import java.util.Scanner;
import jp.vstone.RobotLib.*;

public class MotorStates {
	static final String TAG = "MotorStates";
	
	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);
		
		CRobotMem mem = new CRobotMem();
		CSotaMotion motion = new CSotaMotion(mem);
		
		if(mem.Connect()){
			motion.InitRobot_Sota();
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());
			
			CRobotUtil.Log(TAG, "[n] for next");
			CRobotUtil.Log(TAG, "[e] exit");

			boolean running = true;
			
			Scanner scan = new Scanner(System.in);
			while(running){
				String timestr = scan.next();
				if(timestr.equals("e")){
					running = false;
				}

				CRobotPose pose = motion.getReadPose();
				if(pose == null){
					scan.close();
					return;
				}
			
				CRobotUtil.Log(TAG, "-------------");
				CRobotUtil.Log(TAG, "Head R: "+pose.getServoAngle(CSotaMotion.SV_HEAD_R));
				CRobotUtil.Log(TAG, "Head P: "+pose.getServoAngle(CSotaMotion.SV_HEAD_P));
				CRobotUtil.Log(TAG, "Head Y: "+pose.getServoAngle(CSotaMotion.SV_HEAD_Y));
				CRobotUtil.Log(TAG, "Body Y: "+pose.getServoAngle(CSotaMotion.SV_BODY_Y));
				CRobotUtil.Log(TAG, "L Elbow: "+pose.getServoAngle(CSotaMotion.SV_L_ELBOW));
				CRobotUtil.Log(TAG, "L Shoulder: "+pose.getServoAngle(CSotaMotion.SV_L_SHOULDER));
				CRobotUtil.Log(TAG, "R Elbow: "+pose.getServoAngle(CSotaMotion.SV_R_ELBOW));
				CRobotUtil.Log(TAG, "R Shoulder: "+pose.getServoAngle(CSotaMotion.SV_R_SHOULDER));
			}
			scan.close();
			CRobotUtil.Log(TAG, "Servo Off");
			motion.ServoOff();
		}

	}
}