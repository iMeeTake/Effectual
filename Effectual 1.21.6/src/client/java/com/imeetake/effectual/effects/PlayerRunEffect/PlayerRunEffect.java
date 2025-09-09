package com.imeetake.effectual.effects.PlayerRunEffect;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class PlayerRunEffect {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.runDust() || client.world == null || client.isPaused()) return;

            for (PlayerEntity player : client.world.getPlayers()) {
                if (shouldPlayEffect(player)) {
                    spawnDust(client, player);
                }
            }
        });
    }

    private static boolean shouldPlayEffect(PlayerEntity player) {
        return player.isSprinting()
                && !player.isSneaking()
                && player.isOnGround()
                && !player.isSubmergedInWater()
                && !player.isTouchingWater()
                && !player.isSpectator();
    }

    private static void spawnDust(MinecraftClient client, PlayerEntity player) {
        World world = client.world;
        BlockPos pos = player.getBlockPos();
        BlockState above = world.getBlockState(pos);
        BlockState below = world.getBlockState(pos.down());

        if (above.isOf(Blocks.SNOW) && above.contains(Properties.LAYERS)) {
            spawnParticle(client, ModParticles.SNOW_DUST, player);
            return;
        }

        Block block = below.getBlock();
        if (block == Blocks.SAND || block == Blocks.SUSPICIOUS_SAND) {
            spawnParticle(client, ModParticles.SAND_DUST, player);
        } else if (block == Blocks.RED_SAND) {
            spawnParticle(client, ModParticles.RED_SAND_DUST, player);
        } else if (block == Blocks.SNOW || block == Blocks.SNOW_BLOCK || block == Blocks.POWDER_SNOW) {
            spawnParticle(client, ModParticles.SNOW_DUST, player);
        } else if (block == Blocks.GRAVEL || block == Blocks.SUSPICIOUS_GRAVEL) {
            spawnParticle(client, ModParticles.GRAVEL_DUST, player);
        }
    }

    private static void spawnParticle(MinecraftClient client, ParticleType<?> type, PlayerEntity player) {
        var rand = client.world.random;

        double x = player.getX() + (rand.nextDouble() - 0.5) * 0.5;
        double y = player.getY() + 0.1;
        double z = player.getZ() + (rand.nextDouble() - 0.5) * 0.5;

        double vx = (rand.nextDouble() - 0.5) * 0.02;
        double vz = (rand.nextDouble() - 0.5) * 0.02;

        TClientParticles.spawn(
                new TParticleEffectSimple(type),
                x, y, z,
                vx, 0, vz);
    }
}
