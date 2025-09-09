package com.imeetake.effectual.effects.Sparks;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class FireEntitySparksEffect {

    private static final Random RAND = Random.create();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.fireEntitySparks() || client.world == null || client.player == null || client.isPaused()) return;
            spawn(client);
        });
    }

    private static void spawn(MinecraftClient client) {
        client.world.getEntities().forEach(e -> {
            if (!e.isOnFire()) return;
            if (e.squaredDistanceTo(client.player) > 256) return;
            if (RAND.nextFloat() >= 0.2f) return;
            spark(e);
        });
    }

    private static void spark(Entity e) {
        double x = e.getX() + (RAND.nextDouble() - 0.5) * e.getWidth() * 0.8;
        double y = e.getY() + RAND.nextDouble() * e.getHeight();
        double z = e.getZ() + (RAND.nextDouble() - 0.5) * e.getWidth() * 0.8;

        double dy = 0.008 + RAND.nextDouble() * 0.004;
        double dx = (RAND.nextDouble() - 0.5) * 0.0028;
        double dz = (RAND.nextDouble() - 0.5) * 0.0028;

        int n = 1 + RAND.nextInt(2);
        for (int i = 0; i < n; i++) {
            double jx = dx + (RAND.nextDouble() - 0.5) * 0.0006;
            double jz = dz + (RAND.nextDouble() - 0.5) * 0.0006;
            TClientParticles.spawn(new TParticleEffectSimple(ModParticles.SPARK), x, y, z, jx, dy, jz);
        }
    }
}
