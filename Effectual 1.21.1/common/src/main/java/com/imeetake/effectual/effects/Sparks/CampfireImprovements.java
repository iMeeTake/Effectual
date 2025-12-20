package com.imeetake.effectual.effects.Sparks;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class CampfireImprovements {

    public static void animateTick(Level level, BlockPos pos, RandomSource random) {
        if (!EffectualConfig.get().campfireImprovements) return;
        if (random.nextFloat() < 0.4f) {
            spark(pos, random);
        }
    }

    private static void spark(BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.65;
        double z = pos.getZ() + 0.5;

        double ox = (random.nextDouble() - 0.5) * 0.45;
        double oy = random.nextDouble() * 0.35;
        double oz = (random.nextDouble() - 0.5) * 0.45;

        double angle = random.nextDouble() * Math.PI * 2;
        double speed = 0.01 + random.nextDouble() * 0.025;

        double dx = Math.cos(angle) * speed;
        double dy = 0.04 + random.nextDouble() * 0.05;
        double dz = Math.sin(angle) * speed;

        TClientParticles.spawn(
                ModParticles.SPARK.get(),
                x + ox, y + oy, z + oz,
                dx, dy, dz
        );
    }
}