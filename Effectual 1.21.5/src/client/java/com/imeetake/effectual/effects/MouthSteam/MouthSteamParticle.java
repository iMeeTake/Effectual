package com.imeetake.effectual.effects.MouthSteam;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

public class MouthSteamParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final PlayerEntity player;

    public MouthSteamParticle(
            ClientWorld world,
            PlayerEntity player,
            SpriteProvider spriteProvider
    ) {
        super(world, player.getX(), player.getEyeY() - 0.2, player.getZ(), 0, 0, 0);
        this.spriteProvider = spriteProvider;
        this.player = player;

        this.scale = 0.2F;
        this.maxAge = 20 + this.random.nextInt(10);
        this.collidesWithWorld = false;
        this.gravityStrength = 0.0F;

        this.setAlpha(0.8F);


        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age >= this.maxAge) {
            this.markDead();
            return;
        }


        updateParticlePosition();


        float lifeRatio = (float) this.age / (float) this.maxAge;
        this.setAlpha(0.8F - lifeRatio * 0.8F);


        this.velocityX = 0;
        this.velocityY = 0.002;
        this.velocityZ = 0;


        this.setSpriteForAge(this.spriteProvider);
    }

    private void updateParticlePosition() {
        double offsetForward = 0.4D;
        float yaw = (float) Math.toRadians(this.player.getYaw());
        float pitch = (float) Math.toRadians(this.player.getPitch());


        double xOffset = -Math.sin(yaw) * Math.cos(pitch) * offsetForward;
        double yOffset = -Math.sin(pitch) * offsetForward;
        double zOffset = Math.cos(yaw) * Math.cos(pitch) * offsetForward;


        this.setPos(
                this.player.getX() + xOffset,
                this.player.getEyeY() - 0.1 + yOffset,
                this.player.getZ() + zOffset
        );
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
