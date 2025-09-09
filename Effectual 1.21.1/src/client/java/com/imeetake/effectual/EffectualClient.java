package com.imeetake.effectual;

import com.imeetake.effectual.effects.AirTrail.AirTrailParticle;
import com.imeetake.effectual.effects.MouthSteam.MouthSteamParticleFactory;
import com.imeetake.effectual.effects.Sparks.MetalSparkParticle;
import com.imeetake.effectual.effects.SpeedAura.SpeedAuraParticle;
import com.imeetake.effectual.effects.WaterDrip.WaterDripParticleFactory;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import com.imeetake.tlib.client.particle.TParticles;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import com.imeetake.effectual.effects.Firefly.FireflyParticle;
import com.imeetake.effectual.effects.Levitation.LevitationParticle;
import com.imeetake.effectual.effects.GoldGlow.GoldGlowParticle;
import com.imeetake.effectual.effects.Sparks.SparkParticle;
import com.imeetake.effectual.effects.SparksSoul.SoulSparkParticle;
import com.imeetake.effectual.effects.SoulGlow.SoulGlowParticle;
import com.imeetake.effectual.effects.PlayerRunEffect.SandDustParticle;
import com.imeetake.effectual.effects.PlayerRunEffect.SnowDustParticle;
import com.imeetake.effectual.effects.PlayerRunEffect.GravelDustParticle;
import com.imeetake.effectual.effects.PlayerRunEffect.RedSandDustParticle;

public class EffectualClient implements ClientModInitializer {
	public static final com.imeetake.effectual.EffectualConfig CONFIG = com.imeetake.effectual.EffectualConfig.createAndLoad();
	@Override
	public void onInitializeClient() {
		ModParticles.register();

		TParticles.registerSimple((ParticleType) ModParticles.FIREFLY_PARTICLE, FireflyParticle::new);
		TParticles.registerSimple((ParticleType) ModParticles.METAL_SPARK,      MetalSparkParticle::new);
		TParticles.registerSimple((ParticleType) ModParticles.SAND_DUST,        SandDustParticle::new);
		TParticles.registerSimple((ParticleType) ModParticles.SNOW_DUST,        SnowDustParticle::new);
		TParticles.registerSimple((ParticleType) ModParticles.GRAVEL_DUST,      GravelDustParticle::new);
		TParticles.registerSimple((ParticleType) ModParticles.RED_SAND_DUST,    RedSandDustParticle::new);
		TParticles.registerSimple((ParticleType) ModParticles.LEVITATION_AURA,  LevitationParticle::new);

		TParticles.register((ParticleType<ParticleEffect>) ModParticles.WATER_DRIP, spriteProvider -> new WaterDripParticleFactory(spriteProvider));
		TParticles.register((ParticleType<ParticleEffect>) ModParticles.MOUTH_STEAM, spriteProvider -> new MouthSteamParticleFactory(spriteProvider));

		TParticles.registerOriented(
				(ParticleType<TParticleEffectSimple>) ModParticles.AIR_TRAIL,
				(world, x, y, z, dx, dy, dz, spriteProvider) -> new AirTrailParticle(world, x, y, z, dx, dy, dz, spriteProvider)
		);
		TParticles.registerOriented(
				(ParticleType<TParticleEffectSimple>) ModParticles.SPEED_AURA,
				(world, x, y, z, dx, dy, dz, spriteProvider) -> new SpeedAuraParticle(world, x, y, z, dx, dy, dz, spriteProvider)
		);
		TParticles.registerOriented(
				(ParticleType<TParticleEffectSimple>) ModParticles.SPARK,
				(world, x, y, z, dx, dy, dz, spriteProvider) -> new SparkParticle(world, x, y, z, dx, dy, dz, spriteProvider)
		);
		TParticles.registerOriented(
				(ParticleType<TParticleEffectSimple>) ModParticles.GOLD_GLOW,
				(world, x, y, z, dx, dy, dz, spriteProvider) -> new GoldGlowParticle(world, x, y, z, dx, dy, dz, spriteProvider)
		);
		TParticles.registerOriented(
				(ParticleType<TParticleEffectSimple>) ModParticles.SOUL_SPARK,
				(world, x, y, z, dx, dy, dz, spriteProvider) -> new SoulSparkParticle(world, x, y, z, dx, dy, dz, spriteProvider)
		);
		TParticles.registerOriented(
				(ParticleType<TParticleEffectSimple>) ModParticles.SOUL_GLOW,
				(world, x, y, z, dx, dy, dz, spriteProvider) -> new SoulGlowParticle(world, x, y, z, dx, dy, dz, spriteProvider)
		);


		EffectRegistry.registerEffects();
	}
}