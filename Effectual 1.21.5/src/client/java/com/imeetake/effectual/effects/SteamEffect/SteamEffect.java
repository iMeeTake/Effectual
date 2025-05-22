package com.imeetake.effectual.effects.SteamEffect;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class SteamEffect {

    private static final Random RANDOM = new Random();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
             if (!CONFIG.steamEffect()) return;
            if (client.world == null || client.player == null) return;
            if (client.isPaused()) return;

            BlockPos playerPos = client.player.getBlockPos();


            BlockPos.stream(playerPos.add(-20, -10, -20), playerPos.add(20, 10, 20))
                    .filter(pos -> isWaterNextToLava(client, pos))
                    .forEach(pos -> spawnSteam(client, pos));
        });
    }

    private static boolean isWaterNextToLava(MinecraftClient client, BlockPos pos) {

        if (client.world.getBlockState(pos).getBlock() != Blocks.WATER) return false;


        Block blockAbove = client.world.getBlockState(pos.up()).getBlock();
        if (blockAbove == Blocks.LAVA || blockAbove == Blocks.LAVA_CAULDRON) {
            return false;
        }


        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP) continue;
            BlockPos neighbor = pos.offset(direction);
            Block neighborBlock = client.world.getBlockState(neighbor).getBlock();
            if (neighborBlock == Blocks.LAVA || neighborBlock == Blocks.LAVA_CAULDRON) {
                return true;
            }
        }

        return false;
    }

    private static void spawnSteam(MinecraftClient client, BlockPos pos) {
        int count = RANDOM.nextInt(2) + 1;
        for (int i = 0; i < count; i++) {
            double x = pos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.3;
            double y = pos.getY() + 1.0 + RANDOM.nextDouble() * 0.2;
            double z = pos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.3;

            TClientParticles.spawn(
                    ParticleTypes.POOF,
                    x, y, z,
                    0.0, 0.1 + RANDOM.nextDouble() * 0.05, 0.0
            );
        }
    }
}
