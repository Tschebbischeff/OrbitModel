package de.tschebbischeff;

import de.tschebbischeff.planets.Planet;
import de.tschebbischeff.planets.Vector3d;

public class Main {

    public static final double sunRadius = 12.060877401267325d;
    public static final double rithRadius = 1.0d;

    public static void main(String[] args) {
        Planet sun = new Planet();
        Planet rith = new Planet(sun);
        Planet exes = new Planet(rith);
        rith.setDistanceToParent(4279.666146958275521734777073708d) //In Rith-radius
                .setInclination(0.0d)
                .setInclinationOffset(0.0d)
                .setOrbitalOffset(0.0d)
                .setOrbitalPeriod(1.0d);
        exes.setDistanceToParent(11.47d)
                .setInclination(0.0d)
                .setInclinationOffset(0.0d)
                .setOrbitalOffset(0.0d)
                .setOrbitalPeriod(1.0d/56.53d);
        long timing = System.currentTimeMillis();
        double samples = 0.0d;
        double avgRithDiameter = 0.0d;
        double avgSunDiameter = 0.0d;
        for (double time = 0.0d; time <= 1.0d; time += 0.01d) {
            Vector3d rithPosition = rith.getPosition(time);
            Vector3d exesPosition = exes.getPosition(time);
            double thisRithDiameter = angularDiameter(rithRadius*2.0d, exesPosition.subtract(rithPosition).length()-1.0d);
            avgRithDiameter = ((samples / (samples+1)) * avgRithDiameter + (1 / (samples+1.0d)) * thisRithDiameter);
            double thisSunDiameter = angularDiameter(sunRadius*2.0d, exesPosition.length());
            avgSunDiameter = ((samples / (samples+1)) * avgSunDiameter + (1.0d / (samples+1.0d)) * thisSunDiameter);
            samples++;
        }
        timing = System.currentTimeMillis() - timing;
        avgRithDiameter = angularDiameter(rithRadius*2.0d, 10.47d);
        System.out.println("Done in: " + (timing / 1000.0d) + " seconds.");
        System.out.println("Average angular diameter of Sun in zenith: " + avgSunDiameter);
        System.out.println("Average angular diameter of Rith in zenith: " + avgRithDiameter);
    }

    public static double angularDiameter(double diameter, double distance) {
        return 2.0d*Math.asin(diameter / (2.0d*distance)) * (360.0d / (2.0d*Math.PI));
    }
}
