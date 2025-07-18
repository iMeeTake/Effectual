package com.imeetake.effectual.effects.Sparks;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class FireImprovements {

    private static final Random RAND = Random.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.fireImprovements() || client.world == null || client.player == null || client.isPaused()) return;
            if (++tickCounter < 5) return;
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
                    if (!client.world.getBlockState(pos).isOf(Blocks.FIRE)) continue;
                    if (RAND.nextFloat() >= 0.5f) continue;
                    spark(pos);
                }
            }
        }
    }

    private static void spark(BlockPos pos) {
        double x = pos.getX() + 0.5 + (RAND.nextDouble() - 0.5) * 0.1;
        double y = pos.getY() + 0.85 + RAND.nextDouble() * 0.05;
        double z = pos.getZ() + 0.5 + (RAND.nextDouble() - 0.5) * 0.1;

        double angle = RAND.nextDouble() * Math.PI * 2;
        double speed = RAND.nextDouble() * 0.005;
        double dx = Math.cos(angle) * speed;
        double dz = Math.sin(angle) * speed;
        double dy = 0.04 + RAND.nextDouble() * 0.01;

        TClientParticles.spawn(
                new TParticleEffectSimple(ModParticles.SPARK),
                x, y, z,
                dx, dy, dz);
    }
}
