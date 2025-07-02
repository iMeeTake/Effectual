package com.imeetake.effectual.effects.AirTrail;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import com.imeetake.tlib.client.render.TClientRenderUtils;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AirTrailParticle extends TOrientedParticle<TParticleEffectSimple> {
    private Vec3d initialVelocity;

    public AirTrailParticle(ClientWorld world,
                            double x, double y, double z,
                            double velocityX, double velocityY, double velocityZ,
                            SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);

        this.scale = 0.04f + random.nextFloat() * 0.01f;
        this.maxAge = 10 + random.nextInt(5);
        this.setAlpha(0.4f);

        this.setColor(0.9f, 0.95f, 1.0f);

        this.initialVelocity = new Vec3d(velocityX, velocityY, velocityZ);

        this.gravityStrength = 0.01f;
    }

    @Override
    public void tick() {
        super.tick();

        this.velocityX *= 0.98;
        this.velocityY *= 0.98;
        this.velocityZ *= 0.98;

        float ageRatio = (float) this.age / this.maxAge;
        float alphaFactor = 1.0f - (ageRatio * ageRatio);
        this.setAlpha(0.4f * alphaFactor);

        this.scale = (0.15f + random.nextFloat() * 0.1f) * (1.0f + ageRatio * 0.3f);
    }

    @Override
    public void buildGeometry(VertexConsumer vc, Camera camera, float tickDelta) {
        float px = (float) MathHelper.lerp(tickDelta, this.prevPosX, this.x);
        float py = (float) MathHelper.lerp(tickDelta, this.prevPosY, this.y);
        float pz = (float) MathHelper.lerp(tickDelta, this.prevPosZ, this.z);
        Vec3d camPos = camera.getPos();
        Vec3d center = new Vec3d(px - camPos.x, py - camPos.y, pz - camPos.z);

        Vec3d motion = this.initialVelocity;
        if (motion.lengthSquared() < 0.0001) {
            motion = new Vec3d(0, 0, 1);
        }

        Vec3d forward = motion.normalize().multiply(this.scale * 6.0);

        Vec3d cameraLook = TClientRenderUtils.getCameraLookVector().normalize();
        Vec3d up = cameraLook.crossProduct(forward).normalize().multiply(this.scale * 0.25);

        double rotationAngle = Math.PI * 0.05 * (random.nextBoolean() ? 1 : -1);
        Vec3d rotationAxis = forward.normalize();

        Vec3d upRotated = rotateVectorAroundAxis(up, rotationAxis, rotationAngle);

        if (upRotated.lengthSquared() < 0.001) {
            upRotated = rotateVectorAroundAxis(
                    new Vec3d(0, 1, 0).crossProduct(forward).normalize().multiply(this.scale * 0.25),
                    rotationAxis,
                    rotationAngle
            );
        }
        
        Vec3d p1 = center.add(forward.multiply(0.5)).add(upRotated);
        Vec3d p2 = center.add(forward.multiply(0.5)).subtract(upRotated);
        Vec3d p3 = center.subtract(forward.multiply(0.5)).subtract(upRotated);
        Vec3d p4 = center.subtract(forward.multiply(0.5)).add(upRotated);

        Sprite sprite = this.spriteProvider.getSprite(this.age, this.maxAge);
        float u1 = sprite.getMinU(), u2 = sprite.getMaxU();
        float v1 = sprite.getMinV(), v2 = sprite.getMaxV();
        int light = this.getBrightness(tickDelta);

        vertex(vc, p1, u2, v1, light);
        vertex(vc, p2, u2, v2, light);
        vertex(vc, p3, u1, v2, light);
        vertex(vc, p4, u1, v1, light);
    }

    private void vertex(VertexConsumer vc, Vec3d pos, float u, float v, int light) {
        vc.vertex((float) pos.x, (float) pos.y, (float) pos.z)
                .texture(u, v)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(light);
    }

    private Vec3d rotateVectorAroundAxis(Vec3d vector, Vec3d axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        Vec3d rotated = vector.multiply(cos)
                .add(axis.crossProduct(vector).multiply(sin))
                .add(axis.multiply(axis.dotProduct(vector) * (1 - cos)));

        return rotated;
    }
}