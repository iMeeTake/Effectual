package com.imeetake.effectual.neoforge.client;

import com.imeetake.effectual.Effectual;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = Effectual.MOD_ID, value = Dist.CLIENT)
public final class EffectualNeoForgeClient {
    private EffectualNeoForgeClient() {
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SAND_DUST.get(), SandDustParticle.Factory::new);
        event.registerSpriteSet(ModParticles.RED_SAND_DUST.get(), RedSandDustParticle.Factory::new);
        event.registerSpriteSet(ModParticles.SNOW_DUST.get(), SnowDustParticle.Factory::new);
        event.registerSpriteSet(ModParticles.GRAVEL_DUST.get(), GravelDustParticle.Factory::new);
        event.registerSpriteSet(ModParticles.MUD_DUST.get(), MudDustParticle.Factory::new);
        event.registerSpriteSet(ModParticles.MOUTH_STEAM.get(), MouthSteamParticleFactory::new);
        event.registerSpriteSet(ModParticles.WATER_DRIP.get(), WaterDripParticleFactory::new);
        event.registerSpriteSet(ModParticles.METAL_SPARK.get(), MetalSparkParticle.Factory::new);
        event.registerSpriteSet(ModParticles.ENTITY_SPARK.get(), EntitySparkParticle.Factory::new);

        event.registerSpriteSet(ModParticles.AIR_TRAIL.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.AIR_TRAIL.get(), sprites, AirTrailParticle::new));
        event.registerSpriteSet(ModParticles.GOLD_GLOW.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.GOLD_GLOW.get(), sprites, GoldGlowParticle::new));
        event.registerSpriteSet(ModParticles.SOUL_GLOW.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SOUL_GLOW.get(), sprites, SoulGlowParticle::new));
        event.registerSpriteSet(ModParticles.SPARK.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SPARK.get(), sprites, SparkParticle::new));
        event.registerSpriteSet(ModParticles.SOUL_SPARK.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SOUL_SPARK.get(), sprites, SoulSparkParticle::new));
        event.registerSpriteSet(ModParticles.SNOW_FOOTPRINT.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SNOW_FOOTPRINT.get(), sprites, SnowFootprintParticle::new));
        event.registerSpriteSet(ModParticles.SNOW_RESIDUE.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SNOW_RESIDUE.get(), sprites, SnowResidueParticle::new));
    }
}
