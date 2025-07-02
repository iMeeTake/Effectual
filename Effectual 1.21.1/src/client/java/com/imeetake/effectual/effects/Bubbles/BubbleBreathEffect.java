package com.imeetake.effectual.effects.Bubbles;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class BubbleBreathEffect {

    private static final Random RANDOM = new Random();
    private static final Map<Integer, Integer> tickCounters = new HashMap<>();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.bubbleBreath() || client.world == null || client.isPaused()) return;

            for (PlayerEntity player : client.world.getPlayers()) {
                int id = player.getId();
                if (!shouldPlayEffect(player)) {
                    tickCounters.remove(id);
                    continue;
                }

                int tickCounter = tickCounters.getOrDefault(id, 0) + 1;
                if (tickCounter >= 40) {
                    spawnBubbleParticles(player);
                    tickCounter = 0;
                }
                tickCounters.put(id, tickCounter);
            }
        });
    }

    private static boolean shouldPlayEffect(PlayerEntity player) {
        return player.isSubmergedInWater()
                && player.getAir() > 0
                && !player.isSpectator()
                && !player.isCreative();
    }

    private static void spawnBubbleParticles(PlayerEntity player) {
        double yawRad = Math.toRadians(player.getYaw());
        double x = player.getX() - Math.sin(yawRad) * 0.3;
        double y = player.getEyeY() - 0.2;
        double z = player.getZ() + Math.cos(yawRad) * 0.3;

        for (int i = 0, count = 2 + RANDOM.nextInt(3); i < count; i++) {
            TClientParticles.spawn(
                    ParticleTypes.BUBBLE,
                    x, y, z,
                    (RANDOM.nextDouble() - 0.5) * 0.02,
                    0.1 + RANDOM.nextDouble() * 0.05,
                    (RANDOM.nextDouble() - 0.5) * 0.02
            );
        }
    }
}
