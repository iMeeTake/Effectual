package com.imeetake.effectual.effects.MouthSteam;


import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class MouthSteamParticleEffect implements ParticleEffect {
    private final ParticleType<?> type;

    public MouthSteamParticleEffect(ParticleType<?> type) {
        this.type = type;
    }

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }


    public void write(PacketByteBuf buf) {

    }

    public String asString() {
        return "MouthSteamParticleEffect";
    }
}