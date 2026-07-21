package com.imeetake.effectual.effects.MetalSparks;

import com.imeetake.effectual.EffectualClientParticles;
import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

public class SparksCartEffect {

    private static final RandomSource RAND = RandomSource.create();
    private static final int QUERY_INTERVAL = 2;
    private static final double QUERY_RADIUS = 32.0;
    private static final float SPARK_CHANCE = 0.2f;

    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().minecartSparks || client.level == null || client.player == null || client.isPaused())
                return;
            ClientLevel level = client.level;

            if (++tickCounter < QUERY_INTERVAL) return;
            tickCounter = 0;

            for (AbstractMinecart cart : level.getEntitiesOfClass(AbstractMinecart.class, client.player.getBoundingBox().inflate(QUERY_RADIUS))) {
                spark(client, cart);
            }
        });
    }

    private static void spark(Minecraft client, AbstractMinecart cart) {
        Vec3 vel = cart.getDeltaMovement();
        if (vel.horizontalDistance() < 0.4) return;
        if (RAND.nextFloat() > SPARK_CHANCE) return;

        double y = cart.getY() + 0.1;
        double len = 0.7;
        double wid = 1.15;

        for (int side = -1; side <= 1; side += 2) {
            double x = cart.getX() + side * wid / 2;
            double z = cart.getZ() + RAND.nextDouble() * len - len / 2;

            EffectualClientParticles.spawn(
                    ModParticles.METAL_SPARK.get(),
                    x, y, z,
                    vel.x * 0.2, 0.01, vel.z * 0.2
            );
        }
    }
}
