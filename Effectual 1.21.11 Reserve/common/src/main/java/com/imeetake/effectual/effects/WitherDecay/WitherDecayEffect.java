package com.imeetake.effectual.effects.WitherDecay;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WitherDecayEffect {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Map<UUID, Integer> tickCounters = new HashMap<>();

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().witherDecay || client.level == null || client.isPaused()) return;

            for (Player player : client.level.players()) {
                UUID id = player.getUUID();

                if (!player.hasEffect(MobEffects.WITHER)) {
                    tickCounters.remove(id);
                    continue;
                }

                int count = tickCounters.getOrDefault(id, 0) + 1;
                if (count >= 3) {
                    tickCounters.put(id, 0);
                    spawn(player);
                } else {
                    tickCounters.put(id, count);
                }
            }
        });
    }

    private static void spawn(Player player) {
        double x = player.getX() + (RANDOM.nextDouble() - 0.5) * 1.2;
        double y = player.getY() + 0.3 + RANDOM.nextDouble() * 1.5;
        double z = player.getZ() + (RANDOM.nextDouble() - 0.5) * 1.2;
        double dy = -0.015 - RANDOM.nextDouble() * 0.01;

        TClientParticles.spawn(ParticleTypes.SMOKE, x, y, z, 0, dy, 0);
    }
}