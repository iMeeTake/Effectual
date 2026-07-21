package com.imeetake.effectual.effects.SnowFootprints;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class SnowFootprintEffect {

    private static final Int2ObjectOpenHashMap<FootstepTracker> trackers = new Int2ObjectOpenHashMap<>();

    private static int cleanupTimer = 0;
    private static ClientLevel lastLevel = null;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null) {
                trackers.clear();
                lastLevel = null;
                return;
            }

            if (lastLevel != client.level) {
                trackers.clear();
                lastLevel = client.level;
            }

            if (!EffectualConfig.get().snowFootprints || client.isPaused()) return;

            if (++cleanupTimer >= 100) {
                cleanupTrackers(client.level.players());
                cleanupTimer = 0;
            }

            for (Player player : client.level.players()) {
                if (player.isSpectator() || player.isInvisible()) continue;
                if (!player.onGround()) continue;

                processPlayer(player);
            }
        });
    }

    private static void cleanupTrackers(List<? extends Player> activePlayers) {
        if (trackers.isEmpty()) return;

        IntOpenHashSet activeIds = new IntOpenHashSet(activePlayers.size());
        for (Player player : activePlayers) {
            activeIds.add(player.getId());
        }

        var iterator = trackers.keySet().intIterator();
        while (iterator.hasNext()) {
            int id = iterator.nextInt();
            if (!activeIds.contains(id)) {
                iterator.remove();
            }
        }
    }

    private static void processPlayer(Player player) {
        int id = player.getId();
        FootstepTracker tracker = trackers.get(id);
        if (tracker == null) {
            tracker = new FootstepTracker();
            trackers.put(id, tracker);
        }

        Vec3 pos = player.position();
        double moved = tracker.lastX == Double.MIN_VALUE ? 0 :
                Math.sqrt((pos.x - tracker.lastX) * (pos.x - tracker.lastX) +
                        (pos.y - tracker.lastY) * (pos.y - tracker.lastY) +
                        (pos.z - tracker.lastZ) * (pos.z - tracker.lastZ));

        if (moved > 0.01 && moved < 2.0) {
            tracker.distance += moved;
        }

        tracker.lastX = pos.x;
        tracker.lastY = pos.y;
        tracker.lastZ = pos.z;

        if (tracker.distance >= 0.7) {
            tracker.distance = 0;

            float yawRad = (float) Math.toRadians(player.getYRot());
            boolean isLeft = tracker.leftFoot;

            float sideOffset = isLeft ? -0.2f : 0.2f;
            double sideX = Math.cos(yawRad) * sideOffset;
            double sideZ = Math.sin(yawRad) * sideOffset;

            double fx = pos.x + sideX;
            double fz = pos.z + sideZ;

            double fy = getSnowSurfaceAt(player, fx, pos.y, fz);

            if (fy < 0) {
                fx = pos.x;
                fz = pos.z;
                fy = getSnowSurfaceAt(player, fx, pos.y, fz);
            }

            if (fy < 0) {
                return;
            }

            EffectualClientParticles.spawn(
                    ModParticles.SNOW_FOOTPRINT.get(),
                    fx, fy, fz,
                    yawRad,
                    isLeft ? 1.0 : 0.0,
                    0
            );

            tracker.leftFoot = !tracker.leftFoot;
        }
    }

    private static double getSnowSurfaceAt(Player player, double x, double playerY, double z) {
        BlockPos posAtFeet = BlockPos.containing(x, playerY, z);
        BlockState stateAtFeet = player.level().getBlockState(posAtFeet);

        if (stateAtFeet.is(Blocks.SNOW)) {
            int layers = stateAtFeet.getValue(SnowLayerBlock.LAYERS);
            double snowTop = posAtFeet.getY() + (layers * 0.125);
            if (Math.abs(playerY - snowTop) < 0.2) {
                return snowTop + 0.01;
            }
        }

        BlockPos blockBelow = BlockPos.containing(x, playerY - 0.05, z);
        BlockState stateBelow = player.level().getBlockState(blockBelow);

        VoxelShape shape = stateBelow.getCollisionShape(player.level(), blockBelow);

        if (shape.isEmpty()) {
            return -1;
        }

        double shapeTop = blockBelow.getY() + shape.max(Direction.Axis.Y);

        if (Math.abs(playerY - shapeTop) > 0.1) {
            return -1;
        }

        if (!isSnowy(stateBelow)) {
            return -1;
        }

        return shapeTop + 0.01;
    }

    private static boolean isSnowy(BlockState state) {
        return state.is(Blocks.SNOW_BLOCK)
                || state.is(Blocks.SNOW)
                || state.is(Blocks.POWDER_SNOW)
                || state.is(BlockTags.SNOW);
    }

    private static class FootstepTracker {
        double lastX = Double.MIN_VALUE;
        double lastY = Double.MIN_VALUE;
        double lastZ = Double.MIN_VALUE;
        double distance;
        boolean leftFoot;
    }
}
