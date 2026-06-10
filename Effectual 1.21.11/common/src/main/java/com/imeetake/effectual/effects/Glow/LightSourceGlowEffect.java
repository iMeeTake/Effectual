package com.imeetake.effectual.effects.Glow;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class LightSourceGlowEffect {

    private static final RandomSource RAND = RandomSource.create();
    private static final int RADIUS = 6;
    private static final int SAMPLES_PER_TICK = 128;
    private static final int MIN_SCAN_Y = -2;
    private static final int MAX_SCAN_Y = 3;
    private static final int MAX_SOURCES = 192;
    private static final int SPAWN_PHASE_MASK = 3;

    private static final LongOpenHashSet lightPositions = new LongOpenHashSet();
    private static final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    private static int scanX = -RADIUS;
    private static int scanY = MIN_SCAN_Y;
    private static int scanZ = -RADIUS;
    private static int spawnTick = 0;
    private static Level lastLevel = null;
    private static BlockPos lastPlayerPos = null;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null) {
                clearSources();
                lastLevel = null;
                lastPlayerPos = null;
                return;
            }
            if (client.player == null) return;
            if (client.isPaused()) return;

            if (lastLevel != client.level) {
                clearSources();
                resetScanPosition();
                lastLevel = client.level;
                lastPlayerPos = null;
            }

            boolean torchEnabled = EffectualConfig.get().torchImprovements;
            boolean lanternEnabled = EffectualConfig.get().lanternImprovements;

            if (!torchEnabled && !lanternEnabled) {
                clearSources();
                return;
            }

            BlockPos playerPos = client.player.blockPosition();
            if (lastPlayerPos != null && playerPos.distSqr(lastPlayerPos) > 144) {
                clearSources();
                resetScanPosition();
            }
            lastPlayerPos = playerPos;

            scanSources(client.level, playerPos, torchEnabled, lanternEnabled);
            spawnCached(client.level, playerPos, torchEnabled, lanternEnabled);
        });
    }

    private static void clearSources() {
        lightPositions.clear();
        spawnTick = 0;
    }

    private static void scanSources(Level level, BlockPos center, boolean torchEnabled, boolean lanternEnabled) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int i = 0; i < SAMPLES_PER_TICK; i++) {
            pos.set(cx + scanX, cy + scanY, cz + scanZ);
            BlockState state = level.getBlockState(pos);

            trackScannedPosition(state, torchEnabled, lanternEnabled);

            advanceScanPosition();
        }
    }

    private static void trackScannedPosition(BlockState state, boolean torchEnabled, boolean lanternEnabled) {
        long key = pos.asLong();

        if (isEnabledLightSource(state, torchEnabled, lanternEnabled)) {
            if (lightPositions.contains(key)) return;
            if (lightPositions.size() >= MAX_SOURCES) return;
            lightPositions.add(key);
            spawnLightGlow(pos, state, torchEnabled, lanternEnabled);
        } else {
            lightPositions.remove(key);
        }
    }

    private static void spawnCached(Level level, BlockPos center, boolean torchEnabled, boolean lanternEnabled) {
        if (lightPositions.isEmpty()) return;

        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        int phase = ++spawnTick & SPAWN_PHASE_MASK;

        var iterator = lightPositions.longIterator();
        while (iterator.hasNext()) {
            long key = iterator.nextLong();
            if ((Long.hashCode(key) & SPAWN_PHASE_MASK) != phase) continue;

            int x = BlockPos.getX(key);
            int y = BlockPos.getY(key);
            int z = BlockPos.getZ(key);

            if (x < cx - RADIUS || x > cx + RADIUS || y < cy + MIN_SCAN_Y || y > cy + MAX_SCAN_Y || z < cz - RADIUS || z > cz + RADIUS) {
                iterator.remove();
                continue;
            }

            pos.set(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (!spawnLightGlow(pos, state, torchEnabled, lanternEnabled)) {
                iterator.remove();
            }
        }
    }

    private static boolean isEnabledLightSource(BlockState state, boolean torchEnabled, boolean lanternEnabled) {
        return torchEnabled && (
                state.is(Blocks.TORCH)
                        || state.is(Blocks.WALL_TORCH)
                        || state.is(Blocks.SOUL_TORCH)
                        || state.is(Blocks.SOUL_WALL_TORCH)
        ) || lanternEnabled && (
                state.is(Blocks.LANTERN)
                        || state.is(Blocks.SOUL_LANTERN)
        );
    }

    private static boolean spawnLightGlow(BlockPos blockPos, BlockState state, boolean torchEnabled, boolean lanternEnabled) {
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();

        if (torchEnabled) {
            if (state.is(Blocks.TORCH)) {
                spawnTorchGlow(x, y, z, null, false, false);
                return true;
            }
            if (state.is(Blocks.WALL_TORCH)) {
                spawnTorchGlow(x, y, z, state.getValue(BlockStateProperties.HORIZONTAL_FACING), true, false);
                return true;
            }
            if (state.is(Blocks.SOUL_TORCH)) {
                spawnTorchGlow(x, y, z, null, false, true);
                return true;
            }
            if (state.is(Blocks.SOUL_WALL_TORCH)) {
                spawnTorchGlow(x, y, z, state.getValue(BlockStateProperties.HORIZONTAL_FACING), true, true);
                return true;
            }
        }

        if (lanternEnabled) {
            if (state.is(Blocks.LANTERN)) {
                spawnLanternGlow(x, y, z, false);
                return true;
            }
            if (state.is(Blocks.SOUL_LANTERN)) {
                spawnLanternGlow(x, y, z, true);
                return true;
            }
        }

        return false;
    }

    private static void resetScanPosition() {
        scanX = -RADIUS;
        scanY = MIN_SCAN_Y;
        scanZ = -RADIUS;
    }

    private static void advanceScanPosition() {
        if (++scanX > RADIUS) {
            scanX = -RADIUS;
            if (++scanZ > RADIUS) {
                scanZ = -RADIUS;
                if (++scanY > MAX_SCAN_Y) {
                    scanY = MIN_SCAN_Y;
                }
            }
        }
    }

    private static void spawnTorchGlow(int blockX, int blockY, int blockZ, Direction facing, boolean isWall, boolean isSoul) {
        double x, y, z;

        if (isWall) {
            Direction opposite = facing.getOpposite();
            double offset = 0.27;
            x = blockX + 0.5 + (opposite.getStepX() * offset) + (RAND.nextDouble() - 0.5) * 0.1;
            y = blockY + 0.65;
            z = blockZ + 0.5 + (opposite.getStepZ() * offset) + (RAND.nextDouble() - 0.5) * 0.1;
        } else {
            x = blockX + 0.5 + (RAND.nextDouble() - 0.5) * 0.1;
            y = blockY + 0.65;
            z = blockZ + 0.5 + (RAND.nextDouble() - 0.5) * 0.1;
        }

        SimpleParticleType particle = isSoul ? ModParticles.SOUL_GLOW.get() : ModParticles.GOLD_GLOW.get();
        EffectualClientParticles.spawn(particle, x, y, z, 0, 0, 0);
    }

    private static void spawnLanternGlow(int blockX, int blockY, int blockZ, boolean isSoul) {
        double angle = RAND.nextDouble() * Math.PI * 2;
        double radius = 0.2 + RAND.nextDouble() * 0.05;

        double x = blockX + 0.5 + Math.cos(angle) * radius;
        double y = blockY + 0.1 + RAND.nextDouble() * 0.15;
        double z = blockZ + 0.5 + Math.sin(angle) * radius;

        SimpleParticleType particle = isSoul ? ModParticles.SOUL_GLOW.get() : ModParticles.GOLD_GLOW.get();
        EffectualClientParticles.spawn(particle, x, y, z, 0, 0, 0);
    }
}
