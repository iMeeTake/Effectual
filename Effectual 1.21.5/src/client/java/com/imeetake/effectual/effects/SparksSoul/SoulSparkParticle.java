package com.imeetake.effectual.effects.SparksSoul;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;

public class SoulSparkParticle extends SpriteBillboardParticle {

    public SoulSparkParticle(
            ClientWorld world,
            double x, double y, double z,
            double velocityX, double velocityY, double velocityZ
    ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.scale = 0.015F * (1.0F + random.nextFloat() * 0.5F);
        this.maxAge = 20 + this.random.nextInt(10);
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

        if (this.velocityY > 0) {
            this.velocityY *= 0.8;
        }



        float lifeRatio = (float) this.age / (float) this.maxAge;
        float fade = Math.min(1.0F, Math.min(lifeRatio * 2.0F, (1.0F - lifeRatio) * 2.0F));
        this.setAlpha(0.8F * fade);


        this.velocityX *= 0.8;
        this.velocityZ *= 0.8;

    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}