package com.imeetake.effectual;

import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleProviders;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Function;

public final class EffectualClientParticles {
    private EffectualClientParticles() {
    }

    public static ParticleProvider<SimpleParticleType> provider(
            RegistrySupplier<SimpleParticleType> type,
            SpriteSet sprites,
            Function<SpriteSet, ParticleProvider<SimpleParticleType>> factory
    ) {
        return TParticleProviders.provider(type, sprites, factory);
    }

    public static ParticleProvider<SimpleParticleType> simpleProvider(
            RegistrySupplier<SimpleParticleType> type,
            SpriteSet sprites,
            TParticleProviders.SimpleParticleFactory factory
    ) {
        return TParticleProviders.simpleProvider(type, sprites, factory);
    }

    public static ParticleProvider<SimpleParticleType> orientedProvider(
            RegistrySupplier<SimpleParticleType> type,
            SpriteSet sprites,
            TParticleProviders.OrientedParticleFactory<SimpleParticleType> factory
    ) {
        return TParticleProviders.orientedProvider(type, sprites, factory);
    }

    public static void spawn(RegistrySupplier<? extends SimpleParticleType> type, double x, double y, double z, double dx, double dy, double dz) {
        TClientParticles.spawnCulled(type, x, y, z, dx, dy, dz);
    }

    public static void spawnVanilla(ParticleOptions options, double x, double y, double z, double dx, double dy, double dz) {
        TClientParticles.spawnCulled(options, x, y, z, dx, dy, dz);
    }
}
