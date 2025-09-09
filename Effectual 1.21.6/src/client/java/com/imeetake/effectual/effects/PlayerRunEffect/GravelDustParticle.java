package com.imeetake.effectual.effects.PlayerRunEffect;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;

public class GravelDustParticle extends SpriteBillboardParticle {

    public GravelDustParticle(
            ClientWorld world,
            double x, double y, double z,
            double velocityX, double velocityY, double velocityZ
    ) {
        super(world, x, y, z, velocityX * 2.5, 0.05, velocityZ * 2.5);
        this.scale = 0.02F * (1.0F + random.nextFloat() * 0.5F);
        this.maxAge = 20 + this.random.nextInt(10);
        this.gravityStrength = 0.02F;


        this.setColor(0.33F, 0.31F, 0.33F);
        this.setAlpha(0.8F);
    }

    @Override
    public void tick() {
        super.tick();
        float lifeRatio = (float) this.age / (float) this.maxAge;
        this.setAlpha(0.8F - lifeRatio * 0.8F);

        this.velocityY = Math.min(velocityY, 0.005);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
