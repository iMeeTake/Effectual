package com.imeetake.effectual.effects.SparksCart;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class SparksCartEffect {

    private static final Random RAND = new Random();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.minecartSparks() || client.world == null || client.player == null || client.isPaused()) return;
            ClientWorld world = client.world;
            world.getEntities().forEach(e -> {
                if (e instanceof AbstractMinecartEntity cart) spark(client, cart);
            });
        });
    }

    private static void spark(MinecraftClient client, AbstractMinecartEntity cart) {
        Vec3d vel = cart.getVelocity();
        if (vel.horizontalLength() < 0.4) return;
        if (RAND.nextFloat() > 0.1f) return;

        double y = cart.getY() + 0.1;
        double len = 0.7;
        double wid = 1.15;

        for (int side = -1; side <= 1; side += 2) {
            double x = cart.getX() + side * wid / 2;
            double z = cart.getZ() + RAND.nextDouble() * len - len / 2;

            TClientParticles.spawn(
                    new TParticleEffectSimple(ModParticles.SPARK),
                    x, y, z,
                    vel.x * 0.2, 0.01, vel.z * 0.2
            );
        }
    }
}
