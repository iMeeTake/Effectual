package com.imeetake.effectual.effects.SparksSoul;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SoulFireImprovements {

    private static final RandomSource RAND = RandomSource.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null || client.player == null || client.isPaused()) return;
            if (!EffectualConfig.get().fireImprovements) return;
            if (++tickCounter < 3) return;
            tickCounter = 0;
            spawn(client);
        });
    }

    private static void spawn(Minecraft client) {
        BlockPos center = client.player.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = -8; dx <= 8; dx++) {
            for (int dy = -8; dy <= 8; dy++) {
                for (int dz = -8; dz <= 8; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = client.level.getBlockState(pos);

                    if (!state.is(Blocks.SOUL_FIRE)) continue;
                    if (RAND.nextFloat() >= 0.65f) continue;

                    spark(pos);
                }
            }
        }
    }

    private static void spark(BlockPos pos) {
        double x = pos.getX() + RAND.nextDouble();
        double y = pos.getY() + 0.4 + RAND.nextDouble() * 0.5;
        double z = pos.getZ() + RAND.nextDouble();

        double angle = RAND.nextDouble() * Math.PI * 2;
        double speed = 0.01 + RAND.nextDouble() * 0.03;

        double dx = Math.cos(angle) * speed;
        double dy = 0.04 + RAND.nextDouble() * 0.06;
        double dz = Math.sin(angle) * speed;

        TClientParticles.spawn(ModParticles.SOUL_SPARK.get(), x, y, z, dx, dy, dz);
    }
}