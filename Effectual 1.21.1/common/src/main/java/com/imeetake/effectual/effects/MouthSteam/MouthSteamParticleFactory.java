package com.imeetake.effectual.effects.MouthSteam;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class MouthSteamParticleFactory implements ParticleProvider<SimpleParticleType> {
    private static SpriteSet activeSprites;

    private final SpriteSet spriteSet;

    public MouthSteamParticleFactory(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
        activeSprites = spriteSet;
    }

    static void spawn(double x, double y, double z, double dx, double dy, double dz, BreathAnchor anchor) {
        SpriteSet sprites = activeSprites;
        if (sprites == null) return;

        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null || TClientParticles.shouldCull(x, y, z)) return;

        client.particleEngine.add(new MouthSteamParticle(level, x, y, z, dx, dy, dz, sprites, anchor));
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        return new MouthSteamParticle(level, x, y, z, dx, dy, dz, spriteSet, BreathAnchor.NONE);
    }
}
