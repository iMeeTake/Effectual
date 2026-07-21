package com.imeetake.effectual.effects.Bubbles;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
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
    private static final int SAMPLES_PER_TICK = 48;

    private static final BlockPos.MutableBlockPos posM = new BlockPos.MutableBlockPos();

    private static int scanX = -RADIUS;
    private static int scanY = -RADIUS;
    private static int scanZ = -RADIUS;
    private static ClientLevel lastLevel = null;
    private static BlockPos lastPlayerPos = null;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().bubbleChests || client.isPaused()) return;
            if (client.level == null) {
                resetScanPosition();
                lastLevel = null;
                lastPlayerPos = null;
                return;
            }
            if (client.player == null) return;

            ClientLevel level = client.level;
            if (lastLevel != level) {
                resetScanPosition();
                lastLevel = level;
                lastPlayerPos = null;
            }

            BlockPos center = client.player.blockPosition();
            if (lastPlayerPos != null && center.distSqr(lastPlayerPos) > 100) {
                resetScanPosition();
            }
            lastPlayerPos = center;

            for (int i = 0; i < SAMPLES_PER_TICK; i++) {
                posM.set(center.getX() + scanX, center.getY() + scanY, center.getZ() + scanZ);
                var state = level.getBlockState(posM);

                if ((state.is(Blocks.CHEST) || state.is(Blocks.ENDER_CHEST)) && level.getFluidState(posM).isSource()) {
                    BlockEntity be = level.getBlockEntity(posM);
                    boolean open = false;

                    if (be instanceof ChestBlockEntity chest) {
                        open = chest.getOpenNess(0) > 0;
                    } else if (be instanceof EnderChestBlockEntity ender) {
                        open = ender.getOpenNess(0) > 0;
                    }

                    if (open) {
                        double px = posM.getX() + 0.4 + RAND.nextDouble() * 0.2;
                        double py = posM.getY() + 0.8;
                        double pz = posM.getZ() + 0.4 + RAND.nextDouble() * 0.2;

                        EffectualClientParticles.spawn(ParticleTypes.BUBBLE, px, py, pz, 0, 0.1, 0);
                    }
                }

                advanceScanPosition();
            }
        });
    }

    private static void resetScanPosition() {
        scanX = -RADIUS;
        scanY = -RADIUS;
        scanZ = -RADIUS;
    }

    private static void advanceScanPosition() {
        if (++scanX > RADIUS) {
            scanX = -RADIUS;
            if (++scanZ > RADIUS) {
                scanZ = -RADIUS;
                if (++scanY > RADIUS) {
                    scanY = -RADIUS;
                }
            }
        }
    }
}
