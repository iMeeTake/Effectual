package com.imeetake.effectual.effects.WaterDrip;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class WaterDripParticleFactory implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public WaterDripParticleFactory(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, RandomSource random) {
        Player player = level.getNearestPlayer(x, y, z, 1.2, false);
        if (player == null) return null;

        float yaw = player.getYRot();
        double ry = Math.toRadians(yaw);
        double ox = x - player.getX();
        double oz = z - player.getZ();

        double lx = ox * Math.cos(ry) + oz * Math.sin(ry);
        double lz = -ox * Math.sin(ry) + oz * Math.cos(ry);
        double ly = y - player.getY();

        return new WaterDripParticle(level, player, lx, ly, lz, this.spriteSet.get(random));
    }
}