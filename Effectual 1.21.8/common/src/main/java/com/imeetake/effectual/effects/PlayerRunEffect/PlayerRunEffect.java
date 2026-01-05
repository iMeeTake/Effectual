package com.imeetake.effectual.effects.PlayerRunEffect;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class PlayerRunEffect {

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().runDust || client.level == null || client.isPaused()) return;

            for (Player player : client.level.players()) {
                if (shouldPlayEffect(player)) {
                    spawnDust(client, player);
                }
            }
        });
    }

    private static boolean shouldPlayEffect(Player player) {
        return player.isSprinting()
                && !player.isShiftKeyDown()
                && player.onGround()
                && !player.isUnderWater()
                && !player.isInWater()
                && !player.isSpectator();
    }

    private static void spawnDust(Minecraft client, Player player) {
        Level level = client.level;
        BlockPos pos = player.blockPosition();

        BlockState atFeet = level.getBlockState(pos);
        BlockState below = level.getBlockState(pos.below());

        Vec3 velocity = player.getDeltaMovement();
        double speedFactor = 0.5;
        double vx = -velocity.x * speedFactor;
        double vz = -velocity.z * speedFactor;

        if (atFeet.is(Blocks.SNOW) && atFeet.hasProperty(BlockStateProperties.LAYERS)) {
            spawnParticle(client, ModParticles.SNOW_DUST.get(), player, vx, vz, 0.25);
            return;
        }
        if (atFeet.is(Blocks.MUD) || atFeet.is(Blocks.MUDDY_MANGROVE_ROOTS)) {
            spawnParticle(client, ModParticles.MUD_DUST.get(), player, vx, vz, 0.5);
            return;
        }

        Block block = below.getBlock();
        if (block == Blocks.SAND || block == Blocks.SUSPICIOUS_SAND) {
            spawnParticle(client, ModParticles.SAND_DUST.get(), player, vx, vz, 0.1);
        } else if (block == Blocks.RED_SAND) {
            spawnParticle(client, ModParticles.RED_SAND_DUST.get(), player, vx, vz, 0.1);
        } else if (block == Blocks.SNOW || block == Blocks.SNOW_BLOCK || block == Blocks.POWDER_SNOW) {
            spawnParticle(client, ModParticles.SNOW_DUST.get(), player, vx, vz, 0.1);
        } else if (block == Blocks.GRAVEL || block == Blocks.SUSPICIOUS_GRAVEL) {
            spawnParticle(client, ModParticles.GRAVEL_DUST.get(), player, vx, vz, 0.1);
        } else if (block == Blocks.MUD || block == Blocks.PACKED_MUD || block == Blocks.MUDDY_MANGROVE_ROOTS) {
            spawnParticle(client, ModParticles.MUD_DUST.get(), player, vx, vz, 0.1);
        }
    }

    private static void spawnParticle(Minecraft client, SimpleParticleType type, Player player, double vx, double vz, double yOffset) {
        RandomSource rand = client.level.random;

        double x = player.getX() + (rand.nextDouble() - 0.5) * 0.6;
        double y = player.getY() + yOffset;
        double z = player.getZ() + (rand.nextDouble() - 0.5) * 0.6;

        double noisyVx = vx + (rand.nextDouble() - 0.5) * 0.15;
        double noisyVz = vz + (rand.nextDouble() - 0.5) * 0.15;

        TClientParticles.spawn(
                type,
                x, y, z,
                noisyVx, 0, noisyVz);
    }
}