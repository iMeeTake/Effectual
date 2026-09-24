package com.imeetake.effectual.effects.MouthSteam;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class MouthSteamParticle extends TextureSheetParticle {
    private static final double CARRIED_VELOCITY_RETENTION = 0.5;
    private static final double PUFF_CLEARANCE = 0.7;
    private static final double BOUNDARY_LAYER_WIND = 0.25;
    private static final double BOUNDARY_LAYER_DEPTH = 0.2;

    private final SpriteSet spriteSet;
    private final BreathAnchor anchor;
    private final float baseSize;
    private final float endSize;
    private final float maxAlpha;
    private final float rotSpeed;
    private final double driftX;
    private final double driftZ;
    private double carriedX;
    private double carriedY;
    private double carriedZ;

    public MouthSteamParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double velocityX,
            double velocityY,
            double velocityZ,
            SpriteSet spriteSet,
            BreathAnchor anchor
    ) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.anchor = anchor;

        this.xo = x - anchor.vx();
        this.yo = y - anchor.vy();
        this.zo = z - anchor.vz();
        this.carriedX = anchor.vx();
        this.carriedY = anchor.vy();
        this.carriedZ = anchor.vz();

        this.baseSize = 0.0525F + this.random.nextFloat() * 0.03F;
        this.endSize = this.baseSize * (2.4F + this.random.nextFloat() * 0.5F);
        this.quadSize = this.baseSize;
        this.lifetime = 22 + this.random.nextInt(9);

        this.xd = velocityX;
        this.yd = velocityY;
        this.zd = velocityZ;
        this.driftX = (this.random.nextDouble() - 0.5) * 0.0005;
        this.driftZ = (this.random.nextDouble() - 0.5) * 0.0005;

        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.maxAlpha = 0.32F + this.random.nextFloat() * 0.08F;
        this.alpha = this.maxAlpha * 0.25F;

        this.rotSpeed = (this.random.nextFloat() - 0.5F) * 0.02F;
        this.roll = this.random.nextFloat() * (float) (Math.PI * 2);
        this.oRoll = this.roll;

        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.oRoll = this.roll;
        this.roll += this.rotSpeed;

        this.xd = this.xd * 0.94 + this.driftX;
        this.yd = this.yd * 0.94 + 0.001;
        this.zd = this.zd * 0.94 + this.driftZ;

        this.followAirAroundHead();

        double nextX = this.x + this.xd + this.carriedX;
        double nextZ = this.z + this.zd + this.carriedZ;
        if (this.anchor.hasHead()) {
            double clearance = this.clearanceRadius();
            double headX = this.anchor.headX() + this.anchor.vx();
            double headZ = this.anchor.headZ() + this.anchor.vz();
            double offsetX = nextX - headX;
            double offsetZ = nextZ - headZ;
            double distanceSqr = offsetX * offsetX + offsetZ * offsetZ;
            if (distanceSqr < clearance * clearance && distanceSqr > 1.0E-8) {
                double push = clearance / Math.sqrt(distanceSqr);
                nextX = headX + offsetX * push;
                nextZ = headZ + offsetZ * push;
            }
        }
        this.move(nextX - this.x, this.yd + this.carriedY, nextZ - this.z);

        float lifeRatio = (float) this.age / (float) this.lifetime;
        float growth = 1.0F - (1.0F - lifeRatio) * (1.0F - lifeRatio);
        this.quadSize = this.baseSize + (this.endSize - this.baseSize) * growth;

        float fadeIn = Math.min(1.0F, (this.age + 1.0F) / 3.0F);
        float fadeOut = lifeRatio < 0.35F
                ? 1.0F
                : Math.max(0.0F, 1.0F - (lifeRatio - 0.35F) / 0.65F);
        this.alpha = this.maxAlpha * fadeIn * fadeOut;

        this.setSpriteFromAge(this.spriteSet);
    }

    private void followAirAroundHead() {
        double airX = 0.0;
        double airY = 0.0;
        double airZ = 0.0;

        if (this.anchor.hasHead()) {
            double headVx = this.anchor.vx();
            double headVz = this.anchor.vz();
            double clearance = this.clearanceRadius();
            double offsetX = this.x - this.anchor.headX();
            double offsetZ = this.z - this.anchor.headZ();
            double distance = Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);

            airX = headVx;
            airZ = headVz;
            if (distance > 1.0E-4) {
                double normalX = offsetX / distance;
                double normalZ = offsetZ / distance;
                distance = Math.max(distance, clearance);

                double depth = Math.min(1.0, (distance - clearance) / BOUNDARY_LAYER_DEPTH);
                double windScale = BOUNDARY_LAYER_WIND + (1.0 - BOUNDARY_LAYER_WIND) * depth;
                double windX = -headVx * windScale;
                double windZ = -headVz * windScale;
                double inward = windX * normalX + windZ * normalZ;
                if (inward < 0.0) {
                    windX -= (1.0 - depth) * normalX * inward;
                    windZ -= (1.0 - depth) * normalZ * inward;
                }
                airX = headVx + windX;
                airZ = headVz + windZ;
                airY = this.anchor.vy() * (clearance * clearance) / (distance * distance);
            }
        }

        this.carriedX = airX + (this.carriedX - airX) * CARRIED_VELOCITY_RETENTION;
        this.carriedY = airY + (this.carriedY - airY) * CARRIED_VELOCITY_RETENTION;
        this.carriedZ = airZ + (this.carriedZ - airZ) * CARRIED_VELOCITY_RETENTION;
    }

    private double clearanceRadius() {
        return this.anchor.headRadius() + this.quadSize * PUFF_CLEARANCE;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
