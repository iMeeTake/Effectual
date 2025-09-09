package com.imeetake.effectual.mixin.client;

import com.imeetake.effectual.ModParticles;
import com.imeetake.tlib.client.particle.TClientParticles;
import com.imeetake.tlib.client.particle.TParticleEffectSimple;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static com.imeetake.effectual.EffectualClient.CONFIG;

@Mixin(ClientPlayerInteractionManager.class)
public class MetalHitMixin {

    @Inject(method = "attackBlock", at = @At("HEAD"))
    private void onMetalHit(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (!CONFIG.metalHitSparks()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        ClientWorld world = client.world;
        PlayerEntity player = client.player;

        ItemStack mainHandItem = player.getMainHandStack();
        if (!mainHandItem.isIn(TagKey.of(RegistryKeys.ITEM, Identifier.of("effectual", "metal_items")))) return;

        BlockState blockState = world.getBlockState(pos);
        if (!blockState.isIn(TagKey.of(RegistryKeys.BLOCK, Identifier.of("effectual", "metal_blocks")))) return;

        spawnSparks(world, pos, direction);
    }

    private void spawnSparks(ClientWorld world, BlockPos pos, Direction direction) {
        Random random = world.random;

        double hitX = pos.getX() + 0.5 + direction.getOffsetX() * 0.5;
        double hitY = pos.getY() + 0.5 + direction.getOffsetY() * 0.5;
        double hitZ = pos.getZ() + 0.5 + direction.getOffsetZ() * 0.5;

        for (int i = 0; i < 6 + random.nextInt(4); i++) {
            double offsetX = hitX + (random.nextDouble() - 0.5) * 0.2;
            double offsetY = hitY + (random.nextDouble() - 0.5) * 0.2;
            double offsetZ = hitZ + (random.nextDouble() - 0.5) * 0.2;

            double velocityX = (random.nextDouble() - 0.5) * 0.1 + direction.getOffsetX() * 0.1;
            double velocityY = (random.nextDouble() - 0.5) * 0.1 + direction.getOffsetY() * 0.1;
            double velocityZ = (random.nextDouble() - 0.5) * 0.1 + direction.getOffsetZ() * 0.1;

            TClientParticles.spawn(new TParticleEffectSimple(ModParticles.METAL_SPARK), offsetX, offsetY, offsetZ, velocityX, velocityY, velocityZ);
        }
    }
}
