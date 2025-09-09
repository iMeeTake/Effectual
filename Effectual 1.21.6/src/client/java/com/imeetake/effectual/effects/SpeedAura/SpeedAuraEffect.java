package com.imeetake.effectual.effects.SpeedAura;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class SpeedAuraEffect {
    private static final Random RANDOM = Random.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.speedAura() || client.world == null || client.isPaused()) return;
            if (++tickCounter < 3) return;
            tickCounter = 0;

            for (PlayerEntity player : client.world.getPlayers()) {
                if (!player.hasStatusEffect(StatusEffects.SPEED) || !player.isSprinting()) continue;

                Vec3d vel = player.getVelocity();
                if (vel.lengthSquared() < 0.02) continue;

                Vec3d dir = vel.normalize();
                Vec3d side = new Vec3d(-dir.z, 0, dir.x).normalize();
                Vec3d up = new Vec3d(0, 1, 0);

                if (RANDOM.nextFloat() >= 0.5f) continue;

                double backOffset = -0.5 - RANDOM.nextDouble() * 0.3;
                double sideOffset = (RANDOM.nextDouble() - 0.5) * 0.4;
                double heightOffset = 0.3 + RANDOM.nextDouble() * 0.8;

                Vec3d spawnPos = player.getPos()
                        .add(dir.multiply(backOffset))
                        .add(side.multiply(sideOffset))
                        .add(up.multiply(heightOffset));

                Vec3d motion = dir.multiply(0.15)
                        .add((RANDOM.nextDouble() - 0.5) * 0.005,
                                -0.002 + RANDOM.nextDouble() * 0.005,
                                (RANDOM.nextDouble() - 0.5) * 0.005);

                TClientParticles.spawn(
                        new TParticleEffectSimple(ModParticles.SPEED_AURA),
                        spawnPos.x, spawnPos.y, spawnPos.z,
                        motion.x, motion.y, motion.z
                );
            }
        });
    }
}
