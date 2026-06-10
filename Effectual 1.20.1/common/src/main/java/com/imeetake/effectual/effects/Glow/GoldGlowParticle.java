package com.imeetake.effectual.effects.Glow;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class GoldGlowParticle extends TOrientedParticle<SimpleParticleType> {
    private final double homeX;
    private final double homeZ;
    private double forwardX;
    private double forwardY;
    private double forwardZ;
    private final double seedA;
    private final double seedB;

    protected double prevPosX, prevPosY, prevPosZ;

    public GoldGlowParticle(ClientLevel level,
                            double x, double y, double z,
                            double velocityX, double velocityY, double velocityZ,
                            SpriteSet spriteSet) {
        super(level, x, y, z, velocityX, velocityY, velocityZ, spriteSet);

        this.scale = 0.034f + random.nextFloat() * 0.012f;
        this.lifetime = 56 + random.nextInt(20);
        this.alpha = 0.0F;

        float base = 0.9f + random.nextFloat() * 0.1f;
        float r = base;
        float g = base * (0.55f + random.nextFloat() * 0.15f);
        float b = base * (0.10f + random.nextFloat() * 0.06f);
        this.setColor(r, g, b);

        this.hasPhysics = false;
        this.gravity = 0.0F;

        this.homeX = x;
        this.homeZ = z;

        double velocityLenSqr = velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ;
        if (velocityLenSqr > 1.0E-6) {
            double invLen = 1.0 / Math.sqrt(velocityLenSqr);
            this.forwardX = velocityX * invLen;
            this.forwardY = velocityY * invLen;
            this.forwardZ = velocityZ * invLen;
        } else {
            this.forwardX = 0.0;
            this.forwardY = 1.0;
            this.forwardZ = 0.0;
        }

        this.seedA = random.nextDouble() * 1000.0;
        this.seedB = random.nextDouble() * 1000.0;
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        double t = (this.age + this.seedA) * 0.10;
        double lateralAmp = 0.00022 + 0.00010 * Math.sin((this.age + this.seedB) * 0.17);
        double ax = lateralAmp * Math.sin(t);
        double az = lateralAmp * Math.cos(t);
        double ay = 0.00085 + 0.00025 * Math.sin((this.age + this.seedB) * 0.20);

        double toHomeX = this.homeX - this.x;
        double toHomeZ = this.homeZ - this.z;
        double horizDist = Math.sqrt(toHomeX * toHomeX + toHomeZ * toHomeZ);
        double leashK = 0.0035;
        if (horizDist > 0.35) leashK = 0.010;
        ax += toHomeX * leashK;
        az += toHomeZ * leashK;

        this.xd += ax;
        this.zd += az;
        this.yd += ay;

        double maxSpeed = 0.012;
        double vx = this.xd, vy = this.yd, vz = this.zd;
        double speed = Math.sqrt(vx * vx + vy * vy + vz * vz);
        if (speed > maxSpeed) {
            double k = maxSpeed / speed;
            this.xd *= k;
            this.yd *= k;
            this.zd *= k;
        }

        this.xd *= 0.985;
        this.yd *= 0.985;
        this.zd *= 0.985;

        double velocityLenSqr = this.xd * this.xd + this.yd * this.yd + this.zd * this.zd;
        if (velocityLenSqr > 1.0E-7) {
            double invLen = 1.0 / Math.sqrt(velocityLenSqr);
            this.forwardX = this.xd * invLen;
            this.forwardY = this.yd * invLen;
            this.forwardZ = this.zd * invLen;
        }

        float progress = (float) this.age / (float) this.lifetime;
        float aIn = smoothstep(0.00f, 0.15f, progress);
        float aOut = 1.0f - smoothstep(0.82f, 1.00f, progress);
        this.alpha = 0.80f * aIn * aOut;

        super.tick();
    }

    @Override
    public void buildGeometry(VertexConsumer vc, Camera camera, float tickDelta) {
        float px = (float) Mth.lerp(tickDelta, this.prevPosX, this.x);
        float py = (float) Mth.lerp(tickDelta, this.prevPosY, this.y);
        float pz = (float) Mth.lerp(tickDelta, this.prevPosZ, this.z);
        var cam = camera.getPosition();
        double cx = px - cam.x;
        double cy = py - cam.y;
        double cz = pz - cam.z;

        double fx = this.forwardX;
        double fy = this.forwardY;
        double fz = this.forwardZ;
        double fLenSqr = fx * fx + fy * fy + fz * fz;
        if (fLenSqr < 1.0E-7) {
            fx = 0.0;
            fy = 1.0;
            fz = 0.0;
            fLenSqr = 1.0;
        }
        double fInvLen = 1.0 / Math.sqrt(fLenSqr);
        fx *= fInvLen;
        fy *= fInvLen;
        fz *= fInvLen;

        double refX = 0.0;
        double refY = Math.abs(fy) > 0.99 ? 0.0 : 1.0;
        double refZ = Math.abs(fy) > 0.99 ? 1.0 : 0.0;
        double rightX = fy * refZ - fz * refY;
        double rightY = fz * refX - fx * refZ;
        double rightZ = fx * refY - fy * refX;
        double rightInvLen = 1.0 / Math.sqrt(rightX * rightX + rightY * rightY + rightZ * rightZ);
        rightX *= rightInvLen;
        rightY *= rightInvLen;
        rightZ *= rightInvLen;

        float speed = (float) Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
        float len = this.scale * (0.65f + speed * 6.0f);
        float halfW = this.scale * (0.22f + speed * 1.6f);

        double noseX = fx * len * 0.5;
        double noseY = fy * len * 0.5;
        double noseZ = fz * len * 0.5;
        double rightOffsetX = rightX * halfW;
        double rightOffsetY = rightY * halfW;
        double rightOffsetZ = rightZ * halfW;

        var sprite = this.spriteSet.get(this.age, this.lifetime);
        float u1 = sprite.getU0();
        float u2 = sprite.getU1();
        float v1 = sprite.getV0();
        float v2 = sprite.getV1();
        int light = this.getLightColor(tickDelta);

        vertex(vc, cx + noseX + rightOffsetX, cy + noseY + rightOffsetY, cz + noseZ + rightOffsetZ, u2, v1, light);
        vertex(vc, cx + noseX - rightOffsetX, cy + noseY - rightOffsetY, cz + noseZ - rightOffsetZ, u1, v1, light);
        vertex(vc, cx - noseX - rightOffsetX, cy - noseY - rightOffsetY, cz - noseZ - rightOffsetZ, u1, v2, light);
        vertex(vc, cx - noseX + rightOffsetX, cy - noseY + rightOffsetY, cz - noseZ + rightOffsetZ, u2, v2, light);
    }

    private void vertex(VertexConsumer vc, double x, double y, double z, float u, float v, int light) {
        vc.vertex(x, y, z)
                .uv(u, v)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(light)
                .endVertex();
    }

    private float smoothstep(float edge0, float edge1, float x) {
        float t = Mth.clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }
}
