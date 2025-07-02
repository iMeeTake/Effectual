package com.imeetake.effectual;

import com.imeetake.tlib.client.particle.TParticles;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final ParticleType<?> MOUTH_STEAM = TParticles.simple(Effectual.MOD_ID, "mouth_steam");
    public static final ParticleType<?> FIREFLY_PARTICLE = TParticles.simple(Effectual.MOD_ID, "firefly_particle");
    public static final ParticleType<?> GOLD_GLOW = TParticles.simple(Effectual.MOD_ID, "gold_glow");
    public static final ParticleType<?> SPARK = TParticles.simple(Effectual.MOD_ID, "spark");
    public static final ParticleType<?> SOUL_SPARK = TParticles.simple(Effectual.MOD_ID, "soul_spark");
    public static final ParticleType<?> SOUL_GLOW = TParticles.simple(Effectual.MOD_ID, "soul_glow");
    public static final ParticleType<?> SAND_DUST = TParticles.simple(Effectual.MOD_ID, "sand_dust");
    public static final ParticleType<?> SNOW_DUST = TParticles.simple(Effectual.MOD_ID, "snow_dust");
    public static final ParticleType<?> GRAVEL_DUST = TParticles.simple(Effectual.MOD_ID, "gravel_dust");
    public static final ParticleType<?> RED_SAND_DUST = TParticles.simple(Effectual.MOD_ID, "red_sand_dust");
    public static final ParticleType<?> AIR_TRAIL = TParticles.simple(Effectual.MOD_ID, "air_trail");
    public static final ParticleType<?> WATER_DRIP = TParticles.simple(Effectual.MOD_ID, "water_drip");
    public static final ParticleType<?> LEVITATION_AURA = TParticles.simple(Effectual.MOD_ID, "levitation_aura");
    public static final ParticleType<?> SPEED_AURA = TParticles.simple(Effectual.MOD_ID, "speed_aura");



    public static void register() {

    }
}
