package com.imeetake.effectual;

import com.imeetake.effectual.effects.AirTrail.AirTrailParticle;
import com.imeetake.effectual.effects.GoldGlow.GoldGlowParticle;
import com.imeetake.effectual.effects.MouthSteam.MouthSteamParticleFactory;
import com.imeetake.effectual.effects.PlayerRunEffect.DustParticles.*;
import com.imeetake.effectual.effects.SnowFootprints.SnowFootprintParticle;
import com.imeetake.effectual.effects.SoulGlow.SoulGlowParticle;
import com.imeetake.effectual.effects.FireEntitySparks.EntitySparkParticle;
import com.imeetake.effectual.effects.MetalSparks.MetalSparkParticle;
import com.imeetake.effectual.effects.Sparks.SparkParticle;
import com.imeetake.effectual.effects.SparksSoul.SoulSparkParticle;
import com.imeetake.effectual.effects.WaterDrip.WaterDripParticleFactory;
import com.imeetake.tlib.client.particle.TParticles;

public class EffectualClient {

    public static void init() {
        EffectRegistry.register();

        TParticles.register(ModParticles.SAND_DUST, SandDustParticle.Factory::new);
        TParticles.register(ModParticles.RED_SAND_DUST, RedSandDustParticle.Factory::new);
        TParticles.register(ModParticles.SNOW_DUST, SnowDustParticle.Factory::new);
        TParticles.register(ModParticles.GRAVEL_DUST, GravelDustParticle.Factory::new);
        TParticles.register(ModParticles.MUD_DUST, MudDustParticle.Factory::new);
        TParticles.register(ModParticles.MOUTH_STEAM, MouthSteamParticleFactory::new);
        TParticles.register(ModParticles.WATER_DRIP, WaterDripParticleFactory::new);

        TParticles.registerSimple(ModParticles.METAL_SPARK, MetalSparkParticle::new);
        TParticles.registerSimple(ModParticles.ENTITY_SPARK, EntitySparkParticle::new);

        TParticles.registerOriented(
                ModParticles.AIR_TRAIL,
                AirTrailParticle::new
        );
        TParticles.registerOriented(
                ModParticles.GOLD_GLOW,
                GoldGlowParticle::new
        );
        TParticles.registerOriented(
                ModParticles.SOUL_GLOW,
                SoulGlowParticle::new
        );
        TParticles.registerOriented(
                ModParticles.SPARK,
                SparkParticle::new
        );
        TParticles.registerOriented(
                ModParticles.SOUL_SPARK,
                SoulSparkParticle::new
        );
        TParticles.registerOriented(
                ModParticles.SNOW_FOOTPRINT,
                SnowFootprintParticle::new
        );
    }
}