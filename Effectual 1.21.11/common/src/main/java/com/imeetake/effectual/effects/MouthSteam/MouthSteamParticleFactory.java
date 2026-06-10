package com.imeetake.effectual.effects.MouthSteam;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class MouthSteamParticleFactory implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public MouthSteamParticleFactory(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, RandomSource random) {
        Player player = getPlayer(level, dx, x, y, z);

        if (player == null) {
            return null;
        }

        return new MouthSteamParticle(level, player, spriteSet, spriteSet.get(random));
    }

    private Player getPlayer(ClientLevel level, double encodedId, double x, double y, double z) {
        int id = (int) encodedId;
        if (encodedId == id) {
            Entity entity = level.getEntity(id);
            if (entity instanceof Player player) {
                return player;
            }
        }

        return level.getNearestPlayer(x, y, z, 4.0, false);
    }
}
