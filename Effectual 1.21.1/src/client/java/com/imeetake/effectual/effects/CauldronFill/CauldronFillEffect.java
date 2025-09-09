package com.imeetake.effectual.effects.CauldronFill;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.Map;
import java.util.WeakHashMap;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class CauldronFillEffect {

    private enum Kind { EMPTY, WATER, LAVA, POWDER }
    private static final class Snapshot {
        final Kind kind;
        final int level;
        Snapshot(Kind k, int l) { this.kind = k; this.level = l; }
    }

    private static final Map<BlockPos, Snapshot> last = new WeakHashMap<>();
    private static final Random RAND = Random.create();
    private static int tickCounter = RAND.nextInt(4);

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.cauldronFillEffect() || client.world == null || client.player == null || client.isPaused()) return;
            if (++tickCounter < 3) return;
            tickCounter = 0;
            scan(client);
        });
    }

    private static void scan(MinecraftClient client) {
        BlockPos center = client.player.getBlockPos();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int radius = 6;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -1; dy <= 2; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = client.world.getBlockState(pos);
                    Kind kind = kindOf(state.getBlock());
                    if (kind == null) continue;

                    int level = currentLevel(state, kind);
                    Snapshot prev = last.get(pos);
                    if (prev == null) {
                        last.put(pos.toImmutable(), new Snapshot(kind, level));
                        continue;
                    }

                    if (kind != prev.kind || level > prev.level) {
                        spawnBurst(client, pos, kind, Math.max(1, level - Math.max(0, prev.level)));
                        last.put(pos.toImmutable(), new Snapshot(kind, level));
                    } else if (kind == prev.kind && level != prev.level) {
                        last.put(pos.toImmutable(), new Snapshot(kind, level));
                    }
                }
            }
        }
    }

    private static Kind kindOf(Block b) {
        if (b == Blocks.CAULDRON) return Kind.EMPTY;
        if (b == Blocks.WATER_CAULDRON) return Kind.WATER;
        if (b == Blocks.LAVA_CAULDRON) return Kind.LAVA;
        if (b == Blocks.POWDER_SNOW_CAULDRON) return Kind.POWDER;
        return null;
    }

    private static int currentLevel(BlockState s, Kind k) {
        if (k == Kind.WATER || k == Kind.POWDER) {
            IntProperty p = Properties.LEVEL_3;
            return s.contains(p) ? s.get(p) : 0;
        }
        if (k == Kind.LAVA) return 3;
        return 0;
    }

    private static void spawnBurst(MinecraftClient client, BlockPos pos, Kind kind, int delta) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.94;
        double z = pos.getZ() + 0.5;

        int base = kind == Kind.WATER ? 6 : kind == Kind.LAVA ? 5 : 7;
        int count = base + RAND.nextInt(3) + Math.min(2, delta);

        for (int i = 0; i < count; i++) {
            double ox = (RAND.nextDouble() - 0.5) * 0.6;
            double oz = (RAND.nextDouble() - 0.5) * 0.6;
            double vx = (RAND.nextDouble() - 0.5) * 0.02;
            double vz = (RAND.nextDouble() - 0.5) * 0.02;

            switch (kind) {
                case WATER -> {
                    double vy = 0.04 + RAND.nextDouble() * 0.03;
                    TClientParticles.spawn(ParticleTypes.SPLASH, x + ox, y, z + oz, vx, vy, vz);
                    if (RAND.nextFloat() < 0.35f) {
                        TClientParticles.spawn(ParticleTypes.BUBBLE, x + ox * 0.7, y - 0.15, z + oz * 0.7, 0, 0.01 + RAND.nextDouble() * 0.01, 0);
                    }
                }
                case LAVA -> {
                    double vy = 0.03 + RAND.nextDouble() * 0.02;
                    TClientParticles.spawn(ParticleTypes.LAVA, x + ox, y, z + oz, 0, vy, 0);
                    if (RAND.nextFloat() < 0.30f) {
                        TClientParticles.spawn(ParticleTypes.SMALL_FLAME, x + ox * 0.9, y + 0.02, z + oz * 0.9, 0, 0.005 + RAND.nextDouble() * 0.005, 0);
                    }
                    if (RAND.nextFloat() < 0.25f) {
                        TClientParticles.spawn(ParticleTypes.SMOKE, x + ox, y + 0.04, z + oz, 0, 0.01, 0);
                    }
                }
                case POWDER -> {
                    double vy = 0.02 + RAND.nextDouble() * 0.02;
                    TClientParticles.spawn(ParticleTypes.SNOWFLAKE, x + ox, y + 0.02, z + oz, vx * 0.3, vy, vz * 0.3);
                }
                default -> {}
            }
        }
    }
}
