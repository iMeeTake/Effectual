package com.imeetake.effectual.effects.CauldronFill;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CauldronFillEffect {

    private enum Kind {EMPTY, WATER, LAVA, POWDER}

    private static final class Snapshot {
        final Kind kind;
        final int level;

        Snapshot(Kind k, int l) {
            this.kind = k;
            this.level = l;
        }
    }

    private static final Map<Long, Snapshot> stateCache = new HashMap<>();
    private static final RandomSource RAND = RandomSource.create();

    private static int backgroundScanTimer = 0;
    private static int cleanupTimer = 0;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().cauldronFillEffect
                    || client.level == null
                    || client.player == null
                    || client.isPaused())
                return;

            scanTargetedBlock(client);

            if (++backgroundScanTimer >= 10) {
                scanArea(client);
                backgroundScanTimer = 0;
            }

            if (++cleanupTimer >= 100) {
                cleanup(client.player.blockPosition());
                cleanupTimer = 0;
            }
        });
    }

    private static void scanTargetedBlock(Minecraft client) {
        HitResult hit = client.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos pos = blockHit.getBlockPos();

        if (client.player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 36) return;

        checkBlock(client, pos.getX(), pos.getY(), pos.getZ());
    }

    private static void scanArea(Minecraft client) {
        BlockPos center = client.player.blockPosition();
        int radius = 6;
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -1; dy <= 2; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    checkBlock(client, cx + dx, cy + dy, cz + dz);
                }
            }
        }
    }

    /**
     * Единая логика проверки состояния блока
     */
    private static void checkBlock(Minecraft client, int x, int y, int z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
        BlockState state = client.level.getBlockState(pos);

        Kind kind = kindOf(state.getBlock());
        if (kind == null) return;

        long posKey = BlockPos.asLong(x, y, z);
        int level = currentLevel(state, kind);
        Snapshot prev = stateCache.get(posKey);

        if (prev == null) {
            stateCache.put(posKey, new Snapshot(kind, level));
            return;
        }

        boolean changed = false;

        if (kind != prev.kind) {
            if (kind != Kind.EMPTY) {
                spawnBurst(pos, kind, Math.max(1, level));
            }
            changed = true;
        } else if (level > prev.level) {
            spawnBurst(pos, kind, level - prev.level);
            changed = true;
        } else if (level != prev.level) {
            changed = true;
        }

        if (changed) {
            stateCache.put(posKey, new Snapshot(kind, level));
        }
    }

    private static void cleanup(BlockPos center) {
        long maxDistSqr = 16 * 16;
        Iterator<Map.Entry<Long, Snapshot>> it = stateCache.entrySet().iterator();

        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        while (it.hasNext()) {
            long posLong = it.next().getKey();
            int x = BlockPos.getX(posLong);
            int y = BlockPos.getY(posLong);
            int z = BlockPos.getZ(posLong);

            double dX = cx - x;
            double dY = cy - y;
            double dZ = cz - z;
            double distSqr = dX * dX + dY * dY + dZ * dZ;

            if (distSqr > maxDistSqr) {
                it.remove();
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
            IntegerProperty p = BlockStateProperties.LEVEL_CAULDRON;
            return s.hasProperty(p) ? s.getValue(p) : 0;
        }
        if (k == Kind.LAVA) return 3;
        return 0;
    }

    private static void spawnBurst(BlockPos pos, Kind kind, int delta) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.94;
        double z = pos.getZ() + 0.5;

        int base = kind == Kind.WATER ? 6 : kind == Kind.LAVA ? 5 : 7;
        int count = base + RAND.nextInt(3) + Math.min(4, delta * 2);

        for (int i = 0; i < count; i++) {
            double ox = (RAND.nextDouble() - 0.5) * 0.6;
            double oz = (RAND.nextDouble() - 0.5) * 0.6;
            double vx = (RAND.nextDouble() - 0.5) * 0.05;
            double vz = (RAND.nextDouble() - 0.5) * 0.05;

            switch (kind) {
                case WATER -> {
                    double vy = 0.05 + RAND.nextDouble() * 0.05;
                    TClientParticles.spawn(ParticleTypes.SPLASH, x + ox, y, z + oz, vx, vy, vz);
                    if (RAND.nextFloat() < 0.4f) {
                        TClientParticles.spawn(ParticleTypes.BUBBLE, x + ox * 0.7, y - 0.2, z + oz * 0.7, 0, 0.02 + RAND.nextDouble() * 0.02, 0);
                    }
                }
                case LAVA -> {
                    double vy = 0.04 + RAND.nextDouble() * 0.04;
                    TClientParticles.spawn(ParticleTypes.LAVA, x + ox, y, z + oz, vx, vy, vz);
                    if (RAND.nextFloat() < 0.3f) {
                        TClientParticles.spawn(ParticleTypes.SMOKE, x + ox, y + 0.1, z + oz, 0, 0.02, 0);
                    }
                }
                case POWDER -> {
                    double vy = 0.03 + RAND.nextDouble() * 0.04;
                    TClientParticles.spawn(ParticleTypes.SNOWFLAKE, x + ox, y + 0.1, z + oz, vx * 0.5, vy, vz * 0.5);
                }
                default -> {
                }
            }
        }
    }
}