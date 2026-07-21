package com.imeetake.effectual.effects.Sparks;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FurnaceSparksEffect {

    private static final RandomSource RAND = RandomSource.create();
    private static final int SAMPLES_PER_TICK = 60;
    private static final int RADIUS = 6;
    private static final float SPAWN_CHANCE = 0.45f;

    private static final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.isPaused()) return;
            if (!EffectualConfig.get().furnaceSparks) return;
            if (client.level == null || client.player == null) return;

            if (++tickCounter < 4) return;
            tickCounter = 0;

            spawnNearPlayer(client.level, client.player.blockPosition());
        });
    }

    private static void spawnNearPlayer(Level level, BlockPos center) {
        int startX = center.getX();
        int startY = center.getY();
        int startZ = center.getZ();

        for (int i = 0; i < SAMPLES_PER_TICK; i++) {
            int dx = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;
            int dy = RAND.nextInt(5) - 2;
            int dz = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;

            pos.set(startX + dx, startY + dy, startZ + dz);

            BlockState state = level.getBlockState(pos);

            boolean isFurnace = state.is(Blocks.FURNACE);
            boolean isBlast = state.is(Blocks.BLAST_FURNACE);
            if (!isFurnace && !isBlast) continue;

            if (!state.getOptionalValue(BlockStateProperties.LIT).orElse(false)) continue;
            if (RAND.nextFloat() > SPAWN_CHANCE) continue;

            if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) continue;
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

            spawnFrontSparks(pos.getX(), pos.getY(), pos.getZ(), facing);
        }
    }

    private static void spawnFrontSparks(int blockX, int blockY, int blockZ, Direction facing) {
        double cx = blockX + 0.5;
        double cz = blockZ + 0.5;

        double nx = facing.getStepX();
        double nz = facing.getStepZ();

        double tx = nz;
        double tz = -nx;

        double cy = blockY + 0.25 + RAND.nextDouble() * 0.2;

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

        EffectualClientParticles.spawn(ModParticles.SPARK.get(), px, cy, pz, vx, upSpeed, vz);
    }
}
