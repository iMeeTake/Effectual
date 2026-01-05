package com.imeetake.effectual.effects.GoldGlow;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class LanternGlowEffect {

    private static final RandomSource RAND = RandomSource.create();
    private static final int SAMPLES_PER_TICK = 100;
    private static final int RADIUS = 6;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().lanternImprovements
                    || client.level == null
                    || client.player == null
                    || client.isPaused())
                return;

            spawnNearPlayer(client);
        });
    }

    private static void spawnNearPlayer(Minecraft client) {
        BlockPos center = client.player.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int startX = center.getX();
        int startY = center.getY();
        int startZ = center.getZ();

        for (int i = 0; i < SAMPLES_PER_TICK; i++) {
            int dx = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;
            int dy = RAND.nextInt(5) - 2;
            int dz = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;

            pos.set(startX + dx, startY + dy, startZ + dz);

            BlockState state = client.level.getBlockState(pos);

            if (!state.is(Blocks.LANTERN)) continue;

            glow(pos);
        }
    }

    private static void glow(BlockPos pos) {
        double x = pos.getX() + 0.5 + (RAND.nextDouble() - 0.5) * 0.55;
        double y = pos.getY() + 0.15;
        double z = pos.getZ() + 0.5 + (RAND.nextDouble() - 0.5) * 0.55;

        TClientParticles.spawn(
                ModParticles.GOLD_GLOW.get(),
                x, y, z,
                0, -0.002, 0
        );
    }
}