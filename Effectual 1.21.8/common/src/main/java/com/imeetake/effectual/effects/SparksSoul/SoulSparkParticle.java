package com.imeetake.effectual.effects.SparksSoul;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

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

        Vec3 cam = camera.getPosition();
        Vec3 center = new Vec3(x - cam.x, y - cam.y, z - cam.z);

        Vec3 v = new Vec3(this.xd, this.yd, this.zd);
        if (v.lengthSqr() < 1.0e-6) v = new Vec3(0, 1, 0);

        Vec3 up = v.normalize();
        double currentRoll = this.roll + (this.age + tickDelta) * this.rotSpeed;

        Vec3 tmp = Math.abs(up.y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 right = up.cross(tmp).normalize();
        right = rotateAroundAxis(right, up, currentRoll);

        float halfW = this.scale * 0.8f;
        float halfH = this.scale * 1.2f;

        Vec3 u = up.scale(halfH);
        Vec3 r = right.scale(halfW);

        Vec3 p1 = center.add(r).add(u);
        Vec3 p2 = center.add(r).subtract(u);
        Vec3 p3 = center.subtract(r).subtract(u);
        Vec3 p4 = center.subtract(r).add(u);

        var sprite = this.spriteSet.get(this.age, this.lifetime);
        float u1 = sprite.getU0();
        float u2 = sprite.getU1();
        float v1 = sprite.getV0();
        float v2 = sprite.getV1();

        int light = 0xF000F0;

        vertex(vc, p1, u2, v1, light);
        vertex(vc, p2, u2, v2, light);
        vertex(vc, p3, u1, v2, light);
        vertex(vc, p4, u1, v1, light);

        vertex(vc, p4, u1, v1, light);
        vertex(vc, p3, u1, v2, light);
        vertex(vc, p2, u2, v2, light);
        vertex(vc, p1, u2, v1, light);
    }

    private void vertex(VertexConsumer vc, Vec3 pos, float u, float v, int light) {
        vc.addVertex((float) pos.x, (float) pos.y, (float) pos.z)
                .setUv(u, v)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(light);
    }

    private Vec3 rotateAroundAxis(Vec3 vec, Vec3 axis, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        Vec3 a = axis.normalize();
        return vec.scale(c)
                .add(a.cross(vec).scale(s))
                .add(a.scale(a.dot(vec) * (1 - c)));
    }

    private float smoothstep(float edge0, float edge1, float x) {
        float t = Mth.clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }
}