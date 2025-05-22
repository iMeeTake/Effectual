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
            if (!CONFIG.runDust()) return;
            if (client.world == null) return;
            if (client.isPaused()) return;

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
                && !player.isSpectator();
    }

    private static void spawnDust(MinecraftClient client, PlayerEntity player) {
        World world = client.world;
        BlockPos posAtFeet = player.getBlockPos();
        BlockPos posBelowFeet = posAtFeet.down();

        BlockState stateAtFeet = world.getBlockState(posAtFeet);
        BlockState stateBelowFeet = world.getBlockState(posBelowFeet);


        if (stateAtFeet.isOf(Blocks.SNOW) && stateAtFeet.contains(Properties.LAYERS)) {
            spawnSnowDust(client, player);
            return;
        }


        Block blockBelowFeet = stateBelowFeet.getBlock();
        if (blockBelowFeet == Blocks.SAND ||blockBelowFeet == Blocks.SUSPICIOUS_SAND) {
            spawnSandDust(client, player);
        } else if (blockBelowFeet == Blocks.SNOW || blockBelowFeet == Blocks.SNOW_BLOCK || blockBelowFeet == Blocks.POWDER_SNOW) {
            spawnSnowDust(client, player);
        } else if (blockBelowFeet == Blocks.GRAVEL || blockBelowFeet == Blocks.SUSPICIOUS_GRAVEL) {
            spawnGravelDust(client, player);
        } else if (blockBelowFeet == Blocks.RED_SAND) {
            spawnRedSandDust(client, player);
        }
    }

    private static void spawnSandDust(MinecraftClient client, PlayerEntity player) {
        spawnParticle(client, ModParticles.SAND_DUST , player);
    }

    private static void spawnRedSandDust(MinecraftClient client, PlayerEntity player) {
        spawnParticle(client, ModParticles.RED_SAND_DUST , player);
    }

    private static void spawnSnowDust(MinecraftClient client, PlayerEntity player) {
        spawnParticle(client, ModParticles.SNOW_DUST , player);
    }

    private static void spawnGravelDust(MinecraftClient client, PlayerEntity player) {
        spawnParticle(client, ModParticles.GRAVEL_DUST , player);
    }

    private static void spawnParticle(MinecraftClient client, ParticleType<?> type, PlayerEntity player) {
        double x = player.getX() + (client.world.random.nextDouble() - 0.5) * 0.5;
        double y = player.getY() + 0.1;
        double z = player.getZ() + (client.world.random.nextDouble() - 0.5) * 0.5;

        double velocityX = (client.world.random.nextDouble() - 0.5) * 0.02;
        double velocityY = 0.00;
        double velocityZ = (client.world.random.nextDouble() - 0.5) * 0.02;

        TClientParticles.spawn(
                new TParticleEffectSimple(type),
                x, y, z,
                velocityX, velocityY, velocityZ);
    }
}
