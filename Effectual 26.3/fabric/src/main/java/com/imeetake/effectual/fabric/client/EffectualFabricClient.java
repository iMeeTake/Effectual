package com.imeetake.effectual.fabric.client;

import com.imeetake.effectual.EffectualClient;
import com.imeetake.effectual.EffectualClientParticles;
import com.imeetake.effectual.ModParticles;
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
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;

public final class EffectualFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerParticles();
        EffectualClient.init();
    }

    private void registerParticles() {
        ParticleProviderRegistry registry = ParticleProviderRegistry.getInstance();

        registry.register(ModParticles.SAND_DUST.get(), SandDustParticle.Factory::new);
        registry.register(ModParticles.RED_SAND_DUST.get(), RedSandDustParticle.Factory::new);
        registry.register(ModParticles.SNOW_DUST.get(), SnowDustParticle.Factory::new);
        registry.register(ModParticles.GRAVEL_DUST.get(), GravelDustParticle.Factory::new);
        registry.register(ModParticles.MUD_DUST.get(), MudDustParticle.Factory::new);
        registry.register(ModParticles.MOUTH_STEAM.get(), MouthSteamParticleFactory::new);
        registry.register(ModParticles.WATER_DRIP.get(), WaterDripParticleFactory::new);
        registry.register(ModParticles.METAL_SPARK.get(), MetalSparkParticle.Factory::new);
        registry.register(ModParticles.ENTITY_SPARK.get(), EntitySparkParticle.Factory::new);

        registry.register(ModParticles.AIR_TRAIL.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.AIR_TRAIL.get(), sprites, AirTrailParticle::new));
        registry.register(ModParticles.GOLD_GLOW.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.GOLD_GLOW.get(), sprites, GoldGlowParticle::new));
        registry.register(ModParticles.SOUL_GLOW.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SOUL_GLOW.get(), sprites, SoulGlowParticle::new));
        registry.register(ModParticles.SPARK.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SPARK.get(), sprites, SparkParticle::new));
        registry.register(ModParticles.SOUL_SPARK.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SOUL_SPARK.get(), sprites, SoulSparkParticle::new));
        registry.register(ModParticles.SNOW_FOOTPRINT.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SNOW_FOOTPRINT.get(), sprites, SnowFootprintParticle::new));
        registry.register(ModParticles.SNOW_RESIDUE.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SNOW_RESIDUE.get(), sprites, SnowResidueParticle::new));
    }
}
