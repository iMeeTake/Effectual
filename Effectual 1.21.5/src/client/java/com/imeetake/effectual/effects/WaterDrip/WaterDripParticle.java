package com.imeetake.effectual.effects.WaterDrip;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

public class WaterDripParticle extends SpriteBillboardParticle {
    private final PlayerEntity player;
    private final double initialOffsetX;
    private final double initialOffsetZ;

    public WaterDripParticle(ClientWorld world, PlayerEntity player, double offsetX, double offsetY, double offsetZ, SpriteProvider spriteProvider) {
        super(world, player.getX() + offsetX, player.getY() + offsetY, player.getZ() + offsetZ, 0, 0, 0); // Без начального импульса
        this.player = player;
        this.initialOffsetX = offsetX;
        this.initialOffsetZ = offsetZ;

        this.setSprite(spriteProvider);


        this.scale = 0.02F + random.nextFloat() * 0.02F;
        this.maxAge = 20 + random.nextInt(10);
        this.gravityStrength = 0.03F;
        this.velocityY = 0;
        this.setAlpha(0.9F);


        this.setColor(0.4F, 0.7F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();


        this.setPos(
                player.getX() + initialOffsetX,
                this.y - this.gravityStrength,
                player.getZ() + initialOffsetZ
        );


        if (this.age > this.maxAge - 10) {
            this.setAlpha((this.maxAge - this.age) / 10.0F);
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
