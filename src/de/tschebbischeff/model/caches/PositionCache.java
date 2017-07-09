package de.tschebbischeff.model.caches;

import de.tschebbischeff.math.Vector3d;

/**
 * Caches a position that was calculated based on a argument, to save ressources,
 * in case the same position is calculated twice in a row.
 *
 * @author Tarek
 * @version 1.0
 * @since 2017-07-10
 */
public class PositionCache {

    /**
     * The argument with which the cached position was calculated.
     * In case of an orbit this is the true anomaly. In case of a celestial body it is the time.
     */
    public Double argument = null;
    /**
     * The cached position.
     */
    public Vector3d position = new Vector3d(0.0d, 0.0d, 0.0d);

    public void invalidate() {
        this.argument = null;
        this.position = new Vector3d(0.0d, 0.0d, 0.0d);
    }
}
