package de.tschebbischeff;

import de.tschebbischeff.math.Matrix3d;
import de.tschebbischeff.math.Quat4d;
import de.tschebbischeff.math.Vector3d;
import de.tschebbischeff.model.CelestialBody;
import de.tschebbischeff.model.ModelSettings;
import de.tschebbischeff.model.Orbit;

public class Main {

    public static void main(String[] args) {
        long timing = System.currentTimeMillis();

        ModelSettings.distanceScale = 1000.0d * 69911.0d; //Jupiter radii
        ModelSettings.massScale = 1.0d; //kilograms
        ModelSettings.timeScale = 1281600.0; //years

        CelestialBody star = (new CelestialBody())
                .setRadius(12.060877401267325d)
                .setMass(432900.0d);
        Orbit rithOrbit = (new Orbit(star))
                .setSemiMajorAxis(4279.666146958275521734777073708d)
                .setInclination(0.0d);
        CelestialBody rith = (new CelestialBody(rithOrbit))
                .setRadius(1.0d)
                .setMass(317.8d); //Currently a jupiter clone
        Orbit exesOrbit = (new Orbit(rith))
                .setSemiMajorAxis(11.47d)
                .setInclination(0.0d)
                .setLongitudeOfAscendingNode(0.0d);
        CelestialBody exes = (new CelestialBody(exesOrbit))
                .setRadius(0.09113015119223012115403870635522d)
                .setMass(1.0d); //Currently an earth clone

        /*OrbitOld sun = new OrbitOld();
        OrbitOld rith = new OrbitOld(sun);
        OrbitOld exes = new OrbitOld(rith);
        rith.setDistanceToParent(4279.666146958275521734777073708d) //In Rith-radius = 69911km
                .setInclination(0.0d)
                .setInclinationOffset(0.0d)
                .setOrbitalOffset(0.0d)
                .setOrbitalPeriod(1.0d);
        exes.setDistanceToParent(11.47d)
                .setInclination(0.0d)
                .setInclinationOffset(0.0d)
                .setOrbitalOffset(0.0d)
                .setOrbitalPeriod(1.0d/56.53d);
        double samples = 0.0d;
        double avgRithDiameter = 0.0d;
        double avgSunDiameter = 0.0d;
        for (double time = 0.0d; time <= 1.0d; time += 0.01d) {
            Vector3d rithPosition = rith.getPosition(time);
            Vector3d exesPosition = exes.getPosition(time);
            double thisRithDiameter = angularDiameter(rithRadius*2.0d, exesPosition.sub(rithPosition).len()-1.0d);
            avgRithDiameter = ((samples / (samples+1)) * avgRithDiameter + (1 / (samples+1.0d)) * thisRithDiameter);
            double thisSunDiameter = angularDiameter(sunRadius*2.0d, exesPosition.len());
            avgSunDiameter = ((samples / (samples+1)) * avgSunDiameter + (1.0d / (samples+1.0d)) * thisSunDiameter);
            samples++;
        }
        avgRithDiameter = angularDiameter(rithRadius*2.0d, 10.47d);
        System.out.println("Average angular diameter of Sun in zenith: " + avgSunDiameter);
        System.out.println("Average angular diameter of Rith in zenith: " + avgRithDiameter);*/

        timing = System.currentTimeMillis() - timing;
        System.out.println("Done in: " + (timing / 1000.0d) + " seconds.");
    }

    public static double angularDiameter(double diameter, double distance) {
        return 2.0d * Math.asin(diameter / (2.0d * distance)) * (360.0d / (2.0d * Math.PI));
    }
}
