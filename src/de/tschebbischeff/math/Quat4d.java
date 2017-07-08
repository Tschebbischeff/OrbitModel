package de.tschebbischeff.math;

/**
 * Quaternion implementation with double precision.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class Quat4d {

    private static final double UNITY_FACTOR = 1.01d;
    private static final double UNITY_BOUND_HI = UNITY_FACTOR * UNITY_FACTOR;
    private static final double UNITY_BOUND_LO = (1.0d / UNITY_FACTOR) * (1.0d / UNITY_FACTOR);
    private double[] data;

    public Quat4d(double roll, double pitch, double yaw) {
        this(Quat4d.identity().roll(roll).pitch(pitch).yaw(yaw).getData());
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

    public double getW() {
        return this.data[0];
    }

    public double getI() {
        return this.data[1];
    }

    public double getJ() {
        return this.data[2];
    }

    public double getK() {
        return this.data[3];
    }

    public double[] getData() {
        return this.data;
    }

    public void setW(double w) {
        this.data[0] = w;
    }

    public void setI(double i) {
        this.data[1] = i;
    }

    public void setJ(double j) {
        this.data[2] = j;
    }

    public void setK(double k) {
        this.data[3] = k;
    }

    public void setData(double w, double i, double j, double k) {
        this.setData(new double[]{w, i, j, k});
    }

    public void setData(double[] v) {
        this.data = v;
    }

    private Quat4d checkUnity() {
        double length = this.length2();
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
        return this.multiply(new Quat4d(Math.cos(Math.toRadians(roll) * 0.5d), Math.sin(Math.toRadians(roll) * 0.5d), 0.0d, 0.0d)).checkUnity();
    }

    public Quat4d pitch(double pitch) {
        return this.multiply(new Quat4d(Math.cos(Math.toRadians(pitch) * 0.5d), 0.0d, Math.sin(Math.toRadians(pitch) * 0.5d), 0.0d)).checkUnity();
    }

    public Quat4d yaw(double yaw) {
        System.out.println(this);
        return this.multiply(new Quat4d(Math.cos(Math.toRadians(yaw) * 0.5d), 0.0d, 0.0d, Math.sin(Math.toRadians(yaw) * 0.5d))).checkUnity();
    }

    public Quat4d add(Quat4d b) {
        return new Quat4d(this.getW() + b.getW(), this.getI() + b.getI(), this.getJ() + b.getJ(), this.getK() + b.getK());
    }

    public double scalarProduct(Quat4d b) {
        return this.getW() * b.getW() + this.getI() * b.getI() + this.getJ() * b.getJ() + this.getK() * b.getK();
    }

    public Quat4d crossProduct(Quat4d b) {
        return new Quat4d(
                0.0d,
                this.getImaginaryPart().crossProduct(b.getImaginaryPart())
        );
    }

    public Quat4d multiply(Quat4d r) {
        double x0 = this.getRealPart();
        double y0 = r.getRealPart();
        Vector3d x = this.getImaginaryPart();
        Vector3d y = r.getImaginaryPart();
        return new Quat4d(
                x0 * y0 - x.scalarProduct(y),
                y.multiply(x0).add(x.multiply(y0)).add(x.crossProduct(y))
        );
    }

    public Vector3d rotateVector(Vector3d v) {
        return this.toRotationMatrix().multiply(v);
    }

    public Quat4d conjugate() {
        return new Quat4d(this.getW(), -this.getI(), -this.getJ(), -this.getK());
    }

    public double length2() {
        return this.scalarProduct(this);
    }

    public double length() {
        return Math.sqrt(this.length2());
    }

    public Quat4d normalize() {
        double length = this.length();
        return new Quat4d(this.getW() / length, this.getI() / length, this.getJ() / length, this.getK() / length);
    }

    public static Quat4d zero() {
        return new Quat4d(0.0d, 0.0d, 0.0d, 0.0d);
    }

    public static Quat4d identity() {
        return new Quat4d(1.0d, 0.0d, 0.0d, 0.0d);
    }

    public Matrix3d toRotationMatrix() {
        double s2 = 2.0d / (this.length2() * this.length2());
        return new Matrix3d(
                1.0d - s2 * (this.getJ() * this.getJ() + this.getK() * this.getK()),
                s2 * (this.getI() * this.getJ() - this.getW() * this.getK()),
                s2 * (this.getI() * this.getK() + this.getW() * this.getJ()),

                s2 * (this.getI() * this.getJ() + this.getW() * this.getK()),
                1.0d - s2 * (this.getI() * this.getI() + this.getK() * this.getK()),
                s2 * (this.getJ() * this.getK() - this.getW() * this.getI()),

                s2 * (this.getI() * this.getK() - this.getW() * this.getJ()),
                s2 * (this.getJ() * this.getK() + this.getW() * this.getI()),
                1.0d - s2 * (this.getI() * this.getI() + this.getJ() * this.getJ())
        );
    }

    public String toString(int postComma) {
        return "[" + String.valueOf(Math.round(this.data[0] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + ", " + String.valueOf(Math.round(this.data[1] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + "i, " + String.valueOf(Math.round(this.data[2] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + "j, " + String.valueOf(Math.round(this.data[3] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + "k]";
    }

    @Override
    public String toString() {
        return "[" + String.valueOf(this.data[0]) + ", " + String.valueOf(this.data[1]) + "i, " + String.valueOf(this.data[2]) + "j, " + String.valueOf(this.data[3]) + "k]";
    }
}
