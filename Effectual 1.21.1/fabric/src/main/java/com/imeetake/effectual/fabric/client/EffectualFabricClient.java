package com.imeetake.effectual.fabric.client;

import com.imeetake.effectual.EffectualClientParticles;
import com.imeetake.effectual.ModParticles;
import com.imeetake.effectual.effects.AirTrail.AirTrailParticle;
import com.imeetake.effectual.effects.Glow.GoldGlowParticle;
import com.imeetake.effectual.effects.Glow.SoulGlowParticle;
import com.imeetake.effectual.effects.FireEntitySparks.EntitySparkParticle;
import com.imeetake.effectual.effects.MetalSparks.MetalSparkParticle;
import com.imeetake.effectual.effects.MouthSteam.MouthSteamParticleFactory;
import com.imeetake.effectual.effects.PlayerRunEffect.DustParticles.*;
import com.imeetake.effectual.effects.SnowFootprints.SnowFootprintParticle;
import com.imeetake.effectual.effects.SnowFootprints.SnowResidueParticle;
import com.imeetake.effectual.effects.Sparks.SparkParticle;
import com.imeetake.effectual.effects.Sparks.SoulSparkParticle;
import com.imeetake.effectual.effects.WaterDrip.WaterDripParticleFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public final class EffectualFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerParticles();
    }

    private void registerParticles() {
        ParticleFactoryRegistry r = ParticleFactoryRegistry.getInstance();

        r.register(ModParticles.SAND_DUST.get(), sprites -> EffectualClientParticles.provider(ModParticles.SAND_DUST, sprites, SandDustParticle.Factory::new));
        r.register(ModParticles.RED_SAND_DUST.get(), sprites -> EffectualClientParticles.provider(ModParticles.RED_SAND_DUST, sprites, RedSandDustParticle.Factory::new));
        r.register(ModParticles.SNOW_DUST.get(), sprites -> EffectualClientParticles.provider(ModParticles.SNOW_DUST, sprites, SnowDustParticle.Factory::new));
        r.register(ModParticles.GRAVEL_DUST.get(), sprites -> EffectualClientParticles.provider(ModParticles.GRAVEL_DUST, sprites, GravelDustParticle.Factory::new));
        r.register(ModParticles.MUD_DUST.get(), sprites -> EffectualClientParticles.provider(ModParticles.MUD_DUST, sprites, MudDustParticle.Factory::new));
        r.register(ModParticles.MOUTH_STEAM.get(), sprites -> EffectualClientParticles.provider(ModParticles.MOUTH_STEAM, sprites, MouthSteamParticleFactory::new));
        r.register(ModParticles.WATER_DRIP.get(), sprites -> EffectualClientParticles.provider(ModParticles.WATER_DRIP, sprites, WaterDripParticleFactory::new));
        r.register(ModParticles.METAL_SPARK.get(), sprites -> EffectualClientParticles.simpleProvider(ModParticles.METAL_SPARK, sprites, MetalSparkParticle::new));
        r.register(ModParticles.ENTITY_SPARK.get(), sprites -> EffectualClientParticles.simpleProvider(ModParticles.ENTITY_SPARK, sprites, EntitySparkParticle::new));
        r.register(ModParticles.AIR_TRAIL.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.AIR_TRAIL, sprites, AirTrailParticle::new));
        r.register(ModParticles.GOLD_GLOW.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.GOLD_GLOW, sprites, GoldGlowParticle::new));
        r.register(ModParticles.SOUL_GLOW.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SOUL_GLOW, sprites, SoulGlowParticle::new));
        r.register(ModParticles.SPARK.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SPARK, sprites, SparkParticle::new));
        r.register(ModParticles.SOUL_SPARK.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SOUL_SPARK, sprites, SoulSparkParticle::new));
        r.register(ModParticles.SNOW_FOOTPRINT.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SNOW_FOOTPRINT, sprites, SnowFootprintParticle::new));
        r.register(ModParticles.SNOW_RESIDUE.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SNOW_RESIDUE, sprites, SnowResidueParticle::new));
    }
}
