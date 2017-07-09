package de.tschebbischeff.math;

import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * Vector implementation with double precision.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class Vector3d {

    /**
     * A vector representing the x-axis
     */
    public static final Vector3d X_AXIS = new Vector3d(1.0d, 0.0d, 0.0d);
    /**
     * A vector representing the y-axis
     */
    public static final Vector3d Y_AXIS = new Vector3d(0.0d, 1.0d, 0.0d);
    /**
     * A vector representing the z-axis
     */
    public static final Vector3d Z_AXIS = new Vector3d(0.0d, 0.0d, 1.0d);
    /**
     * Stores the vector data in a 3 element array
     */
    private double[] data;

    /**
     * Creates a new vector from the supplied coordinates.
     *
     * @param x The x coordinate of the new vector.
     * @param y The y coordinate of the new vector.
     * @param z The z coordinate of the new vector.
     */
    public Vector3d(double x, double y, double z) {
        this(new double[]{x, y, z});
    }

    /**
     * Creates a new vector from the data in the array. The array must contain exactly 3 elements.
     * The first element corresponds to the x-coordinate of the vector, while the second and third elements
     * corresponds to the y- and z-coordinate respectively.
     *
     * @param v A three element array containg the data for the new vector.
     */
    public Vector3d(double[] v) {
        if (v.length != 3)
            try {
                throw new InvalidArgumentException(new String[]{"The given data is not of length 3"});
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        this.data = v;
    }

    /**
     * Gets the x-coordinate of this vector.
     *
     * @return This vector's x-coordinate.
     */
    public double getX() {
        return this.data[0];
    }

    /**
     * Gets the y-coordinate of this vector.
     *
     * @return This vector's y-coordinate.
     */
    public double getY() {
        return this.data[1];
    }

    /**
     * Gets the z-coordinate of this vector.
     *
     * @return This vector's z-coordinate.
     */
    public double getZ() {
        return this.data[2];
    }

    /**
     * Gets the x-, y- and z-coordinate of this vector as an array.
     *
     * @return An array containing this vector's x-coordinate as the first element, and this vector's y- and
     * z-coordinates as the second and third element respectively.
     */
    public double[] getData() {
        return this.data;
    }

    /**
     * Sets this vector's x-coordinate.
     *
     * @param x The new x-coordinate of this vector.
     * @return This vector for fluent method calls.
     */
    public Vector3d setX(double x) {
        this.data[0] = x;
        return this;
    }

    /**
     * Sets this vector's y-coordinate.
     *
     * @param y The new y-coordinate of this vector.
     * @return This vector for fluent method calls.
     */
    public Vector3d setY(double y) {
        this.data[1] = y;
        return this;
    }

    /**
     * Sets this vector's z-coordinate.
     *
     * @param z The new z-coordinate of this vector.
     * @return This vector for fluent method calls.
     */
    public Vector3d setZ(double z) {
        this.data[2] = z;
        return this;
    }

    /**
     * Sets this vector's x-, y- and z-coordinate.
     *
     * @param x The new x-coordinate of this vector.
     * @param y The new y-coordinate of this vector.
     * @param z The new z-coordinate of this vector.
     * @return This vector for fluent method calls.
     */
    public Vector3d setData(double x, double y, double z) {
        return this.setData(new double[]{x, y, z});
    }

    /**
     * Sets this vector's x-, y- and z-coordinate.
     *
     * @param v A three element array containing the new x-coordinate as the first element and the new y- and
     *          z-coordinate as the second and third element respectively.
     * @return This vector for fluent method calls.
     */
    public Vector3d setData(double[] v) {
        if (v.length != 3)
            try {
                throw new InvalidArgumentException(new String[]{"The given data is not of length 3"});
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        this.data = v;
        return this;
    }

    /**
     * Produces the dot product of this vector and a second vector.
     *
     * @param b The vector to build the dot product with.
     * @return The dot product of this vector and vector b.
     */
    public double dot(Vector3d b) {
        return this.getX() * b.getX() + this.getY() * b.getY() + this.getZ() * b.getZ();
    }

    /**
     * Produces the cross product of this vector and a second vector.
     *
     * @param b The vector to build the cross product with.
     * @return A new vector which is equal to the cross product of this vector and vector b.
     */
    public Vector3d cross(Vector3d b) {
        return new Vector3d(
                this.getY() * b.getZ() - this.getZ() * b.getY(),
                this.getZ() * b.getX() - this.getX() * b.getZ(),
                this.getX() * b.getY() - this.getY() * b.getX()
        );
    }

    /**
     * Calculates the squared length of this vector.
     * This operation skips the calculation of the square root in the length calculation, to
     * allow for better performance when comparing lengths.
     *
     * @return The length of this vector, squared.
     */
    public double len2() {
        return this.dot(this);
    }

    /**
     * Calculates the length of this vector.
     * See also {@link Vector3d#len2()} for better performance in special cases.
     *
     * @return The length of this vector.
     */
    public double len() {
        return Math.sqrt(this.len2());
    }

    /**
     * Normalizes this vector. I.e. this vector is of length 1 afterwards, but still points in the same direction.
     *
     * @return A new vector, which equals this vector normalized.
     */
    public Vector3d normalize() {
        double length = this.len();
        return new Vector3d(this.getX() / length, this.getY() / length, this.getZ() / length);
    }

    /**
     * Produces the sum of this and another vector.
     *
     * @param b The vector to add to this vector.
     * @return A new vector, which is equal to the sum of this vector and vector b.
     */
    public Vector3d add(Vector3d b) {
        return new Vector3d(this.getX() + b.getX(), this.getY() + b.getY(), this.getZ() + b.getZ());
    }

    /**
     * Produces the difference of this and another vector.
     *
     * @param b The vector to subtract from this vector.
     * @return A new vector, which is equal to this vector minus the vector b.
     */
    public Vector3d sub(Vector3d b) {
        return new Vector3d(this.getX() - b.getX(), this.getY() - b.getY(), this.getZ() - b.getZ());
    }

    /**
     * Multiplies each of this vector's components with a given scalar.
     *
     * @param b The scalar with which each component of this vector is multiplied.
     * @return A new vector, which is equal to this vector scaled by b.
     */
    public Vector3d scale(double b) {
        return new Vector3d(this.getX() * b, this.getY() * b, this.getZ() * b);
    }

    /**
     * Calculates the angle between this vector and another vector.
     *
     * @param b The vector with which to calculate the angle.
     * @return The angle between this vector and vector b.
     */
    public double angle(Vector3d b) {
        return Math.toDegrees(Math.acos(this.dot(b) / this.len() * b.len()));
    }

    /**
     * Produces a vector, which is definitely orthogonal to this vector.
     * No other assumptions can be made, except that the angle between this vector and the one produced is 90 degrees.
     *
     * @return A vector, which is orthogonal to this vector.
     */
    public Vector3d anyOrthogonal() {
        double x = Math.abs(this.getX());
        double y = Math.abs(this.getY());
        double z = Math.abs(this.getZ());
        Vector3d other = x < y ? (x < z ? Vector3d.X_AXIS : Vector3d.Z_AXIS) : (y < z ? Vector3d.Y_AXIS : Vector3d.Z_AXIS);
        return this.cross(other);
    }

    /**
     * Transforms this vector into a readable format, while rounding to the specified number of digits behind the comma.
     *
     * @param precision The number of digits behind the comma.
     * @return A easily readable form of the vector.
     */
    public String toString(int precision) {
        return String.format(
                "[ %1$." + precision + "f   %2$." + precision + "f   %3$." + precision + "f ]",
                this.data[0], this.data[1], this.data[2]);
    }

    /**
     * Transforms this vector into a readable format, while rounding to three digits behind the comma.
     *
     * @return A easily readable form of the vector with three digits behind the comma.
     */
    @Override
    public String toString() {
        return this.toString(3);
    }
}
