package com.imeetake.effectual.effects.WaterDrip;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;

public class WaterDripParticleFactory implements ParticleFactory<ParticleEffect> {
    private final SpriteProvider sprites;

    public WaterDripParticleFactory(SpriteProvider sprites) {
        this.sprites = sprites;
    }

    @Override
    public Particle createParticle(ParticleEffect parameters, ClientWorld world, double x, double y, double z, double vx, double vy, double vz) {
        PlayerEntity player = world.getClosestPlayer(x, y, z, 1.2, p -> p != null && p.isAlive());
        if (player == null) return null;
        float yaw = player.getYaw(0);
        double ry = Math.toRadians(yaw);
        double ox = x - player.getX();
        double oz = z - player.getZ();
        double lx = ox * Math.cos(ry) + oz * Math.sin(ry);
        double lz = -ox * Math.sin(ry) + oz * Math.cos(ry);
        double ly = y - player.getY();
        return new WaterDripParticle(world, player, lx, ly, lz, this.sprites);
    }
}