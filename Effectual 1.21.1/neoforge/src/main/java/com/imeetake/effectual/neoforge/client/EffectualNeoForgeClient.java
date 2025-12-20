package com.imeetake.effectual.neoforge.client;

import com.imeetake.effectual.ModParticles;
import com.imeetake.effectual.effects.AirTrail.AirTrailParticle;
import com.imeetake.effectual.effects.FireEntitySparks.EntitySparkParticle;
import com.imeetake.effectual.effects.GoldGlow.GoldGlowParticle;
import com.imeetake.effectual.effects.MetalSparks.MetalSparkParticle;
import com.imeetake.effectual.effects.MouthSteam.MouthSteamParticleFactory;
import com.imeetake.effectual.effects.PlayerRunEffect.DustParticles.*;
import com.imeetake.effectual.effects.SoulGlow.SoulGlowParticle;
import com.imeetake.effectual.effects.Sparks.SparkParticle;
import com.imeetake.effectual.effects.SparksSoul.SoulSparkParticle;
import com.imeetake.effectual.effects.WaterDrip.WaterDripParticleFactory;
import net.minecraft.client.particle.TextureSheetParticle;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import com.imeetake.effectual.Effectual;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = Effectual.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class EffectualNeoForgeClient {

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


        event.registerSpriteSet(ModParticles.AIR_TRAIL.get(), spriteSet -> (type, level, x, y, z, dx, dy, dz) ->
                new AirTrailParticle(level, x, y, z, dx, dy, dz, spriteSet));
        event.registerSpriteSet(ModParticles.GOLD_GLOW.get(), spriteSet -> (type, level, x, y, z, dx, dy, dz) ->
                new GoldGlowParticle(level, x, y, z, dx, dy, dz, spriteSet));
        event.registerSpriteSet(ModParticles.SOUL_GLOW.get(), spriteSet -> (type, level, x, y, z, dx, dy, dz) ->
                new SoulGlowParticle(level, x, y, z, dx, dy, dz, spriteSet));
        event.registerSpriteSet(ModParticles.SPARK.get(), spriteSet -> (type, level, x, y, z, dx, dy, dz) ->
                new SparkParticle(level, x, y, z, dx, dy, dz, spriteSet));
        event.registerSpriteSet(ModParticles.SOUL_SPARK.get(), spriteSet -> (type, level, x, y, z, dx, dy, dz) ->
                new SoulSparkParticle(level, x, y, z, dx, dy, dz, spriteSet));
    }
}