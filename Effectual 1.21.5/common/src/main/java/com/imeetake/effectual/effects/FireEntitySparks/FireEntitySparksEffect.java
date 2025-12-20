package com.imeetake.effectual.effects.FireEntitySparks;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class FireEntitySparksEffect {
    private static final RandomSource RAND = RandomSource.create();

    public static void tick(Entity e) {
        if (!EffectualConfig.get().fireEntitySparks) return;
        if (RAND.nextFloat() >= 0.2f) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && e.distanceToSqr(mc.player) > 256) return;

        spark(e);
    }

    private static void spark(Entity e) {
        double w = e.getBbWidth();
        double h = e.getBbHeight();
        Vec3 vel = e.getDeltaMovement();

        double spawnWidth = w + 0.25;

        double x = e.getX() + (RAND.nextDouble() - 0.5) * spawnWidth;
        double y = e.getY() + (RAND.nextDouble() * (h + 0.2)) - 0.1;
        double z = e.getZ() + (RAND.nextDouble() - 0.5) * spawnWidth;

        int n = 1 + RAND.nextInt(2);

        for (int i = 0; i < n; i++) {
            double angle = RAND.nextDouble() * Math.PI * 2;
            double driftSpeed = 0.01 + RAND.nextDouble() * 0.02;

            double dx = vel.x + Math.cos(angle) * driftSpeed;
            double dy = vel.y + 0.04 + RAND.nextDouble() * 0.05;
            double dz = vel.z + Math.sin(angle) * driftSpeed;

            TClientParticles.spawn(ModParticles.ENTITY_SPARK.get(), x, y, z, dx, dy, dz);
        }
    }
}