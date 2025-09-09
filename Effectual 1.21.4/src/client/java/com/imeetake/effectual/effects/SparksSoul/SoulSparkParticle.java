package com.imeetake.effectual.effects.SparksSoul;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SoulSparkParticle extends TOrientedParticle<TParticleEffectSimple> {
    private final double roll;
    private Vec3d initialVelocity;

    protected double prevPosX, prevPosY, prevPosZ;

    public SoulSparkParticle(ClientWorld world,
                             double x, double y, double z,
                             double velocityX, double velocityY, double velocityZ,
                             SpriteProvider sprites) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, sprites);

        this.scale = 0.015f + random.nextFloat() * 0.007f;
        this.maxAge = 24 + random.nextInt(10);
        this.setAlpha(0.9F);

        float base = 0.9f + random.nextFloat() * 0.1f;
        float r = base * (0.05f + random.nextFloat() * 0.05f);
        float g = base * (0.75f + random.nextFloat() * 0.15f);
        float b = base * (0.95f + random.nextFloat() * 0.05f);
        this.setColor(r, g, b);

        this.collidesWithWorld = false;
        this.gravityStrength = 0.0F;

        this.initialVelocity = new Vec3d(velocityX, velocityY, velocityZ);
        this.roll = random.nextDouble() * Math.PI * 2.0;

        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        super.tick();

        double t = (double) this.age / (double) this.maxAge;

        this.velocityY += 0.010 * (1.0 - t);

        double jitter = 0.0012 * (1.0 - t);
        this.velocityX += (random.nextDouble() - 0.5) * jitter;
        this.velocityZ += (random.nextDouble() - 0.5) * jitter;

        double speed = Math.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
        double max = 0.06;
        if (speed > max) {
            double k = max / speed;
            this.velocityX *= k;
            this.velocityY *= k;
            this.velocityZ *= k;
        }

        double drag = 0.94 - 0.05 * t;
        this.velocityX *= drag;
        this.velocityY *= drag;
        this.velocityZ *= drag;

        float tf = (float) t;
        float fadeIn  = smoothstep(0.00f, 0.20f, tf);
        float fadeOut = 1.0f - smoothstep(0.70f, 1.00f, tf);
        float alpha = 0.9f * fadeIn * fadeOut;
        if (alpha < 0.05f) alpha = 0.05f;
        this.setAlpha(alpha);
    }

    @Override
    public void buildGeometry(VertexConsumer vc, Camera camera, float tickDelta) {
        float x = (float) MathHelper.lerp(tickDelta, this.prevPosX, this.x);
        float y = (float) MathHelper.lerp(tickDelta, this.prevPosY, this.y);
        float z = (float) MathHelper.lerp(tickDelta, this.prevPosZ, this.z);

        Vec3d cam = camera.getPos();
        Vec3d center = new Vec3d(x - cam.x, y - cam.y, z - cam.z);

        Vec3d v = new Vec3d(this.velocityX, this.velocityY, this.velocityZ);
        if (v.lengthSquared() < 1.0e-6) v = this.initialVelocity;
        if (v.lengthSquared() < 1.0e-6) v = new Vec3d(0, 1, 0);
        Vec3d up = v.normalize();

        Vec3d tmp = Math.abs(up.y) > 0.9 ? new Vec3d(1, 0, 0) : new Vec3d(0, 1, 0);
        Vec3d right = up.crossProduct(tmp).normalize();
        right = rotateAroundAxis(right, up, roll);

        float halfW = this.scale * 0.65f;
        float halfH = this.scale * 1.45f;

        Vec3d u = up.multiply(halfH);
        Vec3d r = right.multiply(halfW);

        Vec3d p1 = center.add(r).add(u);
        Vec3d p2 = center.add(r).subtract(u);
        Vec3d p3 = center.subtract(r).subtract(u);
        Vec3d p4 = center.subtract(r).add(u);

        Sprite sprite = this.spriteProvider.getSprite(this.age, this.maxAge);
        float u1 = sprite.getMinU(), u2 = sprite.getMaxU();
        float v1 = sprite.getMinV(), v2 = sprite.getMaxV();
        int light = this.getBrightness(tickDelta);

        vertex(vc, p1, u2, v1, light);
        vertex(vc, p2, u2, v2, light);
        vertex(vc, p3, u1, v2, light);
        vertex(vc, p4, u1, v1, light);

        vertex(vc, p4, u1, v1, light);
        vertex(vc, p3, u1, v2, light);
        vertex(vc, p2, u2, v2, light);
        vertex(vc, p1, u2, v1, light);
    }

    private void vertex(VertexConsumer vc, Vec3d pos, float u, float v, int light) {
        vc.vertex((float) pos.x, (float) pos.y, (float) pos.z)
                .texture(u, v)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(light);
    }

    private Vec3d rotateAroundAxis(Vec3d vec, Vec3d axis, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        Vec3d a = axis.normalize();
        return vec.multiply(c)
                .add(a.crossProduct(vec).multiply(s))
                .add(a.multiply(a.dotProduct(vec) * (1 - c)));
    }

    private float smoothstep(float edge0, float edge1, float x) {
        float t = MathHelper.clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }
}
