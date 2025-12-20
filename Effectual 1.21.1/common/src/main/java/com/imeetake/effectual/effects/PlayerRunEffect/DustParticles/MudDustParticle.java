package com.imeetake.effectual.effects.PlayerRunEffect.DustParticles;

import com.imeetake.effectual.effects.PlayerRunEffect.RunDustParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class MudDustParticle extends RunDustParticle {

    public MudDustParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz, Weight.HEAVY, 0.23F, 0.21F, 0.20F);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            MudDustParticle p = new MudDustParticle(level, x, y, z, dx, dy, dz);
            p.pickSprite(this.spriteSet);
            return p;
        }
    }
}