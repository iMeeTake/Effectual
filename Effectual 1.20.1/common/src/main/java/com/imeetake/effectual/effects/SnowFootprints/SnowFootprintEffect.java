package com.imeetake.effectual.effects.SnowFootprints;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SnowFootprintEffect {

    private static final Map<UUID, FootstepTracker> trackers = new HashMap<>();

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().snowFootprints) return;
            if (client.level == null || client.isPaused()) return;

            for (Player player : client.level.players()) {
                if (player.isSpectator() || player.isInvisible()) continue;
                if (!player.onGround()) continue;

                processPlayer(player);
            }
        });
    }

    private static void processPlayer(Player player) {
        FootstepTracker tracker = trackers.computeIfAbsent(
                player.getUUID(),
                k -> new FootstepTracker()
        );

        Vec3 pos = player.position();
        double moved = tracker.lastPos == null ? 0 : pos.distanceTo(tracker.lastPos);

        if (moved > 0.01 && moved < 2.0) {
            tracker.distance += moved;
        }

        tracker.lastPos = pos;

        if (tracker.distance >= 0.7) {
            tracker.distance = 0;

            BlockPos below = player.blockPosition().below();
            BlockState state = player.level().getBlockState(below);

            if (!isSnowy(state)) {
                below = player.blockPosition();
                state = player.level().getBlockState(below);
                if (!isSnowy(state)) return;
            }

            float yawRad = (float) Math.toRadians(player.getYRot());
            boolean isLeft = tracker.leftFoot;

            float sideOffset = isLeft ? -0.2f : 0.2f;

            double sideX = Math.cos(yawRad) * sideOffset;
            double sideZ = Math.sin(yawRad) * sideOffset;

            double fx = pos.x + sideX;
            double fz = pos.z + sideZ;

            double fy;
            if (state.is(Blocks.SNOW)) {
                int layers = state.getValue(SnowLayerBlock.LAYERS);
                fy = below.getY() + (layers * 0.125) + 0.001;
            } else {
                fy = below.getY() + 1.001;
            }

            TClientParticles.spawn(
                    ModParticles.SNOW_FOOTPRINT.get(),
                    fx, fy, fz,
                    yawRad,
                    isLeft ? 1.0 : 0.0,
                    0
            );

            tracker.leftFoot = !tracker.leftFoot;
        }
    }

    private static boolean isSnowy(BlockState state) {
        return state.is(Blocks.SNOW_BLOCK)
                || state.is(Blocks.SNOW)
                || state.is(Blocks.POWDER_SNOW)
                || state.is(BlockTags.SNOW);
    }

    private static class FootstepTracker {
        Vec3 lastPos;
        double distance;
        boolean leftFoot;
    }
}