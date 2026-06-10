package com.imeetake.effectual.effects.CaveDust;

import com.imeetake.effectual.EffectualConfig;
import com.imeetake.effectual.EffectualClientParticles;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class CaveDustEffect {

    private static final RandomSource RAND = RandomSource.create();
    private static final int SAMPLES_FOR_CAVE_CHECK = 80;
    private static final int CHECK_RADIUS = 5;

    private static boolean inCaveCached = false;
    private static int checkTimer = 0;

    private static final BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

    private static final Set<Block> NATURAL_BLOCKS = Set.of(
            Blocks.GRAVEL, Blocks.CLAY, Blocks.SAND, Blocks.RED_SAND,
            Blocks.POINTED_DRIPSTONE, Blocks.DRIPSTONE_BLOCK, Blocks.CALCITE, Blocks.TUFF,
            Blocks.AMETHYST_BLOCK, Blocks.BUDDING_AMETHYST, Blocks.MOSS_BLOCK, Blocks.MOSS_CARPET,
            Blocks.GLOW_LICHEN, Blocks.SCULK, Blocks.SCULK_VEIN, Blocks.SCULK_CATALYST,
            Blocks.SCULK_SENSOR, Blocks.SCULK_SHRIEKER, Blocks.WATER, Blocks.LAVA
    );

    private static final Set<Block> ARTIFICIAL_BLOCKS = Set.of(
            Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE, Blocks.MOSSY_COBBLESTONE,
            Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS,
            Blocks.CHISELED_STONE_BRICKS, Blocks.SMOOTH_STONE,
            Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE,
            Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES,
            Blocks.CHISELED_DEEPSLATE, Blocks.BRICKS,
            Blocks.GLASS, Blocks.GLASS_PANE, Blocks.TINTED_GLASS,
            Blocks.IRON_BARS, Blocks.CHAIN,
            Blocks.TORCH, Blocks.WALL_TORCH, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH,
            Blocks.LANTERN, Blocks.SOUL_LANTERN,
            Blocks.CRAFTING_TABLE, Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER,
            Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.BARREL,
            Blocks.BOOKSHELF, Blocks.CHISELED_BOOKSHELF, Blocks.LECTERN,
            Blocks.ENCHANTING_TABLE, Blocks.BREWING_STAND,
            Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER,
            Blocks.LADDER, Blocks.SCAFFOLDING,
            Blocks.TRIAL_SPAWNER, Blocks.VAULT, Blocks.SPAWNER,
            Blocks.CHISELED_TUFF, Blocks.POLISHED_TUFF, Blocks.TUFF_BRICKS, Blocks.CHISELED_TUFF_BRICKS,
            Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER,
            Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_OXIDIZED_COPPER,
            Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER,
            Blocks.WAXED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER,
            Blocks.COPPER_GRATE, Blocks.EXPOSED_COPPER_GRATE, Blocks.WEATHERED_COPPER_GRATE, Blocks.OXIDIZED_COPPER_GRATE,
            Blocks.WAXED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER_GRATE,
            Blocks.COPPER_BULB, Blocks.EXPOSED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB, Blocks.OXIDIZED_COPPER_BULB,
            Blocks.WAXED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB,
            Blocks.COPPER_DOOR, Blocks.EXPOSED_COPPER_DOOR, Blocks.WEATHERED_COPPER_DOOR, Blocks.OXIDIZED_COPPER_DOOR,
            Blocks.WAXED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR,
            Blocks.COPPER_TRAPDOOR, Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.OXIDIZED_COPPER_TRAPDOOR,
            Blocks.WAXED_COPPER_TRAPDOOR, Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR,
            Blocks.CHISELED_COPPER, Blocks.EXPOSED_CHISELED_COPPER, Blocks.WEATHERED_CHISELED_COPPER, Blocks.OXIDIZED_CHISELED_COPPER,
            Blocks.WAXED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER
    );

    public static void register() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (!EffectualConfig.get().caveDust) return;
            if (client.player == null || client.level == null || client.isPaused()) return;

            if (++checkTimer >= 40) {
                checkTimer = 0;
                updateCaveStatus(client.level, client.player.blockPosition());
            }

            if (!inCaveCached) return;

            spawnDustParticles(client.level, client.player.blockPosition());
        });
    }

    private static void updateCaveStatus(Level level, BlockPos center) {
        if (level.getBrightness(LightLayer.SKY, center) > 0) {
            inCaveCached = false;
            return;
        }

        if (level.canSeeSky(center.above())) {
            inCaveCached = false;
            return;
        }

        int naturalCount = 0;
        int artificialCount = 0;

        for (int i = 0; i < SAMPLES_FOR_CAVE_CHECK; i++) {
            int dx = RAND.nextInt(CHECK_RADIUS * 2 + 1) - CHECK_RADIUS;
            int dy = RAND.nextInt(CHECK_RADIUS * 2 + 1) - CHECK_RADIUS;
            int dz = RAND.nextInt(CHECK_RADIUS * 2 + 1) - CHECK_RADIUS;

            checkPos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
            BlockState state = level.getBlockState(checkPos);

            if (state.isAir()) continue;

            if (isNaturalBlock(state)) {
                naturalCount++;
            } else if (isArtificialBlock(state)) {
                artificialCount++;
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

        return NATURAL_BLOCKS.contains(state.getBlock());
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

        return ARTIFICIAL_BLOCKS.contains(state.getBlock());
    }

    private static void spawnDustParticles(Level level, BlockPos center) {
        int frequency = EffectualConfig.get().caveDustFrequency;
        if (frequency <= 0 || RAND.nextInt(10) > frequency) return;

        for (int i = 0; i < 2; i++) {
            double x = center.getX() + (RAND.nextDouble() - 0.5) * 32.0;
            double z = center.getZ() + (RAND.nextDouble() - 0.5) * 32.0;
            double y = center.getY() + RAND.nextDouble() * 6.0 + 1.0;

            BlockPos pos = BlockPos.containing(x, y, z);

            if (!level.getBlockState(pos).isAir()) continue;
            if (!level.getBlockState(pos.below()).isAir()) continue;

            EffectualClientParticles.spawnVanilla(ParticleTypes.WHITE_ASH,
                    x, y, z,
                    0, -0.01, 0);
        }
    }
}
