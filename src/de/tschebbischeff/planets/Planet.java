package de.tschebbischeff.planets;

/**
 * TODO: MISSING JAVADOC
 *
 * @author Tarek
 * @version 1.0.0
 */
public class Planet {

    private Planet rotationalParent = null;
    private double lastQueryTime = Double.MAX_VALUE;
    private Vector3d lastQueryPosition = new Vector3d(0.0d, 0.0d, 0.0d);
    private double distanceToParent = 0.0d;
    private Matrix3d inclinationMatrix = Matrix3d.identity();
    private double orbitalPeriod = 1.0d;
    private double orbitalOffset = 0.0d;
    private Matrix3d inclinationOffsetMatrix = Matrix3d.identity();

    public Planet() {
        this(null);
    }

    public Planet(Planet rotationalParent) {
        this.rotationalParent = rotationalParent;
    }

    public Planet setDistanceToParent(double distanceToParent) {
        this.distanceToParent = distanceToParent;
        return this;
    }

    public Planet setInclination(double inclination) {
        this.inclinationMatrix = new Matrix3d(
                Math.cos(inclination*(2.0d*Math.PI/360.0d)), 0.0d, -1.0d * Math.sin(inclination*(2.0d*Math.PI/360.0d)),
                0.0d, 1.0d, 0.0d,
                Math.sin(inclination*(2.0d*Math.PI/360.0d)), 0.0d, Math.cos(inclination*(2.0d*Math.PI/360.0d))
        );
        return this;
    }

    public Planet setOrbitalPeriod(double orbitalPeriod) {
        this.orbitalPeriod = orbitalPeriod;
        return this;
    }

    public Planet setOrbitalOffset(double orbitalOffset) {
        this.orbitalOffset = orbitalOffset*(2.0d*Math.PI/360.0d);
        return this;
    }

    public Planet setInclinationOffset(double inclinationOffset) {
        this.inclinationOffsetMatrix = new Matrix3d(
                Math.cos(inclinationOffset*(2.0d*Math.PI/360.0d)), -1.0d * Math.sin(inclinationOffset*(2.0d*Math.PI/360.0d)), 0.0d,
                Math.sin(inclinationOffset*(2.0d*Math.PI/360.0d)), Math.cos(inclinationOffset*(2.0d*Math.PI/360.0d)), 0.0d,
                0.0d, 0.0d, 1.0d
        );
        return this;
    }

    public Vector3d getPosition(double time) {
        if (this.lastQueryTime != time) {
            this.lastQueryTime = time;
            if (this.rotationalParent != null) {
                double angle = (2.0d*Math.PI * (time / this.orbitalPeriod)) + this.orbitalOffset;
                Matrix3d rotationMatrix = new Matrix3d(
                        Math.cos(angle), -1.0d * Math.sin(angle), 0.0d,
                        Math.sin(angle), Math.cos(angle), 0.0d,
                        0.0d, 0.0d, 1.0d
                );
                this.lastQueryPosition = this.rotationalParent.getPosition(time).add(this.inclinationOffsetMatrix.multiply(this.inclinationMatrix.multiply(rotationMatrix.multiply(new Vector3d(1.0d, 0.0d, 0.0d)))).multiply(this.distanceToParent));
            } else {
                this.lastQueryPosition = new Vector3d(0.0d, 0.0d, 0.0d);
            }
        }
        return this.lastQueryPosition;
    }

}
