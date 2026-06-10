package com.imeetake.effectual.effects.SnowFootprints;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
import com.imeetake.effectual.ModParticles;
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

public class SnowResidueEffect {

    private static final Int2ObjectOpenHashMap<ResidueTracker> trackers = new Int2ObjectOpenHashMap<>();
    private static final int RESIDUE_DURATION_TICKS = 60;

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

            if (!EffectualConfig.get().snowResidue || client.isPaused()) return;

            if (++cleanupTimer >= 100) {
                cleanupTrackers(client.level.players());
                cleanupTimer = 0;
            }

            for (Player player : client.level.players()) {
                if (player.isSpectator() || player.isInvisible()) continue;

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
        if (!player.onGround()) return;

        int id = player.getId();
        ResidueTracker tracker = trackers.get(id);
        if (tracker == null) {
            tracker = new ResidueTracker();
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

        boolean onSnowNow = isOnSnow(player, pos.y);

        if (onSnowNow) {
            tracker.residueTicks = RESIDUE_DURATION_TICKS;
        } else if (tracker.residueTicks > 0) {
            tracker.residueTicks--;
        }

        if (tracker.distance >= 0.7) {
            tracker.distance = 0;

            boolean shouldSpawn = !onSnowNow && tracker.residueTicks > 0;

            if (shouldSpawn) {
                spawnResidueParticle(player, pos);
            }
        }
    }

    private static void spawnResidueParticle(Player player, Vec3 pos) {
        double offsetX = (player.level().random.nextDouble() - 0.5) * 0.6;
        double offsetZ = (player.level().random.nextDouble() - 0.5) * 0.6;

        double fx = pos.x + offsetX;
        double fz = pos.z + offsetZ;

        double fy = getSurfaceAt(player, fx, pos.y, fz);

        if (fy < 0) {
            fx = pos.x;
            fz = pos.z;
            fy = getSurfaceAt(player, fx, pos.y, fz);
        }

        if (fy < 0) {
            return;
        }

        EffectualClientParticles.spawn(
                ModParticles.SNOW_RESIDUE,
                fx, fy, fz,
                0, 0, 0
        );
    }

    private static boolean isOnSnow(Player player, double playerY) {
        BlockPos posAtFeet = player.blockPosition();
        BlockState stateAtFeet = player.level().getBlockState(posAtFeet);

        if (stateAtFeet.is(Blocks.SNOW)) {
            int layers = stateAtFeet.getValue(SnowLayerBlock.LAYERS);
            double snowTop = posAtFeet.getY() + (layers * 0.125);
            if (Math.abs(playerY - snowTop) < 0.2) {
                return true;
            }
        }

        BlockPos blockBelow = posAtFeet.below();
        BlockState stateBelow = player.level().getBlockState(blockBelow);

        return isSnowy(stateBelow);
    }

    private static double getSurfaceAt(Player player, double x, double playerY, double z) {
        BlockPos blockBelow = BlockPos.containing(x, playerY - 0.05, z);
        BlockState stateBelow = player.level().getBlockState(blockBelow);

        VoxelShape shape = stateBelow.getCollisionShape(player.level(), blockBelow);

        if (shape.isEmpty()) {
            return -1;
        }

        double shapeTop = blockBelow.getY() + shape.max(Direction.Axis.Y);

        if (Math.abs(playerY - shapeTop) > 0.2) {
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

    private static class ResidueTracker {
        double lastX = Double.MIN_VALUE;
        double lastY = Double.MIN_VALUE;
        double lastZ = Double.MIN_VALUE;
        double distance;
        int residueTicks;
    }
}
