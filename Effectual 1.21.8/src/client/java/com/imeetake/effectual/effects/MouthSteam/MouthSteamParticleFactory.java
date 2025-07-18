package com.imeetake.effectual.effects.MouthSteam;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;

public class MouthSteamParticleFactory implements ParticleFactory<ParticleEffect>  {
    private final SpriteProvider spriteProvider;

    public MouthSteamParticleFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(ParticleEffect effect, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        PlayerEntity player = world.getClosestPlayer(x, y, z, 2.0, null);
        if (player == null) return null;

        return new MouthSteamParticle(world, player, spriteProvider);
    }
}