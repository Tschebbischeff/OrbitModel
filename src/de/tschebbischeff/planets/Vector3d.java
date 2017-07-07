package de.tschebbischeff.planets;

/**
 * TODO: MISSING JAVADOC
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

    public void setData(double[] v) {
        this.data = v;
    }

    public double scalarProduct(Vector3d b) {
        return this.getX() * b.getX() + this.getY() * b.getY() + this.getZ() * b.getZ();
    }

    public double length() {
        return Math.sqrt(this.scalarProduct(this));
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

    public String toString(int postComma) {
        return "[" + String.valueOf(Math.round(this.data[0] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + ", " + String.valueOf(Math.round(this.data[1] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + ", " + String.valueOf(Math.round(this.data[2] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + "]";
    }

    @Override
    public String toString() {
        return "[" + String.valueOf(this.data[0]) + ", " + String.valueOf(this.data[1]) + ", " + String.valueOf(this.data[2]) + "]";
    }
}
