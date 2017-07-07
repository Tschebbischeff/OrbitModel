package de.tschebbischeff.model;

import com.sun.javaws.exceptions.InvalidArgumentException;
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
    private static class QueryBuffer {
        static Double time = null;
        static Vector3d position = new Vector3d(0.0d, 0.0d, 0.0d);
    }

    /**
     * The orbit of this celestial body.
     */
    private Orbit orbit = null;
    /**
     * The radius of this celestial body.
     */
    private double radius = Double.MIN_VALUE;

    /**
     * Creates a new star.
     */
    public CelestialBody() {
        this(null);
    }

    /**
     * Creates a new celestial body on the given orbit. If the orbit is null, this celestial body is classified as a star.
     * @param orbit The orbit on which this celestial body moves.
     */
    public CelestialBody(Orbit orbit) {
        this.orbit = orbit;
    }

    /**
     * A star in the context of this model is a celestial body, which has no orbit.
     * @return Whether this body is a star or a satellite.
     */
    public boolean isStar() {
        return this.orbit == null;
    }

    /**
     * Sets the radius of this body
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
     * Gets the radius of this celestial body
     * @return This bodys radius.
     */
    public double getRadius() {
        return this.radius;
    }

    public Vector3d getPosition(double time) {
        if (QueryBuffer.time != time) {
            QueryBuffer.time = time;
            if (this.orbit != null) {
                QueryBuffer.position = new Vector3d(0.0d, 0.0d, 0.0d);
            } else {
                QueryBuffer.position = new Vector3d(0.0d, 0.0d, 0.0d);
            }
        }
        return QueryBuffer.position;
    }

}
