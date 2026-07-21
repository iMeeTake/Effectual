package com.imeetake.effectual;

import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleProviders;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public final class EffectualClientParticles {
    private EffectualClientParticles() {
    }

    public static <T extends ParticleOptions> ParticleProvider<T> orientedProvider(
            ParticleType<T> type,
            SpriteSet sprites,
            TParticleProviders.OrientedParticleFactory<T> factory
    ) {
        return TParticleProviders.orientedProvider(type, sprites, factory);
    }

    public static void spawn(ParticleOptions options, double x, double y, double z, double dx, double dy, double dz) {
        TClientParticles.spawnCulled(options, x, y, z, dx, dy, dz);
    }

    public static void spawn(ParticleOptions options, double x, double y, double z) {
        TClientParticles.spawnCulled(options, x, y, z);
    }
}
