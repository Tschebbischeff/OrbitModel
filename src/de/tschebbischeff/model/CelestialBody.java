package de.tschebbischeff.model;

import com.sun.javaws.exceptions.InvalidArgumentException;
import de.tschebbischeff.math.Quat4d;
import de.tschebbischeff.math.Vector3d;
import de.tschebbischeff.model.caches.OrientationCache;
import de.tschebbischeff.model.caches.PositionCache;

/**
 * Models celestial bodies.
 * These can be put on an orbit, as well as have other orbits around them.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class CelestialBody {

    /**
     * Caches the last result of the calculation of the position.
     */
    private PositionCache positionCache = new PositionCache();

    /**
     * Caches the last result of the calculation of the orientation.
     */
    private OrientationCache orientationCache = new OrientationCache();

    /**
     * The orbit of this celestial body.
     */
    private Orbit orbit = null;
    /**
     * The radius of this celestial body.
     */
    private double radius = Double.MIN_VALUE;
    /**
     * The mass of this celestial body.
     */
    private double mass = Double.MIN_VALUE;
    /**
     * The axis of rotation around which this body rotates.
     */
    private Vector3d axisOfRotation = Vector3d.Z_AXIS;
    /**
     * The offset at time zero, this planet has on its orbit.
     */
    private double orbitalOffset = 0.0d;
    /**
     * The offset at time zero, this planets rotation has.
     */
    private double rotationalOffset = 0.0d;

    /**
     * Creates a new star.
     */
    public CelestialBody() {
        this(null);
    }

    /**
     * Creates a new celestial body on the given orbit. If the orbit is null, this celestial body is classified as a star.
     *
     * @param orbit The orbit on which this celestial body moves.
     */
    public CelestialBody(Orbit orbit) {
        this.orbit = orbit;
    }

    /**
     * A star in the context of this model is a celestial body, which has no orbit.
     *
     * @return Whether this body is a star or a satellite.
     */
    public boolean isStar() {
        return this.orbit == null;
    }

    /**
     * Sets the radius of this body
     *
     * @param r The radius of this body, which must be greater than zero.
     * @return This celestial body for fluent method calls.
     */
    public CelestialBody setRadius(double r) {
        if (r < Double.MIN_VALUE) {
            try {
                throw new InvalidArgumentException(new String[]{"Radius must be greater than zero."});
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
        this.radius = r;
        return this;
    }

    /**
     * Sets the mass of this body
     *
     * @param m The mass of this body, which must be greater than zero.
     * @return This celestial body for fluent method calls.
     */
    public CelestialBody setMass(double m) {
        if (this.isStar() && m < (23835.0d * Scales.earthMass())) {
            try {
                throw new InvalidArgumentException(new String[]{"Mass is too low for a star."});
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
        if (!this.isStar() && m >= (23835.0d * Scales.earthMass())) {
            try {
                throw new InvalidArgumentException(new String[]{"Mass is too high for a non-star celestial body."});
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
        if (!this.isStar() && m < Double.MIN_VALUE) {
            try {
                throw new InvalidArgumentException(new String[]{"Mass must be positive and non-zero."});
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
        this.mass = m;
        return this;
    }

    /**
     * Sets the axis of rotation of this body. The axis is seen as relative to the orbital plane.
     *
     * @param a The axis around which this body rotates.
     * @return This celestial body for fluent method calls.
     */
    public CelestialBody setAxisOfRotation(Vector3d a) {
        this.axisOfRotation = a;
        this.orientationCache.invalidate();
        return this;
    }

    /**
     * Sets the orbital offset of this body. Not applicable to stars. The orbital offset defines
     * how much farther than the periapsis the body is located along its orbit at time zero.
     * A value of zero means it is located at its periapsis, a value of 0.5 means it is located at half the orbit.
     *
     * @param o The orbital offset of this body. Must be in the interval [0,1).
     * @return This celestial body for fluent method calls.
     */
    public CelestialBody setOrbitalOffset(double o) {
        if (this.isStar()) {
            System.out.println("Warning! Trying to set orbital offset of star. Aborted setting orbital offset.");
        } else {
            if (o < 0.0d || o >= 1.0d) {
                try {
                    throw new InvalidArgumentException(new String[]{"Orbital offset is not between 0 and 1!"});
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
            this.orbitalOffset = o;
        }
        return this;
    }

    /**
     * Sets the rotational offset of this body. The rotational offset defines
     * how much the body is already rotated around its axis of rotation at time zero.
     * A value of zero means it is not rotated around itself, a value of 0.5 means it has done half a rotation already.
     *
     * @param r The rotational offset of this body. Must be in the interval [0,1).
     * @return This celestial body for fluent method calls.
     */
    public CelestialBody setRotationalOffset(double r) {
        if (r < 0.0d || r >= 1.0d) {
            try {
                throw new InvalidArgumentException(new String[]{"Rotational offset is not between 0 and 1!"});
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
        this.rotationalOffset = r;
        return this;
    }

    /**
     * Gets the radius of this celestial body
     *
     * @return This body's radius.
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Gets the mass of this celestial body
     *
     * @return This body's mass.
     */
    public double getMass() {
        return this.mass;
    }

    /**
     * Gets the axis around which this body rotates.
     *
     * @return This body's axis of rotation.
     */
    public Vector3d getAxisOfRotation() {
        return this.axisOfRotation;
    }

    /**
     * Gets the orbital offset of this body, see {@link CelestialBody#setOrbitalOffset(double)} for more
     * information regarding the orbital offset.
     *
     * @return The orbital offset of this body as a value between zero and one. Excluding one, inlcuding zero.
     */
    public double getOrbitalOffset() {
        return this.orbitalOffset;
    }

    /**
     * Gets the rotational offset of this body, see {@link CelestialBody#setRotationalOffset(double)} for more
     * information regarding the rotational offset.
     *
     * @return The rotational offset of this body as a value between zero and one. Excluding one, including zero.
     */
    public double getRotationalOffset() {
        return this.rotationalOffset;
    }

    /**
     * Gets the orientation of this body in a global context, that is the orientation of its orbital plane combined
     * with its axis of rotation.
     * This orientation does not rotate around the planets axis of rotation depending on time!
     *
     * @return This body's global orientation.
     */
    public Quat4d getGlobalOrientation() {
        if (this.isStar()) {
            this.orientationCache.parentOrientation = null;
            this.orientationCache.orientation = new Quat4d(Vector3d.Z_AXIS, this.getAxisOfRotation());
        } else {
            Quat4d parentOrientation = this.orbit.getOrbitalPlaneOrientation();
            if (this.orientationCache.parentOrientation == null || !this.orientationCache.parentOrientation.equals(parentOrientation)) {
                this.orientationCache.parentOrientation = parentOrientation;
                this.orientationCache.orientation = new Quat4d(Vector3d.Z_AXIS, this.getAxisOfRotation()).mult(parentOrientation);
            }
        }
        return this.orientationCache.orientation;
    }

    /**
     * Gets the axial tilt of this celestial body.
     *
     * @return This body's axial tilt.
     */
    public double getAxialTilt() {
        if (this.isStar()) {
            return Vector3d.Z_AXIS.angle(this.getAxisOfRotation());
        } else {
            return this.orbit.getOrbitalPlaneOrientation().rotateVector(Vector3d.Z_AXIS).angle(this.getAxisOfRotation());
        }
    }

    /**
     * Calculates the position of this celestial body, depending on a time parameter.
     *
     * @param time The absolute time at which to calculate the position. Time zero is defined as the time at which
     *             all celestial bodies are rotated zero degrees around themselves and at their periapsis, unless
     *             the offsets are given.
     * @return The position of the celestial body in a global coordinate system at the given time.
     */
    public Vector3d getPosition(double time) {
        if (this.positionCache.argument == null || this.positionCache.argument != time) {
            this.positionCache.argument = time;
            if (this.orbit != null) {
                this.positionCache.position = this.orbit.getParentBody().getPosition(time).add(this.orbit.getOrbitalPositionByTrueAnomaly(2 * Math.PI * (time / this.getSiderealPeriod() + this.getOrbitalOffset())));
            } else {
                this.positionCache.position = new Vector3d(0.0d, 0.0d, 0.0d);
            }
        }
        return this.positionCache.position;
    }

    /**
     * Gets this body's sidereal period. I.e. the time it needs to revolve once on its orbit.
     *
     * @return The sidereal period of this celestial body. Zero for stars.
     */
    public double getSiderealPeriod() {
        if (this.orbit == null) {
            return 0.0d;
        } else {
            return 2 * Math.PI * Math.sqrt((this.orbit.getSemiMajorAxis() * this.orbit.getSemiMajorAxis() * this.orbit.getSemiMajorAxis()) / (Scales.gravitationalConstant() * (this.getMass() + this.orbit.getParentBody().getMass())));
        }
    }

}
