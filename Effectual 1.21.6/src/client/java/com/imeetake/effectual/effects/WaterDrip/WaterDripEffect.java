package com.imeetake.effectual.effects.WaterDrip;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;

import java.util.Map;
import java.util.WeakHashMap;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class WaterDripEffect {
    private static final Random RANDOM = Random.create();
    private static final Map<PlayerEntity, Long> lastFullySubmergedTicks = new WeakHashMap<>();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.waterDrip() || client.world == null || client.isPaused()) return;
            ClientWorld world = client.world;
            for (PlayerEntity player : world.getPlayers()) {
                if (shouldPlayEffect(player)) spawnWaterDripParticles(world, player);
            }
        });
    }

    private static boolean shouldPlayEffect(PlayerEntity player) {
        if (player.isSpectator() || player.isCreative()) return false;
        long time = player.getWorld().getTime();
        if (player.isSubmergedInWater()) {
            lastFullySubmergedTicks.put(player, time);
            return false;
        }
        long last = lastFullySubmergedTicks.getOrDefault(player, -200L);
        if (time - last > 100) return false;
        if (player.isTouchingWater()) return true;
        return player.getVelocity().lengthSquared() > 0.0004 || player.isSprinting();
    }

    private static void spawnWaterDripParticles(ClientWorld world, PlayerEntity player) {
        int count = 1 + (RANDOM.nextFloat() < 0.12f ? 1 : 0);
        float yaw = player.getYaw(0);
        double ry = Math.toRadians(-yaw);
        for (int i = 0; i < count; i++) {
            double ring = 0.32 + RANDOM.nextDouble() * 0.08;
            double ang = RANDOM.nextDouble() * Math.PI * 2.0;
            double lx = Math.cos(ang) * ring;
            double lz = Math.sin(ang) * ring;
            double ly = 0.95 + RANDOM.nextDouble() * 0.7;
            double rx = lx * Math.cos(ry) - lz * Math.sin(ry);
            double rz = lx * Math.sin(ry) + lz * Math.cos(ry);
            double x = player.getX() + rx;
            double y = player.getY() + ly;
            double z = player.getZ() + rz;
            TClientParticles.spawn(new WaterDripParticleEffect(ModParticles.WATER_DRIP), x, y, z, 0, 0, 0);
        }
    }
}
