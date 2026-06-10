package com.imeetake.effectual;

import com.imeetake.tlib.particle.TParticleTypes;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Effectual.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> SAND_DUST =
            TParticleTypes.simple(PARTICLE_TYPES, "sand_dust");
    public static final RegistrySupplier<SimpleParticleType> RED_SAND_DUST =
            TParticleTypes.simple(PARTICLE_TYPES, "red_sand_dust");
    public static final RegistrySupplier<SimpleParticleType> SNOW_DUST =
            TParticleTypes.simple(PARTICLE_TYPES, "snow_dust");
    public static final RegistrySupplier<SimpleParticleType> GRAVEL_DUST =
            TParticleTypes.simple(PARTICLE_TYPES, "gravel_dust");
    public static final RegistrySupplier<SimpleParticleType> MUD_DUST =
            TParticleTypes.simple(PARTICLE_TYPES, "mud_dust");

    public static final RegistrySupplier<SimpleParticleType> MOUTH_STEAM =
            TParticleTypes.simple(PARTICLE_TYPES, "mouth_steam");
    public static final RegistrySupplier<SimpleParticleType> WATER_DRIP =
            TParticleTypes.simple(PARTICLE_TYPES, "water_drip");
    public static final RegistrySupplier<SimpleParticleType> METAL_SPARK =
            TParticleTypes.simple(PARTICLE_TYPES, "metal_spark");
    public static final RegistrySupplier<SimpleParticleType> ENTITY_SPARK =
            TParticleTypes.simple(PARTICLE_TYPES, "entity_spark");

    public static final RegistrySupplier<SimpleParticleType> AIR_TRAIL =
            TParticleTypes.simple(PARTICLE_TYPES, "air_trail");
    public static final RegistrySupplier<SimpleParticleType> GOLD_GLOW =
            TParticleTypes.simple(PARTICLE_TYPES, "gold_glow");
    public static final RegistrySupplier<SimpleParticleType> SOUL_GLOW =
            TParticleTypes.simple(PARTICLE_TYPES, "soul_glow");
    public static final RegistrySupplier<SimpleParticleType> SPARK =
            TParticleTypes.simple(PARTICLE_TYPES, "spark");
    public static final RegistrySupplier<SimpleParticleType> SOUL_SPARK =
            TParticleTypes.simple(PARTICLE_TYPES, "soul_spark");
    public static final RegistrySupplier<SimpleParticleType> SNOW_FOOTPRINT =
            TParticleTypes.simple(PARTICLE_TYPES, "snow_footprint");
    public static final RegistrySupplier<SimpleParticleType> SNOW_RESIDUE =
            TParticleTypes.simple(PARTICLE_TYPES, "snow_residue");

    public static void register() {
        PARTICLE_TYPES.register();
    }
}
