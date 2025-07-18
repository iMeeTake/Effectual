package com.imeetake.effectual.effects.SoulGlow;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class SoulLanternGlowEffect {

    private static final Random RAND = Random.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.lanternImprovements() || client.world == null || client.player == null || client.isPaused()) return;
            if (++tickCounter < 5) return;
            tickCounter = 0;
            spawn(client);
        });
    }

    private static void spawn(MinecraftClient client) {
        BlockPos center = client.player.getBlockPos();
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int dx = -8; dx <= 8; dx++) {
            for (int dy = -8; dy <= 8; dy++) {
                for (int dz = -8; dz <= 8; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (!client.world.getBlockState(pos).isOf(Blocks.SOUL_LANTERN)) continue;
                    if (!client.world.getFluidState(pos).isEmpty()) continue;
                    if (RAND.nextFloat() >= 0.2f) continue;
                    glow(pos);
                }
            }
        }
    }

    private static void glow(BlockPos pos) {
        double x = pos.getX() + 0.5 + (RAND.nextDouble() - 0.5) * 0.8;
        double z = pos.getZ() + 0.5 + (RAND.nextDouble() - 0.5) * 0.8;
        double y = pos.getY() + 0.25;

        TClientParticles.spawn(
                new TParticleEffectSimple(ModParticles.SOUL_GLOW),
                x, y, z,
                0, -0.002, 0);
    }
}
