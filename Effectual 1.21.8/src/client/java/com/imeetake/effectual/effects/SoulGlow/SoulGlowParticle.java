package com.imeetake.effectual.effects.SoulGlow;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;

public class SoulGlowParticle extends SpriteBillboardParticle {
    public SoulGlowParticle(
            ClientWorld world,
            double x, double y, double z,
            double velocityX, double velocityY, double velocityZ
    ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.scale = 0.015F * (1.0F + random.nextFloat() * 0.5F);
        this.maxAge = 40 + this.random.nextInt(20);
        this.collidesWithWorld = false;
        this.gravityStrength = 0.0F;

        float baseBrightness = 0.9f + random.nextFloat() * 0.1f;
        float r = baseBrightness * (0.0f + random.nextFloat() * 0.05f);
        float g = baseBrightness * (0.6f + random.nextFloat() * 0.25f);
        float b = baseBrightness * (0.9f + random.nextFloat() * 0.1f);

        this.setColor(r, g, b);
        this.setAlpha(0.9F);
    }

    @Override
    public void tick() {
        super.tick();


        float lifeRatio = (float) this.age / (float) this.maxAge;
        float fade = Math.min(1.0F, Math.min(lifeRatio * 2.0F, (1.0F - lifeRatio) * 2.0F));
        this.setAlpha(0.8F * fade);


        this.velocityY = -0.002;
        this.velocityX = 0.0;
        this.velocityZ = 0.0;

        this.velocityY = Math.min(velocityY, 0.005);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}