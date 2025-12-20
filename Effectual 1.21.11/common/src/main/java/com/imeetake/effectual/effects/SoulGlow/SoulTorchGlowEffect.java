package com.imeetake.effectual.effects.SoulGlow;

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

public class SoulTorchGlowEffect {
    private static final RandomSource RAND = RandomSource.create();
    private static final int SAMPLES_PER_TICK = 100;
    private static final int RADIUS = 6;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().torchImprovements
                    || client.level == null
                    || client.player == null
                    || client.isPaused())
                return;

            spawnNearPlayer(client);
        });
    }

    private static void spawnNearPlayer(Minecraft client) {
        BlockPos center = client.player.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int startX = center.getX();
        int startY = center.getY();
        int startZ = center.getZ();

        for (int i = 0; i < SAMPLES_PER_TICK; i++) {
            int dx = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;
            int dy = RAND.nextInt(5) - 2;
            int dz = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;

            pos.set(startX + dx, startY + dy, startZ + dz);

            BlockState state = client.level.getBlockState(pos);

            boolean isFloor = state.is(Blocks.SOUL_TORCH);
            boolean isWall = state.is(Blocks.SOUL_WALL_TORCH);

            if (!isFloor && !isWall) continue;

            glow(pos, state, isFloor);
        }
    }

    private static void glow(BlockPos pos, BlockState state, boolean isFloor) {
        double x, y, z;

        if (isFloor) {
            x = pos.getX() + 0.5 + (RAND.nextDouble() - 0.5) * 0.1;
            y = pos.getY() + 0.65;
            z = pos.getZ() + 0.5 + (RAND.nextDouble() - 0.5) * 0.1;
        } else {
            Direction f = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction opposite = f.getOpposite();

            double offset = 0.27;
            x = pos.getX() + 0.5 + (opposite.getStepX() * offset) + (RAND.nextDouble() - 0.5) * 0.1;
            y = pos.getY() + 0.65;
            z = pos.getZ() + 0.5 + (opposite.getStepZ() * offset) + (RAND.nextDouble() - 0.5) * 0.1;
        }

        TClientParticles.spawn(
                ModParticles.SOUL_GLOW.get(),
                x, y, z,
                0, -0.002, 0
        );
    }
}