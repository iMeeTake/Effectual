package com.imeetake.effectual.effects.Bubbles;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class BubblePotsEffect {

    private static final Random RAND = Random.create();
    private static final int RADIUS = 5;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.bubblePots() || client.world == null || client.isPaused()) return;

            ClientWorld world = client.world;
            BlockPos center = client.player.getBlockPos();
            BlockPos.Mutable pos = new BlockPos.Mutable();

            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                    for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                        pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);

                        if (!world.getBlockState(pos).isOf(Blocks.DECORATED_POT)) continue;
                        if (!world.getFluidState(pos).isStill()) continue;
                        if (RAND.nextInt(15) != 0) continue;

                        TClientParticles.spawn(
                                ParticleTypes.BUBBLE,
                                pos.getX() + 0.5,
                                pos.getY() + 1.3,
                                pos.getZ() + 0.5,
                                0, 0.1, 0
                        );
                    }
                }
            }
        });
    }
}
