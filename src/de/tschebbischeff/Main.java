package de.tschebbischeff;

import de.tschebbischeff.math.Vector3d;
import de.tschebbischeff.model.CelestialBody;
import de.tschebbischeff.model.Orbit;
import de.tschebbischeff.model.Scales;
import de.tschebbischeff.visualizer.GlVisualizer;

public class Main {

    public static void main(String[] args) {
        long timing = System.currentTimeMillis();

        //==============DEFINITION==============\\
        CelestialBody star = (new CelestialBody())
                .setRadius(8.43188E8d * Scales.meter())
                .setMass(432900.0d * Scales.earthMass());
        Orbit rithOrbit = (new Orbit(star))
                .setSemiMajorAxis(2.9919574E11 * Scales.meter())
                .setEccentricity(0.0d)
                .setInclination(0.0d)
                .setLongitudeOfAscendingNode(0.0d)
                .setArgumentOfPeriapsis(90.0d);
        CelestialBody rith = (new CelestialBody(rithOrbit))
                .setRadius(69911000.0d * Scales.meter())
                .setMass(317.8d * Scales.earthMass()); //Currently a jupiter clone
        Orbit exesOrbit = (new Orbit(rith))
                .setSemiMajorAxis(8.0187917E8 * Scales.meter())
                .setEccentricity(0.0d)
                .setInclination(45.0d)
                .setLongitudeOfAscendingNode(-90.0d)
                .setArgumentOfPeriapsis(0.0d);
        CelestialBody exes = (new CelestialBody(exesOrbit))
                .setRadius(6371000.0d * Scales.meter())
                .setMass(1.0d * Scales.earthMass()); //Currently an earth clone

        //==============OUTPUT==============\\
        double rithSiderealPeriod = rith.getSiderealPeriod();
        double exesSiderealPeriod = exes.getSiderealPeriod();
        System.out.println("===RITH===");
        System.out.println("Sidereal period is: " + (rithSiderealPeriod / Scales.year()) + " years");
        System.out.println("Position at zero: " + rith.getPosition(0.0d));
        System.out.println("===EXES===");
        System.out.println("Sidereal period is: " + (exesSiderealPeriod / Scales.day()) + " days");
        System.out.println("Position at zero: " + exes.getPosition(0.0d));
        System.out.println("===INTERACTION===");
        System.out.println("Exes turns " + (rithSiderealPeriod / exesSiderealPeriod) + " times around Rith per Rith-year.");

        double samples = 0.0d;
        int stepNum = 1000;
        double minRithDiameter = Double.MAX_VALUE;
        double avgRithDiameter = 0.0d;
        double maxRithDiameter = 0.0d;
        double minStarDiameter = Double.MAX_VALUE;
        double avgStarDiameter = 0.0d;
        double maxStarDiameter = 0.0d;
        for (double time = 0.0d; time <= rith.getSiderealPeriod(); time += rith.getSiderealPeriod() / ((double) stepNum)) {
            Vector3d rithPosition = rith.getPosition(time);
            Vector3d exesPosition = exes.getPosition(time);
            double thisRithDiameter = angularDiameter(rith.getRadius() * 2.0d, exesPosition.sub(rithPosition).len() - exes.getRadius());
            minRithDiameter = Math.min(minRithDiameter, thisRithDiameter);
            avgRithDiameter = ((samples / (samples + 1)) * avgRithDiameter + (1 / (samples + 1.0d)) * thisRithDiameter);
            maxRithDiameter = Math.max(maxRithDiameter, thisRithDiameter);
            double thisStarDiameter = angularDiameter(star.getRadius() * 2.0d, exesPosition.len() - exes.getRadius());
            minStarDiameter = Math.min(minStarDiameter, thisStarDiameter);
            avgStarDiameter = ((samples / (samples + 1)) * avgStarDiameter + (1.0d / (samples + 1.0d)) * thisStarDiameter);
            maxStarDiameter = Math.max(maxStarDiameter, thisStarDiameter);
            samples++;
        }
        System.out.println("===ANGULAR DIAMETERS - STAR===");
        System.out.println("Min angular diameter of Star in zenith: " + minStarDiameter);
        System.out.println("Average angular diameter of Star in zenith: " + avgStarDiameter);
        System.out.println("Max angular diameter of Star in zenith: " + maxStarDiameter);
        System.out.println("===ANGULAR DIAMETERS - RITH===");
        System.out.println("Min angular diameter of Rith in zenith: " + minRithDiameter);
        System.out.println("Average angular diameter of Rith in zenith: " + avgRithDiameter);
        System.out.println("Max angular diameter of Rith in zenith: " + maxRithDiameter);

        timing = System.currentTimeMillis() - timing;
        System.out.println("===END===");
        System.out.println("Done in: " + (timing / 1000.0d) + " seconds.");
        System.out.println("===STARTING VISUALIZATION===");

        //==============VISUALIZATION==============\\
        new GlVisualizer(1000, 1000)
                .setOrbitResolution(10)
                .setCelestialBodyResolution(2)
                .setCameraSpeed(Scales.astronomicalUnit()*0.5d)
                .setCameraTurnSpeed(10.0d)
                .setOrbitColorAlpha(0.5f)
                .setCelestialBodyColorAlpha(1.0f)
                .setVisualizationSpeed(rith.getSiderealPeriod() * 0.0d)

                .addCelestialBody(star)
                .addOrbit(rithOrbit)
                .addCelestialBody(rith)
                .addOrbit(exesOrbit)
                .addCelestialBody(exes)

                .run();
    }

    public static double angularDiameter(double diameter, double distance) {
        return 2.0d * Math.asin(diameter / (2.0d * distance)) * (360.0d / (2.0d * Math.PI));
    }
}
