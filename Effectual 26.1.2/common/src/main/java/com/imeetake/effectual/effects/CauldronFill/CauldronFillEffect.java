package com.imeetake.effectual.effects.CauldronFill;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

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

    private static final int MAX_CACHE_SIZE = 256;
    private static final Long2ObjectLinkedOpenHashMap<Snapshot> stateCache = new Long2ObjectLinkedOpenHashMap<>();
    private static final RandomSource RAND = RandomSource.create();

    private static final int SAMPLES_PER_TICK = 50;
    private static final int RADIUS = 6;

    private static final BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();

    private static int backgroundScanTimer = 0;
    private static int cleanupTimer = 0;
    private static Level lastLevel = null;
    private static BlockPos lastPlayerPos = null;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.isPaused()) return;
            if (!EffectualConfig.get().cauldronFillEffect) return;
            if (client.level == null || client.player == null) return;

            if (lastLevel != client.level) {
                stateCache.clear();
                lastLevel = client.level;
                lastPlayerPos = null;
            }

            BlockPos playerPos = client.player.blockPosition();

            if (lastPlayerPos != null && playerPos.distSqr(lastPlayerPos) > 256) {
                stateCache.clear();
            }
            lastPlayerPos = playerPos;

            scanTargetedBlock(client);

            if (++backgroundScanTimer >= 10) {
                scanArea(client.level, playerPos);
                backgroundScanTimer = 0;
            }

            if (++cleanupTimer >= 60) {
                cleanup(playerPos);
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

        checkBlock(client.level, pos.getX(), pos.getY(), pos.getZ());
    }

    private static void scanArea(Level level, BlockPos center) {
        int startX = center.getX();
        int startY = center.getY();
        int startZ = center.getZ();

        for (int i = 0; i < SAMPLES_PER_TICK; i++) {
            int dx = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;
            int dy = RAND.nextInt(4) - 1;
            int dz = RAND.nextInt(RADIUS * 2 + 1) - RADIUS;

            checkBlock(level, startX + dx, startY + dy, startZ + dz);
        }
    }

    private static void checkBlock(Level level, int x, int y, int z) {
        scanPos.set(x, y, z);
        BlockState state = level.getBlockState(scanPos);

        Kind kind = kindOf(state.getBlock());
        if (kind == null) return;

        long posKey = BlockPos.asLong(x, y, z);
        int currentLevel = currentLevel(state, kind);
        Snapshot prev = stateCache.get(posKey);

        if (prev == null) {
            if (stateCache.size() >= MAX_CACHE_SIZE) {
                stateCache.removeFirst();
            }
            stateCache.put(posKey, new Snapshot(kind, currentLevel));
            return;
        }

        boolean changed = false;

        if (kind != prev.kind) {
            if (kind != Kind.EMPTY) {
                spawnBurst(scanPos, kind, Math.max(1, currentLevel));
            }
            changed = true;
        } else if (currentLevel > prev.level) {
            spawnBurst(scanPos, kind, currentLevel - prev.level);
            changed = true;
        } else if (currentLevel != prev.level) {
            changed = true;
        }

        if (changed) {
            stateCache.getAndMoveToLast(posKey);
            stateCache.put(posKey, new Snapshot(kind, currentLevel));
        }
    }

    private static void cleanup(BlockPos center) {
        if (stateCache.isEmpty()) return;

        long maxDistSqr = 10L * 10L;
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        var iterator = stateCache.long2ObjectEntrySet().fastIterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            long posLong = entry.getLongKey();

            int x = BlockPos.getX(posLong);
            int y = BlockPos.getY(posLong);
            int z = BlockPos.getZ(posLong);

            long dX = cx - x;
            long dY = cy - y;
            long dZ = cz - z;
            long distSqr = dX * dX + dY * dY + dZ * dZ;

            if (distSqr > maxDistSqr) {
                iterator.remove();
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
                    EffectualClientParticles.spawn(ParticleTypes.SPLASH, x + ox, y, z + oz, vx, vy, vz);
                    if (RAND.nextFloat() < 0.4f) {
                        EffectualClientParticles.spawn(ParticleTypes.BUBBLE, x + ox * 0.7, y - 0.2, z + oz * 0.7, 0, 0.02 + RAND.nextDouble() * 0.02, 0);
                    }
                }
                case LAVA -> {
                    double vy = 0.04 + RAND.nextDouble() * 0.04;
                    EffectualClientParticles.spawn(ParticleTypes.LAVA, x + ox, y, z + oz, vx, vy, vz);
                    if (RAND.nextFloat() < 0.3f) {
                        EffectualClientParticles.spawn(ParticleTypes.SMOKE, x + ox, y + 0.1, z + oz, 0, 0.02, 0);
                    }
                }
                case POWDER -> {
                    double vy = 0.03 + RAND.nextDouble() * 0.04;
                    EffectualClientParticles.spawn(ParticleTypes.SNOWFLAKE, x + ox, y + 0.1, z + oz, vx * 0.5, vy, vz * 0.5);
                }
                default -> {
                }
            }
        }
    }
}
