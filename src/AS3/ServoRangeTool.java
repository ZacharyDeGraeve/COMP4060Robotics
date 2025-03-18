package AS3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeMap;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CSotaMotion;

public class ServoRangeTool implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final TreeMap<Byte, Double[]> _motorRanges_rad = new TreeMap<>();

    static {
        _motorRanges_rad.put(CSotaMotion.SV_BODY_Y, new Double[]{-1.077363736, 1.077363736});
        _motorRanges_rad.put(CSotaMotion.SV_L_SHOULDER, new Double[]{-2.617993878, 1.745329252});
        _motorRanges_rad.put(CSotaMotion.SV_L_ELBOW, new Double[]{-1.745329252, 1.221730476});
        _motorRanges_rad.put(CSotaMotion.SV_R_SHOULDER, new Double[]{-1.745329252, 2.617993878});
        _motorRanges_rad.put(CSotaMotion.SV_R_ELBOW, new Double[]{-1.221730476, 1.745329252});
        _motorRanges_rad.put(CSotaMotion.SV_HEAD_Y, new Double[]{-1.495996502, 1.495996502});
        _motorRanges_rad.put(CSotaMotion.SV_HEAD_P, new Double[]{-2.617993878, 2.617993878});
        _motorRanges_rad.put(CSotaMotion.SV_HEAD_R, new Double[]{-1.495996502, 1.495996502});
    }
    
    private Short[] _minpos = null;  // internal arrays for precalcualted values
    private Short[] _maxpos = null;
    private Short[] _midpos = null;

    private Byte[] _servoIDs = null;
    private TreeMap<Byte, Byte> _IDtoIndex = new TreeMap<>();

    final static String FILENAME = "servo_ranges.dat";

    ServoRangeTool(Byte[] servoIDs) {
        _servoIDs = servoIDs;
        _minpos = new Short[servoIDs.length];
        _maxpos = new Short[servoIDs.length];
        _midpos = new Short[servoIDs.length];

        // Initialize IDtoIndex map and array
        for (byte i = 0; i < servoIDs.length; i++) {
            _IDtoIndex.put(servoIDs[i], i);
            _minpos[i] = Short.MAX_VALUE;
            _maxpos[i] = Short.MIN_VALUE;
            _midpos[i] = 0;
        }
    }
        
    public void register(CRobotPose pose) {
        register(pose.getServoAngles(_servoIDs));
     }
     public void register(Short[] pos) {
        if (pos == null || pos.length != _servoIDs.length) {
            return;
        }
         
        // Update min and max for each servo
        for (int i = 0; i < pos.length; i++) {
            if (pos[i] != null) {
                if (pos[i] < _minpos[i]) {
                    _minpos[i] = pos[i];
                }
                if (pos[i] > _maxpos[i]) {
                    _maxpos[i] = pos[i];
                }
                _midpos[i] = (short)((_minpos[i] + _maxpos[i]) / 2);
            }
        }
     }

    
    ///==================== Export as CRobotPose objects
    ///====================
    private CRobotPose makePose(Short[] pos) {  // convert short[] to CRobotPose object
        CRobotPose pose = new CRobotPose();
        pose.SetPose(_servoIDs, pos);
        return pose;
    }

    public CRobotPose getMinPose() { return makePose(_minpos);}
    public CRobotPose getMaxPose() { return makePose(_maxpos);}
    public CRobotPose getMidPose() { return makePose(_midpos);}

    ///==================== Angle <-> motor pos conversions
    ///====================
    public RealVector calcAngles(CRobotPose pose) { // convert pose in motor positions to radians
        Short[] pos = pose.getServoAngles(_servoIDs);
        if (pos == null) {
            return null;
        }
        double[] angles = new double[_servoIDs.length];
        
        for (int i = 0; i < _servoIDs.length; i++) {
            if (pos[i] != null) {
                angles[i] = posToRad(_servoIDs[i], pos[i]);
            }
        }
        
        return new ArrayRealVector(angles);
    }

    public CRobotPose calcMotorValues(RealVector angles) { // convert pose in angles to motor positions
        if (angles == null || angles.getDimension() != _servoIDs.length) {
            return null;
        }

        Short[] pos = new Short[_servoIDs.length];

        for (int i = 0; i < _servoIDs.length; i++) {
            pos[i] = radToPos(_servoIDs[i], angles.getEntry(i));
        }
        return makePose(pos);
    }

    private double posToRad(Byte servoID, Short pos) { // convert motor position to angle, in radians 
        Byte index = _IDtoIndex.get(servoID);
        double minRad = _motorRanges_rad.get(servoID)[0];
        double maxRad = _motorRanges_rad.get(servoID)[1];
        double minPos = _minpos[index];
        double maxPos = _maxpos[index];
        return ((pos - minPos)/(maxPos - minPos))*(maxRad - minRad) + minRad;
    }

    private short radToPos(Byte servoID, double angle) { // convert angles, in radians, to motor position
        Byte index = _IDtoIndex.get(servoID);
        double minRad = _motorRanges_rad.get(servoID)[0];
        double maxRad = _motorRanges_rad.get(servoID)[1];
        double minPos = _minpos[index];
        double maxPos = _maxpos[index];

        return (short) (((angle - minRad)/(maxRad - minRad))*(maxPos - minPos) + minPos);
    }
    
	///==================== Pretty Print
    /// ///====================
	private String formattedLine(String title, Byte servoID, Short[] minpos, Short[] maxpos, Short[] middle, double[] pos) {
		// int i = 0;
        int i = _IDtoIndex.get(servoID);
        if (pos == null) {
            String format = "%14s %8d %8d %8d";
		    return String.format(format, title, minpos[i], middle[i], maxpos[i]);
        }
        else {
		    String format = "%14s %8d %8d %8d    %.2f rad";
		    return String.format(format, title, minpos[i], middle[i], maxpos[i], pos[i]);
        }
	}

    public void printMotorRanges() {printMotorRanges(null);}
	public void printMotorRanges(double[] pos) {  // will print the current position as given by the pos array
		System.out.println("-------------");
		System.out.println( formattedLine("Body Y: ", CSotaMotion.SV_BODY_Y, _minpos, _maxpos, _midpos, pos));
		System.out.println( formattedLine("L Shoulder: ", CSotaMotion.SV_L_SHOULDER, _minpos, _maxpos, _midpos, pos));
        System.out.println( formattedLine("L Elbow: ", CSotaMotion.SV_L_ELBOW, _minpos, _maxpos, _midpos, pos));
		System.out.println( formattedLine("R Shoulder: ", CSotaMotion.SV_R_SHOULDER, _minpos, _maxpos, _midpos, pos));
		System.out.println( formattedLine("R Elbow: ", CSotaMotion.SV_R_ELBOW, _minpos, _maxpos, _midpos, pos));
        System.out.println( formattedLine("Head Y: ", CSotaMotion.SV_HEAD_Y, _minpos, _maxpos, _midpos, pos));
		System.out.println( formattedLine("Head P: ", CSotaMotion.SV_HEAD_P, _minpos, _maxpos, _midpos, pos));
        System.out.println( formattedLine("Head R: ", CSotaMotion.SV_HEAD_R, _minpos, _maxpos, _midpos, pos));
	}

    ///==================== LOAD AND SAVE
    ///====================
    public static ServoRangeTool Load(){ return ServoRangeTool.Load(FILENAME);}
    public static ServoRangeTool Load(String filename){
        try(FileInputStream inputFile = new FileInputStream(filename)) {
            ObjectInputStream fileIn = new ObjectInputStream(inputFile);
            return (ServoRangeTool) fileIn.readObject();
        } catch(IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public void save() { save(FILENAME);}
    public void save(String filename) {
        try(FileOutputStream outputFile = new FileOutputStream(filename)) {
            ObjectOutputStream fileOut = new ObjectOutputStream(outputFile);
            fileOut.writeObject(this);
            System.out.println("Servo ranges saved to " + filename);
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
}