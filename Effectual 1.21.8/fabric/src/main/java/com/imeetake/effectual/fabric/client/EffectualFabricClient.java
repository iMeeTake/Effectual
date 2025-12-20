package com.imeetake.effectual.fabric.client;

import com.imeetake.effectual.EffectualClient;
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
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public final class EffectualFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EffectualClient.init();
        registerParticles();
    }

    private void registerParticles() {
        ParticleFactoryRegistry r = ParticleFactoryRegistry.getInstance();

        r.register(ModParticles.SAND_DUST.get(), SandDustParticle.Factory::new);
        r.register(ModParticles.RED_SAND_DUST.get(), RedSandDustParticle.Factory::new);
        r.register(ModParticles.SNOW_DUST.get(), SnowDustParticle.Factory::new);
        r.register(ModParticles.GRAVEL_DUST.get(), GravelDustParticle.Factory::new);
        r.register(ModParticles.MUD_DUST.get(), MudDustParticle.Factory::new);
        r.register(ModParticles.MOUTH_STEAM.get(), MouthSteamParticleFactory::new);
        r.register(ModParticles.WATER_DRIP.get(), WaterDripParticleFactory::new);
        r.register(ModParticles.METAL_SPARK.get(), MetalSparkParticle.Factory::new);
        r.register(ModParticles.ENTITY_SPARK.get(), EntitySparkParticle.Factory::new);


        r.register(ModParticles.AIR_TRAIL.get(), spriteSet -> (type, level, x, y, z, dx, dy, dz) ->
                new AirTrailParticle(level, x, y, z, dx, dy, dz, spriteSet));
        r.register(ModParticles.GOLD_GLOW.get(), spriteSet -> (type, level, x, y, z, dx, dy, dz) ->
                new GoldGlowParticle(level, x, y, z, dx, dy, dz, spriteSet));
        r.register(ModParticles.SOUL_GLOW.get(), spriteSet -> (type, level, x, y, z, dx, dy, dz) ->
                new SoulGlowParticle(level, x, y, z, dx, dy, dz, spriteSet));
        r.register(ModParticles.SPARK.get(), spriteSet -> (type, level, x, y, z, dx, dy, dz) ->
                new SparkParticle(level, x, y, z, dx, dy, dz, spriteSet));
        r.register(ModParticles.SOUL_SPARK.get(), spriteSet -> (type, level, x, y, z, dx, dy, dz) ->
                new SoulSparkParticle(level, x, y, z, dx, dy, dz, spriteSet));
    }
}