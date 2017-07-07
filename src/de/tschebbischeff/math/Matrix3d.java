package de.tschebbischeff.math;

/**
 * Column-first Matrix implementation with double precision.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class Matrix3d {

    private double[][] data;

    public Matrix3d(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
        this(new double[][]{{x1, x2, x3}, {y1, y2, y3}, {z1, z2, z3}});
    }

    public Matrix3d(Vector3d x, Vector3d y, Vector3d z) {
        this(new double[][]{{x.getX(), x.getY(), x.getZ()}, {y.getX(), y.getY(), y.getZ()}, {z.getX(), z.getY(), z.getZ()}});
    }

    public Matrix3d(double[][] m) {
        this.data = m;
    }

    public double[][] getData() {
        return this.data;
    }

    public Vector3d getC1() {
        return this.getColumnByIndex(0);
    }

    public Vector3d getC2() {
        return this.getColumnByIndex(1);
    }

    public Vector3d getC3() {
        return this.getColumnByIndex(2);
    }

    public Vector3d getColumnByIndex(int c) {
        if (c >= 3) return null;
        return new Vector3d(this.data[c][0], this.data[c][1], this.data[c][2]);
    }

    public Vector3d getR1() {
        return this.getRowByIndex(0);
    }

    public Vector3d getR2() {
        return this.getRowByIndex(1);
    }

    public Vector3d getR3() {
        return this.getRowByIndex(2);
    }

    public Vector3d getRowByIndex(int r) {
        if (r >= 3) return null;
        return new Vector3d(this.data[0][r], this.data[1][r], this.data[2][r]);
    }

    public static Matrix3d identity() {
        return new Matrix3d(new double[][]{{1.0d, 0.0d, 0.0d}, {0.0d, 1.0d, 0.0d}, {0.0d, 0.0d, 1.0d}});
    }

    public Matrix3d transpose() {
        return new Matrix3d(this.getR1(), this.getR2(), this.getR3());
    }

    public Matrix3d multiply(Matrix3d b) {
        double[][] result = new double[3][3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                result[c][r] = this.getRowByIndex(r).scalarProduct(b.getColumnByIndex(c));
            }
        }
        return new Matrix3d(result);
    }

    public Vector3d multiply(Vector3d b) {
        double[] result = new double[3];
        for (int r = 0; r < 3; r++) {
            result[r] = this.getRowByIndex(r).scalarProduct(b);
        }
        return new Vector3d(result);
    }

    public String toString(int postComma) {
        return "[" + String.valueOf(Math.round(this.data[0][0] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + ", " + String.valueOf(Math.round(this.data[1][0] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + ", " + String.valueOf(Math.round(this.data[2][0] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + "\n" +
                " " + String.valueOf(Math.round(this.data[0][1] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + ", " + String.valueOf(Math.round(this.data[1][1] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + ", " + String.valueOf(Math.round(this.data[2][1] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + "\n" +
                " " + String.valueOf(Math.round(this.data[0][2] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + ", " + String.valueOf(Math.round(this.data[1][2] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + ", " + String.valueOf(Math.round(this.data[2][2] * Math.pow(10, postComma)) / Math.pow(10, postComma)) + "]";
    }

    @Override
    public String toString() {
        return "[" + String.valueOf(this.data[0][0]) + ", " + String.valueOf(this.data[1][0]) + ", " + String.valueOf(this.data[2][0]) + "\n" +
                " " + String.valueOf(this.data[0][1]) + ", " + String.valueOf(this.data[1][1]) + ", " + String.valueOf(this.data[2][1]) + "\n" +
                " " + String.valueOf(this.data[0][2]) + ", " + String.valueOf(this.data[1][2]) + ", " + String.valueOf(this.data[2][2]) + "]";
    }

}
