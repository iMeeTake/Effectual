package com.imeetake.effectual.forge.client;

import com.imeetake.effectual.Effectual;
import com.imeetake.effectual.EffectualConfig;
import dev.architectury.platform.forge.EventBuses;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class EffectualForgeClient {
    private EffectualForgeClient() {
    }

    public static void init() {
        EventBuses.registerModEventBus(Effectual.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Effectual.init();
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> AutoConfig.getConfigScreen(EffectualConfig.class, parent).get()
                )
        );
    }
}
