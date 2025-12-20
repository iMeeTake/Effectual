package com.imeetake.effectual.effects.MouthSteam;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class MouthSteamEffect {

    private static final RandomSource RANDOM = RandomSource.create();
    private static final Map<Integer, Integer> tickCounters = new HashMap<>();
    private static final Map<Integer, MovementState> lastStates = new HashMap<>();

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().mouthSteam || client.level == null || client.isPaused()) return;

            for (Player player : client.level.players()) {
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
                    spawnBreath(player);
                    tickCounter = 0;
                }

                tickCounters.put(id, tickCounter);
                lastStates.put(id, current);
            }
        });
    }

    private static boolean shouldPlayEffect(Player player) {
        return isColdEnough(player)
                && !player.isUnderWater()
                && !player.isSpectator()
                && !player.isCreative();
    }

    private static boolean isColdEnough(Player player) {
        BlockPos pos = player.blockPosition();
        return player.level().getBiome(pos).value().getBaseTemperature() < 0.15F;
    }

    private static void spawnBreath(Player player) {
        int count = 3 + RANDOM.nextInt(3);

        for (int i = 0; i < count; i++) {
            TClientParticles.spawn(
                    ModParticles.MOUTH_STEAM.get(),
                    player.getX(), player.getEyeY(), player.getZ(),
                    0, 0, 0
            );
        }
    }

    private static int getRandomFrequency(MovementState state) {
        if (!EffectualConfig.get().dynamicBreathSpeed) return 90 + RANDOM.nextInt(21);
        return (state == MovementState.SPRINTING || state == MovementState.JUMPING)
                ? 30 + RANDOM.nextInt(21)
                : 90 + RANDOM.nextInt(21);
    }

    private static MovementState getMovementState(Player p) {
        if (p.isSprinting()) return MovementState.SPRINTING;
        if (!p.onGround() && p.getDeltaMovement().y > 0.0) return MovementState.JUMPING;
        if (p.getDeltaMovement().horizontalDistanceSqr() > 0.1) return MovementState.WALKING;
        return MovementState.STANDING;
    }

    private enum MovementState {
        STANDING, WALKING, SPRINTING, JUMPING
    }
}