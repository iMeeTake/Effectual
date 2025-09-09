package com.imeetake.effectual.effects.SoulGlow;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SoulGlowParticle extends TOrientedParticle<TParticleEffectSimple> {
    private final Vec3d home;
    private Vec3d initialVelocity;
    private Vec3d forward;
    private final double seedA;
    private final double seedB;

    protected double prevPosX, prevPosY, prevPosZ;

    public SoulGlowParticle(ClientWorld world,
                            double x, double y, double z,
                            double velocityX, double velocityY, double velocityZ,
                            SpriteProvider sprites) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, sprites);

        this.scale = 0.028f + random.nextFloat() * 0.010f;
        this.maxAge = 52 + random.nextInt(18);
        this.setAlpha(0.0F);

        float base = 0.9f + random.nextFloat() * 0.1f;
        float r = base * (0.05f + random.nextFloat() * 0.05f);
        float g = base * (0.70f + random.nextFloat() * 0.15f);
        float b = base * (0.95f + random.nextFloat() * 0.05f);
        this.setColor(r, g, b);

        this.collidesWithWorld = false;
        this.gravityStrength = 0.0F;

        this.home = new Vec3d(x, y, z);
        this.initialVelocity = new Vec3d(velocityX, velocityY, velocityZ);
        this.forward = this.initialVelocity.lengthSquared() > 1.0E-6 ? this.initialVelocity.normalize() : new Vec3d(0, 1, 0);

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
        double lateralAmp = 0.00020 + 0.00010 * Math.sin((this.age + this.seedB) * 0.15);
        double ax = lateralAmp * Math.sin(t);
        double az = lateralAmp * Math.cos(t);
        double ay = 0.00065 + 0.00025 * Math.sin((this.age + this.seedB) * 0.22);

        Vec3d toHome = this.home.subtract(this.x, this.y, this.z);
        Vec3d toHomeXZ = new Vec3d(toHome.x, 0.0, toHome.z);
        double horizDist = Math.sqrt(toHomeXZ.x * toHomeXZ.x + toHomeXZ.z * toHomeXZ.z);
        double leashK = 0.0030;
        if (horizDist > 0.35) leashK = 0.009;
        ax += toHomeXZ.x * leashK;
        az += toHomeXZ.z * leashK;

        this.velocityX += ax;
        this.velocityZ += az;
        this.velocityY += ay;

        double maxSpeed = 0.010;
        double vx = this.velocityX, vy = this.velocityY, vz = this.velocityZ;
        double speed = Math.sqrt(vx*vx + vy*vy + vz*vz);
        if (speed > maxSpeed) {
            double k = maxSpeed / speed;
            this.velocityX *= k;
            this.velocityY *= k;
            this.velocityZ *= k;
        }

        this.velocityX *= 0.986;
        this.velocityY *= 0.986;
        this.velocityZ *= 0.986;

        Vec3d v = new Vec3d(this.velocityX, this.velocityY, this.velocityZ);
        if (v.lengthSquared() > 1.0E-7) this.forward = v.normalize();

        float progress = (float)this.age / (float)this.maxAge;
        float aIn = smoothstep(0.00f, 0.15f, progress);
        float aOut = 1.0f - smoothstep(0.82f, 1.00f, progress);
        this.setAlpha(0.80f * aIn * aOut);

        super.tick();
    }

    @Override
    public void buildGeometry(VertexConsumer vc, Camera camera, float tickDelta) {
        float px = (float) MathHelper.lerp(tickDelta, this.prevPosX, this.x);
        float py = (float) MathHelper.lerp(tickDelta, this.prevPosY, this.y);
        float pz = (float) MathHelper.lerp(tickDelta, this.prevPosZ, this.z);
        Vec3d cam = camera.getPos();
        Vec3d center = new Vec3d(px - cam.x, py - cam.y, pz - cam.z);

        Vec3d f = this.forward.lengthSquared() > 1.0E-7 ? this.forward : new Vec3d(0,1,0);
        Vec3d upRef = Math.abs(f.y) > 0.99 ? new Vec3d(0,0,1) : new Vec3d(0,1,0);
        Vec3d right = f.crossProduct(upRef).normalize();
        Vec3d up = right.crossProduct(f).normalize();

        float speed = (float)new Vec3d(this.velocityX, this.velocityY, this.velocityZ).length();
        float len = this.scale * (0.65f + speed * 5.5f);
        float halfW = this.scale * (0.22f + speed * 1.5f);

        Vec3d tail = f.multiply(-len * 0.5);
        Vec3d nose = f.multiply(len * 0.5);
        Vec3d offRight = right.multiply(halfW);
        Vec3d offLeft  = right.multiply(-halfW);

        Vec3d q1 = center.add(nose).add(offRight);
        Vec3d q2 = center.add(nose).add(offLeft);
        Vec3d q3 = center.add(tail).add(offLeft);
        Vec3d q4 = center.add(tail).add(offRight);

        Sprite sprite = this.spriteProvider.getSprite(this.age, this.maxAge);
        float u1 = sprite.getMinU(), u2 = sprite.getMaxU();
        float v1 = sprite.getMinV(), v2 = sprite.getMaxV();
        int light = this.getBrightness(tickDelta);

        vertex(vc, q1, u2, v1, light);
        vertex(vc, q2, u1, v1, light);
        vertex(vc, q3, u1, v2, light);
        vertex(vc, q4, u2, v2, light);
    }

    private void vertex(VertexConsumer vc, Vec3d pos, float u, float v, int light) {
        vc.vertex((float) pos.x, (float) pos.y, (float) pos.z)
                .texture(u, v)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(light);
    }

    private float smoothstep(float edge0, float edge1, float x) {
        float t = MathHelper.clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }
}
