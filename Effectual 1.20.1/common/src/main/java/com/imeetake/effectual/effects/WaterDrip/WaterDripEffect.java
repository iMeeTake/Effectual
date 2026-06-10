package com.imeetake.effectual.effects.WaterDrip;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
import com.imeetake.effectual.ModParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class WaterDripEffect {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Int2LongOpenHashMap lastFullySubmergedTicks = new Int2LongOpenHashMap();

    private static int cleanupTimer = 0;
    private static ClientLevel lastLevel = null;

    static {
        lastFullySubmergedTicks.defaultReturnValue(-200L);
    }

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null) {
                lastFullySubmergedTicks.clear();
                lastLevel = null;
                return;
            }

            if (lastLevel != client.level) {
                lastFullySubmergedTicks.clear();
                lastLevel = client.level;
            }

            if (!EffectualConfig.get().waterDrip || client.isPaused()) return;

            if (++cleanupTimer >= 100) {
                cleanupTimer = 0;
                cleanupMap(client.level.players());
            }

            for (Player player : client.level.players()) {
                if (shouldPlayEffect(player)) {
                    spawnWaterDripParticles(player);
                }
            }
        });
    }

    private static void cleanupMap(List<? extends Player> activePlayers) {
        if (lastFullySubmergedTicks.isEmpty()) return;

        IntOpenHashSet activeIds = new IntOpenHashSet(activePlayers.size());
        for (Player player : activePlayers) {
            activeIds.add(player.getId());
        }

        var iterator = lastFullySubmergedTicks.keySet().intIterator();
        while (iterator.hasNext()) {
            int id = iterator.nextInt();
            if (!activeIds.contains(id)) {
                iterator.remove();
            }
        }
    }

    private static boolean shouldPlayEffect(Player player) {
        if (player.isSpectator() || player.isCreative()) return false;

        int id = player.getId();
        long time = player.level().getGameTime();

        if (player.isUnderWater()) {
            lastFullySubmergedTicks.put(id, time);
            return false;
        }

        long last = lastFullySubmergedTicks.get(id);
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

            EffectualClientParticles.spawn(ModParticles.WATER_DRIP, x, y, z, player.getId(), lx, lz);
        }
    }
}
