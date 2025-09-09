package com.imeetake.effectual.effects.Sparks;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class FurnaceSparksEffect {

    private static final Random RAND = Random.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.furnaceSparks() || client.world == null || client.player == null || client.isPaused()) return;
            if (++tickCounter < 8) return;
            tickCounter = 0;
            spawnNearPlayer(client);
        });
    }

    private static void spawnNearPlayer(MinecraftClient client) {
        BlockPos center = client.player.getBlockPos();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int radius = 6;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = client.world.getBlockState(pos);

                    boolean isFurnace = state.isOf(Blocks.FURNACE);
                    boolean isBlast = state.isOf(Blocks.BLAST_FURNACE);
                    if (!isFurnace && !isBlast) continue;

                    if (!state.getOrEmpty(Properties.LIT).orElse(false)) continue;
                    if (RAND.nextFloat() > 0.88f) continue;

                    Direction facing = state.get(Properties.HORIZONTAL_FACING);
                    if (facing == null) continue;

                    spawnFrontSparks(client, pos, facing);
                }
            }
        }
    }

    private static void spawnFrontSparks(MinecraftClient client, BlockPos pos, Direction facing) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.34 + (RAND.nextDouble() - 0.5) * 0.04;
        double cz = pos.getZ() + 0.5;

        double nx = facing.getOffsetX();
        double nz = facing.getOffsetZ();

        double px = cx + nx * 0.505;
        double pz = cz + nz * 0.505;

        double tx = nz;
        double tz = -nx;

        double lateral = (RAND.nextDouble() - 0.5) * 0.12;
        px += tx * lateral;
        pz += tz * lateral;

        int n = RAND.nextFloat() < 0.25f ? 2 : 1;
        for (int i = 0; i < n; i++) {
            double out = 0.0012 + RAND.nextDouble() * 0.0012;
            double up = 0.006 + RAND.nextDouble() * 0.003;
            double vx = nx * out + tx * (RAND.nextDouble() - 0.5) * 0.0005;
            double vz = nz * out + tz * (RAND.nextDouble() - 0.5) * 0.0005;
            TClientParticles.spawn(new TParticleEffectSimple(ModParticles.SPARK), px, cy, pz, vx, up, vz);
        }
    }
}
