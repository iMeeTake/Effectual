package com.imeetake.effectual.effects.WaterDrip;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;

public class WaterDripParticleFactory implements ParticleFactory<ParticleEffect> {
    private final SpriteProvider spriteProvider;

    public WaterDripParticleFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(ParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        PlayerEntity player = world.getClosestPlayer(x, y, z, 2.0, null);

        if (player == null) {
            return null;
        }

        double offsetX = x - player.getX();
        double offsetY = y - player.getY();
        double offsetZ = z - player.getZ();

        WaterDripParticle particle = new WaterDripParticle(world, player, offsetX, offsetY, offsetZ, this.spriteProvider);
        return particle;
    }
}
