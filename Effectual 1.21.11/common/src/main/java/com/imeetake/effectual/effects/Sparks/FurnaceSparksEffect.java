package com.imeetake.effectual.effects.Sparks;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FurnaceSparksEffect {

    private static final RandomSource RAND = RandomSource.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().furnaceSparks || client.level == null || client.player == null || client.isPaused())
                return;
            if (++tickCounter < 4) return;
            tickCounter = 0;
            spawnNearPlayer(client);
        });
    }

    private static void spawnNearPlayer(Minecraft client) {
        BlockPos center = client.player.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int radius = 6;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = client.level.getBlockState(pos);

                    boolean isFurnace = state.is(Blocks.FURNACE);
                    boolean isBlast = state.is(Blocks.BLAST_FURNACE);
                    if (!isFurnace && !isBlast) continue;

                    if (!state.getOptionalValue(BlockStateProperties.LIT).orElse(false)) continue;
                    if (RAND.nextFloat() > 0.45f) continue;

                    if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) continue;
                    Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

                    spawnFrontSparks(pos, facing);
                }
            }
        }
    }

    private static void spawnFrontSparks(BlockPos pos, Direction facing) {
        double cx = pos.getX() + 0.5;
        double cz = pos.getZ() + 0.5;

        double nx = facing.getStepX();
        double nz = facing.getStepZ();

        double tx = nz;
        double tz = -nx;

        double cy = pos.getY() + 0.25 + RAND.nextDouble() * 0.2;

        double px = cx + nx * 0.52;
        double pz = cz + nz * 0.52;

        double lateral = (RAND.nextDouble() - 0.5) * 0.25;
        px += tx * lateral;
        pz += tz * lateral;

        double outSpeed = 0.008 + RAND.nextDouble() * 0.012;
        double upSpeed = 0.015 + RAND.nextDouble() * 0.02;
        double sideSpeed = (RAND.nextDouble() - 0.5) * 0.008;

        double vx = nx * outSpeed + tx * sideSpeed;
        double vz = nz * outSpeed + tz * sideSpeed;

        TClientParticles.spawn(ModParticles.SPARK.get(), px, cy, pz, vx, upSpeed, vz);
    }
}