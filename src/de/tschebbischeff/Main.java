package de.tschebbischeff;

import de.tschebbischeff.math.Quat4d;
import de.tschebbischeff.math.Vector3d;
import de.tschebbischeff.model.CelestialBody;
import de.tschebbischeff.model.Orbit;
import de.tschebbischeff.model.Scales;
import de.tschebbischeff.visualizer.GlVisualizer;

import java.awt.*;

public class Main {

    public static void main(String[] args) {
        long timing = System.currentTimeMillis();

        /*
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
        */

        timing = System.currentTimeMillis() - timing;
        System.out.println("===END===");
        System.out.println("Done in: " + (timing / 1000.0d) + " seconds.");
        System.out.println("===STARTING VISUALIZATION===");
        System.out.println("");
        System.out.println("--- General Controls ---");
        System.out.println("Escape: Close");
        System.out.println("Left mouse: Focus next celestial body (star / planet)");
        System.out.println("Right mouse: Focus previous celestial body (star / planet)");
        System.out.println("Middle mouse: Set camera to free move mode");
        System.out.println("Up: Increase simulation speed");
        System.out.println("Down: Decrease simulation speed");
        System.out.println("--- Free camera mode ---");
        System.out.println("W,A,S,D: Move camera");
        System.out.println("Q, E: Roll camera left/ right");
        System.out.println("L. Shift: Move faster");
        System.out.println("L. Control: Move slower");
        System.out.println("--- Fixed camera mode ---");
        System.out.println("Scroll wheel: Increase/ Decrease distance from target");
        System.out.println("L. Shift: In-/Decrease distance faster");

        //==============LIGHT CALCULATION==============\\
        /*new LightCalculator(rith)
                .setResolution(3)
                .addLightEmitter(star)
                .calculateAtTime(0d);*/

        //==============VISUALIZATION==============\\
        /*new GlVisualizer(1000, 1000)
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

                .run();*/

        visualizeSolarSystem();
    }

