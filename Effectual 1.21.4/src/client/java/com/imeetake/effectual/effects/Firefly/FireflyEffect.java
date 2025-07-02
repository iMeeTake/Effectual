package com.imeetake.effectual.effects.Firefly;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.TClientEnvironment;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;

import static com.imeetake.effectual.EffectualClient.CONFIG;

public class FireflyEffect {

    private static final Random RAND = Random.create();
    private static final TagKey<Biome> FIREFLY_EFFECT_BIOMES = TagKey.of(RegistryKeys.BIOME, Identifier.of("effectual", "firefly_effect_biomes"));

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.fireflyEffect() || client.world == null || client.player == null || client.isPaused()) return;

            PlayerEntity player = client.player;
            float chance = CONFIG.fireflySpawnRate() / 100f;

            if (!TClientEnvironment.isNight()) return;
            if (!isInFireflyBiome(player)) return;
            if (RAND.nextFloat() >= chance) return;

            spawn(client, player);
        });
    }

    private static boolean isInFireflyBiome(PlayerEntity p) {
        return p.getWorld().getBiome(p.getBlockPos()).isIn(FIREFLY_EFFECT_BIOMES);
    }

    private static boolean canSpawnAt(MinecraftClient client, double x, double y, double z) {
        if (y < 60) return false;
        BlockPos pos = BlockPos.ofFloored(x, y, z);
        return client.world.getBlockState(pos).isAir()
                && client.world.getLightLevel(pos) < 8
                && client.world.getFluidState(pos).isEmpty();
    }

    private static void spawn(MinecraftClient client, PlayerEntity player) {
        double x = player.getX() + (RAND.nextDouble() - 0.5) * 30;
        double y = player.getY() + RAND.nextDouble() * 3 + 2;
        double z = player.getZ() + (RAND.nextDouble() - 0.5) * 30;

        if (!canSpawnAt(client, x, y, z)) return;

        double vx = (RAND.nextDouble() - 0.5) * 0.02;
        double vy = (RAND.nextDouble() - 0.5) * 0.02;
        double vz = (RAND.nextDouble() - 0.5) * 0.02;

        TClientParticles.spawn(
                new TParticleEffectSimple(ModParticles.FIREFLY_PARTICLE),
                x, y, z,
                vx, vy, vz
        );
    }
}
