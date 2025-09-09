package com.imeetake.effectual.effects.SoulGlow;


import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class SoulTorchGlowEffect {

    private static final Random RAND = Random.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.torchImprovements() || client.world == null || client.player == null || client.isPaused()) return;
            if (++tickCounter < 6) return;
            tickCounter = 0;
            spawnNearPlayer(client);
        });
    }

    private static void spawnNearPlayer(MinecraftClient client) {
        BlockPos center = client.player.getBlockPos();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int radius = 6;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = client.world.getBlockState(pos);
                    boolean isFloor = state.isOf(Blocks.SOUL_TORCH);
                    boolean isWall = state.isOf(Blocks.SOUL_WALL_TORCH);
                    if (!isFloor && !isWall) continue;
                    if (RAND.nextFloat() > 0.55f) continue;
                    glow(pos, state);
                }
            }
        }
    }

    private static void glow(BlockPos pos, BlockState state) {
        double[] p = tipPos(pos, state);
        TClientParticles.spawn(
                new TParticleEffectSimple(ModParticles.SOUL_GLOW),
                p[0], p[1], p[2],
                0, -0.002, 0
        );
    }

    private static double[] tipPos(BlockPos pos, BlockState state) {
        if (state.isOf(Blocks.SOUL_TORCH)) {
            double x = pos.getX() + 0.5 + (RAND.nextDouble() - 0.5) * 0.06;
            double y = pos.getY() + 0.65;
            double z = pos.getZ() + 0.5 + (RAND.nextDouble() - 0.5) * 0.06;
            return new double[]{x, y, z};
        } else {
            Direction f = state.get(Properties.HORIZONTAL_FACING);
            double off = 0.27;
            double x = pos.getX() + 0.5 - f.getOffsetX() * off + (RAND.nextDouble() - 0.5) * 0.05;
            double y = pos.getY() + 0.65;
            double z = pos.getZ() + 0.5 - f.getOffsetZ() * off + (RAND.nextDouble() - 0.5) * 0.05;
            return new double[]{x, y, z};
        }
    }
}