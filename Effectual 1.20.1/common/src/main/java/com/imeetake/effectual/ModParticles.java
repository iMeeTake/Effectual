package com.imeetake.effectual;

import com.imeetake.tlib.client.particle.TParticles;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Effectual.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> SAND_DUST =
            PARTICLE_TYPES.register("sand_dust", () -> new SimpleParticleType(false) {
            });
    public static final RegistrySupplier<SimpleParticleType> RED_SAND_DUST =
            PARTICLE_TYPES.register("red_sand_dust", () -> new SimpleParticleType(false) {
            });
    public static final RegistrySupplier<SimpleParticleType> SNOW_DUST =
            PARTICLE_TYPES.register("snow_dust", () -> new SimpleParticleType(false) {
            });
    public static final RegistrySupplier<SimpleParticleType> GRAVEL_DUST =
            PARTICLE_TYPES.register("gravel_dust", () -> new SimpleParticleType(false) {
            });
    public static final RegistrySupplier<SimpleParticleType> MUD_DUST =
            PARTICLE_TYPES.register("mud_dust", () -> new SimpleParticleType(false) {
            });

    public static final RegistrySupplier<SimpleParticleType> MOUTH_STEAM =
            PARTICLE_TYPES.register("mouth_steam", () -> new SimpleParticleType(false) {
            });
    public static final RegistrySupplier<SimpleParticleType> WATER_DRIP =
            PARTICLE_TYPES.register("water_drip", () -> new SimpleParticleType(false) {
            });
    public static final RegistrySupplier<SimpleParticleType> METAL_SPARK =
            PARTICLE_TYPES.register("metal_spark", () -> new SimpleParticleType(false) {
            });
    public static final RegistrySupplier<SimpleParticleType> ENTITY_SPARK =
            PARTICLE_TYPES.register("entity_spark", () -> new SimpleParticleType(false) {
            });


    public static final RegistrySupplier<SimpleParticleType> AIR_TRAIL =
            TParticles.simple(PARTICLE_TYPES, "air_trail");
    public static final RegistrySupplier<SimpleParticleType> GOLD_GLOW =
            TParticles.simple(PARTICLE_TYPES, "gold_glow");
    public static final RegistrySupplier<SimpleParticleType> SOUL_GLOW =
            TParticles.simple(PARTICLE_TYPES, "soul_glow");
    public static final RegistrySupplier<SimpleParticleType> SPARK =
            TParticles.simple(PARTICLE_TYPES, "spark");
    public static final RegistrySupplier<SimpleParticleType> SOUL_SPARK =
            TParticles.simple(PARTICLE_TYPES, "soul_spark");
    public static final RegistrySupplier<SimpleParticleType> SNOW_FOOTPRINT =
            TParticles.simple(PARTICLE_TYPES, "snow_footprint");


    public static void register() {
        PARTICLE_TYPES.register();
    }
}