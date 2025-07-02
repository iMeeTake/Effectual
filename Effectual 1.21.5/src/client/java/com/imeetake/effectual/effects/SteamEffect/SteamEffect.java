package com.imeetake.effectual.effects.SteamEffect;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class SteamEffect {

    private static final Set<BlockPos> activeSteamPositions = new HashSet<>();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.steamEffect()) return;
            if (client.world == null || client.player == null) return;
            if (client.isPaused()) return;

            if (++tickCounter % 40 == 0) {
                updateSteamPositions(client);
            }

            activeSteamPositions.forEach(SteamEffect::spawnSteam);
        });
    }

    private static void updateSteamPositions(MinecraftClient client) {
        activeSteamPositions.clear();
        BlockPos playerPos = client.player.getBlockPos();
        var world = client.world;

        for (int dx = -20; dx <= 20; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                for (int dz = -20; dz <= 20; dz++) {
                    BlockPos pos = playerPos.add(dx, dy, dz);
                    if (isWaterNextToLava(world, pos)) {
                        activeSteamPositions.add(pos);
                    }
                }
            }
        }
    }

    private static boolean isWaterNextToLava(net.minecraft.client.world.ClientWorld world, BlockPos pos) {
        var state = world.getBlockState(pos);
        if (!state.getFluidState().isIn(FluidTags.WATER)) return false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    var neighbor = world.getBlockState(pos.add(dx, dy, dz));
                    if (neighbor.getFluidState().isIn(FluidTags.LAVA) || neighbor.isOf(Blocks.LAVA_CAULDRON)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static void spawnSteam(BlockPos pos) {
        var random = ThreadLocalRandom.current();
        int count = random.nextInt(2) + 1;
        for (int i = 0; i < count; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
            double y = pos.getY() + 1.0 + random.nextDouble() * 0.2;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;

            TClientParticles.spawn(
                    ParticleTypes.POOF,
                    x, y, z,
                    0.0, 0.1 + random.nextDouble() * 0.05, 0.0
            );
        }
    }
}
