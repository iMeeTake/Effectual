package com.imeetake.effectual;

import com.imeetake.effectual.effects.AirTrail.AirTrailParticle;
import com.imeetake.effectual.effects.FireEntitySparks.EntitySparkParticle;
import com.imeetake.effectual.effects.Glow.GoldGlowParticle;
import com.imeetake.effectual.effects.Glow.SoulGlowParticle;
import com.imeetake.effectual.effects.MetalSparks.MetalSparkParticle;
import com.imeetake.effectual.effects.MouthSteam.MouthSteamParticleFactory;
import com.imeetake.effectual.effects.PlayerRunEffect.DustParticles.GravelDustParticle;
import com.imeetake.effectual.effects.PlayerRunEffect.DustParticles.MudDustParticle;
import com.imeetake.effectual.effects.PlayerRunEffect.DustParticles.RedSandDustParticle;
import com.imeetake.effectual.effects.PlayerRunEffect.DustParticles.SandDustParticle;
import com.imeetake.effectual.effects.PlayerRunEffect.DustParticles.SnowDustParticle;
import com.imeetake.effectual.effects.SnowFootprints.SnowFootprintParticle;
import com.imeetake.effectual.effects.SnowFootprints.SnowResidueParticle;
import com.imeetake.effectual.effects.Sparks.SoulSparkParticle;
import com.imeetake.effectual.effects.Sparks.SparkParticle;
import com.imeetake.effectual.effects.WaterDrip.WaterDripParticleFactory;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleProviders;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public final class EffectualClientParticles {
    private EffectualClientParticles() {
    }

    public static void registerProviders() {
        TParticleProviders.register(ModParticles.SAND_DUST, SandDustParticle.Factory::new);
        TParticleProviders.register(ModParticles.RED_SAND_DUST, RedSandDustParticle.Factory::new);
        TParticleProviders.register(ModParticles.SNOW_DUST, SnowDustParticle.Factory::new);
        TParticleProviders.register(ModParticles.GRAVEL_DUST, GravelDustParticle.Factory::new);
        TParticleProviders.register(ModParticles.MUD_DUST, MudDustParticle.Factory::new);
        TParticleProviders.register(ModParticles.MOUTH_STEAM, MouthSteamParticleFactory::new);
        TParticleProviders.register(ModParticles.WATER_DRIP, WaterDripParticleFactory::new);

        TParticleProviders.registerSimple(ModParticles.METAL_SPARK, MetalSparkParticle::new);
        TParticleProviders.registerSimple(ModParticles.ENTITY_SPARK, EntitySparkParticle::new);

        TParticleProviders.registerOriented(ModParticles.AIR_TRAIL, AirTrailParticle::new);
        TParticleProviders.registerOriented(ModParticles.GOLD_GLOW, GoldGlowParticle::new);
        TParticleProviders.registerOriented(ModParticles.SOUL_GLOW, SoulGlowParticle::new);
        TParticleProviders.registerOriented(ModParticles.SPARK, SparkParticle::new);
        TParticleProviders.registerOriented(ModParticles.SOUL_SPARK, SoulSparkParticle::new);
        TParticleProviders.registerOriented(ModParticles.SNOW_FOOTPRINT, SnowFootprintParticle::new);
        TParticleProviders.registerOriented(ModParticles.SNOW_RESIDUE, SnowResidueParticle::new);
    }

    public static void spawn(RegistrySupplier<? extends SimpleParticleType> type, double x, double y, double z, double dx, double dy, double dz) {
        TClientParticles.spawnCulled(type, x, y, z, dx, dy, dz);
    }

    public static void spawnVanilla(ParticleOptions options, double x, double y, double z, double dx, double dy, double dz) {
        TClientParticles.spawnCulled(options, x, y, z, dx, dy, dz);
    }
}
