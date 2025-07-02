package com.imeetake.effectual.effects.SpeedAura;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SpeedAuraParticle extends TOrientedParticle<TParticleEffectSimple> {
    private final Vec3d initialVelocity;
    private final boolean isWhite;
    private Vec3d visualDirection;

    public SpeedAuraParticle(ClientWorld world,
                             double x, double y, double z,
                             double velocityX, double velocityY, double velocityZ,
                             SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);

        this.initialVelocity = new Vec3d(velocityX, velocityY, velocityZ);
        this.visualDirection = this.initialVelocity.normalize();
        this.isWhite = random.nextFloat() < 0.3f;

        this.setAlpha(0.9f);

        if (isWhite) {
            this.setColor(1.0f, 1.0f, 1.0f);
        } else {
            this.setColor(0.3f, 0.7f, 1.0f);
        }

        this.scale = 0.05f + random.nextFloat() * 0.03f;
        this.maxAge = 6 + random.nextInt(3);

        this.gravityStrength = -0.02f;
    }

    @Override
    public void tick() {
        super.tick();
        if (initialVelocity.lengthSquared() > 0.0001) {
            Vec3d targetDir = initialVelocity.normalize();
            visualDirection = visualDirection.lerp(targetDir, 0.2);
        }

        float progress = (float) age / maxAge;

        float fade;
        if (progress < 0.4f) {
            fade = 1.0f;
        } else {
            float fadeProgress = (progress - 0.4f) / 0.6f;
            fade = 1.0f - fadeProgress * fadeProgress;
        }

        this.setAlpha(0.9f * fade);

        this.scale = (0.05f + random.nextFloat() * 0.03f) * (1.0f + progress * 0.2f);
    }

    @Override
    public void buildGeometry(VertexConsumer vc, Camera camera, float tickDelta) {
        float px = (float) MathHelper.lerp(tickDelta, prevPosX, x);
        float py = (float) MathHelper.lerp(tickDelta, prevPosY, y);
        float pz = (float) MathHelper.lerp(tickDelta, prevPosZ, z);
        Vec3d camPos = camera.getPos();
        Vec3d center = new Vec3d(px - camPos.x, py - camPos.y, pz - camPos.z);

        Vec3d horizontalDir = new Vec3d(visualDirection.x, 0, visualDirection.z);

        if (horizontalDir.lengthSquared() < 0.0001) {
            horizontalDir = new Vec3d(0, 0, 1);
        } else {
            horizontalDir = horizontalDir.normalize();
        }

        Vec3d forward = horizontalDir.multiply(scale * 6.0);
        Vec3d up = new Vec3d(0, 1, 0).multiply(scale * 0.6);

        Vec3d p1 = center.add(forward.multiply(0.5)).add(up);
        Vec3d p2 = center.add(forward.multiply(0.5)).subtract(up);
        Vec3d p3 = center.subtract(forward.multiply(0.5)).subtract(up);
        Vec3d p4 = center.subtract(forward.multiply(0.5)).add(up);

        Sprite sprite = spriteProvider.getSprite(age, maxAge);
        float u1 = sprite.getMinU(), u2 = sprite.getMaxU();
        float v1 = sprite.getMinV(), v2 = sprite.getMaxV();
        int light = getBrightness(tickDelta);

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
                .color(red, green, blue, alpha)
                .light(light);
    }

}
