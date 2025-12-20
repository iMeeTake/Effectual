package com.imeetake.effectual.effects.PlayerRunEffect.DustParticles;

import com.imeetake.effectual.effects.PlayerRunEffect.RunDustParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class GravelDustParticle extends RunDustParticle {

    public GravelDustParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz, Weight.HEAVY, 0.45F, 0.42F, 0.45F);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            GravelDustParticle p = new GravelDustParticle(level, x, y, z, dx, dy, dz);
            p.pickSprite(this.spriteSet);
            return p;
        }
    }
}