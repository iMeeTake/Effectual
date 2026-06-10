package com.imeetake.effectual.effects.WaterDrip;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
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
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        Player player = getPlayer(level, dx, x, y, z);
        if (player == null) return null;

        if (isEncodedPlayerId(level, dx)) {
            return new WaterDripParticle(level, player, dy, y - player.getY(), dz, this.spriteSet);
        }

        float yaw = player.getYRot();
        double ry = Math.toRadians(yaw);
        double ox = x - player.getX();
        double oz = z - player.getZ();

        double lx = ox * Math.cos(ry) + oz * Math.sin(ry);
        double lz = -ox * Math.sin(ry) + oz * Math.cos(ry);
        double ly = y - player.getY();

        return new WaterDripParticle(level, player, lx, ly, lz, this.spriteSet);
    }

    private Player getPlayer(ClientLevel level, double encodedId, double x, double y, double z) {
        int id = (int) encodedId;
        if (encodedId == id) {
            Entity entity = level.getEntity(id);
            if (entity instanceof Player player) {
                return player;
            }
        }

        return level.getNearestPlayer(x, y, z, 1.2, false);
    }

    private boolean isEncodedPlayerId(ClientLevel level, double encodedId) {
        int id = (int) encodedId;
        return encodedId == id && level.getEntity(id) instanceof Player;
    }
}
