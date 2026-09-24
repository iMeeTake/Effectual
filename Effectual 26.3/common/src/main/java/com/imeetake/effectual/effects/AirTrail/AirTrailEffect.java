package com.imeetake.effectual.effects.AirTrail;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class AirTrailEffect {
    private static final double MIN_SPEED_SQR = 0.55;
    private static final int SPAWN_INTERVAL = 4;
    private static final RandomSource RAND = RandomSource.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().airTrail || client.level == null || client.isPaused()) return;
            if (client.player == null) return;

            tickCounter++;
            if (tickCounter < SPAWN_INTERVAL) return;
            tickCounter = 0;

            for (Player player : client.level.players()) {
                if (!player.isFallFlying() || player.getAbilities().flying || player.isSpectator() || player.isInvisible()) {
                    continue;
                }
                spawnAirTrail(player);
            }
        });
    }

    private static void spawnAirTrail(Player player) {
        Vec3 velocity = player.getDeltaMovement();
        if (velocity.lengthSqr() < MIN_SPEED_SQR) return;

        Vec3 dir = velocity.normalize();

        Vec3 up = new Vec3(0, 1, 0);
        Vec3 side = dir.cross(up).normalize();
        if (side.lengthSqr() < 0.001) {
            side = new Vec3(1, 0, 0);
        }

        boolean leftSide = RAND.nextBoolean();
        double baseOffset = leftSide ? -1.0 : 1.0;
        double lateralVariation = (RAND.nextDouble() - 0.5) * 0.4;
        double depthVariation = (RAND.nextDouble() - 0.5) * 0.3;
        double heightVariation = (RAND.nextDouble() - 0.5) * 0.25;

        Vec3 pos = player.position()
                .add(0, player.getEyeHeight() * 0.3 + heightVariation, 0)
                .add(dir.scale(-0.25 + depthVariation))
                .add(side.scale(baseOffset + lateralVariation));

        double speed = velocity.length();
        double drag = 0.1 + RAND.nextDouble() * 0.08;

        Vec3 baseVel = dir.scale(-speed * drag);

        double noiseX = (RAND.nextDouble() - 0.5) * 0.03;
        double noiseY = (RAND.nextDouble() - 0.5) * 0.02;
        double noiseZ = (RAND.nextDouble() - 0.5) * 0.03;

        EffectualClientParticles.spawn(
                ModParticles.AIR_TRAIL.get(),
                pos.x, pos.y, pos.z,
                baseVel.x + noiseX, baseVel.y + noiseY, baseVel.z + noiseZ
        );
    }
}