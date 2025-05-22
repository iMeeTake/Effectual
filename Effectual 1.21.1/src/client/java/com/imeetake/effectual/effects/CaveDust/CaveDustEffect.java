package com.imeetake.effectual.effects.CaveDust;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static com.imeetake.effectual.EffectualClient.CONFIG;


public class CaveDustEffect {

    private static final Random RANDOM = Random.create();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.caveDust()) return;
            if (client.player == null || client.world == null) return;
            if (!client.world.getRegistryKey().equals(World.OVERWORLD)) return;
            if (client.isPaused()) return;

            BlockPos playerPos = client.player.getBlockPos();


            if (playerPos.getY() < 60 && !client.world.isSkyVisible(playerPos.up(5)) && isInNaturalCave(client, playerPos)) {
                spawnDustParticles(client, playerPos);
            }
        });
    }

    private static boolean isInNaturalCave(MinecraftClient client, BlockPos center) {
        int solidCount = 0;
        int artificialCount = 0;

        for (BlockPos pos : BlockPos.iterate(center.add(-4, -2, -4), center.add(4, 2, 4))) {
            var state = client.world.getBlockState(pos);

            if (state.isAir()) continue;
            if (isArtificialBlock(state)) artificialCount++;
            else solidCount++;
        }
        if (solidCount == 0) return false;
        return solidCount > artificialCount;
    }

    private static boolean isArtificialBlock(BlockState state) {
        return state.isOf(Blocks.SPAWNER)
                || state.isOf(Blocks.TRIAL_SPAWNER)
                || state.isOf(Blocks.CHEST)
                || state.isOf(Blocks.BARREL)
                || state.isOf(Blocks.FURNACE)
                || state.isOf(Blocks.LODESTONE)
                || state.isOf(Blocks.BEACON);
    }

    private static void spawnDustParticles(MinecraftClient client, BlockPos playerPos) {
        int particleCount = CONFIG.caveDustFrequency();
        int attempts = particleCount * 3;
        int spawned = 0;

        for (int i = 0; i < attempts && spawned < particleCount; i++) {
            int x = playerPos.getX() + RANDOM.nextInt(61) - 30;
            int y = playerPos.getY() + RANDOM.nextInt(10) - 5;
            int z = playerPos.getZ() + RANDOM.nextInt(61) - 30;

            BlockPos randomPos = new BlockPos(x, y, z);


            if (client.world.getBlockState(randomPos).isAir()) {
                TClientParticles.spawn(
                        ParticleTypes.WHITE_ASH,
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        0, -0.02, 0
                );
                spawned++;
            }
        }
    }
}
