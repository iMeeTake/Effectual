package com.imeetake.effectual.effects.WitherDecay;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class WitherDecayEffect {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Int2IntOpenHashMap tickCounters = new Int2IntOpenHashMap();

    private static int cleanupTimer = 0;
    private static ClientLevel lastLevel = null;

    static {
        tickCounters.defaultReturnValue(0);
    }

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null) {
                tickCounters.clear();
                lastLevel = null;
                return;
            }

            if (lastLevel != client.level) {
                tickCounters.clear();
                lastLevel = client.level;
            }

            if (!EffectualConfig.get().witherDecay || client.isPaused()) return;

            if (++cleanupTimer >= 100) {
                cleanupTimer = 0;
                cleanupMap(client.level.players());
            }

            for (Player player : client.level.players()) {
                int id = player.getId();

                if (!player.hasEffect(MobEffects.WITHER)) {
                    tickCounters.remove(id);
                    continue;
                }

                int count = tickCounters.get(id) + 1;
                if (count >= 3) {
                    tickCounters.put(id, 0);
                    spawn(player);
                } else {
                    tickCounters.put(id, count);
                }
            }
        });
    }

    private static void cleanupMap(List<? extends Player> activePlayers) {
        if (tickCounters.isEmpty()) return;

        IntOpenHashSet activeIds = new IntOpenHashSet(activePlayers.size());
        for (Player player : activePlayers) {
            activeIds.add(player.getId());
        }

        var iterator = tickCounters.keySet().intIterator();
        while (iterator.hasNext()) {
            int id = iterator.nextInt();
            if (!activeIds.contains(id)) {
                iterator.remove();
            }
        }
    }

    private static void spawn(Player player) {
        double x = player.getX() + (RANDOM.nextDouble() - 0.5) * 1.2;
        double y = player.getY() + 0.3 + RANDOM.nextDouble() * 1.5;
        double z = player.getZ() + (RANDOM.nextDouble() - 0.5) * 1.2;
        double dy = -0.015 - RANDOM.nextDouble() * 0.01;

        EffectualClientParticles.spawnVanilla(ParticleTypes.SMOKE, x, y, z, 0, dy, 0);
    }
}
