package com.imeetake.effectual.effects.SoulGlow;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class SoulLanternGlowEffect {
    private static final Random RANDOM = Random.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.lanternImprovements()) return;
            if (client.world == null || client.player == null) return;
            if (client.isPaused()) return;
            if (++tickCounter < 5) return;
            tickCounter = 0;


            spawnParticlesAroundLantern(client);
        });
    }

    private static void spawnParticlesAroundLantern(MinecraftClient client) {
        BlockPos playerPos = client.player.getBlockPos();

        for (int x = -8; x <= 8; x++) {
            for (int y = -8; y <= 8; y++) {
                for (int z = -8; z <= 8; z++) {
                    BlockPos pos = playerPos.add(x, y, z);


                    if (client.world.getBlockState(pos).isOf(Blocks.SOUL_LANTERN)) {
                        if (!client.world.getFluidState(pos).isEmpty()) return;

                        if (RANDOM.nextFloat() < 0.20) {
                            spawnGlowParticle(client, pos);
                        }
                    }
                }
            }
        }
    }

    private static void spawnGlowParticle(MinecraftClient client, BlockPos lanternPos) {
        double offsetXZ = 0.8;
        double x = lanternPos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * offsetXZ;
        double z = lanternPos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * offsetXZ;
        double y = lanternPos.getY() + 0.25;

        TClientParticles.spawn(
                new TParticleEffectSimple(ModParticles.SOUL_GLOW),
                x, y, z,
                0.0, -0.002, 0.0
        );
    }
}