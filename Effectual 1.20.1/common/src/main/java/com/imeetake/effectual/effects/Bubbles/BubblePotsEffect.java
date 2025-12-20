package com.imeetake.effectual.effects.Bubbles;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;

public class BubblePotsEffect {
    private static final RandomSource RAND = RandomSource.create();
    private static final int RADIUS = 5;
    private static final int SAMPLES_PER_TICK = 48;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().bubblePots || client.level == null || client.isPaused()) return;
            if (client.player == null) return;

            ClientLevel level = client.level;
            BlockPos center = client.player.blockPosition();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            for (int i = 0; i < SAMPLES_PER_TICK; i++) {
                int dx = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;
                int dy = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;
                int dz = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;

                pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);

                if (!level.getFluidState(pos).isSource()) continue;
                if (!level.getBlockState(pos).is(Blocks.DECORATED_POT)) continue;

                TClientParticles.spawn(
                        ParticleTypes.BUBBLE,
                        pos.getX() + 0.5,
                        pos.getY() + 1.1,
                        pos.getZ() + 0.5,
                        0, 0.1, 0
                );
            }
        });
    }
}