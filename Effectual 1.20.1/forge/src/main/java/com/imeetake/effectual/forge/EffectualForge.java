package com.imeetake.effectual.forge;

import com.imeetake.effectual.EffectualConfig;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.imeetake.effectual.Effectual;

@Mod(Effectual.MOD_ID)
public final class EffectualForge {
    public EffectualForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(Effectual.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        Effectual.init();

        net.minecraftforge.fml.ModLoadingContext.get().registerExtensionPoint(
                net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> me.shedaniel.autoconfig.AutoConfig.getConfigScreen(EffectualConfig.class, parent).get()
                )
        );
    }
}
