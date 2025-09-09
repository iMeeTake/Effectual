package com.imeetake.effectual.effects.WaterDrip;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

public class WaterDripParticle extends SpriteBillboardParticle {
    private final PlayerEntity player;
    private final double localOffsetX;
    private final double localOffsetZ;
    private int stickTicks;
    private boolean released;

    public WaterDripParticle(ClientWorld world, PlayerEntity player, double localOffsetX, double localOffsetY, double localOffsetZ, SpriteProvider sprites) {
        super(world, player.getX(), player.getY() + localOffsetY, player.getZ(), 0, 0, 0);
        this.player = player;
        this.localOffsetX = localOffsetX;
        this.localOffsetZ = localOffsetZ;
        this.setSprite(sprites);
        this.scale = 0.016F + random.nextFloat() * 0.012F;
        this.maxAge = 20 + random.nextInt(12);
        this.gravityStrength = 0.038F;
        this.setAlpha(0.45F + random.nextFloat() * 0.2F);
        this.setColor(0.45F, 0.70F, 1.00F);
        this.stickTicks = 6 + random.nextInt(4);
        this.released = false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!released && this.age <= this.stickTicks) {
            double yaw = Math.toRadians(-this.player.getYaw(0));
            double cos = Math.cos(yaw);
            double sin = Math.sin(yaw);
            double ox = this.localOffsetX * cos - this.localOffsetZ * sin;
            double oz = this.localOffsetX * sin + this.localOffsetZ * cos;
            double slide = this.gravityStrength * 0.4;
            double px = this.player.getX() + ox;
            double pz = this.player.getZ() + oz;
            this.setPos(px, this.y - slide, pz);
            this.velocityX = 0;
            this.velocityY = 0;
            this.velocityZ = 0;
            if (this.age == this.stickTicks) {
                this.released = true;
                this.velocityX = this.player.getVelocity().x * 0.25;
                this.velocityZ = this.player.getVelocity().z * 0.25;
                this.velocityY = -this.gravityStrength * 0.5;
            }
        } else {
            this.velocityY -= this.gravityStrength * 0.6;
            this.x += this.velocityX;
            this.y += this.velocityY;
            this.z += this.velocityZ;
            this.velocityX *= 0.95;
            this.velocityZ *= 0.95;
            if (this.velocityY < -0.16) this.velocityY = -0.16;
        }

        float t = (float) this.age / (float) this.maxAge;
        float aIn = t < 0.25F ? t * 4F : 1F;
        float aOut = t > 0.75F ? Math.max(0F, (1F - t) / 0.25F) : 1F;
        this.setAlpha((0.45F + 0.2F) * aIn * aOut);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
