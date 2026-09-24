package com.imeetake.effectual.effects.MouthSteam;

import com.imeetake.effectual.EffectualConfig;
import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class MouthSteamEffect {

    private static final RandomSource RANDOM = RandomSource.create();
    private static final int BURST_DURATION = 12;
    private static final double MODEL_PIXEL = 15.0 / 256.0;
    private static final double MOUTH_FRONT = MODEL_PIXEL * 5.0;
    private static final double MOUTH_BELOW_EYE = MODEL_PIXEL * 2.0;
    private static final double HEAD_RADIUS = MODEL_PIXEL * 4.0;
    private static final double MOUTH_SIDE_OFFSET = 0.04;
    private static final double MOVING_SIDE_ANGLE = Math.toRadians(10.0);
    private static final double WALK_TICK_DISTANCE = 0.216;
    private static final double MAX_INHERITED_SPEED_SQR = 16.0;
    private static final Int2IntOpenHashMap tickCounters = new Int2IntOpenHashMap();
    private static final Int2IntOpenHashMap burstTicks = new Int2IntOpenHashMap();
    private static final Int2ObjectOpenHashMap<BreathAnchor> anchors = new Int2ObjectOpenHashMap<>();

    private static int cleanupTimer = 0;
    private static ClientLevel lastLevel = null;

    static {
        tickCounters.defaultReturnValue(0);
        burstTicks.defaultReturnValue(-1);
    }

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null) {
                tickCounters.clear();
                burstTicks.clear();
                releaseAllAnchors();
                lastLevel = null;
                return;
            }

            if (lastLevel != client.level) {
                tickCounters.clear();
                burstTicks.clear();
                releaseAllAnchors();
                lastLevel = client.level;
            }

            if (!EffectualConfig.get().mouthSteam) {
                burstTicks.clear();
                releaseAllAnchors();
                return;
            }
            if (client.isPaused()) return;

            if (++cleanupTimer >= 100) {
                cleanupMaps(client.level.players());
                cleanupTimer = 0;
            }

            for (Player player : client.level.players()) {
                int id = player.getId();

                if (!shouldPlayEffect(player)) {
                    tickCounters.remove(id);
                    burstTicks.remove(id);
                    releaseAnchor(id);
                    continue;
                }

                BreathAnchor anchor = anchorFor(id);
                trackHead(anchor, player);

                tickBurst(player, id, anchor);

                MovementState current = getMovementState(player, anchor);
                int tickCounter = tickCounters.get(id) + 1;

                int frequency = getRandomFrequency(current);
                if (tickCounter >= frequency) {
                    burstTicks.put(id, 0);
                    anchor.setExhaleSide(RANDOM.nextBoolean() ? 1 : -1);
                    tickCounter = 0;
                }

                tickCounters.put(id, tickCounter);
            }
        });
    }

    private static BreathAnchor anchorFor(int id) {
        BreathAnchor anchor = anchors.get(id);
        if (anchor == null) {
            anchor = new BreathAnchor();
            anchors.put(id, anchor);
        }
        return anchor;
    }

    private static void releaseAnchor(int id) {
        BreathAnchor anchor = anchors.remove(id);
        if (anchor != null) anchor.release();
    }

    private static void releaseAllAnchors() {
        if (anchors.isEmpty()) return;

        for (BreathAnchor anchor : anchors.values()) {
            anchor.release();
        }
        anchors.clear();
    }

    private static void cleanupMaps(List<? extends Player> activePlayers) {
        if (tickCounters.isEmpty() && burstTicks.isEmpty() && anchors.isEmpty()) return;

        IntOpenHashSet activeIds = new IntOpenHashSet(activePlayers.size());
        for (Player player : activePlayers) {
            activeIds.add(player.getId());
        }

        var iterator = tickCounters.keySet().intIterator();
        while (iterator.hasNext()) {
            int id = iterator.nextInt();
            if (!activeIds.contains(id)) {
                iterator.remove();
                burstTicks.remove(id);
                releaseAnchor(id);
            }
        }

        var burstIterator = burstTicks.keySet().intIterator();
        while (burstIterator.hasNext()) {
            if (!activeIds.contains(burstIterator.nextInt())) {
                burstIterator.remove();
            }
        }

        var anchorIterator = anchors.keySet().intIterator();
        while (anchorIterator.hasNext()) {
            int anchorId = anchorIterator.nextInt();
            if (!activeIds.contains(anchorId)) {
                anchors.get(anchorId).release();
                anchorIterator.remove();
            }
        }
    }

    private static boolean shouldPlayEffect(Player player) {
        return player.isAlive()
                && !player.isSleeping()
                && !player.isUnderWater()
                && !player.isSpectator()
                && !player.isCreative()
                && isColdEnough(player);
    }

    private static boolean isColdEnough(Player player) {
        BlockPos pos = player.blockPosition();
        return player.level().getBiome(pos).value().getBaseTemperature() < 0.15F;
    }

    private static void trackHead(BreathAnchor anchor, Player player) {
        double dx = player.getX() - player.xOld;
        double dy = player.getY() - player.yOld;
        double dz = player.getZ() - player.zOld;

        if (dx * dx + dy * dy + dz * dz > MAX_INHERITED_SPEED_SQR) {
            anchor.release();
            return;
        }

        anchor.set(player.getX(), player.getZ(), HEAD_RADIUS * player.getScale(), dx, dy, dz);
    }

    private static void tickBurst(Player player, int id, BreathAnchor anchor) {
        int burstTick = burstTicks.get(id);
        if (burstTick < 0) return;

        double flowStrength = Math.sin(Math.PI * (burstTick + 1) / (BURST_DURATION + 1));
        spawnBreath(player, flowStrength, anchor);

        if (++burstTick >= BURST_DURATION) {
            burstTicks.remove(id);
        } else {
            burstTicks.put(id, burstTick);
        }
    }

    private static void spawnBreath(Player player, double flowStrength, BreathAnchor anchor) {
        float yaw = (float) Math.toRadians(player.getYRot());
        float pitch = (float) Math.toRadians(player.getXRot());

        double sinYaw = Mth.sin(yaw);
        double cosYaw = Mth.cos(yaw);
        double sinPitch = Mth.sin(pitch);
        double cosPitch = Mth.cos(pitch);

        double lookX = -sinYaw * cosPitch;
        double lookY = -sinPitch;
        double lookZ = cosYaw * cosPitch;
        double rightX = -cosYaw;
        double rightZ = -sinYaw;
        double upX = -sinYaw * sinPitch;
        double upY = cosPitch;
        double upZ = cosYaw * sinPitch;

        int side = anchor.exhaleSide();
        double headSpeed = Math.sqrt(anchor.vx() * anchor.vx() + anchor.vz() * anchor.vz());
        double sideShare = side * Math.min(1.0, headSpeed / WALK_TICK_DISTANCE);
        double sideAngle = sideShare * MOVING_SIDE_ANGLE * (0.75 + RANDOM.nextDouble() * 0.5);
        double jetForward = Math.cos(sideAngle);
        double jetSide = Math.sin(sideAngle);
        double jetX = lookX * jetForward + rightX * jetSide;
        double jetY = lookY * jetForward;
        double jetZ = lookZ * jetForward + rightZ * jetSide;

        double playerScale = player.getScale();
        double mouthForward = MOUTH_FRONT * playerScale;
        double mouthX = player.getX() + lookX * mouthForward;
        double mouthY = player.getEyeY() - MOUTH_BELOW_EYE * playerScale + lookY * mouthForward;
        double mouthZ = player.getZ() + lookZ * mouthForward;

        double lateralOffset = sideShare * MOUTH_SIDE_OFFSET * playerScale + (RANDOM.nextDouble() - 0.5) * 0.012;
        double verticalOffset = (RANDOM.nextDouble() - 0.5) * 0.02;
        double forwardSpeed = 0.024 + flowStrength * 0.016 + RANDOM.nextDouble() * 0.006;
        double lateralSpeed = (RANDOM.nextDouble() - 0.5) * 0.005;
        double verticalSpeed = (RANDOM.nextDouble() - 0.5) * 0.008;
        double lift = 0.001 + RANDOM.nextDouble() * 0.002;

        MouthSteamParticleFactory.spawn(
                mouthX + rightX * lateralOffset + upX * verticalOffset + anchor.vx(),
                mouthY + upY * verticalOffset + anchor.vy(),
                mouthZ + rightZ * lateralOffset + upZ * verticalOffset + anchor.vz(),
                jetX * forwardSpeed + rightX * lateralSpeed + upX * verticalSpeed,
                jetY * forwardSpeed + upY * verticalSpeed + lift,
                jetZ * forwardSpeed + rightZ * lateralSpeed + upZ * verticalSpeed,
                anchor
        );
    }

    private static int getRandomFrequency(MovementState state) {
        if (!EffectualConfig.get().dynamicBreathSpeed) return 90 + RANDOM.nextInt(21);
        return (state == MovementState.SPRINTING || state == MovementState.JUMPING)
                ? 30 + RANDOM.nextInt(21)
                : 90 + RANDOM.nextInt(21);
    }

    private static MovementState getMovementState(Player p, BreathAnchor anchor) {
        if (p.isSprinting()) return MovementState.SPRINTING;
        if (!p.onGround() && anchor.vy() > 0.0) return MovementState.JUMPING;
        if (anchor.vx() * anchor.vx() + anchor.vz() * anchor.vz() > 0.1) return MovementState.WALKING;
        return MovementState.STANDING;
    }

    private enum MovementState {
        STANDING, WALKING, SPRINTING, JUMPING
    }
}
