package com.imeetake.effectual.effects.AirTrail;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.imeetake.tlib.client.render.TClientRenderUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class AirTrailParticle extends TOrientedParticle<SimpleParticleType> {
    private final Vec3 initialVelocity;

    public AirTrailParticle(ClientLevel level,
                            double x, double y, double z,
                            double velocityX, double velocityY, double velocityZ,
                            SpriteSet spriteSet) {
        super(level, x, y, z, velocityX, velocityY, velocityZ, spriteSet);

        this.scale = 0.04f + random.nextFloat() * 0.01f;
        this.lifetime = 10 + random.nextInt(5);
        this.alpha = 0.4f;

        this.setColor(0.9f, 0.95f, 1.0f);

        this.initialVelocity = new Vec3(velocityX, velocityY, velocityZ);
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

        Vec3 camPos = camera.getPosition();
        Vec3 center = new Vec3(px - camPos.x, py - camPos.y, pz - camPos.z);

        Vec3 motion = this.initialVelocity;
        if (motion.lengthSqr() < 0.0001) {
            motion = new Vec3(0, 0, 1);
        }

        Vec3 forward = motion.normalize().scale(this.scale * 6.0);
        Vec3 cameraLook = TClientRenderUtils.getCameraLookVector().normalize();

        Vec3 up = cameraLook.cross(forward).normalize().scale(this.scale * 0.25);

        double rotationAngle = Math.PI * 0.05 * (random.nextBoolean() ? 1 : -1);
        Vec3 rotationAxis = forward.normalize();

        Vec3 upRotated = rotateVectorAroundAxis(up, rotationAxis, rotationAngle);

        if (upRotated.lengthSqr() < 0.001) {
            upRotated = rotateVectorAroundAxis(
                    new Vec3(0, 1, 0).cross(forward).normalize().scale(this.scale * 0.25),
                    rotationAxis,
                    rotationAngle
            );
        }

        Vec3 p1 = center.add(forward.scale(0.5)).add(upRotated);
        Vec3 p2 = center.add(forward.scale(0.5)).subtract(upRotated);
        Vec3 p3 = center.subtract(forward.scale(0.5)).subtract(upRotated);
        Vec3 p4 = center.subtract(forward.scale(0.5)).add(upRotated);

        var sprite = this.spriteSet.get(this.age, this.lifetime);
        float u1 = sprite.getU0();
        float u2 = sprite.getU1();
        float v1 = sprite.getV0();
        float v2 = sprite.getV1();
        int light = this.getLightColor(tickDelta);

        vertex(vc, p1, u2, v1, light);
        vertex(vc, p2, u2, v2, light);
        vertex(vc, p3, u1, v2, light);
        vertex(vc, p4, u1, v1, light);
    }

    private void vertex(VertexConsumer vc, Vec3 pos, float u, float v, int light) {
        vc.vertex(pos.x, pos.y, pos.z)
                .uv(u, v)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(light)
                .endVertex();
    }

    private Vec3 rotateVectorAroundAxis(Vec3 vector, Vec3 axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        return vector.scale(cos)
                .add(axis.cross(vector).scale(sin))
                .add(axis.scale(axis.dot(vector) * (1 - cos)));
    }
}