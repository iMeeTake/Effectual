package com.imeetake.effectual.effects.SteamEffect;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class SteamEffect {

    private static final Set<BlockPos> activeSteamPositions = new HashSet<>();
    private static final RandomSource RANDOM = RandomSource.create();

    private static final int POSITIONS_PER_TICK = 462;
    private static final int RADIUS = 20;
    private static final int HEIGHT = 5;

    private static int scanX = -RADIUS;
    private static int scanY = -HEIGHT;
    private static int scanZ = -RADIUS;


    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().steamEffect) return;
            if (client.level == null || client.player == null) return;
            if (client.isPaused()) return;

            updateSteamPositions(client);

            for (BlockPos pos : activeSteamPositions) {
                spawnSteam(pos);
            }
        });
    }

    private static void updateSteamPositions(Minecraft client) {
        BlockPos center = client.player.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int checkedCount = 0;

        while (checkedCount < POSITIONS_PER_TICK) {

            pos.set(center.getX() + scanX, center.getY() + scanY, center.getZ() + scanZ);

            boolean isSteam = isWaterNextToLava(client, pos);
            if (isSteam) {
                activeSteamPositions.add(pos.immutable());
            } else {
                activeSteamPositions.remove(pos);
            }

            if (++scanX > RADIUS) {
                scanX = -RADIUS;
                if (++scanZ > RADIUS) {
                    scanZ = -RADIUS;
                    if (++scanY > HEIGHT) {
                        scanY = -HEIGHT;
                    }
                }
            }
            checkedCount++;
        }
    }

    private static boolean isWaterNextToLava(Minecraft client, BlockPos pos) {
        BlockState state = client.level.getBlockState(pos);
        if (!state.getFluidState().is(FluidTags.WATER)) return false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    BlockPos neighborPos = pos.offset(dx, dy, dz);
                    BlockState neighbor = client.level.getBlockState(neighborPos);

                    if (neighbor.getFluidState().is(FluidTags.LAVA) || neighbor.is(Blocks.LAVA_CAULDRON)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void spawnSteam(BlockPos pos) {
        int count = RANDOM.nextInt(2) + 1;
        for (int i = 0; i < count; i++) {
            double x = pos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.3;
            double y = pos.getY() + 1.0 + RANDOM.nextDouble() * 0.2;
            double z = pos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.3;

            TClientParticles.spawn(
                    ParticleTypes.POOF,
                    x, y, z,
                    0.0, 0.1 + RANDOM.nextDouble() * 0.05, 0.0
            );
        }
    }
}