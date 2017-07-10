package de.tschebbischeff.math;

import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * Column-first Matrix implementation with double precision.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class Matrix3d {

    /**
     * Stores the matrix data in a 3x3 array
     */
    private double[][] data;

    /**
     * Create a new matrix with the given values. The values are stored column-first, and in this constructor the values
     * are listed in first row, then second row, then third row, from left to right.
     *
     * @param x1 Value for Row 1, Column 1.
     * @param y1 Value for Row 1, Column 2.
     * @param z1 Value for Row 1, Column 3.
     * @param x2 Value for Row 2, Column 1.
     * @param y2 Value for Row 2, Column 2.
     * @param z2 Value for Row 2, Column 3.
     * @param x3 Value for Row 3, Column 1.
     * @param y3 Value for Row 3, Column 2.
     * @param z3 Value for Row 3, Column 3.
     */
    public Matrix3d(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
        this(new double[][]{{x1, x2, x3}, {y1, y2, y3}, {z1, z2, z3}});
    }

    /**
     * Creates a new matrix from three column vectors.
     *
     * @param x First column of the matrix.
     * @param y Second column of the matrix.
     * @param z Third column of the matrix.
     */
    public Matrix3d(Vector3d x, Vector3d y, Vector3d z) {
        this(new double[][]{{x.getX(), x.getY(), x.getZ()}, {y.getX(), y.getY(), y.getZ()}, {z.getX(), z.getY(), z.getZ()}});
    }

    /**
     * Creates a new matrix from the given array. The first index of the array is saved as column. I.e. m[0] is the first
     * column of the matrix, while {m[0][0], m[1][0], m[2][0]} would be the first row of the matrix.
     *
     * @param m A three by three array with the values for the matrix.
     */
    public Matrix3d(double[][] m) {
        if (m.length != 3 || m[0].length != 3 || m[1].length != 3 || m[2].length != 3)
            try {
                throw new InvalidArgumentException(new String[]{"The given data is not of length 3 by 3"});
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        this.data = m;
    }

    /**
     * Gets the data of this matrix as a three by three array, with the first index describing the column.
     *
     * @return The column-first values as a double array.
     */
    public double[][] getData() {
        return this.data;
    }

    /**
     * Returns the first column of this matrix as a vector.
     *
     * @return This matrix's first column as a vector.
     */
    public Vector3d getColumn1() {
        return this.getColumnByIndex(0);
    }

    /**
     * Returns the second column of this matrix as a vector.
     *
     * @return This matrix's second column as a vector.
     */
    public Vector3d getColumn2() {
        return this.getColumnByIndex(1);
    }

    /**
     * Returns the third column of this matrix as a vector.
     *
     * @return This matrix's third column as a vector.
     */
    public Vector3d getColumn3() {
        return this.getColumnByIndex(2);
    }

    /**
     * Returns the column of this matrix identified by the index (starting at zero) as a vector.
     *
     * @param c The index of the column to return.
     * @return This matrix's c-th column as a vector.
     */
    public Vector3d getColumnByIndex(int c) {
        if (c >= 3) return null;
        return new Vector3d(this.data[c][0], this.data[c][1], this.data[c][2]);
    }

    /**
     * Return the first row of this matrix as a column-vector.
     *
     * @return This matrix's first row as a vector.
     */
    public Vector3d getRow1() {
        return this.getRowByIndex(0);
    }

    /**
     * Return the second row of this matrix as a column-vector.
     *
     * @return This matrix's second row as a vector.
     */
    public Vector3d getRow2() {
        return this.getRowByIndex(1);
    }

    /**
     * Return the third row of this matrix as a column-vector.
     *
     * @return This matrix's third row as a vector.
     */
    public Vector3d getRow3() {
        return this.getRowByIndex(2);
    }

    /**
     * Returns the row of this matrix identified by the index (starting at zero) as a column-vector.
     *
     * @param r The index of the row to return.
     * @return This matrix's r-th row as a vector.
     */
    public Vector3d getRowByIndex(int r) {
        if (r >= 3) return null;
        return new Vector3d(this.data[0][r], this.data[1][r], this.data[2][r]);
    }

    /**
     * Returns an identity matrix, which's values are 0 on non-diagonal elements and 1 on diagonal elements.
     *
     * @return A new object of the identity matrix.
     */
    public static Matrix3d identity() {
        return new Matrix3d(new double[][]{{1.0d, 0.0d, 0.0d}, {0.0d, 1.0d, 0.0d}, {0.0d, 0.0d, 1.0d}});
    }

    /**
     * Transposes this matrix, by switching its rows and columns. I.e. each element is inserted into the position
     * mirrored on the diagonal of the matrix.
     *
     * @return A new object containing a transposed matrix of this one.
     */
    public Matrix3d transpose() {
        return new Matrix3d(this.getRow1(), this.getRow2(), this.getRow3());
    }

    /**
     * Multiplies this matrix with a second matrix. As multiplication is not commutative this matrix is multiplied
     * from the left with the second matrix b.
     *
     * @param b The matrix to multiply this matrix with.
     * @return A new object containing the product of this and the second matrix b: this*b.
     */
    public Matrix3d mult(Matrix3d b) {
        double[][] result = new double[3][3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                result[c][r] = this.getRowByIndex(r).dot(b.getColumnByIndex(c));
            }
        }
        return new Matrix3d(result);
    }

    /**
     * Multiplies this matrix with a vector of the same dimension. The matrix is multiplied from the left with
     * the vector v.
     *
     * @param v The vector to multiply this matrix with.
     * @return A new object containing the product of this matrix and the vector v: this*v.
     */
    public Vector3d mult(Vector3d v) {
        double[] result = new double[3];
        for (int r = 0; r < 3; r++) {
            result[r] = this.getRowByIndex(r).dot(v);
        }
        return new Vector3d(result);
    }

    /**
     * Transforms this matrix into a readable padded format, while rounding to the specified number of digits behind the comma.
     * Also pads with spaces from the left, depending on the number of digits of the highest number in the column.
     *
     * @param precision The number of digits behind the comma.
     * @return A padded representation of the matrix.
     */
    public String toString(int precision) {
        int[] max = new int[]{0, 0, 0};
        int[] neg = new int[]{0, 0, 0};
        for (int c = 0; c < 3; c++) {
            for (int r = 0; r < 3; r++) {
                while (Math.abs(this.data[c][r]) > Math.pow(10, max[c])) {
                    max[c]++;
                    neg[c] = (this.data[c][r] < 0) ? 1 : 0;
                }
            }
            max[c]++;
        }
        String[] format = new String[]{
                "$0" + (max[0] + neg[0] + precision) + "." + precision + "f",
                "$0" + (max[1] + neg[1] + precision) + "." + precision + "f",
                "$0" + (max[2] + neg[2] + precision) + "." + precision + "f"};
        String formatted = String.format(
                "[ %1" + format[0] + "   %2" + format[1] + "   %3" + format[2] + "\n" +
                        "  %4" + format[0] + "   %5" + format[1] + "   %6" + format[2] + "\n" +
                        "  %7" + format[0] + "   %8" + format[1] + "   %9" + format[2] + " ]",
                this.data[0][0], this.data[1][0], this.data[2][0],
                this.data[0][1], this.data[1][1], this.data[2][1],
                this.data[0][2], this.data[1][2], this.data[2][2]);
        String result = formatted;
        do {
            formatted = result;
            result = formatted
                    .replaceAll(" -0", "  -")
                    .replaceAll(" 0", "  ");
        } while (!formatted.equals(result));
        return result.replaceAll(" ,", "0,");
    }

    /**
     * Transforms this matrix into a readable padded format with three digits behind the comma.
     * Also pads with spaces from the left, depending on the biggest number in each column of the matrix.
     *
     * @return The string representation of this matrix.
     */
    @Override
    public String toString() {
        return this.toString(3);
    }

}
