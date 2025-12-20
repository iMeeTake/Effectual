package com.imeetake.effectual.mixin;

import com.imeetake.effectual.effects.FireEntitySparks.FireEntitySparksEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract boolean isOnFire();

    @Shadow
    public Level level;

    @Inject(method = "tick", at = @At("TAIL"))
    private void effectual$tick(CallbackInfo ci) {
        if (this.level.isClientSide() && this.isOnFire()) {
            FireEntitySparksEffect.tick((Entity) (Object) this);
        }
    }
}