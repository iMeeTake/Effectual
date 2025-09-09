package com.imeetake.effectual.effects.SparksSoul;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class SoulFireImprovements {

    private static final Random RAND = Random.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.fireImprovements() || client.world == null || client.player == null || client.isPaused()) return;
            if (++tickCounter < 3) return;
            tickCounter = 0;
            spawn(client);
        });
    }

    private static void spawn(MinecraftClient client) {
        BlockPos center = client.player.getBlockPos();
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int dx = -8; dx <= 8; dx++) {
            for (int dy = -8; dy <= 8; dy++) {
                for (int dz = -8; dz <= 8; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (!client.world.getBlockState(pos).isOf(Blocks.SOUL_FIRE)) continue;
                    if (RAND.nextFloat() >= 0.35f) continue;
                    spark(pos);
                }
            }
        }
    }

    private static void spark(BlockPos pos) {
        double x = pos.getX() + 0.5 + (RAND.nextDouble() - 0.5) * 0.05;
        double y = pos.getY() + 0.82 + RAND.nextDouble() * 0.03;
        double z = pos.getZ() + 0.5 + (RAND.nextDouble() - 0.5) * 0.05;

        double angle = RAND.nextDouble() * Math.PI * 2;
        double speed = RAND.nextDouble() * 0.0016;

        double dx = Math.cos(angle) * speed;
        double dz = Math.sin(angle) * speed;
        double dy = 0.009 + RAND.nextDouble() * 0.004;

        int n = 1 + RAND.nextInt(2);
        for (int i = 0; i < n; i++) {
            double jx = dx + (RAND.nextDouble() - 0.5) * 0.0006;
            double jz = dz + (RAND.nextDouble() - 0.5) * 0.0006;
            TClientParticles.spawn(new TParticleEffectSimple(ModParticles.SOUL_SPARK), x, y, z, jx, dy, jz);
        }
    }
}
