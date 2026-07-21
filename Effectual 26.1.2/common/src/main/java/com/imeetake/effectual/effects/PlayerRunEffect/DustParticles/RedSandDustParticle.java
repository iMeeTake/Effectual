package com.imeetake.effectual.effects.PlayerRunEffect.DustParticles;

import com.imeetake.effectual.effects.PlayerRunEffect.RunDustParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public class RedSandDustParticle extends RunDustParticle {

    public RedSandDustParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, TextureAtlasSprite sprite) {
        super(level, x, y, z, vx, vy, vz, Weight.LIGHT, 0.58F, 0.29F, 0.12F, sprite);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, RandomSource random) {
            return new RedSandDustParticle(level, x, y, z, dx, dy, dz, this.spriteSet.get(random));
        }
    }
}