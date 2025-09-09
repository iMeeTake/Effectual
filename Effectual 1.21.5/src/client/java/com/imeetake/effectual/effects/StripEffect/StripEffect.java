package com.imeetake.effectual.effects.StripEffect;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class StripEffect {

    private static final Random RAND = Random.create();

    private record Pending(BlockState oldState, Direction face, long expiresAt) { }

    private static final Map<BlockPos, Pending> PENDING = new HashMap<>();

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (!CONFIG.stripEffect() || !world.isClient()) return ActionResult.PASS;
            if (!(hit instanceof BlockHitResult bhr)) return ActionResult.PASS;

            ItemStack stack = player.getStackInHand(hand);
            if (!(stack.getItem() instanceof AxeItem)) return ActionResult.PASS;

            BlockPos pos = bhr.getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (!isLog(state)) return ActionResult.PASS;

            long now = world.getTime();
            PENDING.put(pos.toImmutable(), new Pending(state, bhr.getSide(), now + 6L));
            return ActionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.stripEffect() || client.world == null || client.isPaused()) return;
            tick(client);
        });
    }

    private static void tick(MinecraftClient client) {
        ClientWorld world = client.world;
        long now = world.getTime();

        Iterator<Map.Entry<BlockPos, Pending>> it = PENDING.entrySet().iterator();
        while (it.hasNext()) {
            var e = it.next();
            BlockPos pos = e.getKey();
            Pending p = e.getValue();

            if (now >= p.expiresAt()) { it.remove(); continue; }

            BlockState cur = world.getBlockState(pos);
            if (cur.getBlock() == p.oldState().getBlock()) continue;

            if (isLog(p.oldState()) && isLog(cur)) {
                spawnParticles(pos, p.oldState(), p.face());
            }

            it.remove();
        }
    }

    private static boolean isLog(BlockState state) {
        return state.isIn(BlockTags.LOGS);
    }

    private static void spawnParticles(BlockPos pos, BlockState state, Direction face) {
        double nx = face.getOffsetX();
        double ny = face.getOffsetY();
        double nz = face.getOffsetZ();

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

            TClientParticles.spawn(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                    cx + sx, cy + sy, cz + sz,
                    vx, vy, vz
            );
        }
    }
}
