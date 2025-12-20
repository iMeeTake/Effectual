package com.imeetake.effectual.effects.MetalSparks;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

public class SparksCartEffect {

    private static final RandomSource RAND = RandomSource.create();

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().minecartSparks || client.level == null || client.player == null || client.isPaused())
                return;
            ClientLevel level = client.level;

            level.entitiesForRendering().forEach(e -> {
                if (e instanceof AbstractMinecart cart) spark(client, cart);
            });
        });
    }

    private static void spark(Minecraft client, AbstractMinecart cart) {
        Vec3 vel = cart.getDeltaMovement();
        if (vel.horizontalDistance() < 0.4) return;
        if (RAND.nextFloat() > 0.1f) return;

        double y = cart.getY() + 0.1;
        double len = 0.7;
        double wid = 1.15;

        for (int side = -1; side <= 1; side += 2) {
            double x = cart.getX() + side * wid / 2;
            double z = cart.getZ() + RAND.nextDouble() * len - len / 2;

            TClientParticles.spawn(
                    ModParticles.METAL_SPARK.get(),
                    x, y, z,
                    vel.x * 0.2, 0.01, vel.z * 0.2
            );
        }
    }
}