package com.imeetake.effectual.effects.Firefly;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.TClientEnvironment;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import static com.imeetake.effectual.EffectualClient.CONFIG;

public class FireflyEffect {

    private static final Random RANDOM = Random.create();
    private static final TagKey<Biome> FIREFLY_EFFECT_BIOMES = TagKey.of(RegistryKeys.BIOME, Identifier.of("effectual", "firefly_effect_biomes"));

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!CONFIG.fireflyEffect()) return;
            if (client.world == null || client.player == null) return;
            if (client.isPaused()) return;

            PlayerEntity player = client.player;
            float chance =
                   CONFIG.fireflySpawnRate() / 100.0f;

            if (TClientEnvironment.isNight() && isInFireflyBiome(player)) {
                if (RANDOM.nextFloat() < chance) {
                    spawnFireflyParticle(client, player);
                }
            }
        });
    }


    private static boolean canSpawnAt(MinecraftClient client, double x, double y, double z) {
        if (y < 60) return false;
        BlockPos pos = BlockPos.ofFloored(x, y, z);
        return client.world.getBlockState(pos).isAir()
                && client.world.getLightLevel(pos) < 8
                && client.world.getFluidState(pos).isEmpty();
    }

    private static boolean isInFireflyBiome(PlayerEntity player) {
        RegistryEntry<Biome> biomeEntry = player.getWorld().getBiome(player.getBlockPos());


        return biomeEntry.isIn(FIREFLY_EFFECT_BIOMES);
    }

    private static void spawnFireflyParticle(MinecraftClient client, PlayerEntity player) {
        double x = player.getX() + (RANDOM.nextDouble() - 0.5) * 30;
        double y = player.getY() + RANDOM.nextDouble() * 3 + 2;
        double z = player.getZ() + (RANDOM.nextDouble() - 0.5) * 30;

        if (!canSpawnAt(client, x, y, z)) return;

        double velocityX = (RANDOM.nextDouble() - 0.5) * 0.02;
        double velocityY = (RANDOM.nextDouble() - 0.5) * 0.02;
        double velocityZ = (RANDOM.nextDouble() - 0.5) * 0.02;

        TClientParticles.spawn(
                new TParticleEffectSimple(ModParticles.FIREFLY_PARTICLE),
                x, y, z,
                velocityX, velocityY, velocityZ
        );
    }
}
