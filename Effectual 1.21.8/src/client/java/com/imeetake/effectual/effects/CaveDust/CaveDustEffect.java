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

    private static final Random RAND = Random.create();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.caveDust()) return;
            if (client.player == null || client.world == null || client.isPaused()) return;
            if (!client.world.getRegistryKey().equals(World.OVERWORLD)) return;

            BlockPos pos = client.player.getBlockPos();
            if (pos.getY() >= 60) return;
            if (client.world.isSkyVisible(pos.up(5))) return;
            if (!isInNaturalCave(client, pos)) return;

            spawnDustParticles(client, pos);
        });
    }

    private static boolean isInNaturalCave(MinecraftClient client, BlockPos center) {
        int solid = 0;
        int artificial = 0;
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = client.world.getBlockState(pos);
                    if (state.isAir()) continue;
                    if (isArtificialBlock(state)) artificial++;
                    else solid++;
                }
            }
        }

        return solid > artificial && solid > 0;
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

    private static void spawnDustParticles(MinecraftClient client, BlockPos center) {
        int count = CONFIG.caveDustFrequency();
        int attempts = count * 3;
        int spawned = 0;

        for (int i = 0; i < attempts && spawned < count; i++) {
            int x = center.getX() + RAND.nextInt(61) - 30;
            int y = center.getY() + RAND.nextInt(10) - 5;
            int z = center.getZ() + RAND.nextInt(61) - 30;
            BlockPos pos = new BlockPos(x, y, z);

            if (!client.world.getBlockState(pos).isAir()) continue;

            TClientParticles.spawn(ParticleTypes.WHITE_ASH,
                    x + 0.5, y + 0.5, z + 0.5,
                    0, -0.02, 0);
            spawned++;
        }
    }
}
