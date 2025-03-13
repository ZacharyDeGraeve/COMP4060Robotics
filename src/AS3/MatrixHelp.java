package AS3;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

public class MatrixHelp {  // creates homogeneous rotation matrices
    
    public static RealMatrix rotX(double theta) { // returns a homogenous rotation matrix around the X axis
        return null; // TODO
    }

    public static RealMatrix rotY(double theta) { // returns a homogenous rotation matrix around the Y axis
        return null; // TODO
    }

    public static RealMatrix rotZ(double theta) { // returns a homogenous rotation matrix around the Z axis
        return null; // TODO
    }

    // a 4 translation vector. assume w/normalized.
    public static RealMatrix trans(RealVector t) {return trans(t.getEntry(0), t.getEntry(1), t.getEntry(2));  }
    public static RealMatrix trans(double tx, double ty, double tz) { // returns a homogenous translation matrix
        return null; // TODO
    }

    public static RealMatrix T(RealMatrix R, double tx, double ty, double tz) { // constructs a Rt matrix
        return null; // TODO
    }


    //============ These functions are provided because they are annoying to implement

    public static RealVector normalizeH(RealVector v) {   // returns a copy, as a length 1 homogeneous vector
        RealVector newv = v.getSubVector(0, 3);  // strip the w
        newv.unitize(); // normalize
        return newv.append(1); // add a w of 1.
    }
  
    // Construct a rotation matrix that rotates around the given arbitrary vector
    // v is a 4x1 homogeneous vector
    public static RealMatrix rotRodrigues(double vx, double vy, double vz, double theta) {return rotRodrigues(MatrixUtils.createRealVector( new double[]{vx, vy, vz, 1}), theta);}

    public static RealMatrix rotRodrigues(RealVector v, double theta) {    
        v = MatrixHelp.normalizeH(v);
        
        // Create the skew-symmetric matrix [v]_x
        double[][] skew = {
            {0,             -v.getEntry(2),   v.getEntry(1)},
            {v.getEntry(2),   0,              -v.getEntry(0)},
            {-v.getEntry(1),  v.getEntry(0),    0}
        };
        RealMatrix v_cross = MatrixUtils.createRealMatrix(skew);
        RealMatrix v_cross_squared = v_cross.multiply(v_cross);  // [v]_x^2
                
        // precalculate values to simplify syntax below
        double sinTheta = Math.sin(theta); double cosTheta = Math.cos(theta);
        RealMatrix I = MatrixUtils.createRealIdentityMatrix(3);
        
        // Rodrigues' formula: R = I + sin(theta) * [v]_x + (1 - cos(theta)) * [v]_x^2
        RealMatrix R = I.add(v_cross.scalarMultiply(sinTheta)).add(v_cross_squared.scalarMultiply(1 - cosTheta));
        RealMatrix Rh = MatrixUtils.createRealIdentityMatrix(4);
        Rh.setSubMatrix(R.getData(), 0, 0);
        return Rh;
    }

    // extract the YPR from the given rotation matrix. Note the order since R is done first it is the third value.
    public static RealVector getYPRVec(RealMatrix R) {return MatrixUtils.createRealVector(getYPR(R)); }
    public static double[] getYPR(RealMatrix R) {  
        RealMatrix subR = R.getSubMatrix(0, 2, 0, 2);
        return (new Rotation(subR.getData(), 1.0e-10)).getAngles(RotationOrder.ZYX, RotationConvention.VECTOR_OPERATOR);
    }

    public static RealVector getTrans(RealMatrix R) { // extract the translation from a Rt matrix.
        return (R.getColumnVector(3)); // right-most row of homogeneous matrix
    }


