package com.imeetake.effectual.effects.Bubbles;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class BubbleBreathEffect {

    private static final Random RANDOM = Random.create();
    private static final Map<UUID, Integer> tickCounters = new HashMap<>();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.bubbleBreath()) return;
            if (client.world == null) return;
            if (client.isPaused()) return;

            for (PlayerEntity player : client.world.getPlayers()) {
                UUID uuid = player.getUuid();
                int counter = tickCounters.getOrDefault(uuid, 0);

                if (shouldPlayEffect(player)) {
                    counter++;
                    if (counter >= 40) {
                        spawnBubbleParticles(client, player);
                        counter = 0;
                    }
                } else {
                    counter = 0;
                }

                if (counter != 0) {
                    tickCounters.put(uuid, counter);
                } else {
                    tickCounters.remove(uuid);
                }
            }
        });
    }


    private static boolean shouldPlayEffect(PlayerEntity player) {
        return player.isSubmergedInWater()
                && player.getAir() > 0
                && !player.isSpectator()
                && !player.isCreative();
    }

    private static void spawnBubbleParticles(MinecraftClient client, PlayerEntity player) {
        double x = player.getX();
        double y = player.getEyeY() - 0.2;
        double z = player.getZ();

        float yaw = player.getYaw() * MathHelper.RADIANS_PER_DEGREE;
        double offsetForward = 0.3D;
        x += -Math.sin(yaw) * offsetForward;
        z += Math.cos(yaw) * offsetForward;

        int count = 2 + RANDOM.nextInt(3);
        for (int i = 0; i < count; i++) {
            double velocityX = (RANDOM.nextDouble() - 0.5) * 0.02;
            double velocityY = 0.1 + RANDOM.nextDouble() * 0.05;
            double velocityZ = (RANDOM.nextDouble() - 0.5) * 0.02;

            TClientParticles.spawn(
                    ParticleTypes.BUBBLE,
                    x, y, z,
                    velocityX, velocityY, velocityZ
            );
        }
    }
}
