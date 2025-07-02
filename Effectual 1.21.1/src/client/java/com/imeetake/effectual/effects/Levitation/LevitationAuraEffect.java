package com.imeetake.effectual.effects.Levitation;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class LevitationAuraEffect {

    private static final Random RANDOM = Random.create();
    private static final Map<Integer, Integer> tickCounters = new HashMap<>();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.levitationAura() || client.world == null || client.isPaused()) return;

            for (PlayerEntity player : client.world.getPlayers()) {
                int id = player.getId();
                if (!shouldPlayEffect(player)) {
                    tickCounters.remove(id);
                    continue;
                }

                int tickCounter = tickCounters.getOrDefault(id, 0) + 1;
                if (tickCounter >= 2) {
                    spawnLevitationParticles(player);
                    tickCounter = 0;
                }

                tickCounters.put(id, tickCounter);
            }
        });
    }

    private static boolean shouldPlayEffect(PlayerEntity player) {
        return player.hasStatusEffect(StatusEffects.LEVITATION);
    }

    private static void spawnLevitationParticles(PlayerEntity player) {
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4 + RANDOM.nextDouble() * 0.2;
            double radius = 0.25 + RANDOM.nextDouble() * 0.1;

            double x = px + Math.cos(angle) * radius;
            double z = pz + Math.sin(angle) * radius;
            double y = py + 0.175 + RANDOM.nextDouble() * 0.05;

            TClientParticles.spawn(
                    new TParticleEffectSimple(ModParticles.LEVITATION_AURA),
                    x, y, z,
                    0, 0.07, 0
            );
        }
    }
}
