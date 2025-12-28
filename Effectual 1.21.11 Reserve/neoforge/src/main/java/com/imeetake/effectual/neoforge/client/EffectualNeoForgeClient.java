package com.imeetake.effectual.neoforge.client;

import com.imeetake.effectual.Effectual;
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
import com.imeetake.tlib.client.particle.TClientParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = Effectual.MOD_ID, value = Dist.CLIENT)
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

        event.registerSpriteSet(ModParticles.AIR_TRAIL.get(), spriteSet -> {
            TClientParticles.registerOriented(ModParticles.AIR_TRAIL.get(), AirTrailParticle::new, spriteSet);
            return (type, level, x, y, z, dx, dy, dz, random) -> null;
        });
        event.registerSpriteSet(ModParticles.GOLD_GLOW.get(), spriteSet -> {
            TClientParticles.registerOriented(ModParticles.GOLD_GLOW.get(), GoldGlowParticle::new, spriteSet);
            return (type, level, x, y, z, dx, dy, dz, random) -> null;
        });
        event.registerSpriteSet(ModParticles.SOUL_GLOW.get(), spriteSet -> {
            TClientParticles.registerOriented(ModParticles.SOUL_GLOW.get(), SoulGlowParticle::new, spriteSet);
            return (type, level, x, y, z, dx, dy, dz, random) -> null;
        });
        event.registerSpriteSet(ModParticles.SPARK.get(), spriteSet -> {
            TClientParticles.registerOriented(ModParticles.SPARK.get(), SparkParticle::new, spriteSet);
            return (type, level, x, y, z, dx, dy, dz, random) -> null;
        });
        event.registerSpriteSet(ModParticles.SOUL_SPARK.get(), spriteSet -> {
            TClientParticles.registerOriented(ModParticles.SOUL_SPARK.get(), SoulSparkParticle::new, spriteSet);
            return (type, level, x, y, z, dx, dy, dz, random) -> null;
        });
    }
}