package com.imeetake.effectual.effects.MouthSteam;

final class BreathAnchor {

    static final BreathAnchor NONE = new BreathAnchor();

    private double headX;
    private double headZ;
    private double headRadius;
    private double vx;
    private double vy;
    private double vz;
    private int exhaleSide = 1;

    void set(double headX, double headZ, double headRadius, double vx, double vy, double vz) {
        this.headX = headX;
        this.headZ = headZ;
        this.headRadius = headRadius;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
    }

    void release() {
        this.headRadius = 0.0;
        this.vx = 0.0;
        this.vy = 0.0;
        this.vz = 0.0;
    }

    boolean hasHead() {
        return this.headRadius > 0.0;
    }

    double headX() {
        return this.headX;
    }

    double headZ() {
        return this.headZ;
    }

    double headRadius() {
        return this.headRadius;
    }

    double vx() {
        return this.vx;
    }

    double vy() {
        return this.vy;
    }

    double vz() {
        return this.vz;
    }

    int exhaleSide() {
        return this.exhaleSide;
    }

    void setExhaleSide(int exhaleSide) {
        this.exhaleSide = exhaleSide;
    }
}
