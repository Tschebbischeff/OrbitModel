package de.tschebbischeff.model;

/**
 * Contains scales from SI units to the values you supply to the model system.
 * It is advised to let the scales remain at the SI units, when not modeling values exceeding values in the solar system
 * and if no precision higher than 3 digits is required.
 *
 * @author Tarek
 * @version 1.0
 * @since 2017-07-08
 */
public class Scales {

    /**
     * The scale to use for distance values.
     */
    private static double distanceScale = 1.0d;
    /**
     * The scale to use for mass values.
     */
    private static double massScale = 1.0d;
    /**
     * The scale to use for time values.
     */
    private static double timeScale = 1.0d;

    /**
     * Changes the distance scale factor.
     * 1.0 means that any distance value you supply to the model is in meters.
     * 1000.0 means that any distance value you supply to the model is in kilometers.
     * Generally speaking 1.0 of a distance value corresponds to distanceScale meters.
     *
     * @param distanceScale The new distance scale to use in the model.
     */
    public static void setDistanceScale(double distanceScale) {
        Scales.distanceScale = distanceScale;
    }

    /**
     * Changes the mass scale factor.
     * 1.0 means that any mass value you supply to the model is in kilograms.
     * 1000.0 means that any mass value you supply to the model is in tons.
     * Generally speaking 1.0 of a mass value corresponds to massScale kilograms.
     *
     * @param massScale The new mass scale to use in the model.
     */
    public static void setMassScale(double massScale) {
        Scales.massScale = massScale;
    }

    /**
     * Changes the time scale factor.
     * 1.0 means that any time value you supply to the model is in seconds.
     * 60.0 means that any time value you supply to the model is in minutes.
     * Generally speaking 1.0 of a time value corresponds to timeScale seconds.
     *
     * @param timeScale The new time scale to use in the model.
     */
    public static void setTimeScale(double timeScale) {
        Scales.timeScale = timeScale;
    }

    /**
     * Returns a meter.
     * @return One meter considering the set scale.
     */
    public static double meter() {
        return 1.0d / distanceScale;
    }

    /**
     * Returns a kilometer.
     * @return One kilometer considering the set scale.
     */
    public static double kilometer() {
        return 1000.0d / distanceScale;
    }

    /**
     * Returns the earth radius.
     * @return The radius of earth considering the set scale.
     */
    public static double earthRadius() {
        return 6.378100 * (1000000.0d / distanceScale);
    }

    /**
     * Returns the solar radius.
     * @return The radius of the sun considering the set scale.
     */
    public static double solarRadius() {
        return 6.957d * (100000000.0d / distanceScale);
    }

    /**
     * Returns an astronomical unit.
     * @return One astronomical unit considering the set scale.
     */
    public static double astronomicalUnit() {
        return 1.495978707 * (100000000000.0d / distanceScale);
    }

    /**
     * Returns a kilogram.
     * @return One kilogram considering the set scale.
     */
    public static double kilogram() {
        return 1.0d / massScale;
    }

    /**
     * Returns a ton.
     * @return One ton considering the set scale.
     */
    public static double ton() {
        return 1000.0d / massScale;
    }

    /**
     * Returns the earth mass.
     * @return The mass of earth considering the set scale.
     */
    public static double earthMass() {
        return 5.97237d * (1000000000000000000000000.0d / massScale);
    }

    /**
     * Returns the solar mass.
     * @return The mass of the sun considering the set scale.
     */
    public static double solarMass() {
        return 1.98855d * (1000000000000000000000000000000.0d / massScale);
    }

    /**
     * Returns a second.
     * @return One second considering the set scale.
     */
    public static double second() {
        return 1.0d / timeScale;
    }

    /**
     * Returns a minute.
     * @return One minute considering the set scale.
     */
    public static double minute() {
        return 6.0d * (10.0d / timeScale);
    }

    /**
     * Returns an hour.
     * @return One hour considering the set scale.
     */
    public static double hour() {
        return 3.6d * (1000.0d / timeScale);
    }

    /**
     * Returns a day.
     * @return One day considering the set scale.
     */
    public static double day() {
        return 8.64d * (10000.0d / timeScale);
    }

    /**
     * Returns a year.
     * @return One year considering the set scale.
     */
    public static double year() {
        return 3.15576d * ( 10000000.0d / timeScale);
    }

    public static double gravitationalConstant() {
        return 6.67408d * 0.00000000001d * ((((1.0d / distanceScale) * (1.0d / distanceScale) * (1.0d / distanceScale) * massScale) * timeScale) * timeScale);
    }

}
