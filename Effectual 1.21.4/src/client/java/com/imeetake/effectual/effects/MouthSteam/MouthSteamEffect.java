package com.imeetake.effectual.effects.MouthSteam;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class MouthSteamEffect {

    private static final Random RANDOM = Random.create();
    private static final Map<Integer, Integer> tickCounters = new HashMap<>();
    private static final Map<Integer, MovementState> lastStates = new HashMap<>();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.mouthSteam() || client.world == null || client.isPaused()) return;

            for (PlayerEntity player : client.world.getPlayers()) {
                int id = player.getId();

                if (!shouldPlayEffect(player)) {
                    tickCounters.remove(id);
                    lastStates.remove(id);
                    continue;
                }

                MovementState current = getMovementState(player);
                MovementState previous = lastStates.getOrDefault(id, MovementState.STANDING);
                int tickCounter = tickCounters.getOrDefault(id, 0);
                if (current != previous) tickCounter = 0;
                tickCounter++;

                int frequency = getRandomFrequency(current);
                if (tickCounter >= frequency) {
                    spawn(client, player);
                    tickCounter = 0;
                }

                tickCounters.put(id, tickCounter);
                lastStates.put(id, current);
            }
        });
    }

    private static boolean shouldPlayEffect(PlayerEntity player) {
        return isColdEnough(player)
                && !player.isSubmergedInWater()
                && !player.isSpectator()
                && !player.isCreative();
    }

    private static boolean isColdEnough(PlayerEntity player) {
        return player.getWorld().getBiome(player.getBlockPos()).value().getTemperature() < 0.15F;
    }

    private static void spawn(MinecraftClient client, PlayerEntity player) {
        double x = player.getX();
        double y = player.getEyeY() - 0.1;
        double z = player.getZ();
        double yaw = Math.toRadians(player.getYaw());

        x += -Math.sin(yaw) * 0.3;
        z += Math.cos(yaw) * 0.3;

        TClientParticles.spawn(
                new MouthSteamParticleEffect(ModParticles.MOUTH_STEAM),
                x, y, z,
                0, 0.001, 0
        );
    }

    private static int getRandomFrequency(MovementState state) {
        if (!CONFIG.dynamicBreathSpeed()) return 90 + RANDOM.nextInt(21);
        return (state == MovementState.SPRINTING || state == MovementState.JUMPING)
                ? 30 + RANDOM.nextInt(21)
                : 90 + RANDOM.nextInt(21);
    }

    private static MovementState getMovementState(PlayerEntity p) {
        if (p.isSprinting()) return MovementState.SPRINTING;
        if (!p.isOnGround() && p.getVelocity().y > 0.0) return MovementState.JUMPING;
        if (p.getVelocity().horizontalLengthSquared() > 0.1) return MovementState.WALKING;
        return MovementState.STANDING;
    }

    private enum MovementState {
        STANDING, WALKING, SPRINTING, JUMPING
    }
}
