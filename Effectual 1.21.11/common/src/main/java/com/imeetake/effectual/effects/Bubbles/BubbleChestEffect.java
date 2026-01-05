package com.imeetake.effectual.effects.Bubbles;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class BubbleChestEffect {
    private static final RandomSource RAND = RandomSource.create();
    private static final int RADIUS = 5;
    private static final int SAMPLES_PER_TICK = 64;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().bubbleChests || client.level == null || client.isPaused()) return;
            if (client.player == null) return;

            ClientLevel level = client.level;
            BlockPos center = client.player.blockPosition();
            BlockPos.MutableBlockPos posM = new BlockPos.MutableBlockPos();

            for (int i = 0; i < SAMPLES_PER_TICK; i++) {
                int dx = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;
                int dy = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;
                int dz = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;

                posM.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);

                if (!level.getFluidState(posM).isSource()) continue;

                if (!level.getBlockState(posM).is(Blocks.CHEST)
                        && !level.getBlockState(posM).is(Blocks.ENDER_CHEST)) {
                    continue;
                }

                BlockEntity be = level.getBlockEntity(posM);
                boolean open = false;

                if (be instanceof ChestBlockEntity chest) {
                    open = chest.getOpenNess(0) > 0;
                } else if (be instanceof EnderChestBlockEntity ender) {
                    open = ender.getOpenNess(0) > 0;
                }

                if (!open) continue;

                double px = posM.getX() + 0.4 + RAND.nextDouble() * 0.2;
                double py = posM.getY() + 0.8;
                double pz = posM.getZ() + 0.4 + RAND.nextDouble() * 0.2;

                TClientParticles.spawn(ParticleTypes.BUBBLE, px, py, pz, 0, 0.1, 0);
            }
        });
    }
}