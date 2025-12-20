package com.imeetake.effectual.effects.MouthSteam;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class MouthSteamParticleFactory implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public MouthSteamParticleFactory(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        Player player = level.getNearestPlayer(x, y, z, 4.0, false);

        if (player == null) {
            return null;
        }

        return new MouthSteamParticle(level, player, spriteSet);
    }
}