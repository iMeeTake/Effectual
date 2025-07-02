package com.imeetake.effectual.effects.AirTrail;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class AirTrailEffect {
    private static final Map<Integer, Integer> tickers = new HashMap<>();
    private static final Random RAND = Random.create();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.airTrail() || client.world == null || client.isPaused()) return;

            for (PlayerEntity player : client.world.getPlayers()) {
                int id = player.getId();
                if (!player.isFallFlying()) {
                    tickers.remove(id);
                    continue;
                }

                int tick = tickers.getOrDefault(id, 0) + 1;

                if (tick >= 4) {
                    spawnAirTrail(player);
                    tick = 0;
                }

                tickers.put(id, tick);
            }
        });
    }

    private static void spawnAirTrail(PlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        if (velocity.lengthSquared() < 0.55) return;

        Vec3d dir = velocity.normalize();
        Vec3d side = new Vec3d(-dir.z, 0, dir.x).normalize();

        double lateral = -1.2 + RAND.nextDouble() * 2.4;
        double depth = -0.05 - RAND.nextDouble() * 0.1;
        double height = -0.1 + RAND.nextDouble() * 0.2;

        Vec3d pos = player.getPos()
                .add(0, player.getStandingEyeHeight() * 0.3 + height, 0)
                .add(dir.multiply(depth))
                .add(side.multiply(lateral));

        Vec3d baseVel = dir.multiply(velocity.length() * 0.2);
        Vec3d finalVel = baseVel.add(
                (RAND.nextDouble() - 0.5) * 0.04,
                (RAND.nextDouble() - 0.5) * 0.02,
                (RAND.nextDouble() - 0.5) * 0.04
        );

        TClientParticles.spawn(
                new TParticleEffectSimple(ModParticles.AIR_TRAIL),
                pos.x, pos.y, pos.z,
                finalVel.x, finalVel.y, finalVel.z
        );
    }
}
