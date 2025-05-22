package com.imeetake.effectual.effects.WaterDrip;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class WaterDripEffect {
    private static final Random RANDOM = Random.create();
    private static final Map<PlayerEntity, Long> lastFullySubmergedTicks = new HashMap<>();
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.waterDrip()) return;
            if (client.world == null) return;
            if (client.isPaused()) return;

            ClientWorld world = client.world;


            for (PlayerEntity player : world.getPlayers()) {
                if (shouldPlayEffect(player)) {
                    spawnWaterDripParticles(world, player);
                }
            }
        });
    }

    private static boolean shouldPlayEffect(PlayerEntity player) {
        if (player.isSpectator() || player.isCreative()) return false;


        if (player.isSubmergedInWater()) {
            lastFullySubmergedTicks.put(player, player.getWorld().getTime());
            return false;
        }

        long lastSubmerged = lastFullySubmergedTicks.getOrDefault(player, -101L);
        return player.getWorld().getTime() - lastSubmerged <= 100;
    }

    private static void spawnWaterDripParticles(ClientWorld world, PlayerEntity player) {
        if (RANDOM.nextInt(5) == 0) {
            double offsetX = RANDOM.nextFloat() * 0.4 - 0.25;
            double offsetY = RANDOM.nextFloat() * 0.8 + 1.0;
            double offsetZ = RANDOM.nextFloat() * 0.4 - 0.25;

            TClientParticles.spawn(new WaterDripParticleEffect(ModParticles.WATER_DRIP),
                    player.getX() + offsetX,
                    player.getY() + offsetY,
                    player.getZ() + offsetZ,
                    0, -0.02, 0);
        }
    }
}
