package com.imeetake.effectual.mixin;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.tlib.client.particle.TClientParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class BlockPlaceMixin {

    @Inject(method = "useItemOn", at = @At("TAIL"))
    private void onBlockPlace(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!EffectualConfig.get().blockPlaceEffect) return;

        if (!cir.getReturnValue().consumesAction()) return;

        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) return;

        BlockPos placedBlockPos = hitResult.getBlockPos().relative(hitResult.getDirection());
        BlockState state = world.getBlockState(placedBlockPos);
        Block block = state.getBlock();

        if (block == Blocks.SNOW || block == Blocks.SNOW_BLOCK || block == Blocks.POWDER_SNOW ||
                block == Blocks.SAND || block == Blocks.SUSPICIOUS_SAND ||
                block == Blocks.GRAVEL || block == Blocks.SUSPICIOUS_GRAVEL ||
                block == Blocks.RED_SAND) {

            RandomSource random = world.random;

            for (int i = 0; i < 12; i++) {
                TClientParticles.spawn(
                        new BlockParticleOption(ParticleTypes.BLOCK, state),
                        placedBlockPos.getX() + random.nextDouble(),
                        placedBlockPos.getY() + 0.2,
                        placedBlockPos.getZ() + random.nextDouble(),
                        (random.nextDouble() - 0.5) * 0.1,
                        random.nextDouble() * 0.1,
                        (random.nextDouble() - 0.5) * 0.1
                );
            }
        }
    }
}