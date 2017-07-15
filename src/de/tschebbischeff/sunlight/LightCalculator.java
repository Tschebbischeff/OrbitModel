package de.tschebbischeff.sunlight;

import de.tschebbischeff.math.Quat4d;
import de.tschebbischeff.math.Vector3d;
import de.tschebbischeff.model.CelestialBody;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Calculates the sunlight that reaches a given point on a planet depending on a light emitter and a
 * light blocking body.
 *
 * @author Tarek
 * @version 1.0.0
 */
public class LightCalculator {

    /**
     * The bodies, which emit light.
     */
    private ArrayList<CelestialBody> lightEmitters;

    /**
     * The bodies, which block the light.
     */
    private ArrayList<CelestialBody> lightBlockers;

    /**
     * The body for which the light is calculated.
     */
    private CelestialBody calculatingObject;

    /**
     * Determines the resolution of the resulting lightmap.
     */
    private int resolution = 6;

    /**
     * Creates a new sunlight calculator.
     *
     * @param calculatingObject The object for which the light should be calculated.
     */
    public LightCalculator(CelestialBody calculatingObject) {
        this.calculatingObject = calculatingObject;
        this.lightEmitters = new ArrayList<>();
        this.lightBlockers = new ArrayList<>();
    }

    /**
     * Adds a specified celestial body as a light emitter.
     *
     * @param emitter The light emitting celestial body to add.
     * @return This LightCalculator for fluent method calls.
     */
    public LightCalculator addLightEmitter(CelestialBody emitter) {
        this.lightEmitters.add(emitter);
        return this;
    }

    /**
     * Adds a specified celestial body as a light blocker.
     *
     * @param blocker The light blocking celestial body to add.
     * @return This LightCalculator for fluent method calls.
     */
    public LightCalculator addLightBlocker(CelestialBody blocker) {
        this.lightBlockers.add(blocker);
        return this;
    }

    /**
     * Sets the resolution of the resulting light calculation.
     * The celestial body will be samples in steps of (1 / 2^resolution) along its azimuth angles
     * and in steps of (2 / 2^resolution) along its zenith angles.
     * The resulting lightmap will be of width 2^resolution and height 2^(resolution-1)+1.
     * The minimum value is 3.
     *
     * @param resolution The new resolution to sample the light with.
     * @return This LightCalculator for fluent method calls.
     */
    public LightCalculator setResolution(int resolution) {
        this.resolution = Math.max(3, resolution);
        return this;
    }

    /**
     * Calculates the light on the set body at a given time.
     *
     * @param time The time at which to calculate positions of the bodies.
     * @return An image of resolution specified in {@link LightCalculator#setResolution(int)}
     */
    public BufferedImage calculateAtTime(double time) {
        Vector3d sourcePosition = this.calculatingObject.getPositionOnSurface(0, 0, 0);
        for (int azimuthStep = 0; azimuthStep < Math.pow(2, this.resolution); azimuthStep++) {

        }
        return null;
    }

}
