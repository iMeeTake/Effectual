package com.imeetake.effectual.effects.Bubbles;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BubbleBreathEffect {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Map<Integer, Integer> lastAirValues = new HashMap<>();
    private static final Map<Integer, Integer> breathingTimers = new HashMap<>();

    private static int cleanupTimer = 0;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().bubbleBreath || client.level == null || client.isPaused()) return;

            if (++cleanupTimer >= 100) {
                cleanupMap(client.level.players());
                cleanupTimer = 0;
            }

            for (Player player : client.level.players()) {
                int id = player.getId();

                if (shouldPlayEffect(player)) {
                    int currentAir = player.getAirSupply();
                    int previousAir = lastAirValues.getOrDefault(id, currentAir);
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

                int breatheTicks = breathingTimers.getOrDefault(id, 0);
                if (breatheTicks > 0) {
                    processBreathTick(player);
                    breathingTimers.put(id, breatheTicks - 1);
                }
            }
        });
    }

    private static void cleanupMap(java.util.List<? extends Player> activePlayers) {
        Iterator<Integer> iterator = lastAirValues.keySet().iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            if (activePlayers.stream().noneMatch(p -> p.getId() == id)) {
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

            TClientParticles.spawn(
                    ParticleTypes.BUBBLE,
                    originX, originY, originZ,
                    velocityX, velocityY, velocityZ
            );
        }
    }
}