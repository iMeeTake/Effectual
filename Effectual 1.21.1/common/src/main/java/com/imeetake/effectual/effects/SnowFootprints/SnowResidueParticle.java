package com.imeetake.effectual.effects.SnowFootprints;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class SnowResidueParticle extends TOrientedParticle<SimpleParticleType> {
    private final float rotation;

    public SnowResidueParticle(ClientLevel level,
                               double x, double y, double z,
                               double velocityX, double velocityY, double velocityZ,
                               SpriteSet spriteSet) {
        super(level, x, y, z, 0, 0, 0, spriteSet);

        this.lifetime = 200 + random.nextInt(100);
        this.alpha = 1.0f;

        this.rCol = 0.85f;
        this.gCol = 0.85f;
        this.bCol = 0.85f;

        this.rotation = random.nextFloat() * (float) (Math.PI * 2);

        this.hasPhysics = false;
        this.gravity = 0.0f;

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        this.age++;

        float life = (float) this.age / (float) this.lifetime;
        if (life > 0.4f) {
            float fadeProgress = Math.min((life - 0.4f) / 0.6f, 1.0f);
            this.alpha = 1.0f * (1.0f - fadeProgress);
        }

        if (this.alpha <= 0.01f) {
            this.remove();
        }
    }

    @Override
    public void buildGeometry(VertexConsumer vc, Camera camera, float tickDelta) {
        var cam = camera.getPosition();
        double px = this.x - cam.x;
        double py = this.y - cam.y;
        double pz = this.z - cam.z;

        float cos = Mth.cos(rotation);
        float sin = Mth.sin(rotation);

        float half = 0.5f;

        double x0 = vertexX(-half, -half, cos, sin, px);
        double z0 = vertexZ(-half, -half, cos, sin, pz);
        double x1 = vertexX(-half, half, cos, sin, px);
        double z1 = vertexZ(-half, half, cos, sin, pz);
        double x2 = vertexX(half, half, cos, sin, px);
        double z2 = vertexZ(half, half, cos, sin, pz);
        double x3 = vertexX(half, -half, cos, sin, px);
        double z3 = vertexZ(half, -half, cos, sin, pz);

        float u0 = this.sprite.getU0();
        float u1 = this.sprite.getU1();
        float v0 = this.sprite.getV0();
        float v1 = this.sprite.getV1();

        int light = this.getLightColor(tickDelta);

        vertex(vc, x0, py, z0, u0, v1, light);
        vertex(vc, x1, py, z1, u0, v0, light);
        vertex(vc, x2, py, z2, u1, v0, light);
        vertex(vc, x3, py, z3, u1, v1, light);

        vertex(vc, x3, py, z3, u1, v1, light);
        vertex(vc, x2, py, z2, u1, v0, light);
        vertex(vc, x1, py, z1, u0, v0, light);
        vertex(vc, x0, py, z0, u0, v1, light);
    }

    private double vertexX(float localX, float localZ, float cos, float sin, double px) {
        return px + localX * cos - localZ * sin;
    }

    private double vertexZ(float localX, float localZ, float cos, float sin, double pz) {
        return pz + localX * sin + localZ * cos;
    }

    private void vertex(VertexConsumer vc, double x, double y, double z, float u, float v, int light) {
        vc.addVertex((float) x, (float) y, (float) z)
                .setUv(u, v)
                .setColor(rCol, gCol, bCol, alpha)
                .setLight(light);
    }
}
