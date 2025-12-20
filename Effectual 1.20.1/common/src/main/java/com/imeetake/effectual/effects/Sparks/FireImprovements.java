package com.imeetake.effectual.effects.Sparks;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FireImprovements {

    private static final RandomSource RAND = RandomSource.create();
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null || client.player == null || client.isPaused()) return;
            if (!EffectualConfig.get().fireImprovements) return;
            if (++tickCounter < 3) return;
            tickCounter = 0;
            spawn(client);
        });
    }

    private static void spawn(Minecraft client) {
        BlockPos center = client.player.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = -8; dx <= 8; dx++) {
            for (int dy = -8; dy <= 8; dy++) {
                for (int dz = -8; dz <= 8; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = client.level.getBlockState(pos);

                    if (!state.is(Blocks.FIRE)) continue;
                    if (RAND.nextFloat() >= 0.65f) continue;

                    spark(client, pos);
                }
            }
        }
    }

    private static void spark(Minecraft client, BlockPos pos) {
        Direction attachedFace = getFireAttachmentFace(client, pos);

        double x, y, z;

        if (attachedFace == Direction.DOWN) {
            x = pos.getX() + RAND.nextDouble();
            y = pos.getY() + 0.4 + RAND.nextDouble() * 0.5;
            z = pos.getZ() + RAND.nextDouble();
        } else {
            x = pos.getX() + 0.5;
            y = pos.getY() + 0.3 + RAND.nextDouble() * 0.5;
            z = pos.getZ() + 0.5;

            double offset = 0.3 + RAND.nextDouble() * 0.15;
            x += attachedFace.getStepX() * offset;
            z += attachedFace.getStepZ() * offset;

            double perpX = attachedFace.getStepZ();
            double perpZ = -attachedFace.getStepX();
            double lateral = (RAND.nextDouble() - 0.5) * 0.6;
            x += perpX * lateral;
            z += perpZ * lateral;
        }

        double angle = RAND.nextDouble() * Math.PI * 2;
        double speed = 0.01 + RAND.nextDouble() * 0.03;

        double dx = Math.cos(angle) * speed;
        double dy = 0.04 + RAND.nextDouble() * 0.06;
        double dz = Math.sin(angle) * speed;

        TClientParticles.spawn(ModParticles.SPARK.get(), x, y, z, dx, dy, dz);
    }

    private static Direction getFireAttachmentFace(Minecraft client, BlockPos firePos) {
        BlockPos below = firePos.below();
        if (client.level.getBlockState(below).isSolidRender(client.level, below)) {
            return Direction.DOWN;
        }

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos adjacent = firePos.relative(dir);
            if (client.level.getBlockState(adjacent).isSolidRender(client.level, adjacent)) {
                return dir;
            }
        }

        return Direction.DOWN;
    }
}