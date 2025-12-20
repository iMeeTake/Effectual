package com.imeetake.effectual.effects.WaterDrip;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;

public class WaterDripParticle extends SingleQuadParticle {
    private final Player player;
    private final double localOffsetX;
    private final double localOffsetZ;
    private int stickTicks;
    private boolean released;

    public WaterDripParticle(ClientLevel level, Player player, double localOffsetX, double localOffsetY, double localOffsetZ, TextureAtlasSprite sprite) {
        super(level, player.getX(), player.getY() + localOffsetY, player.getZ(), 0, 0, 0, sprite);
        this.player = player;
        this.localOffsetX = localOffsetX;
        this.localOffsetZ = localOffsetZ;

        this.quadSize = 0.016F + random.nextFloat() * 0.012F;
        this.lifetime = 20 + random.nextInt(12);
        this.gravity = 0.038F;

        this.alpha = 0.45F + random.nextFloat() * 0.2F;
        this.setColor(0.45F, 0.70F, 1.00F);

        this.stickTicks = 6 + random.nextInt(4);
        this.released = false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!released && this.age <= this.stickTicks) {
            double yaw = Math.toRadians(-this.player.getYRot());
            double cos = Math.cos(yaw);
            double sin = Math.sin(yaw);

            double ox = this.localOffsetX * cos - this.localOffsetZ * sin;
            double oz = this.localOffsetX * sin + this.localOffsetZ * cos;

            double slide = this.gravity * 0.4;
            double px = this.player.getX() + ox;
            double pz = this.player.getZ() + oz;

            this.setPos(px, this.y - slide, pz);
            this.xd = 0;
            this.yd = 0;
            this.zd = 0;

            if (this.age == this.stickTicks) {
                this.released = true;
                this.xd = this.player.getDeltaMovement().x * 0.25;
                this.zd = this.player.getDeltaMovement().z * 0.25;
                this.yd = -this.gravity * 0.5;
            }
        } else {
            this.yd -= this.gravity * 0.6;
            this.x += this.xd;
            this.y += this.yd;
            this.z += this.zd;
            this.xd *= 0.95;
            this.zd *= 0.95;
            if (this.yd < -0.16) this.yd = -0.16;
        }

        float t = (float) this.age / (float) this.lifetime;
        float aIn = t < 0.25F ? t * 4F : 1F;
        float aOut = t > 0.75F ? Math.max(0F, (1F - t) / 0.25F) : 1F;
        this.alpha = (0.45F + 0.2F) * aIn * aOut;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }
}