package com.imeetake.effectual.effects.Sparks;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
import com.imeetake.effectual.ModParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.registries.RegistrySupplier;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FireImprovements {

    private static final RandomSource RAND = RandomSource.create();
    private static final LongOpenHashSet firePositions = new LongOpenHashSet();
    private static final int DISCOVERY_SAMPLES_PER_TICK = 256;
    private static final int RADIUS = 8;
    private static final int MAX_POSITIONS = 1024;
    private static final int MAX_SPARKS_PER_SPAWN_TICK = 64;
    private static final float SPARK_CHANCE = 0.67f;

    private static final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    private static int scanX = -RADIUS;
    private static int scanY = -RADIUS;
    private static int scanZ = -RADIUS;
    private static int tickCounter = 0;
    private static Level lastLevel = null;
    private static BlockPos lastPlayerPos = null;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null) {
                firePositions.clear();
                lastLevel = null;
                lastPlayerPos = null;
                return;
            }

            if (lastLevel != client.level) {
                firePositions.clear();
                resetScanPosition();
                lastLevel = client.level;
                lastPlayerPos = null;
            }

            if (client.isPaused()) return;
            if (!EffectualConfig.get().fireImprovements) return;
            if (client.player == null) return;

            BlockPos playerPos = client.player.blockPosition();

            if (lastPlayerPos != null && playerPos.distSqr(lastPlayerPos) > 400) {
                firePositions.clear();
                resetScanPosition();
            }
            lastPlayerPos = playerPos;

            discover(client.level, playerPos);

            if (++tickCounter < 3) return;
            tickCounter = 0;

            spawn(client.level, playerPos);
        });
    }

    private static void discover(Level level, BlockPos center) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int i = 0; i < DISCOVERY_SAMPLES_PER_TICK; i++) {
            pos.set(cx + scanX, cy + scanY, cz + scanZ);

            BlockState state = level.getBlockState(pos);
            long key = pos.asLong();

            if (isFire(state)) {
                if (firePositions.size() < MAX_POSITIONS) {
                    firePositions.add(key);
                }
            } else {
                firePositions.remove(key);
            }

            advanceScanPosition();
        }
    }

    private static void resetScanPosition() {
        scanX = -RADIUS;
        scanY = -RADIUS;
        scanZ = -RADIUS;
    }

    private static void advanceScanPosition() {
        if (++scanX > RADIUS) {
            scanX = -RADIUS;
            if (++scanZ > RADIUS) {
                scanZ = -RADIUS;
                if (++scanY > RADIUS) {
                    scanY = -RADIUS;
                }
            }
        }
    }

    private static void spawn(Level level, BlockPos center) {
        if (firePositions.isEmpty()) return;

        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        long maxDistSqr = 20L * 20L;
        int sparksLeft = MAX_SPARKS_PER_SPAWN_TICK;

        var iterator = firePositions.longIterator();
        while (iterator.hasNext()) {
            long posLong = iterator.nextLong();
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

            pos.set(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (!isFire(state)) {
                iterator.remove();
                continue;
            }

            if (RAND.nextFloat() > SPARK_CHANCE) continue;
            if (sparksLeft <= 0) continue;

            if (spawnFireSpark(pos, state, state.is(Blocks.SOUL_FIRE))) {
                sparksLeft--;
            }
        }
    }

    private static boolean isFire(BlockState state) {
        return state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE);
    }

    private static boolean spawnFireSpark(BlockPos blockPos, BlockState state, boolean isSoul) {
        double x, y, z;

        if (isSoul) {
            x = blockPos.getX() + RAND.nextDouble();
            y = blockPos.getY() + 0.4 + RAND.nextDouble() * 0.5;
            z = blockPos.getZ() + RAND.nextDouble();
        } else {
            boolean north = state.hasProperty(FireBlock.NORTH) && state.getValue(FireBlock.NORTH);
            boolean south = state.hasProperty(FireBlock.SOUTH) && state.getValue(FireBlock.SOUTH);
            boolean east = state.hasProperty(FireBlock.EAST) && state.getValue(FireBlock.EAST);
            boolean west = state.hasProperty(FireBlock.WEST) && state.getValue(FireBlock.WEST);
            boolean up = state.hasProperty(FireBlock.UP) && state.getValue(FireBlock.UP);

            boolean hasHorizontal = north || south || east || west;

            if (up && !hasHorizontal) return false;

            if (!hasHorizontal) {
                x = blockPos.getX() + RAND.nextDouble();
                y = blockPos.getY() + 0.4 + RAND.nextDouble() * 0.5;
                z = blockPos.getZ() + RAND.nextDouble();
            } else {
                x = blockPos.getX() + 0.5;
                y = blockPos.getY() + 0.3 + RAND.nextDouble() * 0.5;
                z = blockPos.getZ() + 0.5;

                double offsetX = 0;
                double offsetZ = 0;
                int sides = 0;

                if (north) {
                    offsetZ -= 1;
                    sides++;
                }
                if (south) {
                    offsetZ += 1;
                    sides++;
                }
                if (east) {
                    offsetX += 1;
                    sides++;
                }
                if (west) {
                    offsetX -= 1;
                    sides++;
                }

                if (sides > 0) {
                    double offset = 0.3 + RAND.nextDouble() * 0.15;
                    double normX = offsetX / sides;
                    double normZ = offsetZ / sides;

                    x += normX * offset;
                    z += normZ * offset;

                    double perpX = -normZ;
                    double perpZ = normX;
                    double lateral = (RAND.nextDouble() - 0.5) * 0.8;
                    x += perpX * lateral;
                    z += perpZ * lateral;
                }
            }
        }

        double angle = RAND.nextDouble() * Math.PI * 2;
        double speed = 0.01 + RAND.nextDouble() * 0.03;

        double dx = Math.cos(angle) * speed;
        double dy = 0.04 + RAND.nextDouble() * 0.06;
        double dz = Math.sin(angle) * speed;

        RegistrySupplier<SimpleParticleType> particle = isSoul ? ModParticles.SOUL_SPARK : ModParticles.SPARK;
        EffectualClientParticles.spawn(particle, x, y, z, dx, dy, dz);
        return true;
    }
}
