package com.imeetake.effectual.effects.CaveDust;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.tlib.client.particle.TClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CaveDustEffect {

    private static final RandomSource RAND = RandomSource.create();
    private static boolean inCaveCached = false;
    private static int checkTimer = 0;

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().caveDust) return;
            if (client.player == null || client.level == null || client.isPaused()) return;

            if (checkTimer++ >= 20) {
                checkTimer = 0;
                updateCaveStatus(client);
            }

            if (!inCaveCached) return;

            spawnDustParticles(client);
        });
    }

    private static void updateCaveStatus(Minecraft client) {
        BlockPos center = client.player.blockPosition();

        if (client.level.getBrightness(LightLayer.SKY, center) > 0) {
            inCaveCached = false;
            return;
        }

        if (client.level.canSeeSky(center.above())) {
            inCaveCached = false;
            return;
        }

        int radius = 6;
        int naturalCount = 0;
        int artificialCount = 0;

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    mpos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = client.level.getBlockState(mpos);

                    if (state.isAir()) continue;

                    if (isNaturalBlock(state)) {
                        naturalCount++;
                    } else if (isArtificialBlock(state)) {
                        artificialCount++;
                    }
                }
            }
        }

        int total = naturalCount + artificialCount;
        if (total == 0) {
            inCaveCached = false;
            return;
        }

        float naturalRatio = (float) naturalCount / total;
        inCaveCached = naturalRatio >= 0.85f;
    }

    private static boolean isNaturalBlock(BlockState state) {
        if (state.is(BlockTags.BASE_STONE_OVERWORLD)) return true;
        if (state.is(BlockTags.DIRT)) return true;
        if (state.is(BlockTags.COAL_ORES)) return true;
        if (state.is(BlockTags.IRON_ORES)) return true;
        if (state.is(BlockTags.COPPER_ORES)) return true;
        if (state.is(BlockTags.GOLD_ORES)) return true;
        if (state.is(BlockTags.REDSTONE_ORES)) return true;
        if (state.is(BlockTags.LAPIS_ORES)) return true;
        if (state.is(BlockTags.DIAMOND_ORES)) return true;
        if (state.is(BlockTags.EMERALD_ORES)) return true;

        Block b = state.getBlock();
        return b == Blocks.GRAVEL
                || b == Blocks.CLAY
                || b == Blocks.SAND
                || b == Blocks.RED_SAND
                || b == Blocks.POINTED_DRIPSTONE
                || b == Blocks.DRIPSTONE_BLOCK
                || b == Blocks.CALCITE
                || b == Blocks.TUFF
                || b == Blocks.AMETHYST_BLOCK
                || b == Blocks.BUDDING_AMETHYST
                || b == Blocks.MOSS_BLOCK
                || b == Blocks.MOSS_CARPET
                || b == Blocks.GLOW_LICHEN
                || b == Blocks.SCULK
                || b == Blocks.SCULK_VEIN
                || b == Blocks.SCULK_CATALYST
                || b == Blocks.SCULK_SENSOR
                || b == Blocks.SCULK_SHRIEKER
                || b == Blocks.WATER
                || b == Blocks.LAVA;
    }

    private static boolean isArtificialBlock(BlockState state) {
        if (state.is(BlockTags.PLANKS)) return true;
        if (state.is(BlockTags.WOOL)) return true;
        if (state.is(BlockTags.WOOL_CARPETS)) return true;
        if (state.is(BlockTags.BEDS)) return true;
        if (state.is(BlockTags.DOORS)) return true;
        if (state.is(BlockTags.TRAPDOORS)) return true;
        if (state.is(BlockTags.FENCES)) return true;
        if (state.is(BlockTags.WALLS)) return true;
        if (state.is(BlockTags.STAIRS)) return true;
        if (state.is(BlockTags.SLABS)) return true;
        if (state.is(BlockTags.RAILS)) return true;
        if (state.is(BlockTags.SIGNS)) return true;
        if (state.is(BlockTags.BANNERS)) return true;
        if (state.is(BlockTags.TERRACOTTA)) return true;
        if (state.is(BlockTags.CANDLES)) return true;
        if (state.is(BlockTags.CAMPFIRES)) return true;
        if (state.is(BlockTags.ANVIL)) return true;
        if (state.is(BlockTags.SHULKER_BOXES)) return true;

        Block b = state.getBlock();
        return b == Blocks.COBBLESTONE
                || b == Blocks.COBBLED_DEEPSLATE
                || b == Blocks.MOSSY_COBBLESTONE
                || b == Blocks.STONE_BRICKS
                || b == Blocks.MOSSY_STONE_BRICKS
                || b == Blocks.CRACKED_STONE_BRICKS
                || b == Blocks.CHISELED_STONE_BRICKS
                || b == Blocks.SMOOTH_STONE
                || b == Blocks.POLISHED_ANDESITE
                || b == Blocks.POLISHED_DIORITE
                || b == Blocks.POLISHED_GRANITE
                || b == Blocks.POLISHED_DEEPSLATE
                || b == Blocks.DEEPSLATE_BRICKS
                || b == Blocks.DEEPSLATE_TILES
                || b == Blocks.CHISELED_DEEPSLATE
                || b == Blocks.BRICKS
                || b == Blocks.GLASS
                || b == Blocks.GLASS_PANE
                || b == Blocks.TINTED_GLASS
                || b == Blocks.IRON_BARS
                || b == Blocks.CHAIN
                || b == Blocks.TORCH
                || b == Blocks.WALL_TORCH
                || b == Blocks.SOUL_TORCH
                || b == Blocks.SOUL_WALL_TORCH
                || b == Blocks.LANTERN
                || b == Blocks.SOUL_LANTERN
                || b == Blocks.CRAFTING_TABLE
                || b == Blocks.FURNACE
                || b == Blocks.BLAST_FURNACE
                || b == Blocks.SMOKER
                || b == Blocks.CHEST
                || b == Blocks.TRAPPED_CHEST
                || b == Blocks.BARREL
                || b == Blocks.BOOKSHELF
                || b == Blocks.CHISELED_BOOKSHELF
                || b == Blocks.LECTERN
                || b == Blocks.ENCHANTING_TABLE
                || b == Blocks.BREWING_STAND
                || b == Blocks.HOPPER
                || b == Blocks.DROPPER
                || b == Blocks.DISPENSER
                || b == Blocks.LADDER
                || b == Blocks.SCAFFOLDING;
    }

    private static void spawnDustParticles(Minecraft client) {
        int frequency = EffectualConfig.get().caveDustFrequency;
        if (frequency <= 0 || RAND.nextInt(10) > frequency) return;

        BlockPos center = client.player.blockPosition();

        for (int i = 0; i < 2; i++) {
            double x = center.getX() + (RAND.nextDouble() - 0.5) * 32.0;
            double z = center.getZ() + (RAND.nextDouble() - 0.5) * 32.0;
            double y = center.getY() + RAND.nextDouble() * 6.0 + 1.0;

            BlockPos pos = BlockPos.containing(x, y, z);

            if (!client.level.getBlockState(pos).isAir()) continue;
            if (!client.level.getBlockState(pos.below()).isAir()) continue;

            TClientParticles.spawn(ParticleTypes.WHITE_ASH,
                    x, y, z,
                    0, -0.01, 0);
        }
    }
}