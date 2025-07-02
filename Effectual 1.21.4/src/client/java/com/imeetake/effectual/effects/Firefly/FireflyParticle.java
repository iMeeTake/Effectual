package com.imeetake.effectual.effects.Firefly;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;

public class FireflyParticle extends SpriteBillboardParticle {

    public FireflyParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.scale = 0.1F;
        this.maxAge = 200 + this.random.nextInt(100);
        this.collidesWithWorld = false;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }

    @Override
    public void tick() {
        super.tick();

        this.alpha = 0.5F + 0.5F * (float) Math.sin(this.age * 0.2);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
