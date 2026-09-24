package com.imeetake.effectual.effects.WaterDrip;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
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
        if (WaterDripEffect.isDripSuppressed(level)) return null;

        Player encoded = getEncodedPlayer(level, dx);
        Player player = encoded != null ? encoded : level.getNearestPlayer(x, y, z, 1.2, false);
        if (player == null) return null;

        if (encoded != null) {
            return new WaterDripParticle(level, player, dy, y - player.getY(), dz, this.spriteSet.get(random));
        }

        float yaw = player.getYRot();
        double ry = Math.toRadians(yaw);
        double ox = x - player.getX();
        double oz = z - player.getZ();

        double lx = ox * Math.cos(ry) + oz * Math.sin(ry);
        double lz = -ox * Math.sin(ry) + oz * Math.cos(ry);
        double ly = y - player.getY();

        return new WaterDripParticle(level, player, lx, ly, lz, this.spriteSet.get(random));
    }

    @Nullable
    private Player getEncodedPlayer(ClientLevel level, double encodedId) {
        int id = (int) encodedId;
        if (encodedId != id) {
            return null;
        }

        Entity entity = level.getEntity(id);
        return entity instanceof Player player ? player : null;
    }
}
