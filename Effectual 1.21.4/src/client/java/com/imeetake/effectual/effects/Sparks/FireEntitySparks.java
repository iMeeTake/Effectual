package com.imeetake.effectual.effects.Sparks;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class FireEntitySparks {
    private static final Random RANDOM = Random.create();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.fireEntitySparks()) return;
            if (client.world == null || client.player == null) return;
            if (client.isPaused()) return;

            spawnFromBurningEntities(client);
        });
    }

    private static void spawnFromBurningEntities(MinecraftClient client) {
        client.world.getEntities().forEach(entity -> {
            if (entity.isOnFire() && entity.squaredDistanceTo(client.player) <= 16 * 16) {
                if (RANDOM.nextFloat() < 0.2f) {
                    spawnSparkParticle(client, entity);
                }
            }
        });
    }

    private static void spawnSparkParticle(MinecraftClient client, Entity entity) {
        double x = entity.getX() + (RANDOM.nextDouble() - 0.5) * entity.getWidth() * 0.8;
        double y = entity.getY() + RANDOM.nextDouble() * entity.getHeight(); // от ног до головы
        double z = entity.getZ() + (RANDOM.nextDouble() - 0.5) * entity.getWidth() * 0.8;

        double dx = (RANDOM.nextDouble() - 0.5) * 0.01;
        double dy = 0.04 + RANDOM.nextDouble() * 0.005;
        double dz = (RANDOM.nextDouble() - 0.5) * 0.01;

        ParticleEffect effect = new TParticleEffectSimple(ModParticles.SPARK);

        TClientParticles.spawn(effect, x, y, z, dx, dy, dz);
    }
}
