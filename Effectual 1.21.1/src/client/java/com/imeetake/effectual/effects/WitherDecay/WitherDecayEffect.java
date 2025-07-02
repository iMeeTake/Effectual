package com.imeetake.effectual.effects.WitherDecay;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class WitherDecayEffect {
    private static final Random RANDOM = Random.create();
    private static final Map<UUID, Integer> tickCounters = new HashMap<>();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.witherDecay() || client.world == null || client.isPaused()) return;

            client.world.getPlayers().forEach(player -> {
                UUID id = player.getUuid();

                if (!player.hasStatusEffect(StatusEffects.WITHER)) {
                    tickCounters.remove(id);
                    return;
                }

                int count = tickCounters.getOrDefault(id, 0) + 1;
                if (count >= 3) {
                    tickCounters.put(id, 0);
                    spawn(player);
                } else {
                    tickCounters.put(id, count);
                }
            });
        });
    }

    private static void spawn(PlayerEntity player) {
        double x = player.getX() + (RANDOM.nextDouble() - 0.5) * 1.2;
        double y = player.getY() + 0.3 + RANDOM.nextDouble() * 1.5;
        double z = player.getZ() + (RANDOM.nextDouble() - 0.5) * 1.2;
        double dy = -0.015 - RANDOM.nextDouble() * 0.01;

        TClientParticles.spawn(ParticleTypes.SMOKE, x, y, z, 0, dy, 0);
    }
}
