package com.imeetake.effectual.mixin;

import com.imeetake.effectual.effects.Sparks.CampfireImprovements;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {

    @Inject(method = "animateTick", at = @At("TAIL"))
    private void effectual$onAnimateTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!state.getValue(CampfireBlock.LIT)) return;

        boolean isSoul = state.is(Blocks.SOUL_CAMPFIRE);
        CampfireImprovements.animateTick(level, pos, random, isSoul);
    }
}
