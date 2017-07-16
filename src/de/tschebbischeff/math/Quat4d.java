package de.tschebbischeff.math;

import com.andreaskahler.math.Matrix4f;
import com.andreaskahler.math.Vector4f;

/**
 * Quaternion implementation with double precision.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class Quat4d {

    /**
     * Factor defining, when the quaternion is considered to not be a unit quaternion anymore.
     */
    private static final double UNITY_FACTOR = 1.01d;
    /**
     * The upper bound determining unity of the quaternion, if the length of the quaternion is above this threshold
     * it is not considered a unit quaternion anymore.
     */
    private static final double UNITY_BOUND_HI = UNITY_FACTOR * UNITY_FACTOR;
    /**
     * The lower bound determining unity of the quaternion, if the length of the quaternion is below this threshold
     * it is not considered a unit quaternion anymore.
     */
    private static final double UNITY_BOUND_LO = (1.0d / UNITY_FACTOR) * (1.0d / UNITY_FACTOR);
    /**
     * Stores the quaternion data in a 4 element array
     */
    private double[] data;

    /**
     * Creates a quaternion that rolls pitches and yaws
     *
     * @param roll  The roll represented by the quaternion.
     * @param pitch The pitch represented by the quaternion.
     * @param yaw   The yaw represented by the quaternion.
     */
    public Quat4d(double roll, double pitch, double yaw) {
        this(Quat4d.identity().roll(roll).pitch(pitch).yaw(yaw).getData());
    }

    /**
     * Creates a quaternion representing the rotation from one vector to another.
     *
     * @param x The vector which is rotated by the resulting quaternion to obtain y.
     * @param y The vector that is the result of x's rotation by the resulting quaternion.
     */
    public Quat4d(Vector3d x, Vector3d y) {
        if (x.dot(y) > 0.999999d) {
            this.setData(Quat4d.identity().getData());
        } else if (x.dot(y) < -0.999999d) {
            this.setData(new Quat4d(0.0d, x.anyOrthogonal().normalize()).getData());
        } else {
            Vector3d prod = x.cross(y);
            this.setData(new Quat4d(Math.sqrt(x.len2() * y.len2()) + x.dot(y), prod).normalize().getData());
        }
    }

    public Quat4d(double real, Vector3d imaginary) {
        this(real, imaginary.getX(), imaginary.getY(), imaginary.getZ());
    }

    public Quat4d(double w, double i, double j, double k) {
        this(new double[]{w, i, j, k});
    }

    public Quat4d(double[] v) {
        this.data = v;
    }

    public static Quat4d zero() {
        return new Quat4d(0.0d, 0.0d, 0.0d, 0.0d);
    }

    public static Quat4d identity() {
        return new Quat4d(1.0d, 0.0d, 0.0d, 0.0d);
    }

    public double getW() {
        return this.data[0];
    }

    public Quat4d setW(double w) {
        this.data[0] = w;
        return this;
    }

    public double getI() {
        return this.data[1];
    }

    public Quat4d setI(double i) {
        this.data[1] = i;
        return this;
    }

    public double getJ() {
        return this.data[2];
    }

    public Quat4d setJ(double j) {
        this.data[2] = j;
        return this;
    }

    public double getK() {
        return this.data[3];
    }

    public Quat4d setK(double k) {
        this.data[3] = k;
        return this;
    }

    public double[] getData() {
        return this.data;
    }

    public Quat4d setData(double[] v) {
        this.data = v;
        return this;
    }

    public Quat4d setData(double w, double i, double j, double k) {
        return this.setData(new double[]{w, i, j, k});
    }

    private Quat4d checkUnity() {
        double length = this.len2();
        if (length > UNITY_BOUND_HI || length < UNITY_BOUND_LO) {
            return this.normalize();
        }
        return this;
    }

    public double getRealPart() {
        return this.getW();
    }

    public Vector3d getImaginaryPart() {
        return new Vector3d(this.getI(), this.getJ(), this.getK());
    }

    public Quat4d roll(double roll) {
        return new Quat4d(Math.cos(Math.toRadians(roll) * 0.5d), Math.sin(Math.toRadians(roll) * 0.5d), 0.0d, 0.0d).mult(this).checkUnity();
    }

    public Quat4d pitch(double pitch) {
        return new Quat4d(Math.cos(Math.toRadians(pitch) * 0.5d), 0.0d, Math.sin(Math.toRadians(pitch) * 0.5d), 0.0d).mult(this).checkUnity();
    }

    public Quat4d yaw(double yaw) {
        return new Quat4d(Math.cos(Math.toRadians(yaw) * 0.5d), 0.0d, 0.0d, Math.sin(Math.toRadians(yaw) * 0.5d)).mult(this).checkUnity();
    }

    public Quat4d rotate(Vector3d axis, double angle) {
        double sine = Math.sin(Math.toRadians(angle) * 0.5d);
        return new Quat4d(Math.cos(Math.toRadians(angle) * 0.5d), axis.getX() * sine, axis.getY() * sine, axis.getZ() * sine).mult(this).checkUnity();
    }

    public Quat4d add(Quat4d b) {
        return new Quat4d(this.getW() + b.getW(), this.getI() + b.getI(), this.getJ() + b.getJ(), this.getK() + b.getK());
    }

    public double dot(Quat4d b) {
        return this.getW() * b.getW() + this.getI() * b.getI() + this.getJ() * b.getJ() + this.getK() * b.getK();
    }

    public Quat4d cross(Quat4d b) {
        return new Quat4d(
                0.0d,
                this.getImaginaryPart().cross(b.getImaginaryPart())
        );
    }

    public Quat4d mult(Quat4d r) {
        double x0 = this.getRealPart();
        double y0 = r.getRealPart();
        Vector3d x = this.getImaginaryPart();
        Vector3d y = r.getImaginaryPart();
        return new Quat4d(
                x0 * y0 - x.dot(y),
                y.scale(x0).add(x.scale(y0)).add(x.cross(y))
        );
    }

    public Vector3d rotateVector(Vector3d v) {
        return this.toRotationMatrix().mult(v);
    }

    public Quat4d conjugate() {
        return new Quat4d(this.getW(), -this.getI(), -this.getJ(), -this.getK());
    }

    public double len2() {
        return this.dot(this);
    }

    public double len() {
        return Math.sqrt(this.len2());
    }

    public Quat4d normalize() {
        double length = this.len();
        return new Quat4d(this.getW() / length, this.getI() / length, this.getJ() / length, this.getK() / length);
    }

    public Matrix3d toRotationMatrix() {
        double s2 = 2.0d / (this.len2() * this.len2());
        //RIGHT HANDED
        return new Matrix3d(
                1.0d - s2 * (this.getJ() * this.getJ() + this.getK() * this.getK()),
                s2 * (this.getI() * this.getJ() + this.getW() * this.getK()),
                s2 * (this.getI() * this.getK() - this.getW() * this.getJ()),

                s2 * (this.getI() * this.getJ() - this.getW() * this.getK()),
                1.0d - s2 * (this.getI() * this.getI() + this.getK() * this.getK()),
                s2 * (this.getJ() * this.getK() + this.getW() * this.getI()),

                s2 * (this.getI() * this.getK() + this.getW() * this.getJ()),
                s2 * (this.getJ() * this.getK() - this.getW() * this.getI()),
                1.0d - s2 * (this.getI() * this.getI() + this.getJ() * this.getJ())
        );
        //LEFT HANDED
        /*return new Matrix3d(
                1.0d - s2 * (this.getJ() * this.getJ() + this.getK() * this.getK()),
                s2 * (this.getI() * this.getJ() - this.getW() * this.getK()),
                s2 * (this.getI() * this.getK() + this.getW() * this.getJ()),

                s2 * (this.getI() * this.getJ() + this.getW() * this.getK()),
                1.0d - s2 * (this.getI() * this.getI() + this.getK() * this.getK()),
                s2 * (this.getJ() * this.getK() - this.getW() * this.getI()),

                s2 * (this.getI() * this.getK() - this.getW() * this.getJ()),
                s2 * (this.getJ() * this.getK() + this.getW() * this.getI()),
                1.0d - s2 * (this.getI() * this.getI() + this.getJ() * this.getJ())
        );*/
    }

    public Matrix4f toGlRotationMatrix() {
        double[][] rotation = this.toRotationMatrix().getData();
        return new Matrix4f(
                new Vector4f((float) rotation[0][0], (float) rotation[1][0], (float) rotation[2][0], 0f),
                new Vector4f((float) rotation[0][1], (float) rotation[1][1], (float) rotation[2][1], 0f),
                new Vector4f((float) rotation[0][2], (float) rotation[1][2], (float) rotation[2][2], 0f),
                new Vector4f(0f, 0f, 0f, 1f)
        ).transpose();
    }

    @Override
    public boolean equals(Object r) {
        if (r instanceof Quat4d) {
            Quat4d q = (Quat4d) r;
            return (this.getW() == q.getW() && this.getI() == q.getI() && this.getJ() == q.getJ() && this.getK() == q.getK());
        }
        return false;
    }

    public String toString(int precision) {
        return String.format(
                "[ %1$." + precision + "f   %2$." + precision + "fi   %3$." + precision + "fj   %4$." + precision + "fk ]",
                this.data[0], this.data[1], this.data[2], this.data[3]);
    }

    @Override
    public String toString() {
        return this.toString(3);
    }
}
