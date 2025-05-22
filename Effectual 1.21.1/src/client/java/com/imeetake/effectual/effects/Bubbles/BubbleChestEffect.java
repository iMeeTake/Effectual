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
    private static final Random RANDOM = Random.create();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.bubbleChests()) return;
            if (client.world == null) return;
            if (client.isPaused()) return;

            ClientWorld world = client.world;


            BlockPos playerPos = client.player.getBlockPos();
            int radius = 5;

            for (BlockPos pos : BlockPos.iterate(
                    playerPos.add(-radius, -radius, -radius),
                    playerPos.add(radius, radius, radius)
            )) {

                if (world.getBlockState(pos).isOf(Blocks.CHEST) || world.getBlockState(pos).isOf(Blocks.ENDER_CHEST)) {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    boolean isOpen = false;


                    if (blockEntity instanceof ChestBlockEntity chestEntity) {
                        isOpen = chestEntity.getAnimationProgress(0) > 0;
                    } else if (blockEntity instanceof EnderChestBlockEntity enderChestEntity) {
                        isOpen = enderChestEntity.getAnimationProgress(0) > 0;
                    }

                    double px = pos.getX() + 0.4 + RANDOM.nextDouble() * 0.2;
                    double py = pos.getY() + 0.8;
                    double pz = pos.getZ() + 0.4 + RANDOM.nextDouble() * 0.2;

                    if (isOpen && world.getFluidState(pos).isStill()) {
                        if (RANDOM.nextInt(20) == 0) {
                            TClientParticles.spawn(ParticleTypes.BUBBLE,
                                    px,
                                    py,
                                    pz,
                                    0, 0.1, 0);
                        }
                    }
                }
            }
        });
    }
}
