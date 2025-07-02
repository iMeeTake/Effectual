package com.imeetake.effectual.effects.Levitation;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;

public class LevitationParticle extends SpriteBillboardParticle {
    public LevitationParticle(ClientWorld world,
                              double x, double y, double z,
                              double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.scale = 0.07F + random.nextFloat() * 0.03F;
        this.maxAge = 20 + random.nextInt(8);
        this.collidesWithWorld = false;
        this.gravityStrength = 0.0F;

        this.setColor(0.6f, 0.1f, 0.85f);
        this.setAlpha(0.8F);

        this.velocityX = 0;
        this.velocityZ = 0;
        this.velocityY = 0.07f;
    }

    @Override
    public void tick() {
        super.tick();


        float flicker = 0.95f + random.nextFloat() * 0.05f;
        setColor(0.6f * flicker, 0.1f * flicker, 0.85f * flicker);

        float lifeRatio = (float) age / (float) maxAge;
        float fade = Math.min(1.0F, Math.min(lifeRatio * 2.0F, (1.0F - lifeRatio) * 2.0F));
        setAlpha(0.8F * fade);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
