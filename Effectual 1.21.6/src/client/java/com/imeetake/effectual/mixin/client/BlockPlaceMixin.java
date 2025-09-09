package com.imeetake.effectual.mixin.client;

import com.imeetake.tlib.client.particle.TClientParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.imeetake.effectual.EffectualClient.CONFIG;

@Mixin(ClientPlayerInteractionManager.class)
public class BlockPlaceMixin {
    @Inject(method = "interactBlock", at = @At("TAIL"))
    private void onBlockPlace(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
         if (!CONFIG.blockPlaceEffect()) return;

        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        BlockPos placedBlockPos = hitResult.getBlockPos().offset(hitResult.getSide());
        BlockState state = world.getBlockState(placedBlockPos);
        Block block = state.getBlock();


        if (block == Blocks.SNOW || block == Blocks.SNOW_BLOCK || block == Blocks.POWDER_SNOW || block == Blocks.SAND || block == Blocks.SUSPICIOUS_SAND || block == Blocks.GRAVEL || block == Blocks.SUSPICIOUS_GRAVEL || block == Blocks.RED_SAND) {
            for (int i = 0; i < 12; i++) {
                TClientParticles.spawn(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                        placedBlockPos.getX() + world.random.nextDouble(),
                        placedBlockPos.getY() + 0.2,
                        placedBlockPos.getZ() + world.random.nextDouble(),
                        (world.random.nextDouble() - 0.5) * 0.1,
                        world.random.nextDouble() * 0.1,
                        (world.random.nextDouble() - 0.5) * 0.1
                );
            }
        }
    }
}
