package com.imeetake.effectual.effects.Sparks;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class SoulSparkParticle extends TOrientedParticle<SimpleParticleType> {
    private final double roll;
    private final float rotSpeed;

    private final double driftAngle;
    private final double driftSpeed;
    private final double riseAccel;
    private final double turbulenceScale;

    protected double prevPosX, prevPosY, prevPosZ;

    public SoulSparkParticle(ClientLevel level,
                             double x, double y, double z,
                             double velocityX, double velocityY, double velocityZ,
                             SpriteSet spriteSet) {
        super(level, x, y, z, velocityX, velocityY, velocityZ, spriteSet);

        this.scale = (0.02f + random.nextFloat() * 0.01f) / 1.2f;

        this.lifetime = 40 + random.nextInt(20);
        this.alpha = 1.0F;

        float base = 0.9f + random.nextFloat() * 0.1f;
        this.rCol = base * (0.05f + random.nextFloat() * 0.05f);
        this.gCol = base * (0.75f + random.nextFloat() * 0.15f);
        this.bCol = base * (0.95f + random.nextFloat() * 0.05f);

        this.hasPhysics = false;
        this.gravity = 0.0F;

        this.xd = velocityX * 0.2;
        this.yd = velocityY * 0.2;
        this.zd = velocityZ * 0.2;

        this.roll = random.nextDouble() * Math.PI * 2.0;
        this.rotSpeed = (random.nextFloat() - 0.5f) * 0.1f;

        this.driftAngle = random.nextDouble() * Math.PI * 2.0;
        this.driftSpeed = 0.0002 + random.nextDouble() * 0.0003;
        this.riseAccel = 0.0015 + random.nextDouble() * 0.001;
        this.turbulenceScale = 0.3 + random.nextDouble() * 0.4;

        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float life = (float) this.age / (float) this.lifetime;

        double time = this.age * 0.15;
        double turbX = Math.sin(time + driftAngle) * Math.cos(time * 0.7) * turbulenceScale;
        double turbZ = Math.cos(time * 0.9 + driftAngle) * Math.sin(time * 0.6) * turbulenceScale;

        this.xd += Math.cos(driftAngle) * driftSpeed + turbX * 0.0001;
        this.zd += Math.sin(driftAngle) * driftSpeed + turbZ * 0.0001;

        double riseBoost = riseAccel * (1.0 - life * 0.5);
        this.yd += riseBoost;

        this.move(this.xd, this.yd, this.zd);

        double drag = 0.92 - life * 0.03;
        this.xd *= drag;
        this.yd *= drag;
        this.zd *= drag;

        float fadeStart = 0.7f;
        if (life > fadeStart) {
            float fadeProgress = (life - fadeStart) / (1.0f - fadeStart);
            this.alpha = 1.0f - smoothstep(0.0f, 1.0f, fadeProgress);
        } else {
            this.alpha = smoothstep(0.0f, 0.1f, life);
        }
    }

    @Override
    public void buildGeometry(VertexConsumer vc, Camera camera, float tickDelta) {
        float x = (float) Mth.lerp(tickDelta, this.prevPosX, this.x);
        float y = (float) Mth.lerp(tickDelta, this.prevPosY, this.y);
        float z = (float) Mth.lerp(tickDelta, this.prevPosZ, this.z);

        var cam = camera.position();
        double cx = x - cam.x;
        double cy = y - cam.y;
        double cz = z - cam.z;

        double upX = this.xd;
        double upY = this.yd;
        double upZ = this.zd;
        double upLenSqr = upX * upX + upY * upY + upZ * upZ;
        if (upLenSqr < 1.0E-6) {
            upX = 0.0;
            upY = 1.0;
            upZ = 0.0;
            upLenSqr = 1.0;
        }

        double upInvLen = 1.0 / Math.sqrt(upLenSqr);
        upX *= upInvLen;
        upY *= upInvLen;
        upZ *= upInvLen;

        double currentRoll = this.roll + (this.age + tickDelta) * this.rotSpeed;

        double rightX;
        double rightY;
        double rightZ;
        if (Math.abs(upY) > 0.9) {
            rightX = 0.0;
            rightY = upZ;
            rightZ = -upY;
        } else {
            rightX = -upZ;
            rightY = 0.0;
            rightZ = upX;
        }

        double rightInvLen = 1.0 / Math.sqrt(rightX * rightX + rightY * rightY + rightZ * rightZ);
        rightX *= rightInvLen;
        rightY *= rightInvLen;
        rightZ *= rightInvLen;

        double cos = Math.cos(currentRoll);
        double sin = Math.sin(currentRoll);
        double crossX = upY * rightZ - upZ * rightY;
        double crossY = upZ * rightX - upX * rightZ;
        double crossZ = upX * rightY - upY * rightX;
        double dot = upX * rightX + upY * rightY + upZ * rightZ;
        double oneMinusCos = 1.0 - cos;

        double rotatedRightX = rightX * cos + crossX * sin + upX * dot * oneMinusCos;
        double rotatedRightY = rightY * cos + crossY * sin + upY * dot * oneMinusCos;
        double rotatedRightZ = rightZ * cos + crossZ * sin + upZ * dot * oneMinusCos;

        float halfW = this.scale * 0.8f;
        float halfH = this.scale * 1.2f;

        double ux = upX * halfH;
        double uy = upY * halfH;
        double uz = upZ * halfH;
        double rx = rotatedRightX * halfW;
        double ry = rotatedRightY * halfW;
        double rz = rotatedRightZ * halfW;

        var sprite = this.spriteSet.get(this.age, this.lifetime);
        float u1 = sprite.getU0();
        float u2 = sprite.getU1();
        float v1 = sprite.getV0();
        float v2 = sprite.getV1();

        int light = 0xF000F0;

        vertex(vc, cx + rx + ux, cy + ry + uy, cz + rz + uz, u2, v1, light);
        vertex(vc, cx + rx - ux, cy + ry - uy, cz + rz - uz, u2, v2, light);
        vertex(vc, cx - rx - ux, cy - ry - uy, cz - rz - uz, u1, v2, light);
        vertex(vc, cx - rx + ux, cy - ry + uy, cz - rz + uz, u1, v1, light);

        vertex(vc, cx - rx + ux, cy - ry + uy, cz - rz + uz, u1, v1, light);
        vertex(vc, cx - rx - ux, cy - ry - uy, cz - rz - uz, u1, v2, light);
        vertex(vc, cx + rx - ux, cy + ry - uy, cz + rz - uz, u2, v2, light);
        vertex(vc, cx + rx + ux, cy + ry + uy, cz + rz + uz, u2, v1, light);
    }

    private void vertex(VertexConsumer vc, double x, double y, double z, float u, float v, int light) {
        vc.addVertex((float) x, (float) y, (float) z)
                .setUv(u, v)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(light);
    }

    private float smoothstep(float edge0, float edge1, float x) {
        float t = Mth.clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }
}
