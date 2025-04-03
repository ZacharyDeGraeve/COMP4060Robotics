package sample4060;

import java.awt.Point;
import java.time.Duration;
import java.time.Instant;

import jp.vstone.RobotLib.*;
import jp.vstone.camera.CRoboCamera;
import jp.vstone.camera.FaceDetectResult;

//    Check the JavaDocs (https://sota.vstone.co.jp/sota/javadoc/) for CRoboCamera although the documentation is terrible.
public class FaceDetection {
	static final String TAG = "sample";

	static final String RESOURCES = "../resources/";
	static final String SOUNDS = RESOURCES+"sound/";

	static final int TRACKING_TIME_s = 10;
	static final int PROCESSING_TIME_s = 30;
	static final int MOTION_DETECT_TIME_s = PROCESSING_TIME_s;

	public static void FaceTrackingDemo(CSotaMotion motion, CRoboCamera cam) {
		// cam.setEnableFaceSearch(false);  // can disable following a face... possibly equivalent to StartFaceDetect
		motion.ServoOn();  // only if face "search" is true
					
		// start the face tracking and diagnostics module. Takes a lot of CPU. 
		// it automatically grabs frames live and processes them. You don't need to send it new frames.
		cam.StartFaceTraking();  // note the English typo
		
		CRobotUtil.Log(TAG, "----- Starting "+TRACKING_TIME_s+"s face find and track");

		int detectcnt = 0;
		Instant startTime = Instant.now();
		while (Duration.between(startTime, Instant.now()).getSeconds() < TRACKING_TIME_s) {
			FaceDetectResult result = cam.getDetectResult();
			if(result.isDetect())
				detectcnt++;
		}
		System.out.println("Detected face in "+detectcnt+" frames.");
		cam.StopFaceTraking();  // turn it off when you are not using it.
		motion.ServoOff(); // only needed if you turned them on to track.
		CRobotUtil.wait(250);
	}

	public static void FaceProcessingDemo(CSotaMotion motion, CRoboCamera cam) {
		CRobotUtil.Log(TAG, "----- Starting "+PROCESSING_TIME_s+"s face info extraction withuot tracking");

		cam.StartFaceDetect();
		Instant startTime = Instant.now();
		while (Duration.between(startTime, Instant.now()).getSeconds() < PROCESSING_TIME_s) {
			FaceDetectResult result = cam.getDetectResult();   // check FaceDetectResult JavaDoc
			if(result.isDetect()) {
				System.out.println("------------");
				System.out.println("Found "+result.getFaceNum()+" faces, working on face 0");
				
				Point center = result.getCenterPoint(0);
				int height = result.getHeight(0); 
				int width = result.getWidth(0);
				System.out.println("Face at "+center.toString()+" size "+width+"x"+height);

				Point gaze = result.getGazeVectoer();
				Point face = result.getFaceVectoer();
				System.out.println("Face vector: "+face.toString()+",  gaze vector: "+gaze.toString());
				System.out.println("YPR: "+result.getAngleYaw()+" ("+result.getAngleYawMoveScore() +")  "+
											result.getAnglePitch()+" ("+result.getAnglePitchMoveScore() +")  "+
											result.getAngleRoll()+" ("+result.getAngleRollMoveScore() +")");
				System.out.println("Smiling: "+result.getSmile());
				// System.out.println(" Blink L: "+result.getBlinkLeft()+"  R: "+result.getBlinkRight());   Appears to be not implemented.

				// Biometrics -- warning, may be offensive, certainly not good. Old fashioned, and you shouldn't use them.
				// System.out.println("Age: "+result.getAge()+" Female: "+result.isFemale()+" Male: "+result.isMale());
			}
		}
		cam.StopFaceDetect();
	}
	
	public static void MotionDetectDemo(CSotaMotion motion, CRoboCamera cam) {
		final int Hz = 10;
		cam.StartMotionDetection();
		Instant startTime = Instant.now();
		while (Duration.between(startTime, Instant.now()).getSeconds() < MOTION_DETECT_TIME_s) {
			System.out.println("Motion detection result:"+ cam.getMotionDetectResult());
			CRobotUtil.wait(1000 / Hz);
		}
		cam.StopMotionDetection();
	}

	public static void main(String args[]){
		CRobotUtil.Log(TAG, "Start " + TAG);

		CRobotMem mem = new CRobotMem(); // connector for the Sota's information system (VSMD), connects via internal socket.
		CSotaMotion motion = new CSotaMotion(mem);   // motion control class. Pass it an instantiated CRobotMem
		
		if(mem.Connect()){ // connect to the robot's subsystem
			CRobotUtil.Log(TAG, "connect " + TAG);
			motion.InitRobot_Sota();  // initialize the Sota VSMD			
			CRobotUtil.Log(TAG, "Rev. " + mem.FirmwareRev.get());

			CRobotUtil.Log(TAG, "Camera Test");
			CRoboCamera cam = new CRoboCamera("/dev/video0", motion);  // initialize the camera object, pointing at the device and linking to the Sota subsystem.
			
			FaceTrackingDemo(motion, cam);
			// FaceProcessingDemo(motion, cam);
			// MotionDetectDemo(motion, cam);			
		}
		
	}
}
