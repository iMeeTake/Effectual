package com.imeetake.effectual.effects.Bubbles;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Blocks;

public class BubblePotsEffect {
    private static final int RADIUS = 5;
    private static final int SAMPLES_PER_TICK = 48;

    private static final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    private static int scanX = -RADIUS;
    private static int scanY = -RADIUS;
    private static int scanZ = -RADIUS;
    private static ClientLevel lastLevel = null;
    private static BlockPos lastPlayerPos = null;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().bubblePots || client.isPaused()) return;
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
                pos.set(center.getX() + scanX, center.getY() + scanY, center.getZ() + scanZ);

                if (level.getBlockState(pos).is(Blocks.DECORATED_POT) && level.getFluidState(pos).isSource()) {
                    EffectualClientParticles.spawn(
                            ParticleTypes.BUBBLE,
                            pos.getX() + 0.5,
                            pos.getY() + 1.1,
                            pos.getZ() + 0.5,
                            0, 0.1, 0
                    );
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