    public static void visualizeSolarSystem() {
        //Scales.setDistanceScale(Scales.astronomicalUnit() * 10d);

        CelestialBody sun = new CelestialBody()
                .setRadius(Scales.solarRadius())
                .setMass(Scales.solarMass())
                .setRotationalPeriod(24.47 * Scales.day());
        Orbit mercuryOrbit = new Orbit(sun)
                .setEccentricity(0.2056)
                .setInclination(7.005)
                .setSemiMajorAxis(0.387 * Scales.astronomicalUnit());
        Orbit venusOrbit = new Orbit(sun)
                .setEccentricity(0.0068)
                .setInclination(3.3947)
                .setSemiMajorAxis(0.723 * Scales.astronomicalUnit());
        Orbit earthOrbit = new Orbit(sun)
                .setEccentricity(0.0167)
                .setInclination(0.0)
                .setSemiMajorAxis(1.0 * Scales.astronomicalUnit());
        Orbit marsOrbit = new Orbit(sun)
                .setEccentricity(0.0934)
                .setInclination(1.851)
                .setSemiMajorAxis(1.524 * Scales.astronomicalUnit());
        Orbit jupiterOrbit = new Orbit(sun)
                .setEccentricity(0.0484)
                .setInclination(1.305)
                .setSemiMajorAxis(5.203 * Scales.astronomicalUnit());
        Orbit saturnOrbit = new Orbit(sun)
                .setEccentricity(0.0542)
                .setInclination(2.484)
                .setSemiMajorAxis(9.537 * Scales.astronomicalUnit());
        Orbit uranusOrbit = new Orbit(sun)
                .setEccentricity(0.0472)
                .setInclination(0.770)
                .setSemiMajorAxis(19.191 * Scales.astronomicalUnit());
        Orbit neptuneOrbit = new Orbit(sun)
                .setEccentricity(0.0086)
                .setInclination(1.769)
                .setSemiMajorAxis(30.069 * Scales.astronomicalUnit());
        Orbit plutoOrbit = new Orbit(sun)
                .setEccentricity(0.2488)
                .setInclination(17.142)
                .setSemiMajorAxis(39.482 * Scales.astronomicalUnit());
        CelestialBody mercury = new CelestialBody(mercuryOrbit)
                .setMass(0.0553 * Scales.earthMass())
                .setRadius(2439 * Scales.kilometer())
                .setAxisOfRotation(Quat4d.identity().pitch(0.0).rotateVector(Vector3d.Z_AXIS))
                .setRotationalPeriod(58.785 * Scales.day());
        CelestialBody venus = new CelestialBody(venusOrbit)
                .setMass(0.815 * Scales.earthMass())
                .setRadius(6052 * Scales.kilometer())
                .setAxisOfRotation(Quat4d.identity().pitch(177.36).rotateVector(Vector3d.Z_AXIS))
                .setRotationalPeriod(243.686 * Scales.day());
        CelestialBody earth = new CelestialBody(earthOrbit)
                .setMass(1.0 * Scales.earthMass())
                .setRadius(6371 * Scales.kilometer())
                .setAxisOfRotation(Quat4d.identity().pitch(23.45).rotateVector(Vector3d.Z_AXIS))
                .setRotationalPeriod(23.9345 * Scales.hour());
        CelestialBody mars = new CelestialBody(marsOrbit)
                .setMass(0.107 * Scales.earthMass())
                .setRadius(3389 * Scales.kilometer())
                .setAxisOfRotation(Quat4d.identity().pitch(25.19).rotateVector(Vector3d.Z_AXIS))
                .setRotationalPeriod(24.6229 * Scales.hour());
        CelestialBody jupiter = new CelestialBody(jupiterOrbit)
                .setMass(317.83 * Scales.earthMass())
                .setRadius(69911 * Scales.kilometer())
                .setAxisOfRotation(Quat4d.identity().pitch(3.13).rotateVector(Vector3d.Z_AXIS))
                .setRotationalPeriod(9.9250 * Scales.hour());
        CelestialBody saturn = new CelestialBody(saturnOrbit)
                .setMass(95.159 * Scales.earthMass())
                .setRadius(58232 * Scales.kilometer())
                .setAxisOfRotation(Quat4d.identity().pitch(26.73).rotateVector(Vector3d.Z_AXIS))
                .setRotationalPeriod(10.656 * Scales.hour());
        CelestialBody uranus = new CelestialBody(uranusOrbit)
                .setMass(14.536 * Scales.earthMass())
                .setRadius(25362 * Scales.kilometer())
                .setAxisOfRotation(Quat4d.identity().pitch(97.77).rotateVector(Vector3d.Z_AXIS))
                .setRotationalPeriod(17.24 * Scales.hour());
        CelestialBody neptune = new CelestialBody(neptuneOrbit)
                .setMass(17.147 * Scales.earthMass())
                .setRadius(24622 * Scales.kilometer())
                .setAxisOfRotation(Quat4d.identity().pitch(28.32).rotateVector(Vector3d.Z_AXIS))
                .setRotationalPeriod(16.11 * Scales.hour());
        CelestialBody pluto = new CelestialBody(plutoOrbit)
                .setMass(0.0021 * Scales.earthMass())
                .setRadius(1195 * Scales.kilometer())
                .setAxisOfRotation(Quat4d.identity().pitch(122.53).rotateVector(Vector3d.Z_AXIS))
                .setRotationalPeriod(6.405 * Scales.day());

        //Visualizer
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        new GlVisualizer(Math.round(((float) width) * 0.8f), Math.round(((float) height) * 0.8f))
                .setOrbitResolution(10)
                .setCelestialBodyResolution(2)
                .setCameraSpeed(Scales.astronomicalUnit()*0.5d)
                .setCameraTurnSpeed(10.0d)
                .setOrbitColorAlpha(0.5f)
                .setCelestialBodyColorAlpha(1.0f)
                .setVisualizationSpeed(0.0d)
                .setBodyScale(mercuryOrbit.getSemiMajorAxis() * 0.2d)

                .addCelestialBody(sun)
                .addOrbit(mercuryOrbit)
                .addCelestialBody(mercury)
                .addOrbit(venusOrbit)
                .addCelestialBody(venus)
                .addOrbit(earthOrbit)
                .addCelestialBody(earth)
                .addOrbit(marsOrbit)
                .addCelestialBody(mars)
                .addOrbit(jupiterOrbit)
                .addCelestialBody(jupiter)
                .addOrbit(saturnOrbit)
                .addCelestialBody(saturn)
                .addOrbit(uranusOrbit)
                .addCelestialBody(uranus)
                .addOrbit(neptuneOrbit)
                .addCelestialBody(neptune)
                .addOrbit(plutoOrbit)
                .addCelestialBody(pluto)

                .run();
    }

    public static double angularDiameter(double diameter, double distance) {
        return 2.0d * Math.asin(diameter / (2.0d * distance)) * (360.0d / (2.0d * Math.PI));
    }
}
