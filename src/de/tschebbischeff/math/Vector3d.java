package de.tschebbischeff.math;

/**
 * Vector implementation with double precision.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class Vector3d {

    private double[] data;

    public Vector3d(double x, double y, double z) {
        this(new double[]{x, y, z});
    }

    public Vector3d(double[] v) {
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

    public void setData(double x, double y, double z) {
        this.setData(new double[]{x, y, z});
    }

    public static Vector3d xBase() {
        return new Vector3d(1.0d, 0.0d, 0.0d);
    }

    public static Vector3d yBase() {
        return new Vector3d(0.0d, 1.0d, 0.0d);
    }

    public static Vector3d zBase() {
        return new Vector3d(0.0d, 0.0d, 1.0d);
    }

    public void setData(double[] v) {
        this.data = v;
    }

    public double scalarProduct(Vector3d b) {
        return this.getX() * b.getX() + this.getY() * b.getY() + this.getZ() * b.getZ();
    }

    public Vector3d crossProduct(Vector3d b) {
        return new Vector3d(
                this.getY() * b.getZ() - this.getZ() * b.getY(),
                this.getZ() * b.getX() - this.getX() * b.getZ(),
                this.getX() * b.getY() - this.getY() * b.getX()
        );
    }

    public double length2() {
        return this.scalarProduct(this);
    }

    public double length() {
        return Math.sqrt(this.length2());
    }

    public Vector3d normalize() {
        double length = this.length();
        return new Vector3d(this.getX() / length, this.getY() / length, this.getZ() / length);
    }

    public Vector3d add(Vector3d b) {
        return new Vector3d(this.getX() + b.getX(), this.getY() + b.getY(), this.getZ() + b.getZ());
    }

    public Vector3d subtract(Vector3d b) {
        return new Vector3d(this.getX() - b.getX(), this.getY() - b.getY(), this.getZ() - b.getZ());
    }

    public Vector3d multiply(double b) {
        return new Vector3d(this.getX() * b, this.getY() * b, this.getZ() * b);
    }

    public String toString(int precision) {
        return String.format(
                "[ %1$." + precision + "f   %2$." + precision + "f   %3$." + precision + "f ]",
                this.data[0], this.data[1], this.data[2]);
    }

    @Override
    public String toString() {
        return this.toString(3);
    }
}