    // Inverting a matrix is hard, and these are often not invertable so we use a 
    // pseudoinverse. The SLOW but robust method is a full SVD and use that to solve a
    // least squares solution to find a close solution.
    // A much faster solution is to use the Moore-Penrose Pseudo Inverse. However, it has more 
    // constraints and will not be always stable so this is simpler and safer for now.
    //  for more reading and details, see: https://robotics.caltech.edu/~jwb/courses/ME115/handouts/pseudo.pdf
    public static RealMatrix pseudoInverse(RealMatrix M) {  // calculate pseudoinverse based on SVD
        SingularValueDecomposition svd = new SingularValueDecomposition(M);
        RealMatrix U = svd.getU();
        RealMatrix S = svd.getS();
        RealMatrix V = svd.getV();

        for (int i = 0; i < S.getRowDimension(); i++) {
            double entry = S.getEntry(i, i);
            if ( entry > 1e-10)  // Avoid division by zero for small numbers
                S.setEntry(i, i, 1.0 / entry);
        }
        // pseudoinverse: V * S+ * Ut
        return V.multiply(S).multiply(U.transpose());
    }


    static public void printMatrix(RealMatrix M, int spacing, int precision) { printMatrix(null, M, spacing, precision); }
    static public void printMatrix(String title, RealMatrix M, int spacing, int precision) {
        if (title != null)
            System.out.println("----- "+title+" ("+M.getRowDimension()+","+M.getColumnDimension()+")");
        
        for (int i = 0; i < M.getRowDimension(); i++) {
            for (int j = 0; j < M.getColumnDimension(); j++) {
                System.out.printf("%"+spacing+"."+precision+"f ", M.getEntry(i, j)); // 6 characters wide, 2 decimal places
            }
            System.out.println();  // Move to the next row
        }
    }

    static public void printVector(RealVector v) { printVector(null, v); }
    static public void printVector(String title, RealVector v) { printVector(title, v, 5, 3); }
    static public void printVector(RealVector v, int spacing, int precision) { printVector(null, v, spacing, precision);}
    static public void printVector(String title, RealVector v, int spacing, int precision) {
        if (title != null)
            System.out.print("----- "+title+"  ");
        for (int i = 0; i < v.getDimension(); i++)
                System.out.printf("%"+spacing+"."+precision+"f ", v.getEntry(i)); // 6 characters wide, 2 decimal places
        System.out.println();  
    }

    static public void printFrame(RealMatrix R) { printFrame(null, R);}
    static public void printFrame(String title, RealMatrix R) { printFrame(title, R, 5, 3);}
    static public void printFrame(String title, RealMatrix R, int spacing, int precision) {
        if (title != null)
            System.out.print("----- "+title+"  ");
        RealVector t = getTrans(R);

        System.out.print("t: ");
        for (int i = 0; i < t.getDimension(); i++)
            System.out.printf("%"+spacing+"."+precision+"f ", t.getEntry(i)); // 6 characters wide, 2 decimal places

        double[] ypr = getYPR(R);
        System.out.print("  ypr: ");
        for (int i = 0; i < ypr.length; i++)
            System.out.printf("%"+spacing+"."+precision+"f ", ypr[i]); // 6 characters wide, 2 decimal places

        System.out.println();
    }


    /// SAMPLE code to see how to use the matrix library.
    public static void main(String[] args) {
        // Create a test matrix (it can be any matrix)
        double[][] matrixData = {
            {1, 2},
            {3, 4}
        };
        
        RealMatrix M = MatrixUtils.createRealMatrix(matrixData);

        // Compute the pseudo-inverse
        RealMatrix MPlus = MatrixHelp.pseudoInverse(M);

        // Check property: A * A⁺ * A = A
        RealMatrix leftSide = M.multiply(MPlus).multiply(M);
        System.out.println("A * A⁺ * A = \n" + leftSide);

        // Check property: A⁺ * A * A⁺ = A⁺
        RealMatrix rightSide = MPlus.multiply(M).multiply(MPlus);
        System.out.println("A⁺ * A * A⁺ = \n" + rightSide);

        // Optionally, check how close these are to the original matrix
        System.out.println("Mplus = \n" + MPlus);
        
        // Optionally, check how close these are to the original matrix
        System.out.println("Original Matrix M = \n" + M);

        RealVector v = MatrixUtils.createRealVector(new double[]{1, 2, 3, 1}); // sample to create new vector
    }
}
