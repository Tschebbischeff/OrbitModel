package de.tschebbischeff.model;

/**
 * Contains static settings for the model.
 *
 * @author Roboscope
 * @version 1.0
 * @since 2017-07-08
 */
public class ModelSettings {

    /**
     * Change this factor to the scale you are using for distance.
     * 1.0 means that any distance value you supply to the model is in meters.
     * 1000.0 means that any distance value you supply to the model is in kilometers.
     * Generally speaking 1.0 of your value corresponds to distanceScale meters.
     */
    public static double distanceScale = 1000.0d;
    /**
     * Change this factor to the scale you are using for mass.
     * 1.0 means that any mass value you supply to the model is in earth masses,
     * which equals 5.972 * 10^24 kg.
     * Generally speaking 1.0 of your value corresponds to massScale earth masses, it
     * may be most useful to supply the mass of your host star in earth masses as the scale.
     */
    public static double massScale = 1.0d;

}
