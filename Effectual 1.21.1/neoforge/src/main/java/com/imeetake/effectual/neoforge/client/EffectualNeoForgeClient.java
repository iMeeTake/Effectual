package com.imeetake.effectual.neoforge.client;

import com.imeetake.effectual.Effectual;
import com.imeetake.effectual.EffectualClientParticles;
import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.ModParticles;
import com.imeetake.effectual.effects.AirTrail.AirTrailParticle;
import com.imeetake.effectual.effects.FireEntitySparks.EntitySparkParticle;
import com.imeetake.effectual.effects.Glow.GoldGlowParticle;
import com.imeetake.effectual.effects.Glow.SoulGlowParticle;
import com.imeetake.effectual.effects.MetalSparks.MetalSparkParticle;
import com.imeetake.effectual.effects.MouthSteam.MouthSteamParticleFactory;
import com.imeetake.effectual.effects.PlayerRunEffect.DustParticles.*;
import com.imeetake.effectual.effects.SnowFootprints.SnowFootprintParticle;
import com.imeetake.effectual.effects.SnowFootprints.SnowResidueParticle;
import com.imeetake.effectual.effects.Sparks.SparkParticle;
import com.imeetake.effectual.effects.Sparks.SoulSparkParticle;
import com.imeetake.effectual.effects.WaterDrip.WaterDripParticleFactory;
import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = Effectual.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class EffectualNeoForgeClient {

    public static void init() {
        Effectual.init();

        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (client, parent) -> AutoConfig.getConfigScreen(EffectualConfig.class, parent).get()
        );
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SAND_DUST.get(), sprites -> EffectualClientParticles.provider(ModParticles.SAND_DUST, sprites, SandDustParticle.Factory::new));
        event.registerSpriteSet(ModParticles.RED_SAND_DUST.get(), sprites -> EffectualClientParticles.provider(ModParticles.RED_SAND_DUST, sprites, RedSandDustParticle.Factory::new));
        event.registerSpriteSet(ModParticles.SNOW_DUST.get(), sprites -> EffectualClientParticles.provider(ModParticles.SNOW_DUST, sprites, SnowDustParticle.Factory::new));
        event.registerSpriteSet(ModParticles.GRAVEL_DUST.get(), sprites -> EffectualClientParticles.provider(ModParticles.GRAVEL_DUST, sprites, GravelDustParticle.Factory::new));
        event.registerSpriteSet(ModParticles.MUD_DUST.get(), sprites -> EffectualClientParticles.provider(ModParticles.MUD_DUST, sprites, MudDustParticle.Factory::new));
        event.registerSpriteSet(ModParticles.MOUTH_STEAM.get(), sprites -> EffectualClientParticles.provider(ModParticles.MOUTH_STEAM, sprites, MouthSteamParticleFactory::new));
        event.registerSpriteSet(ModParticles.WATER_DRIP.get(), sprites -> EffectualClientParticles.provider(ModParticles.WATER_DRIP, sprites, WaterDripParticleFactory::new));
        event.registerSpriteSet(ModParticles.METAL_SPARK.get(), sprites -> EffectualClientParticles.simpleProvider(ModParticles.METAL_SPARK, sprites, MetalSparkParticle::new));
        event.registerSpriteSet(ModParticles.ENTITY_SPARK.get(), sprites -> EffectualClientParticles.simpleProvider(ModParticles.ENTITY_SPARK, sprites, EntitySparkParticle::new));
        event.registerSpriteSet(ModParticles.AIR_TRAIL.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.AIR_TRAIL, sprites, AirTrailParticle::new));
        event.registerSpriteSet(ModParticles.GOLD_GLOW.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.GOLD_GLOW, sprites, GoldGlowParticle::new));
        event.registerSpriteSet(ModParticles.SOUL_GLOW.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SOUL_GLOW, sprites, SoulGlowParticle::new));
        event.registerSpriteSet(ModParticles.SPARK.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SPARK, sprites, SparkParticle::new));
        event.registerSpriteSet(ModParticles.SOUL_SPARK.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SOUL_SPARK, sprites, SoulSparkParticle::new));
        event.registerSpriteSet(ModParticles.SNOW_FOOTPRINT.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SNOW_FOOTPRINT, sprites, SnowFootprintParticle::new));
        event.registerSpriteSet(ModParticles.SNOW_RESIDUE.get(), sprites -> EffectualClientParticles.orientedProvider(ModParticles.SNOW_RESIDUE, sprites, SnowResidueParticle::new));
    }
}
