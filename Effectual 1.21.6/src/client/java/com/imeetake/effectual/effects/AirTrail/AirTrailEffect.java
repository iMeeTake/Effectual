package com.imeetake.effectual.effects.AirTrail;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class AirTrailEffect {
    private static final Map<UUID, Integer> tickers = new HashMap<>();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.airTrail()) return;
            if (client.world == null || client.isPaused()) return;

            for (PlayerEntity player : client.world.getPlayers()) {
                if (player.getPose() != EntityPose.GLIDING) {
                    tickers.remove(player.getUuid());
                    continue;
                }

                UUID id = player.getUuid();
                int ticker = tickers.getOrDefault(id, 0);
                ticker++;

                if (ticker >= 4) {
                    spawnAirTrail(player);
                    ticker = 0;
                }

                tickers.put(id, ticker);
            }
        });
    }

    private static void spawnAirTrail(PlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        double speedSq = velocity.lengthSquared();
        if (speedSq < 0.55) return;

        Vec3d dir = velocity.normalize();
        Random rand = Random.create();

        Vec3d side = new Vec3d(-dir.z, 0, dir.x).normalize();
        Vec3d up = side.crossProduct(dir).normalize();

        double lateralOffset = -1.2 + rand.nextDouble() * 2.4;
        double depth = -0.05 - rand.nextDouble() * 0.1;
        double height = -0.1 + rand.nextDouble() * 0.2;

        Vec3d spawnPos = player.getPos()
                .add(0, player.getStandingEyeHeight() * 0.3 + height, 0)
                .add(dir.multiply(depth))
                .add(side.multiply(lateralOffset));

        Vec3d baseVel = dir.multiply(velocity.length() * 0.2);
        Vec3d particleVel = baseVel.add(
                (rand.nextDouble() - 0.5) * 0.04,
                (rand.nextDouble() - 0.5) * 0.02,
                (rand.nextDouble() - 0.5) * 0.04
        );

        TClientParticles.spawn(
                new TParticleEffectSimple(ModParticles.AIR_TRAIL),
                spawnPos.x, spawnPos.y, spawnPos.z,
                particleVel.x, particleVel.y, particleVel.z
        );
    }
}
