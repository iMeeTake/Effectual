package com.imeetake.effectual.effects.MetalSparks;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;

public class MetalSparkParticle extends TextureSheetParticle {

    private final float rotSpeed;
    private final float baseSize;

    public MetalSparkParticle(
            ClientLevel level,
            double x, double y, double z,
            double velocityX, double velocityY, double velocityZ
    ) {
        super(level, x, y, z, velocityX, velocityY, velocityZ);

        this.baseSize = 0.015F * (1.0F + random.nextFloat() * 0.5F);
        this.quadSize = this.baseSize;

        this.lifetime = 20 + this.random.nextInt(10);
        this.hasPhysics = true;
        this.gravity = 1.0F;

        this.xd = velocityX;
        this.yd = velocityY + 0.1;
        this.zd = velocityZ;

        float baseBrightness = 0.9f + random.nextFloat() * 0.1f;
        float r = baseBrightness * (0.9f + random.nextFloat() * 0.1f);
        float g = baseBrightness * (0.5f + random.nextFloat() * 0.4f);
        float b = baseBrightness * random.nextFloat() * 0.2f;

        this.setColor(r, g, b);
        this.alpha = 1.0F;

        this.rotSpeed = (this.random.nextFloat() - 0.5F) * 0.3F;
        this.roll = this.random.nextFloat() * (float) (Math.PI * 2);
        this.oRoll = this.roll;
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

        this.yd -= 0.04D * (double) this.gravity;
        this.move(this.xd, this.yd, this.zd);

        this.xd *= 0.9;
        this.yd *= 0.9;
        this.zd *= 0.9;

        if (this.onGround) {
            this.xd *= 0.7;
            this.zd *= 0.7;
        }

        int fadeTime = 5;
        if (this.age > this.lifetime - fadeTime) {
            float fadeProgress = (float) (this.age - (this.lifetime - fadeTime)) / (float) fadeTime;
            this.quadSize = this.baseSize * (1.0F - fadeProgress);
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}