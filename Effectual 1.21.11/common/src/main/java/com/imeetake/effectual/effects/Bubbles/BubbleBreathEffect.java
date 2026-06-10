package com.imeetake.effectual.effects.Bubbles;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BubbleBreathEffect {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Int2IntOpenHashMap lastAirValues = new Int2IntOpenHashMap();
    private static final Int2IntOpenHashMap breathingTimers = new Int2IntOpenHashMap();

    private static int cleanupTimer = 0;
    private static ClientLevel lastLevel = null;

    static {
        lastAirValues.defaultReturnValue(-1);
        breathingTimers.defaultReturnValue(0);
    }

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null) {
                lastAirValues.clear();
                breathingTimers.clear();
                lastLevel = null;
                return;
            }

            if (lastLevel != client.level) {
                lastAirValues.clear();
                breathingTimers.clear();
                lastLevel = client.level;
            }

            if (!EffectualConfig.get().bubbleBreath || client.isPaused()) return;

            if (++cleanupTimer >= 100) {
                cleanupMap(client.level.players());
                cleanupTimer = 0;
            }

            for (Player player : client.level.players()) {
                int id = player.getId();

                if (shouldPlayEffect(player)) {
                    int currentAir = player.getAirSupply();
                    int previousAir = lastAirValues.get(id);
                    if (previousAir == -1) previousAir = currentAir;
                    lastAirValues.put(id, currentAir);

                    boolean airDecreased = currentAir < previousAir;
                    boolean uiBubblePopped = (currentAir % 30 == 0) && currentAir < 300;

                    if (airDecreased && uiBubblePopped) {
                        breathingTimers.put(id, 8);
                    }
                } else {
                    lastAirValues.remove(id);
                    breathingTimers.remove(id);
                }

                int breatheTicks = breathingTimers.get(id);
                if (breatheTicks > 0) {
                    processBreathTick(player);
                    breathingTimers.put(id, breatheTicks - 1);
                }
            }
        });
    }

    private static void cleanupMap(List<? extends Player> activePlayers) {
        if (lastAirValues.isEmpty()) return;

        IntOpenHashSet activeIds = new IntOpenHashSet(activePlayers.size());
        for (Player player : activePlayers) {
            activeIds.add(player.getId());
        }

        var iterator = lastAirValues.keySet().intIterator();
        while (iterator.hasNext()) {
            int id = iterator.nextInt();
            if (!activeIds.contains(id)) {
                iterator.remove();
                breathingTimers.remove(id);
            }
        }
    }

    private static boolean shouldPlayEffect(Player player) {
        return player.isUnderWater()
                && !player.isSpectator()
                && !player.isCreative();
    }

    private static void processBreathTick(Player player) {
        float xRot = player.getXRot();
        float yRot = player.getYRot();

        float xRotRad = xRot * ((float) Math.PI / 180F);
        float yRotRad = -yRot * ((float) Math.PI / 180F);

        double lookX = Mth.sin(yRotRad) * Mth.cos(xRotRad);
        double lookY = -Mth.sin(xRotRad);
        double lookZ = Mth.cos(yRotRad) * Mth.cos(xRotRad);

        double mouthOffsetForward = 0.25;
        double mouthOffsetDown = 0.15;

        double originX = player.getX() + (lookX * mouthOffsetForward);
        double originY = player.getEyeY() - mouthOffsetDown + (lookY * mouthOffsetForward);
        double originZ = player.getZ() + (lookZ * mouthOffsetForward);
        Vec3 playerVel = player.getDeltaMovement();

        if (RANDOM.nextBoolean()) {
            double velocityX = (lookX * 0.1) + (playerVel.x * 0.8);
            double velocityY = (lookY * 0.1) + (playerVel.y * 0.8) + 0.05;
            double velocityZ = (lookZ * 0.1) + (playerVel.z * 0.8);

            EffectualClientParticles.spawn(
                    ParticleTypes.BUBBLE,
                    originX, originY, originZ,
                    velocityX, velocityY, velocityZ
            );
        }
    }
}
