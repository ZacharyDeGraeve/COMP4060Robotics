package sample4060;
import java.io.IOException;

import jp.vstone.RobotLib.*;
import jp.vstone.camera.CameraCapture;

public class CameraSnapTest {
	static final String TAG = "AS3_5";   // set this to support the Sota logging system

	// private variables
	CRobotPose _sotaPose = new CRobotPose();
	CRobotMem _sotaMem = new CRobotMem();
	CSotaMotion _sotaMotion = new CSotaMotion(_sotaMem);

	static final String RESOURCES = "../resources/";
	static final String SOUNDS = RESOURCES+"sound/";

	CameraSnapTest() {
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
		CPlayWave.PlayWave_wait(SOUNDS+"start_face_test.wav");
	
		/**
	     * jpegデータを保存 
	     */
		
		 {
		    String deviceName="/dev/video0";
			CameraCapture cap = new CameraCapture(CameraCapture.CAP_IMAGE_SIZE_HD_1080, CameraCapture.CAP_FORMAT_MJPG);
			try {
				cap.openDevice(deviceName);
				cap.snap();
				cap.snap();
				CRobotUtil.wait(1000);
				cap.snap();
				cap.saveImage("v1");
				cap.snap();
				cap.snap();
				CRobotUtil.wait(1000);
				cap.snap();
				cap.saveImage("v2");
				CRobotUtil.wait(1000);
				cap.snap();
				cap.saveImage("v3");
				cap.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]){
		CameraSnapTest sota = new CameraSnapTest();
		if (!sota.connect())
			return;
		CRobotUtil.Log(TAG, "Startup Successful");
		sota.run();
			
		CRobotUtil.Log(TAG, "Program End Reached");
	}
}

