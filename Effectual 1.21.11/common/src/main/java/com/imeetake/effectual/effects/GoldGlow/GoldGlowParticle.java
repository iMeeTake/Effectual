package com.imeetake.effectual.effects.GoldGlow;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class GoldGlowParticle extends TOrientedParticle<SimpleParticleType> {
    private final Vec3 home;
    private Vec3 initialVelocity;
    private Vec3 forward;
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

        this.home = new Vec3(x, y, z);
        this.initialVelocity = new Vec3(velocityX, velocityY, velocityZ);
        this.forward = this.initialVelocity.lengthSqr() > 1.0E-6 ? this.initialVelocity.normalize() : new Vec3(0, 1, 0);

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

        Vec3 toHome = this.home.subtract(this.x, this.y, this.z);
        Vec3 toHomeXZ = new Vec3(toHome.x, 0.0, toHome.z);
        double horizDist = Math.sqrt(toHomeXZ.x * toHomeXZ.x + toHomeXZ.z * toHomeXZ.z);
        double leashK = 0.0035;
        if (horizDist > 0.35) leashK = 0.010;
        ax += toHomeXZ.x * leashK;
        az += toHomeXZ.z * leashK;

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

        Vec3 v = new Vec3(this.xd, this.yd, this.zd);
        if (v.lengthSqr() > 1.0E-7) this.forward = v.normalize();

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
        Vec3 cam = camera.position();
        Vec3 center = new Vec3(px - cam.x, py - cam.y, pz - cam.z);

        Vec3 f = this.forward.lengthSqr() > 1.0E-7 ? this.forward : new Vec3(0, 1, 0);
        Vec3 upRef = Math.abs(f.y) > 0.99 ? new Vec3(0, 0, 1) : new Vec3(0, 1, 0);
        Vec3 right = f.cross(upRef).normalize();
        Vec3 up = right.cross(f).normalize();

        float speed = (float) new Vec3(this.xd, this.yd, this.zd).length();
        float len = this.scale * (0.65f + speed * 6.0f);
        float halfW = this.scale * (0.22f + speed * 1.6f);

        Vec3 tail = f.scale(-len * 0.5);
        Vec3 nose = f.scale(len * 0.5);
        Vec3 offRight = right.scale(halfW);
        Vec3 offLeft = right.scale(-halfW);

        Vec3 q1 = center.add(nose).add(offRight);
        Vec3 q2 = center.add(nose).add(offLeft);
        Vec3 q3 = center.add(tail).add(offLeft);
        Vec3 q4 = center.add(tail).add(offRight);

        var sprite = this.spriteSet.get(this.age, this.lifetime);
        float u1 = sprite.getU0();
        float u2 = sprite.getU1();
        float v1 = sprite.getV0();
        float v2 = sprite.getV1();
        int light = this.getLightColor(tickDelta);

        vertex(vc, q1, u2, v1, light);
        vertex(vc, q2, u1, v1, light);
        vertex(vc, q3, u1, v2, light);
        vertex(vc, q4, u2, v2, light);
    }

    private void vertex(VertexConsumer vc, Vec3 pos, float u, float v, int light) {
        vc.addVertex((float) pos.x, (float) pos.y, (float) pos.z)
                .setUv(u, v)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(light);
    }

    private float smoothstep(float edge0, float edge1, float x) {
        float t = Mth.clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }
}