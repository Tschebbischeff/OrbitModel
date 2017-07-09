package de.tschebbischeff.model.caches;

import de.tschebbischeff.math.Quat4d;

/**
 * Caches an orientation that was calculated based on an orientation of a parent orbit or celestial body,
 * to save ressources, in case the same orientation is calculated twice in a row.
 *
 * @author Tarek
 * @version 1.0
 * @since 2017-07-10
 */
public class OrientationCache {

    /**
     * The orientation of the parent orbit or body, with which the cached orientation was calculated.
     */
    public Quat4d parentOrientation = null;
    /**
     * The cached orientation.
     */
    public Quat4d orientation = null;

    /**
     * Invalidates this cache. I.e. marks this cache as containing values, which need to be recalculated.
     * The values are deleted from the cache permanently.
     */
    public void invalidate() {
        this.orientation = null;
        this.parentOrientation = null;
    }
}
