package de.tschebbischeff.model;

import com.sun.javaws.exceptions.InvalidArgumentException;
import de.tschebbischeff.math.Quat4d;
import de.tschebbischeff.math.Vector3d;

/**
 * Models any orbit around a given central celestial body.
 * All angles are stored in radians. Setters and Getters return degrees however.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class Orbit {

    /**
     * Buffers the last result of the calculation of the orbital position.
     */
    private static class QueryBuffer {
        static Double trueAnomaly = null;
        static Vector3d position = new Vector3d(0.0d, 0.0d, 0.0d);
    }

    /**
     * The celestial body at the center of the orbit.
     */
    private CelestialBody parent = null;
    /**
     * The orbits eccentricity.
     */
    private double eccentricity = 0.0d;
    /**
     * The greatest distance to the parent celestial body on the orbit.
     */
    private double semiMajorAxis = 1.0d;
    /**
     * The inclination of the orbit.
     */
    private double inclination = 0.0d;
    /**
     * The longitude of the ascending node of the orbit.
     */
    private double longitudeOfAscendingNode = 3.0d/2.0d * Math.PI;
    /**
     * The argument of periapsis of the orbit.
     */
    private double argumentOfPeriapsis = Math.PI;

    /**
     * Creates a new orbit around a given celestial body.
     * @param parent The celestial body at the center of the orbit.
     */
    public Orbit(CelestialBody parent) {
        this.parent = parent;
    }

    /**
     * Sets the eccentricity of this orbit. The eccentricity defines how far from circular the orbit is.
     * @param e The new eccentricity between 0 and 1, including 0, excluding 1. A value of 0 means the orbit is circular.
     * @return This orbit for fluent method calls.
     */
    public Orbit setEccentricity(double e) {
        if (e < 0 || e >= 1) {
            try {
                throw new InvalidArgumentException(new String[]{"Orbital eccentricity can only be modeled in the interval [0,1)"});
            } catch (InvalidArgumentException e1) {
                e1.printStackTrace();
            }
        }
        this.eccentricity = e;
        return this;
    }

    /**
     * Gets this orbits eccentricity, which is guaranteed to remain in the interval [0,1).
     * @return This orbits eccentricity.
     */
    public double getEccentricity() {
        return this.eccentricity;
    }

    /**
     * Sets the semi major axis of this orbit. The semi major axis together with the eccentricity defines the shape
     * of the elliptic orbit. The semi major axis is the greatest distance from the parent body on the orbit.
     * @param a The new semi major axis for this orbit. Must be greater than zero.
     * @return This orbit for fluent method calls.
     */
    public Orbit setSemiMajorAxis(double a) {
        if (a < Double.MIN_VALUE) {
            try {
                throw new InvalidArgumentException(new String[]{"Semi major axis must be greater than zero."});
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
        this.semiMajorAxis = a;
        return this;
    }

    /**
     * Gets the semi major axis of this orbit.
     * @return This orbits semi major axis.
     */
    public double getSemiMajorAxis() {
        return this.semiMajorAxis;
    }

    /**
     * Gets the semi minor axis of this orbit.
     * @return This orbits semi minor axis.
     */
    public double getSemiMinorAxis() {
        return this.semiMajorAxis*Math.sqrt(1.0d-eccentricity*eccentricity);
    }

    /**
     * Sets the inclination, the angle between the parent celestial body's orbital plane and this orbital plane.
     * @param inclination The inclination of this orbit, degrees.
     * @return This orbit for fluent method calls.
     */
    public Orbit setInclination(double inclination) {
        this.inclination = Math.toRadians(inclination % 360.0d);
        return this;
    }

    /**
     * Gets the inclination of this orbit.
     * @return This orbits inclination, in degrees.
     */
    public double getInclination() {
        return Math.toDegrees(this.inclination);
    }

    /**
     * Sets the longitude of ascending node of this orbit, which defines the rotational axis of the inclination.
     * @param longitudeOfAscendingNode The longitude of ascending node of this orbit, in degrees.
     * @return This orbit for fluent method calls.
     */
    public Orbit setLongitudeOfAscendingNode(double longitudeOfAscendingNode) {
        this.longitudeOfAscendingNode = Math.toRadians(longitudeOfAscendingNode % 360.0d);
        return this;
    }

    /**
     * Gets the longitude of ascending node of this orbit.
     * @return This orbits longitude of ascending node, in degrees.
     */
    public double getLongitudeOfAscendingNode() {
        return Math.toDegrees(this.longitudeOfAscendingNode);
    }

    /**
     * Sets the argument of periapsis of this orbit, which defines at which point of the orbit the distance to
     * the parent celestial body reaches its maximum.
     * @param argumentOfPeriapsis The argument of periapsis of this orbit, in degrees.
     * @return This orbit for fluent method calls.
     */
    public Orbit setArgumentOfPeriapsis(double argumentOfPeriapsis) {
        this.argumentOfPeriapsis = Math.toRadians(argumentOfPeriapsis % 360.0d);
        return this;
    }

    /**
     * Gets the argument of periapsis of this orbit.
     * @return This orbits argument of periapsis, in degrees.
     */
    public double getArgumentOfPeriapsis() {
        return Math.toDegrees(this.argumentOfPeriapsis);
    }

    /**
     * Gets the orientation of the plane of this orbit.
     * @return The orientation of this orbit's plane.
     */
    public Quat4d getOrbitalPlaneOrientation() {
        //TODO: Buffer results and invalidate, if parameters should change, this is the "static rotation" which was deleted in last commit.
        Quat4d orbitalRotation = new Quat4d(0.0d, this.getInclination(), -(this.getLongitudeOfAscendingNode() -270.0d));
        return orbitalRotation.multiply(this.parent.getGlobalOrientation());
    }

    /*public Vector3d getOrbitalPositionByTrueAnomaly(double trueAnomaly) {
        if (QueryBuffer.trueAnomaly != trueAnomaly) {
            QueryBuffer.trueAnomaly = trueAnomaly;
            if (this.parent != null) {
                double angle = (2.0d*Math.PI * (trueAnomaly / this.orbitalPeriod)) + this.orbitalOffset;
                Matrix3d rotationMatrix = new Matrix3d(
                        Math.cos(angle), -1.0d * Math.sin(angle), 0.0d,
                        Math.sin(angle), Math.cos(angle), 0.0d,
                        0.0d, 0.0d, 1.0d
                );
                QueryBuffer.position = this.parent.getPosition(trueAnomaly).add(this.inclinationOffsetMatrix.multiply(this.inclinationMatrix.multiply(rotationMatrix.multiply(new Vector3d(1.0d, 0.0d, 0.0d)))).multiply(this.distanceToParent));
            } else {
                QueryBuffer.position = new Vector3d(0.0d, 0.0d, 0.0d);
            }
        }
        return QueryBuffer.position;
    }*/

}
