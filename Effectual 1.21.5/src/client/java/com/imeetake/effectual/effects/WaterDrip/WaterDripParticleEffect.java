package com.imeetake.effectual.effects.WaterDrip;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class WaterDripParticleEffect implements ParticleEffect {
    private final ParticleType<?> type;

    public WaterDripParticleEffect(ParticleType<?> type) {
        this.type = type;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public void write(PacketByteBuf buf) {

    }

    public String asString() {
        return "WaterDripParticleEffect";
    }
}
