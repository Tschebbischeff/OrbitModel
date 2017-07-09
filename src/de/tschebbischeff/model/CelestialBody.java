package de.tschebbischeff.model;

import com.sun.javaws.exceptions.InvalidArgumentException;
import de.tschebbischeff.math.Quat4d;
import de.tschebbischeff.math.Vector3d;

/**
 * Models celestial bodies.
 * These can be put on an orbit, as well as have other orbits around them.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class CelestialBody {

    /**
     * Buffers the last result of the calculation of the position.
     */
    private class PositionBuffer {
        Double time = null;
        Vector3d position = new Vector3d(0.0d, 0.0d, 0.0d);
    }
    private PositionBuffer positionBuffer = new PositionBuffer();

    /**
     * Buffers the last result of the calculation of the orientation.
     */
    private class OrientationBuffer {
        Quat4d orientation = null;
        Quat4d parentOrientation = null;

        void invalidate() {
            this.orientation = null;
            this.parentOrientation = null;
        }
    }
    private OrientationBuffer orientationBuffer = new OrientationBuffer();

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
        if (this.isStar() && m < (23835.0d / ModelSettings.massScale)) {
            try {
                throw new InvalidArgumentException(new String[]{"Mass is too low for a star."});
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
        if (!this.isStar() && m >= (23835.0d / ModelSettings.massScale)) {
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
        this.orientationBuffer.invalidate();
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
     * Gets the orientation of this body in a global context, that is the orientation of its orbital plane combined
     * with its axis of rotation.
     *
     * @return This body's global orientation.
     */
    public Quat4d getGlobalOrientation() {
        if (this.isStar()) {
            this.orientationBuffer.parentOrientation = null;
            this.orientationBuffer.orientation = new Quat4d(Vector3d.Z_AXIS, this.getAxisOfRotation());
        } else {
            Quat4d parentOrientation = this.orbit.getOrbitalPlaneOrientation();
            if (!this.orientationBuffer.parentOrientation.equals(parentOrientation)) {
                this.orientationBuffer.parentOrientation = parentOrientation;
                this.orientationBuffer.orientation = new Quat4d(Vector3d.Z_AXIS, this.getAxisOfRotation()).multiply(parentOrientation);
            }
        }
        return this.orientationBuffer.orientation;
    }

    /**
     * Gets the axial tilt of this celestial body.
     *
     * @return This body's axial tilt.
     */
    public double getAxialTilt() {
        if (this.isStar()) {
            return Vector3d.Z_AXIS.angleBetween(this.getAxisOfRotation());
        } else {
            return this.orbit.getOrbitalPlaneOrientation().rotateVector(Vector3d.Z_AXIS).angleBetween(this.getAxisOfRotation());
        }
    }

    public Vector3d getPosition(double time) {
        if (this.positionBuffer.time != time) {
            this.positionBuffer.time = time;
            if (this.orbit != null) {
                this.positionBuffer.position = new Vector3d(0.0d, 0.0d, 0.0d);
            } else {
                this.positionBuffer.position = new Vector3d(0.0d, 0.0d, 0.0d);
            }
        }
        return this.positionBuffer.position;
    }

}
