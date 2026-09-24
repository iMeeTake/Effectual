package com.imeetake.effectual.effects.PlayerRunEffect;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public abstract class RunDustParticle extends SingleQuadParticle {

    public enum Weight {
        LIGHT,
        HEAVY
    }

    protected final Weight weight;
    protected final float rotSpeed;
    protected float baseSize;

    protected RunDustParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz,
                              Weight weight, float r, float g, float b, TextureAtlasSprite sprite) {
        super(level, x, y, z, vx, vy, vz, sprite);

        this.weight = weight;

        float brightnessNoise = (random.nextFloat() - 0.5F) * 0.15F;

        this.rCol = Math.max(0.0F, Math.min(1.0F, r + brightnessNoise));
        this.gCol = Math.max(0.0F, Math.min(1.0F, g + brightnessNoise));
        this.bCol = Math.max(0.0F, Math.min(1.0F, b + brightnessNoise));

        this.baseSize = 0.04F * (1.0F + random.nextFloat() * 0.5F);
        this.quadSize = this.baseSize;

        this.rotSpeed = (random.nextFloat() - 0.5F) * (weight == Weight.HEAVY ? 0.25F : 0.1F);
        this.roll = random.nextFloat() * 6.28F;
        this.oRoll = this.roll;

        if (weight == Weight.LIGHT) {
            this.lifetime = 15 + this.random.nextInt(10);
            this.gravity = 0.02F;
            this.alpha = 0.9F;

            this.xd = vx;
            this.yd = vy + 0.05;
            this.zd = vz;
        } else {
            this.lifetime = 25 + this.random.nextInt(15);
            this.gravity = 0.5F;
            this.alpha = 1.0F;

            this.xd = vx;
            this.yd = vy + 0.1;
            this.zd = vz;
        }
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

        if (weight == Weight.LIGHT || !this.onGround) {
            this.oRoll = this.roll;
            this.roll += this.rotSpeed;
        }

        if (weight == Weight.LIGHT) {
            if (this.age < 4) {
                this.baseSize += 0.004F;
                this.quadSize = this.baseSize;
            }

            this.yd -= 0.005D + this.gravity;
            this.move(this.xd, this.yd, this.zd);

            this.xd *= 0.9;
            this.yd *= 0.9;
            this.zd *= 0.9;

            if (this.onGround) {
                this.xd *= 0.7;
                this.zd *= 0.7;
            }

        } else {
            if (!this.onGround) {
                this.yd -= 0.04D;
            } else {
                this.xd = 0;
                this.zd = 0;
            }

            this.move(this.xd, this.yd, this.zd);

            if (!this.onGround) {
                this.xd *= 0.95;
                this.zd *= 0.95;
            }
        }
        int fadeTime = (weight == Weight.HEAVY) ? 10 : 7;

        if (this.age > this.lifetime - fadeTime) {
            float fadeProgress = (float) (this.age - (this.lifetime - fadeTime)) / (float) fadeTime;
            this.quadSize = this.baseSize * (1.0F - fadeProgress);
        }
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }
}