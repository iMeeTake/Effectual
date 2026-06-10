package com.imeetake.effectual.effects.MouthSteam;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class MouthSteamEffect {

    private static final RandomSource RANDOM = RandomSource.create();
    private static final Int2IntOpenHashMap tickCounters = new Int2IntOpenHashMap();
    private static final Int2ObjectOpenHashMap<MovementState> lastStates = new Int2ObjectOpenHashMap<>();

    private static int cleanupTimer = 0;
    private static ClientLevel lastLevel = null;

    static {
        tickCounters.defaultReturnValue(0);
    }

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null) {
                tickCounters.clear();
                lastStates.clear();
                lastLevel = null;
                return;
            }

            if (lastLevel != client.level) {
                tickCounters.clear();
                lastStates.clear();
                lastLevel = client.level;
            }

            if (!EffectualConfig.get().mouthSteam || client.isPaused()) return;

            if (++cleanupTimer >= 100) {
                cleanupMaps(client.level.players());
                cleanupTimer = 0;
            }

            for (Player player : client.level.players()) {
                int id = player.getId();

                if (!shouldPlayEffect(player)) {
                    tickCounters.remove(id);
                    lastStates.remove(id);
                    continue;
                }

                MovementState current = getMovementState(player);
                MovementState previous = lastStates.getOrDefault(id, MovementState.STANDING);
                int tickCounter = tickCounters.get(id);
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

    private static void cleanupMaps(List<? extends Player> activePlayers) {
        if (tickCounters.isEmpty()) return;

        IntOpenHashSet activeIds = new IntOpenHashSet(activePlayers.size());
        for (Player player : activePlayers) {
            activeIds.add(player.getId());
        }

        var iterator = tickCounters.keySet().intIterator();
        while (iterator.hasNext()) {
            int id = iterator.nextInt();
            if (!activeIds.contains(id)) {
                iterator.remove();
                lastStates.remove(id);
            }
        }
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
            EffectualClientParticles.spawn(
                    ModParticles.MOUTH_STEAM.get(),
                    player.getX(), player.getEyeY(), player.getZ(),
                    player.getId(), 0, 0
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
