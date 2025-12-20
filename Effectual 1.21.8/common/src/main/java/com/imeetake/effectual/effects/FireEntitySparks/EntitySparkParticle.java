package com.imeetake.effectual.effects.FireEntitySparks;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class EntitySparkParticle extends TextureSheetParticle {

    private final float rotSpeed;
    private final float baseSize;

    public EntitySparkParticle(
            ClientLevel level,
            double x, double y, double z,
            double velocityX, double velocityY, double velocityZ
    ) {
        super(level, x, y, z, velocityX, velocityY, velocityZ);

        this.baseSize = 0.015F * (1.0F + random.nextFloat() * 0.5F);
        this.quadSize = this.baseSize;

        this.lifetime = 15 + this.random.nextInt(10);

        this.hasPhysics = true;
        this.gravity = 0.0F;

        this.xd = velocityX * 0.5;
        this.yd = velocityY * 0.5;
        this.zd = velocityZ * 0.5;

        float baseBrightness = 0.9f + random.nextFloat() * 0.1f;
        float r = baseBrightness * (0.9f + random.nextFloat() * 0.1f);
        float g = baseBrightness * (0.5f + random.nextFloat() * 0.4f);
        float b = baseBrightness * random.nextFloat() * 0.2f;

        this.setColor(r, g, b);
        this.alpha = 1.0F;

        this.rotSpeed = (this.random.nextFloat() - 0.5F) * 0.15F;
        this.roll = this.random.nextFloat() * (float) (Math.PI * 2);
        this.oRoll = this.roll;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.oRoll = this.roll;
        this.roll += this.rotSpeed;

        this.yd += 0.001D;

        this.move(this.xd, this.yd, this.zd);

        this.xd *= 0.96;
        this.yd *= 0.96;
        this.zd *= 0.96;

        if (this.age > this.lifetime / 2) {
            float progress = (float) (this.age - this.lifetime / 2) / (this.lifetime / 2);
            this.quadSize = this.baseSize * (1.0F - progress);
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            EntitySparkParticle particle = new EntitySparkParticle(level, x, y, z, dx, dy, dz);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}