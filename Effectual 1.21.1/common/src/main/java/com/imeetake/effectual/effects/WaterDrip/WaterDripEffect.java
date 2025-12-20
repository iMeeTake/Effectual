package com.imeetake.effectual.effects.WaterDrip;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.WeakHashMap;

public class WaterDripEffect {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Map<Player, Long> lastFullySubmergedTicks = new WeakHashMap<>();

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().waterDrip || client.level == null || client.isPaused()) return;
            ClientLevel level = client.level;
            for (Player player : level.players()) {
                if (shouldPlayEffect(player)) spawnWaterDripParticles(player);
            }
        });
    }

    private static boolean shouldPlayEffect(Player player) {
        if (player.isSpectator() || player.isCreative()) return false;
        long time = player.level().getGameTime();

        if (player.isUnderWater()) {
            lastFullySubmergedTicks.put(player, time);
            return false;
        }

        long last = lastFullySubmergedTicks.getOrDefault(player, -200L);
        if (time - last > 100) return false;

        if (player.isInWater()) return true;

        return player.getDeltaMovement().lengthSqr() > 0.0004 || player.isSprinting();
    }

    private static void spawnWaterDripParticles(Player player) {
        int count = 1 + (RANDOM.nextFloat() < 0.12f ? 1 : 0);
        float yaw = player.getYRot();
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

            TClientParticles.spawn(ModParticles.WATER_DRIP.get(), x, y, z, 0, 0, 0);
        }
    }
}