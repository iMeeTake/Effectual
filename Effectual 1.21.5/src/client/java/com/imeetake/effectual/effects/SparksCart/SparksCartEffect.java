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

    private static final Random RANDOM = new Random();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.minecartSparks()) return;
            if (client.world == null || client.player == null) return;
            if (client.isPaused()) return;


            ClientWorld world = client.world;


            for (var entity : world.getEntities()) {
                if (entity instanceof AbstractMinecartEntity minecart) {
                    spawnSparks(client, minecart, world);
                }
            }
        });
    }

    private static void spawnSparks(MinecraftClient client, AbstractMinecartEntity minecart, ClientWorld world) {
        Vec3d velocity = minecart.getVelocity();
        double speed = velocity.horizontalLength();


        if (speed < 0.4) return;


        double chance = 0.1;
        if (RANDOM.nextFloat() > chance) return;


        double cartWidth = 1.15;
        double cartLength = 0.7;
        double yOffset = 0.1;


        for (int side = -1; side <= 1; side += 2) {
            double x = minecart.getX() + side * (cartWidth / 2);
            double z = minecart.getZ() + RANDOM.nextDouble() * cartLength - (cartLength / 2);
            double y = minecart.getY() + yOffset;


            TClientParticles.spawn(
                    new TParticleEffectSimple(ModParticles.SPARK),
                    x, y, z,
                    velocity.x * 0.2, 0.01, velocity.z * 0.2
            );
        }
    }
}