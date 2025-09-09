package com.imeetake.effectual.effects.Bubbles;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class BubbleChestEffect {

    private static final Random RAND = Random.create();
    private static final int RADIUS = 5;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.bubbleChests() || client.world == null || client.isPaused()) return;

            ClientWorld world     = client.world;
            BlockPos center       = client.player.getBlockPos();
            BlockPos.Mutable posM = new BlockPos.Mutable();

            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                    for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                        posM.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);

                        if (!(world.getBlockState(posM).isOf(Blocks.CHEST)
                                || world.getBlockState(posM).isOf(Blocks.ENDER_CHEST))) {
                            continue;
                        }

                        BlockEntity be = world.getBlockEntity(posM);
                        boolean open   = be instanceof ChestBlockEntity chest
                                ? chest.getAnimationProgress(0) > 0
                                : be instanceof EnderChestBlockEntity ender
                                && ender.getAnimationProgress(0) > 0;

                        if (!open) continue;
                        if (!world.getFluidState(posM).isStill()) continue;
                        if (RAND.nextInt(20) != 0) continue;

                        double px = posM.getX() + 0.4 + RAND.nextDouble() * 0.2;
                        double py = posM.getY() + 0.8;
                        double pz = posM.getZ() + 0.4 + RAND.nextDouble() * 0.2;

                        TClientParticles.spawn(ParticleTypes.BUBBLE, px, py, pz, 0, 0.1, 0);
                    }
                }
            }
        });
    }
}
