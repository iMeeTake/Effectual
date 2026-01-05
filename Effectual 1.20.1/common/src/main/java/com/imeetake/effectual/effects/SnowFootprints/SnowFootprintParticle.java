package com.imeetake.effectual.effects.SnowFootprints;

import com.imeetake.tlib.client.particle.TOrientedParticle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SnowFootprintParticle extends TOrientedParticle<SimpleParticleType> {
    private final float rotation;
    private final boolean mirrored;

    public SnowFootprintParticle(ClientLevel level,
                                 double x, double y, double z,
                                 double velocityX, double velocityY, double velocityZ,
                                 SpriteSet spriteSet) {
        super(level, x, y, z, 0, 0, 0, spriteSet);

        this.lifetime = 200 + random.nextInt(100);
        this.alpha = 0.3f;

        this.rCol = 0.85f;
        this.gCol = 0.85f;
        this.bCol = 0.85f;

        this.rotation = (float) velocityX;
        this.mirrored = velocityY > 0.5;

        this.hasPhysics = false;
        this.gravity = 0.0f;

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        BlockPos below = BlockPos.containing(this.x, this.y - 0.1, this.z);
        if (this.level.getBlockState(below).isAir()) {
            this.remove();
            return;
        }

        float life = (float) this.age / (float) this.lifetime;
        if (life > 0.4f) {
            float fadeProgress = (life - 0.4f) / 0.6f;
            this.alpha = 0.3f * (1.0f - fadeProgress);

            if (this.alpha < 0.01f) {
                this.remove();
            }
        }
    }

    @Override
    public void buildGeometry(VertexConsumer vc, Camera camera, float tickDelta) {
        Vec3 cam = camera.getPosition();
        double px = this.x - cam.x;
        double py = this.y - cam.y;
        double pz = this.z - cam.z;

        float cos = Mth.cos(rotation);
        float sin = Mth.sin(rotation);

        float half = 0.5f;

        float[][] local = {
                {-half, -half},
                {-half, half},
                {half, half},
                {half, -half}
        };

        Vec3[] verts = new Vec3[4];
        for (int i = 0; i < 4; i++) {
            float lx = mirrored ? -local[i][0] : local[i][0];
            float lz = local[i][1];

            float rx = lx * cos - lz * sin;
            float rz = lx * sin + lz * cos;

            verts[i] = new Vec3(px + rx, py, pz + rz);
        }

        float u0 = this.sprite.getU0();
        float u1 = this.sprite.getU1();
        float v0 = this.sprite.getV0();
        float v1 = this.sprite.getV1();

        if (mirrored) {
            float tmp = u0;
            u0 = u1;
            u1 = tmp;
        }

        int light = this.getLightColor(tickDelta);

        vc.vertex(verts[0].x, verts[0].y, verts[0].z).uv(u0, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        vc.vertex(verts[1].x, verts[1].y, verts[1].z).uv(u0, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        vc.vertex(verts[2].x, verts[2].y, verts[2].z).uv(u1, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        vc.vertex(verts[3].x, verts[3].y, verts[3].z).uv(u1, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();

        vc.vertex(verts[3].x, verts[3].y, verts[3].z).uv(u1, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        vc.vertex(verts[2].x, verts[2].y, verts[2].z).uv(u1, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        vc.vertex(verts[1].x, verts[1].y, verts[1].z).uv(u0, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        vc.vertex(verts[0].x, verts[0].y, verts[0].z).uv(u0, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
    }
}