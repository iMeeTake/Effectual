package com.imeetake.effectual.effects.StripEffect;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import dev.architectury.event.EventResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StripEffect {

    private static final RandomSource RAND = RandomSource.create();
    private static final int MAX_PENDING_ENTRIES = 64;

    private record Pending(BlockState oldState, Direction face, long expiresAt) {
    }

    private static final Map<BlockPos, Pending> PENDING = new HashMap<>();
    private static ClientLevel lastLevel = null;

    public static void register() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> {
            if (!EffectualConfig.get().stripEffect) return EventResult.pass();
            if (!player.level().isClientSide()) return EventResult.pass();

            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof AxeItem)) return EventResult.pass();

            BlockState state = player.level().getBlockState(pos);
            if (!isLog(state)) return EventResult.pass();

            if (PENDING.size() >= MAX_PENDING_ENTRIES) {
                return EventResult.pass();
            }

            long now = player.level().getGameTime();
            PENDING.put(pos.immutable(), new Pending(state, face, now + 6L));
            return EventResult.pass();
        });

        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null) {
                PENDING.clear();
                lastLevel = null;
                return;
            }

            if (lastLevel != client.level) {
                PENDING.clear();
                lastLevel = client.level;
            }

            if (!EffectualConfig.get().stripEffect || client.isPaused()) {
                PENDING.clear();
                return;
            }

            tick(client);
        });
    }

    private static void tick(Minecraft client) {
        ClientLevel level = client.level;
        long now = level.getGameTime();

        Iterator<Map.Entry<BlockPos, Pending>> it = PENDING.entrySet().iterator();
        while (it.hasNext()) {
            var e = it.next();
            BlockPos pos = e.getKey();
            Pending p = e.getValue();

            if (now >= p.expiresAt()) {
                it.remove();
                continue;
            }

            BlockState cur = level.getBlockState(pos);
            if (cur.getBlock() == p.oldState().getBlock()) continue;

            if (isLog(p.oldState()) && isLog(cur)) {
                spawnParticles(pos, p.oldState(), p.face());
            }

            it.remove();
        }
    }

    private static boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS);
    }

    private static void spawnParticles(BlockPos pos, BlockState state, Direction face) {
        double nx = face.getStepX();
        double ny = face.getStepY();
        double nz = face.getStepZ();

        double cx = pos.getX() + 0.5 + nx * 0.51;
        double cy = pos.getY() + 0.5 + ny * 0.51;
        double cz = pos.getZ() + 0.5 + nz * 0.51;

        int chips = 7 + RAND.nextInt(4);
        for (int i = 0; i < chips; i++) {
            double t = (RAND.nextDouble() - 0.5) * 0.16;
            double sx = nx == 0 ? t : 0;
            double sy = ny == 0 ? t : 0;
            double sz = nz == 0 ? t : 0;
            double vx = nx * (0.04 + RAND.nextDouble() * 0.03);
            double vy = ny * (0.04 + RAND.nextDouble() * 0.03);
            double vz = nz * (0.04 + RAND.nextDouble() * 0.03);

            EffectualClientParticles.spawn(
                    new BlockParticleOption(ParticleTypes.BLOCK, state),
                    cx + sx, cy + sy, cz + sz,
                    vx, vy, vz
            );
        }
    }
}
