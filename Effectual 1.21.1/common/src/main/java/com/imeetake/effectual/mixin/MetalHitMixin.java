package com.imeetake.effectual.mixin;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
import com.imeetake.effectual.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MetalHitMixin {

    @Unique
    private static final TagKey<Item> METAL_ITEMS_TAG = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("effectual", "metal_items")
    );

    @Unique
    private static final TagKey<Block> METAL_BLOCKS_TAG = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("effectual", "metal_blocks")
    );

    @Inject(method = "startDestroyBlock", at = @At("HEAD"))
    private void onMetalHit(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (!EffectualConfig.get().metalHitSparks) return;

        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) return;

        ClientLevel world = client.level;
        LocalPlayer player = client.player;

        ItemStack mainHandItem = player.getMainHandItem();

        if (!mainHandItem.is(METAL_ITEMS_TAG)) return;

        BlockState blockState = world.getBlockState(pos);
        if (!blockState.is(METAL_BLOCKS_TAG)) return;

        Vec3 hitPos;
        HitResult hitResult = client.hitResult;

        if (hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getBlockPos().equals(pos)) {
            hitPos = blockHitResult.getLocation();
        } else {
            hitPos = Vec3.atCenterOf(pos).add(
                    direction.getStepX() * 0.5,
                    direction.getStepY() * 0.5,
                    direction.getStepZ() * 0.5
            );
        }

        spawnSparks(world, hitPos, direction);
    }

    @Unique
    private void spawnSparks(ClientLevel world, Vec3 pos, Direction direction) {
        RandomSource random = world.random;

        for (int i = 0; i < 6 + random.nextInt(4); i++) {
            double offsetX = pos.x + (random.nextDouble() - 0.5) * 0.2;
            double offsetY = pos.y + (random.nextDouble() - 0.5) * 0.2;
            double offsetZ = pos.z + (random.nextDouble() - 0.5) * 0.2;

            double velocityX = (random.nextDouble() - 0.5) * 0.1 + direction.getStepX() * 0.1;
            double velocityY = (random.nextDouble() - 0.5) * 0.1 + direction.getStepY() * 0.1;
            double velocityZ = (random.nextDouble() - 0.5) * 0.1 + direction.getStepZ() * 0.1;

            EffectualClientParticles.spawn(ModParticles.METAL_SPARK, offsetX, offsetY, offsetZ, velocityX, velocityY, velocityZ);
        }
    }
}
