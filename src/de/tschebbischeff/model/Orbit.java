package de.tschebbischeff.model;

import com.sun.javaws.exceptions.InvalidArgumentException;
import de.tschebbischeff.math.Quat4d;
import de.tschebbischeff.math.Vector3d;
import de.tschebbischeff.model.caches.OrientationCache;
import de.tschebbischeff.model.caches.PositionCache;

/**
 * Models any orbit around a given central celestial body.
 * All angles are stored in radians. Setters and Getters return degrees however.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class Orbit {

    /**
     * Caches the last result of the calculation of the orbital position.
     */
    private PositionCache positionCache = new PositionCache();

    /**
     * Caches the last result of the calculation of the orbital orientation.
     */
    private OrientationCache orientationCache = new OrientationCache();

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
    private double semiMajorAxis = Scales.astronomicalUnit();
    /**
     * The inclination of the orbit.
     */
    private double inclination = 0.0d;
    /**
     * The longitude of the ascending node of the orbit.
     */
    private double longitudeOfAscendingNode = 0.0d;
    /**
     * The argument of periapsis of the orbit.
     */
    private double argumentOfPeriapsis = 0.0d;

    /**
     * Creates a new orbit around a given celestial body.
     *
     * @param parent The celestial body at the center of the orbit.
     */
    public Orbit(CelestialBody parent) {
        this.parent = parent;
    }

    /**
     * Sets the eccentricity of this orbit. The eccentricity defines how far from circular the orbit is.
     *
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
     *
     * @return This orbits eccentricity.
     */
    public double getEccentricity() {
        return this.eccentricity;
    }

    /**
     * Sets the semi major axis of this orbit. The semi major axis together with the eccentricity defines the shape
     * of the elliptic orbit. The semi major axis is the greatest distance from the center of the orbit.
     *
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
     *
     * @return This orbits semi major axis.
     */
    public double getSemiMajorAxis() {
        return this.semiMajorAxis;
    }

    /**
     * Gets the semi minor axis of this orbit.
     *
     * @return This orbits semi minor axis.
     */
    public double getSemiMinorAxis() {
        return this.semiMajorAxis * Math.sqrt(1.0d - eccentricity * eccentricity);
    }

    /**
     * Sets the inclination, the angle between the parent celestial body's orbital plane and this orbital plane.
     *
     * @param inclination The inclination of this orbit, degrees.
     * @return This orbit for fluent method calls.
     */
    public Orbit setInclination(double inclination) {
        this.inclination = Math.toRadians(inclination % 360.0d);
        this.orientationCache.invalidate();
        return this;
    }

    /**
     * Gets the inclination of this orbit.
     *
     * @return This orbits inclination, in degrees.
     */
    public double getInclination() {
        return Math.toDegrees(this.inclination);
    }

    /**
     * Sets the longitude of ascending node of this orbit, which defines the rotational axis of the inclination.
     * Zero longitude of ascending node means, the ascending node is positioned in -Y direction.
     * (I.e. the reference direction is (0, -1, 0) in a right-handed coordinate system)
     *
     * @param longitudeOfAscendingNode The longitude of ascending node of this orbit, in degrees.
     * @return This orbit for fluent method calls.
     */
    public Orbit setLongitudeOfAscendingNode(double longitudeOfAscendingNode) {
        this.longitudeOfAscendingNode = Math.toRadians(longitudeOfAscendingNode % 360.0d);
        this.orientationCache.invalidate();
        return this;
    }

    /**
     * Gets the longitude of ascending node of this orbit.
     *
     * @return This orbits longitude of ascending node, in degrees.
     */
    public double getLongitudeOfAscendingNode() {
        return Math.toDegrees(this.longitudeOfAscendingNode);
    }

    /**
     * Sets the argument of periapsis of this orbit, which defines at which point of the orbit the distance to
     * the parent celestial body reaches its minimum.
     * An argument of zero means, that the periapsis coincides with the ascending node.
     * (I.e. the angle is measured from the ascending node)
     *
     * @param argumentOfPeriapsis The argument of periapsis of this orbit, in degrees.
     * @return This orbit for fluent method calls.
     */
    public Orbit setArgumentOfPeriapsis(double argumentOfPeriapsis) {
        this.argumentOfPeriapsis = Math.toRadians(argumentOfPeriapsis % 360.0d);
        return this;
    }

    /**
     * Gets the argument of periapsis of this orbit.
     *
     * @return This orbits argument of periapsis, in degrees.
     */
    public double getArgumentOfPeriapsis() {
        return Math.toDegrees(this.argumentOfPeriapsis);
    }

    /**
     * Gets the orientation of the plane of this orbit.
     *
     * @return The orientation of this orbit's plane.
     */
    public Quat4d getOrbitalPlaneOrientation() {
        Quat4d parentOrientation = this.parent.getGlobalOrientation();
        if (this.orientationCache.parentOrientation == null || !this.orientationCache.parentOrientation.equals(parentOrientation)) {
            Quat4d orbitalRotation = Quat4d.identity().yaw(-this.getLongitudeOfAscendingNode()).pitch(this.getInclination()).yaw(-this.getArgumentOfPeriapsis());
            this.orientationCache.parentOrientation = parentOrientation;
            this.orientationCache.orientation = orbitalRotation.mult(parentOrientation);
        }
        return this.orientationCache.orientation;
    }

    /**
     * Returns a position in three dimensional space on this orbit, defined by the true anomaly.
     *
     * @param trueAnomaly The angle between the periapsis and the current position on the orbit, in degrees.
     * @return The position as a three dimensional vector, corresponding to that angle.
     */
    public Vector3d getOrbitalPositionByTrueAnomaly(double trueAnomaly) {
        if (this.positionCache.argument == null || this.positionCache.argument != trueAnomaly) {
            this.positionCache.argument = trueAnomaly;
            //Local coordinate system calculations
            double major = this.getSemiMajorAxis();
            double minor = this.getSemiMinorAxis();
            //Vector3d orbitalPosition = new Vector3d(major * Math.cos(trueAnomaly), minor * Math.sin(trueAnomaly), 0.0d);
            Vector3d orbitalPosition = new Vector3d(minor * Math.sin(trueAnomaly), -major * Math.cos(trueAnomaly), 0.0d);
            Vector3d focusPosition = Vector3d.Y_AXIS_NEG.scale(Math.sqrt(major * major - minor * minor));
            //Transform positions to global coordinate system...
            this.positionCache.position = this.getOrbitalPlaneOrientation().rotateVector(orbitalPosition.sub(focusPosition));
        }
        return this.positionCache.position;
    }

    /**
     * Gets the star, which is the basis of this orbit and its hierarchy.
     *
     * @return The star at the center of the orbital hieararchy.
     */
    public CelestialBody getSystemStar() {
        return this.parent.getSystemStar();
    }

    /**
     * Gets the celestial body at the center / focus of this orbit.
     *
     * @return The parent celestial body.
     */
    public CelestialBody getParentBody() {
        return this.parent;
    }

}
