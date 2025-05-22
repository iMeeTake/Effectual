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
    private static final Map<PlayerEntity, Integer> tickers = new HashMap<>();
    private static final Map<PlayerEntity, MovementState> lastStates = new HashMap<>();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.mouthSteam()) return;
            if (client.world == null) return;
            if (client.isPaused()) return;


            for (PlayerEntity player : client.world.getPlayers()) {
                if (shouldPlayEffect(player)) {
                    MovementState newState = getMovementState(player);
                    MovementState lastState = lastStates.getOrDefault(player, MovementState.STANDING);
                    int ticker = tickers.getOrDefault(player, 0);

                    if (newState != lastState) {
                        ticker = 0;
                    }

                    ticker++;
                    int frequency = getRandomFrequency(newState);

                    if (ticker >= frequency) {
                        spawnSteamParticle(client, player);
                        ticker = 0;
                    }

                    tickers.put(player, ticker);
                    lastStates.put(player, newState);
                } else {
                    tickers.remove(player);
                    lastStates.remove(player);
                }
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
        var biome = player.getWorld().getBiome(player.getBlockPos()).value();
        return biome.getTemperature() < 0.15F;
    }

    private static void spawnSteamParticle(MinecraftClient client, PlayerEntity player) {
        double x = player.getX();
        double y = player.getEyeY() - 0.1;
        double z = player.getZ();

        float yaw = player.getYaw() * 0.017453292F;
        double offsetForward = 0.3D;
        x += -Math.sin(yaw) * offsetForward;
        z += Math.cos(yaw) * offsetForward;

        TClientParticles.spawn(
                new MouthSteamParticleEffect(ModParticles.MOUTH_STEAM),
                x, y, z,
                0, 0.001, 0
        );
    }

    private static int getRandomFrequency(MovementState state) {
        if (!CONFIG.dynamicBreathSpeed()) {
            return 90 + RANDOM.nextInt(21);
        }

        return (state == MovementState.SPRINTING || state == MovementState.JUMPING)
                ? 30 + RANDOM.nextInt(21)
                : 90 + RANDOM.nextInt(21);
    }

    private static MovementState getMovementState(PlayerEntity player) {
        if (player.isSprinting()) {
            return MovementState.SPRINTING;
        } else if (isJumping(player)) {
            return MovementState.JUMPING;
        } else if (isPlayerMoving(player)) {
            return MovementState.WALKING;
        } else {
            return MovementState.STANDING;
        }
    }

    private static boolean isPlayerMoving(PlayerEntity player) {
        double speedSquared = player.getVelocity().x * player.getVelocity().x
                + player.getVelocity().z * player.getVelocity().z;
        return speedSquared > 0.1;
    }

    private static boolean isJumping(PlayerEntity player) {
        return !player.isOnGround() && player.getVelocity().y > 0.0;
    }

    private enum MovementState {
        STANDING,
        WALKING,
        SPRINTING,
        JUMPING
    }
}
