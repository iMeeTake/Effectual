package com.imeetake.effectual.effects.MetalSparks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class MetalSparkTargets {

    private static final String ITEMS_RESOURCE = "/data/effectual/tags/items/metal_items.json";
    private static final String BLOCKS_RESOURCE = "/data/effectual/tags/blocks/metal_blocks.json";

    private static Set<Item> items;
    private static Set<Block> blocks;

    private MetalSparkTargets() {
    }

    public static boolean isMetalItem(Item item) {
        if (items == null) {
            items = resolve(ITEMS_RESOURCE, BuiltInRegistries.ITEM);
        }
        return items.contains(item);
    }

    public static boolean isMetalBlock(Block block) {
        if (blocks == null) {
            blocks = resolve(BLOCKS_RESOURCE, BuiltInRegistries.BLOCK);
        }
        return blocks.contains(block);
    }

    private static <T> Set<T> resolve(String resource, Registry<T> registry) {
        Set<String> ids = readIds(resource);
        if (ids.isEmpty()) {
            return Collections.emptySet();
        }

        Set<T> resolved = new HashSet<>(ids.size());
        for (T value : registry) {
            if (ids.contains(registry.getKey(value).toString())) {
                resolved.add(value);
            }
        }
        return resolved;
    }

    private static Set<String> readIds(String resource) {
        try (InputStream stream = MetalSparkTargets.class.getResourceAsStream(resource)) {
            if (stream == null) {
                return Collections.emptySet();
            }

            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            Set<String> ids = new HashSet<>();
            for (JsonElement entry : root.getAsJsonArray("values")) {
                ids.add(entry.getAsString());
            }
            return ids;
        } catch (IOException e) {
            return Collections.emptySet();
        }
    }
}
