package com.imeetake.effectual.effects.MouthSteam;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class MouthSteamParticle extends SingleQuadParticle {
    private final SpriteSet spriteSet;
    private final Player player;

    private final int detachTime;
    private double forwardOffset;
    private double exitSpeed;

    public MouthSteamParticle(
            ClientLevel level,
            Player player,
            SpriteSet spriteSet,
            TextureAtlasSprite sprite
    ) {
        super(level, player.getX(), player.getEyeY(), player.getZ(), 0, 0, 0, sprite);
        this.spriteSet = spriteSet;
        this.player = player;

        this.quadSize = 0.035F + this.random.nextFloat() * 0.05F;
        this.lifetime = 30 + this.random.nextInt(10);
        this.detachTime = 6 + this.random.nextInt(4);

        this.exitSpeed = 0.06 + this.random.nextDouble() * 0.03;
        this.forwardOffset = 0.2;

        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.alpha = 0.6F;

        this.setSpriteFromAge(this.spriteSet);

        this.updateAttachedPosition();
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

        if (this.age <= this.detachTime) {
            if (!this.player.isAlive() || this.player.level() != this.level) {
                this.remove();
                return;
            }

            this.forwardOffset += this.exitSpeed;
            this.exitSpeed *= 0.9;

            this.quadSize += 0.008F;

            updateAttachedPosition();
        } else {
            this.yd = 0.005;
            this.move(this.xd, this.yd, this.zd);

            this.xd *= 0.95;
            this.zd *= 0.95;
        }

        float lifeRatio = (float) this.age / (float) this.lifetime;
        this.alpha = 0.6F - (lifeRatio * 0.6F);

        this.setSpriteFromAge(this.spriteSet);
    }

    private void updateAttachedPosition() {
        float yaw = (float) Math.toRadians(this.player.getYRot());
        float pitch = (float) Math.toRadians(this.player.getXRot());

        double lookX = -Mth.sin(yaw) * Mth.cos(pitch);
        double lookY = -Mth.sin(pitch);
        double lookZ = Mth.cos(yaw) * Mth.cos(pitch);

        double targetX = this.player.getX() + (lookX * this.forwardOffset);
        double targetY = (this.player.getEyeY() - 0.15) + (lookY * this.forwardOffset);
        double targetZ = this.player.getZ() + (lookZ * this.forwardOffset);

        this.setPos(targetX, targetY, targetZ);

        this.xd = lookX * 0.02;
        this.zd = lookZ * 0.02;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }
}