package com.imeetake.effectual.effects.Sparks;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class CampfireImprovements {
    private static final Random RANDOM = Random.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.campfireImprovements()) return;
            if (client.world == null || client.player == null) return;
            if (client.isPaused()) return;
            if (++tickCounter < 5) return;
            tickCounter = 0;

            spawnSparks(client);
        });
    }

    private static void spawnSparks(MinecraftClient client) {
        BlockPos playerPos = client.player.getBlockPos();


        for (int x = -8; x <= 8; x++) {
            for (int y = -8; y <= 8; y++) {
                for (int z = -8; z <= 8; z++) {
                    BlockPos pos = playerPos.add(x, y, z);


                    var state = client.world.getBlockState(pos);
                    if (state.isOf(Blocks.CAMPFIRE)
                            && state.getOrEmpty(Properties.LIT).orElse(false)) { // Костёр горит


                        if (RANDOM.nextFloat() < 0.5) {
                            spawnSparkParticle(client, pos);
                        }
                    }
                }
            }
        }
    }

    private static void spawnSparkParticle(MinecraftClient client, BlockPos blockPos) {
        double x = blockPos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.1;
        double y = blockPos.getY() + 0.85 + RANDOM.nextDouble() * 0.05;
        double z = blockPos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.1;

        double angle = RANDOM.nextDouble() * 2 * Math.PI;
        double horizontalSpeed = RANDOM.nextDouble() * 0.005;
        double verticalSpeed = 0.04 + RANDOM.nextDouble() * 0.01;

        double dx = Math.cos(angle) * horizontalSpeed;
        double dz = Math.sin(angle) * horizontalSpeed;
        double dy = verticalSpeed;

        TClientParticles.spawn(
                new TParticleEffectSimple(ModParticles.SPARK),
                x, y, z,
                dx, dy, dz
        );
    }
}