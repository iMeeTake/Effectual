package com.imeetake.effectual.effects.Bubbles;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class BubblePotsEffect {
    private static final Random RANDOM = Random.create();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (!CONFIG.bubblePots()) return;
            if (client.world == null) return;
            if (client.isPaused()) return;

            ClientWorld world = client.world;


            BlockPos playerPos = client.player.getBlockPos();
            int radius = 5;

            for (BlockPos pos : BlockPos.iterate(
                    playerPos.add(-radius, -radius, -radius),
                    playerPos.add(radius, radius, radius)
            )) {

                if (world.getBlockState(pos).isOf(Blocks.DECORATED_POT)) {
                    BlockEntity blockEntity = world.getBlockEntity(pos);


                    if (world.getFluidState(pos).isStill()) {
                        if (RANDOM.nextInt(15) == 0) {
                            TClientParticles.spawn(ParticleTypes.BUBBLE,
                                    pos.getX() + 0.5,
                                    pos.getY() + 1.3,
                                    pos.getZ() + 0.5,
                                    0, 0.1, 0);
                        }
                    }
                }
            }
        });
    }
}
