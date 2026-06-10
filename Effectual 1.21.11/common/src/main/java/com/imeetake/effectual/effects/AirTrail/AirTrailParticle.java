package com.imeetake.effectual.effects.AirTrail;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.imeetake.tlib.client.render.TClientRenderUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class AirTrailParticle extends TOrientedParticle<SimpleParticleType> {
    private final double initialVelocityX;
    private final double initialVelocityY;
    private final double initialVelocityZ;
    private final double rotationAngle;

    public AirTrailParticle(ClientLevel level,
                            double x, double y, double z,
                            double velocityX, double velocityY, double velocityZ,
                            SpriteSet spriteSet) {
        super(level, x, y, z, velocityX, velocityY, velocityZ, spriteSet);

        this.scale = 0.04f + random.nextFloat() * 0.01f;
        this.lifetime = 10 + random.nextInt(5);
        this.alpha = 0.4f;

        this.setColor(0.9f, 0.95f, 1.0f);

        this.initialVelocityX = velocityX;
        this.initialVelocityY = velocityY;
        this.initialVelocityZ = velocityZ;
        this.rotationAngle = Math.PI * 0.05 * (random.nextBoolean() ? 1 : -1);
        this.gravity = 0.01f;
    }

    @Override
    public void tick() {
        super.tick();

        this.xd *= 0.98;
        this.yd *= 0.98;
        this.zd *= 0.98;

        float ageRatio = (float) this.age / this.lifetime;
        float alphaFactor = 1.0f - (ageRatio * ageRatio);
        this.alpha = 0.4f * alphaFactor;

        this.scale = (0.15f + random.nextFloat() * 0.1f) * (1.0f + ageRatio * 0.3f);
    }

    @Override
    public void buildGeometry(VertexConsumer vc, Camera camera, float tickDelta) {
        float px = (float) Mth.lerp(tickDelta, this.xo, this.x);
        float py = (float) Mth.lerp(tickDelta, this.yo, this.y);
        float pz = (float) Mth.lerp(tickDelta, this.zo, this.z);

        var camPos = camera.position();
        double cx = px - camPos.x;
        double cy = py - camPos.y;
        double cz = pz - camPos.z;

        double mx = this.initialVelocityX;
        double my = this.initialVelocityY;
        double mz = this.initialVelocityZ;
        double motionLenSqr = mx * mx + my * my + mz * mz;
        if (motionLenSqr < 0.0001) {
            mx = 0.0;
            my = 0.0;
            mz = 1.0;
            motionLenSqr = 1.0;
        }

        double motionInvLen = 1.0 / Math.sqrt(motionLenSqr);
        double axisX = mx * motionInvLen;
        double axisY = my * motionInvLen;
        double axisZ = mz * motionInvLen;

        double forwardScale = this.scale * 6.0;
        double fx = axisX * forwardScale;
        double fy = axisY * forwardScale;
        double fz = axisZ * forwardScale;

        var cameraLook = TClientRenderUtils.getCameraLookVector().normalize();
        double ux = cameraLook.y * fz - cameraLook.z * fy;
        double uy = cameraLook.z * fx - cameraLook.x * fz;
        double uz = cameraLook.x * fy - cameraLook.y * fx;
        double upLenSqr = ux * ux + uy * uy + uz * uz;
        double upScale = this.scale * 0.25;

        if (upLenSqr < 1.0E-6) {
            ux = fz;
            uy = 0.0;
            uz = -fx;
            upLenSqr = ux * ux + uz * uz;
        }

        if (upLenSqr < 1.0E-6) {
            ux = 0.0;
            uy = 0.0;
            uz = upScale;
        } else {
            double upInvLen = upScale / Math.sqrt(upLenSqr);
            ux *= upInvLen;
            uy *= upInvLen;
            uz *= upInvLen;
        }

        double cos = Math.cos(this.rotationAngle);
        double sin = Math.sin(this.rotationAngle);
        double dot = axisX * ux + axisY * uy + axisZ * uz;
        double crossX = axisY * uz - axisZ * uy;
        double crossY = axisZ * ux - axisX * uz;
        double crossZ = axisX * uy - axisY * ux;
        double oneMinusCos = 1.0 - cos;

        double rx = ux * cos + crossX * sin + axisX * dot * oneMinusCos;
        double ry = uy * cos + crossY * sin + axisY * dot * oneMinusCos;
        double rz = uz * cos + crossZ * sin + axisZ * dot * oneMinusCos;

        double hx = fx * 0.5;
        double hy = fy * 0.5;
        double hz = fz * 0.5;

        var sprite = this.spriteSet.get(this.age, this.lifetime);
        float u1 = sprite.getU0();
        float u2 = sprite.getU1();
        float v1 = sprite.getV0();
        float v2 = sprite.getV1();
        int light = this.getLightColor(tickDelta);

        vertex(vc, cx + hx + rx, cy + hy + ry, cz + hz + rz, u2, v1, light);
        vertex(vc, cx + hx - rx, cy + hy - ry, cz + hz - rz, u2, v2, light);
        vertex(vc, cx - hx - rx, cy - hy - ry, cz - hz - rz, u1, v2, light);
        vertex(vc, cx - hx + rx, cy - hy + ry, cz - hz + rz, u1, v1, light);
    }

    private void vertex(VertexConsumer vc, double x, double y, double z, float u, float v, int light) {
        vc.addVertex((float) x, (float) y, (float) z)
                .setUv(u, v)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(light);
    }
}
