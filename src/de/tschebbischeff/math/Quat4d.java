package de.tschebbischeff.math;

/**
 * Quaternion implementation with double precision.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class Quat4d {

    private double[] data;

    public Quat4d(double x, double y, double z, double w) {
        this(new double[]{x, y, z, w});
    }

    public Quat4d(double[] v) {
        this.data = v;
    }

    public double getX() {
        return this.data[0];
    }

    public double getY() {
        return this.data[1];
    }

    public double getZ() {
        return this.data[2];
    }

    public double getW() {
        return this.data[3];
    }

    public double[] getData() {
        return this.data;
    }

    public void setX(double x) {
        this.data[0] = x;
    }

    public void setY(double y) {
        this.data[1] = y;
    }

    public void setZ(double z) {
        this.data[2] = z;
    }

    public void setW(double w) {
        this.data[3] = w;
    }

    public void setData(double x, double y, double z, double w) {
        this.setData(new double[]{x, y, z, w});
    }

    public void setData(double[] v) {
        this.data = v;
    }

    public Quat4d multiply(Quat4d q) {
        return new Quat4d(
                q.getX() * this.getX() - q.getY() * this.getY() - q.getZ() * this.getZ() - q.getW() * this.getW(),
                q.getX() * this.getY() - q.getY() * this.getX() - q.getZ() * this.getW() - q.getW() * this.getZ(),
                q.getX() * this.getZ() - q.getY() * this.getW() - q.getZ() * this.getX() - q.getW() * this.getY(),
                q.getX() * this.getW() - q.getY() * this.getZ() - q.getZ() * this.getY() - q.getW() * this.getX()
        );
    }

    public Vector3d multiply(Vector3d v) {
        return this.toRotationMatrix().multiply(v);
    }

    public Quat4d roll(double roll) {
        return this.multiply(new Quat4d(Math.sin(Math.toRadians(roll) * 0.5), 0.0d, 0.0d, Math.cos(Math.toRadians(roll) * 0.5d)));
    }

    public Quat4d pitch(double pitch) {
        return this.multiply(new Quat4d(0.0d, Math.sin(Math.toRadians(pitch) * 0.5), 0.0d, Math.cos(Math.toRadians(pitch) * 0.5)));
    }

    public Quat4d yaw(double yaw) {
        return this.multiply(new Quat4d(0.0d, 0.0d, Math.sin(Math.toRadians(yaw) * 0.5), Math.cos(Math.toRadians(yaw) * 0.5)));
    }

    public double norm() {
        return this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
    }

    public Quat4d normalize() {
        double norm = this.norm();
        return new Quat4d(this.getX() / Math.sqrt(norm), this.getY() / Math.sqrt(norm), this.getZ() / Math.sqrt(norm), this.getW() / Math.sqrt(norm));
    }

    public static Quat4d identity() {
        return new Quat4d(0.0d, 0.0d, 0.0d, 1.0d);
    }

    public Matrix3d toRotationMatrix() {
        if (this.norm() != 1.0d && this.norm() != 0.0d) {
            return this.normalize().toRotationMatrix();
        } else {
            return new Matrix3d(
                    1.0d - 2.0d * (this.getZ() * this.getZ() + this.getW() * this.getW()),
                    2.0d * (this.getY() * this.getZ() + this.getX() * this.getW()),
                    2.0d * (this.getY() * this.getW() - this.getX() * this.getZ()),

                    2.0d * (this.getY() * this.getZ() - this.getX() * this.getW()),
                    1.0d - 2.0d * (this.getY() * this.getY() + this.getW() * this.getW()),
                    2.0d * (this.getZ() * this.getW() + this.getX() * this.getY()),

                    2.0d * (this.getY() * this.getW() + this.getX() * this.getZ()),
                    2.0d * (this.getZ() * this.getW() - this.getX() * this.getY()),
                    1.0d - 2.0d * (this.getY() * this.getY() + this.getZ() * this.getZ())
            );
        }
    }

    public String toString(int postComma) {
        return "[" + String.valueOf(Math.round(this.data[0] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + ", " + String.valueOf(Math.round(this.data[1] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + "i, " + String.valueOf(Math.round(this.data[2] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + "j, " + String.valueOf(Math.round(this.data[3] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + "k]";
    }

    @Override
    public String toString() {
        return "[" + String.valueOf(this.data[0]) + ", " + String.valueOf(this.data[1]) + "i, " + String.valueOf(this.data[2]) + "j, " + String.valueOf(this.data[3]) + "k]";
    }
}
