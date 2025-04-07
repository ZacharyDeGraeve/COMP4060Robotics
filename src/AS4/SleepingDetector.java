package AS4;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.camera.CRoboCamera;
import jp.vstone.camera.FaceDetectResult;

public class SleepingDetector {

    static final String TAG = "SleepingDetector";
    static final int BLINK_THRESHOLD = 40;  
    static final long SLEEP_THRESHOLD_MS = 5000;
    
    private CRoboCamera cam;
    private long eyesClosedStartTime = 0;
    private boolean eyesPreviouslyClosed = false;
    
    public SleepingDetector() {
        CRobotUtil.Log(TAG, "Initializing SleepingDetector");
        CRobotMem mem = new CRobotMem();
        CSotaMotion motion = new CSotaMotion(mem);
        
        if(mem.Connect()) {
            motion.InitRobot_Sota();
            CRobotUtil.Log(TAG, "Connected to robot, firmware: " + mem.FirmwareRev.get());
            
            cam = new CRoboCamera("/dev/video0", motion);
            
            cam.setEnableBlinkDetect(true);
            
            cam.StartFaceDetect();
        } else {
            CRobotUtil.Log(TAG, "Failed to connect to robot");
        }
    }

    public boolean isPersonSleeping() {
        if (cam == null) {
            CRobotUtil.Log(TAG, "Camera not initialized");
            return false;
        }
        
        FaceDetectResult result = cam.getDetectResult();
        
        if (result.isDetect() && result.isBlinkDetect()) {
            // Get blink values for both eyes
            int leftEyeValue = result.getBlinkLeft();
            int rightEyeValue = result.getBlinkRight();
            
            CRobotUtil.Log(TAG, "Left eye: " + leftEyeValue + ", Right eye: " + rightEyeValue);
            
            boolean eyesClosed = (leftEyeValue < BLINK_THRESHOLD && rightEyeValue < BLINK_THRESHOLD);
            
            if (eyesClosed) {
                // If eyes just closed, record the time
                if (!eyesPreviouslyClosed) {
                    eyesClosedStartTime = System.currentTimeMillis();
                    eyesPreviouslyClosed = true;
                    CRobotUtil.Log(TAG, "Eyes closed, starting timer");
                }
                
                // Check if eyes have been closed for longer than threshold
                long eyesClosedDuration = System.currentTimeMillis() - eyesClosedStartTime;
                CRobotUtil.Log(TAG, "Eyes closed for " + eyesClosedDuration + "ms");
                
                if (eyesClosedDuration >= SLEEP_THRESHOLD_MS) {
                    CRobotUtil.Log(TAG, "Person is sleeping! Eyes closed for " + eyesClosedDuration + "ms");
                    return true;
                }
            } else {
                // Reset if eyes are open
                if (eyesPreviouslyClosed) {
                    CRobotUtil.Log(TAG, "Eyes opened, resetting timer");
                    eyesPreviouslyClosed = false;
                }
            }
        } else {
            if (eyesPreviouslyClosed) {
                CRobotUtil.Log(TAG, "Lost face detection, resetting timer");
                eyesPreviouslyClosed = false;
            }
        }
        
        return false;
    }
    
    public void cleanup() {
        if (cam != null) {
            cam.StopFaceDetect();
        }
    }
}