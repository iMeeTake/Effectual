package com.imeetake.effectual.effects.SteamEffect;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SteamEffect {

    private static final Long2ObjectOpenHashMap<SteamSource> steamSources = new Long2ObjectOpenHashMap<>();

    private static final RandomSource RANDOM = RandomSource.create();

    private static final int POSITIONS_PER_TICK = 250;
    private static final int RADIUS = 20;
    private static final int HEIGHT = 5;
    private static final int MAX_POSITIONS = 512;
    private static final int MAX_PARTICLES_PER_TICK = 48;
    private static final int SOURCE_VALIDATION_INTERVAL = 28;
    private static final int SOURCE_VALIDATION_JITTER_MASK = 15;

    private static final int[][] STEAM_OFFSETS = {
            {1, 1, 0}, {1, -1, 0}, {-1, 1, 0}, {-1, -1, 0},
            {0, 1, 1}, {0, 1, -1}, {0, -1, 1}, {0, -1, -1},
            {1, 0, 1}, {1, 0, -1}, {-1, 0, 1}, {-1, 0, -1},
            {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}
    };

    private static final BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();
    private static final BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
    private static final BlockPos.MutableBlockPos abovePos = new BlockPos.MutableBlockPos();

    private static int scanX = -RADIUS;
    private static int scanY = -HEIGHT;
    private static int scanZ = -RADIUS;
    private static int spawnTick = 0;

    private static Level lastLevel = null;
    private static BlockPos lastPlayerPos = null;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null) {
                clearPositions();
                lastLevel = null;
                lastPlayerPos = null;
                return;
            }

            if (lastLevel != client.level) {
                clearPositions();
                resetScanPosition();
                lastLevel = client.level;
                lastPlayerPos = null;
            }

            if (client.isPaused()) return;
            if (!EffectualConfig.get().steamEffect) {
                clearPositions();
                return;
            }
            if (client.player == null) return;

            BlockPos playerPos = client.player.blockPosition();

            if (lastPlayerPos != null && playerPos.distSqr(lastPlayerPos) > 400) {
                clearPositions();
                resetScanPosition();
            }
            lastPlayerPos = playerPos;

            updateSteamPositions(client.level, playerPos);
            spawnSteam(client.level, playerPos);
        });
    }

    private static void clearPositions() {
        steamSources.clear();
    }

    private static void resetScanPosition() {
        scanX = -RADIUS;
        scanY = -HEIGHT;
        scanZ = -RADIUS;
    }

    private static void updateSteamPositions(Level level, BlockPos center) {
        for (int i = 0; i < POSITIONS_PER_TICK; i++) {
            scanPos.set(center.getX() + scanX, center.getY() + scanY, center.getZ() + scanZ);

            BlockState state = level.getBlockState(scanPos);
            if (isHotBlock(state)) {
                addSteamPositionsAroundHotBlock(level, scanPos);
            }

            if (++scanX > RADIUS) {
                scanX = -RADIUS;
                if (++scanZ > RADIUS) {
                    scanZ = -RADIUS;
                    if (++scanY > HEIGHT) {
                        scanY = -HEIGHT;
                    }
                }
            }
        }
    }

    private static boolean isHotBlock(BlockState state) {
        return state.getFluidState().is(FluidTags.LAVA) || state.is(Blocks.LAVA_CAULDRON);
    }

    private static void addSteamPositionsAroundHotBlock(Level level, BlockPos hotPos) {
        for (int[] offset : STEAM_OFFSETS) {
            neighborPos.set(hotPos.getX() + offset[0], hotPos.getY() + offset[1], hotPos.getZ() + offset[2]);
            BlockState neighbor = level.getBlockState(neighborPos);
            if (neighbor.getFluidState().is(FluidTags.WATER)) {
                long posLong = neighborPos.asLong();
                SteamSource source = steamSources.get(posLong);
                if (source == null) {
                    if (steamSources.size() >= MAX_POSITIONS) continue;
                    source = new SteamSource();
                    steamSources.put(posLong, source);
                    if (!updateSteamSource(level, neighborPos, neighbor, source, posLong)) {
                        steamSources.remove(posLong);
                    }
                } else if (source.nextValidationTick <= spawnTick && !updateSteamSource(level, neighborPos, neighbor, source, posLong)) {
                    steamSources.remove(posLong);
                }
            }
        }
    }

    private static boolean updateSteamSource(Level level, BlockPos pos, BlockState state, SteamSource source, long posLong) {
        if (!state.getFluidState().is(FluidTags.WATER)) return false;

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        double waterX = x + 0.5;
        double waterZ = z + 0.5;
        double sumX = 0.0;
        double sumZ = 0.0;
        int contacts = 0;

        for (int[] offset : STEAM_OFFSETS) {
            neighborPos.set(x + offset[0], y + offset[1], z + offset[2]);
            if (isHotBlock(level.getBlockState(neighborPos))) {
                sumX += waterX + offset[0] * 0.5;
                sumZ += waterZ + offset[2] * 0.5;
                contacts++;
            }
        }

        if (contacts == 0) return false;

        source.x = sumX / contacts;
        source.y = y + state.getFluidState().getHeight(level, pos) + 0.04;
        source.z = sumZ / contacts;
        source.spread = 0.12 + Math.min(contacts, 5) * 0.025;
        source.hasSpace = hasSteamSpace(level, x, y, z);
        source.nextValidationTick = spawnTick + SOURCE_VALIDATION_INTERVAL + (Long.hashCode(posLong) & SOURCE_VALIDATION_JITTER_MASK);
        return true;
    }

    private static void spawnSteam(Level level, BlockPos center) {
        if (steamSources.isEmpty()) return;

        spawnTick++;

        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        long maxDistSqr = 22L * 22L;
        int activeCount = steamSources.size();
        int phaseMask = phaseMaskFor(activeCount);
        int phase = spawnTick & phaseMask;
        int particlesLeft = MAX_PARTICLES_PER_TICK;

        var iterator = steamSources.long2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            long posLong = entry.getLongKey();

            int hash = Long.hashCode(posLong);
            if (phaseMask != 0 && (hash & phaseMask) != phase) continue;

            int x = BlockPos.getX(posLong);
            int y = BlockPos.getY(posLong);
            int z = BlockPos.getZ(posLong);

            long dx = cx - x;
            long dy = cy - y;
            long dz = cz - z;
            if (dx * dx + dy * dy + dz * dz > maxDistSqr) {
                iterator.remove();
                continue;
            }

            SteamSource source = entry.getValue();

            if (source.nextValidationTick <= spawnTick) {
                scanPos.set(x, y, z);
                BlockState state = level.getBlockState(scanPos);
                if (!updateSteamSource(level, scanPos, state, source, posLong)) {
                    iterator.remove();
                    continue;
                }
            }

            if (particlesLeft > 0 && source.hasSpace && shouldSpawnForLoad(activeCount)) {
                particlesLeft -= spawnSteamAt(source, activeCount, particlesLeft);
            }
        }
    }

    private static int phaseMaskFor(int activeCount) {
        if (activeCount <= 12) return 0;
        if (activeCount <= 48) return 1;
        if (activeCount <= 128) return 3;
        return 7;
    }

    private static boolean shouldSpawnForLoad(int activeCount) {
        if (activeCount <= 64) return true;
        if (activeCount <= 256) return RANDOM.nextInt(3) != 0;
        return RANDOM.nextBoolean();
    }

    private static boolean hasSteamSpace(Level level, int x, int y, int z) {
        abovePos.set(x, y + 1, z);
        BlockState state = level.getBlockState(abovePos);
        var fluidState = state.getFluidState();
        return state.isAir() || (
                !fluidState.is(FluidTags.WATER)
                        && !fluidState.is(FluidTags.LAVA)
                        && !state.isCollisionShapeFullBlock(level, abovePos)
        );
    }

    private static int spawnSteamAt(SteamSource source, int activeCount, int particleBudget) {
        int count = Math.min(particleBudget, particleCountFor(activeCount));

        for (int i = 0; i < count; i++) {
            double layer = count == 1 ? RANDOM.nextDouble() : (i + RANDOM.nextDouble() * 0.65) / count;
            double height = 0.02 + layer * 1.05 + RANDOM.nextDouble() * 0.08;
            double radius = 0.035 + layer * source.spread;
            double angle = RANDOM.nextDouble() * Math.PI * 2.0;
            double outX = Math.cos(angle);
            double outZ = Math.sin(angle);
            double px = source.x + outX * radius + (RANDOM.nextDouble() - 0.5) * 0.035;
            double py = source.y + height;
            double pz = source.z + outZ * radius + (RANDOM.nextDouble() - 0.5) * 0.035;
            double vx = outX * (0.006 + layer * 0.012) + (RANDOM.nextDouble() - 0.5) * 0.006;
            double vy = 0.04 + RANDOM.nextDouble() * 0.035 + (1.0 - layer) * 0.015;
            double vz = outZ * (0.006 + layer * 0.012) + (RANDOM.nextDouble() - 0.5) * 0.006;

            EffectualClientParticles.spawnVanilla(
                    ParticleTypes.POOF,
                    px, py, pz,
                    vx, vy, vz
            );
        }

        return count;
    }

    private static int particleCountFor(int activeCount) {
        if (activeCount <= 8) return 3 + RANDOM.nextInt(2);
        if (activeCount <= 32) return 2 + RANDOM.nextInt(2);
        return 1 + RANDOM.nextInt(2);
    }

    private static class SteamSource {
        private double x;
        private double y;
        private double z;
        private double spread;
        private boolean hasSpace;
        private int nextValidationTick;
    }
}
