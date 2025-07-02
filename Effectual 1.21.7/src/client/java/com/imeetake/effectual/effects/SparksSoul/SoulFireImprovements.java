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
            if (++tickCounter < 5) return;
            tickCounter = 0;
            spawnSparks(client);
        });
    }

    private static void spawnSparks(MinecraftClient client) {
        BlockPos base = client.player.getBlockPos();
        for (int x = -8; x <= 8; x++) {
            for (int y = -8; y <= 8; y++) {
                for (int z = -8; z <= 8; z++) {
                    BlockPos pos = base.add(x, y, z);
                    if (client.world.getBlockState(pos).isOf(Blocks.SOUL_FIRE)) {
                        if (RAND.nextFloat() < 0.5f) spawnSparkParticle(pos);
                    }
                }
            }
        }
    }

    private static void spawnSparkParticle(BlockPos pos) {
        double x = pos.getX() + 0.5 + (RAND.nextDouble() - 0.5) * 0.1;
        double y = pos.getY() + 0.85 + RAND.nextDouble() * 0.05;
        double z = pos.getZ() + 0.5 + (RAND.nextDouble() - 0.5) * 0.1;

        double angle = RAND.nextDouble() * Math.PI * 2;
        double h = RAND.nextDouble() * 0.005;
        double dx = Math.cos(angle) * h;
        double dz = Math.sin(angle) * h;
        double dy = 0.04 + RAND.nextDouble() * 0.01;

        TClientParticles.spawn(
                new TParticleEffectSimple(ModParticles.SOUL_SPARK),
                x, y, z,
                dx, dy, dz);
    }
}
