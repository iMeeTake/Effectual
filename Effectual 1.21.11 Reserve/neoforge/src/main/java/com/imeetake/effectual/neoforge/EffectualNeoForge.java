package com.imeetake.effectual.neoforge;

import com.imeetake.effectual.Effectual;
import com.imeetake.effectual.EffectualConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import me.shedaniel.autoconfig.gui.DefaultGuiProviders;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Effectual.MOD_ID)
public final class EffectualNeoForge {
    public EffectualNeoForge() {
        Effectual.init();

        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (client, parent) -> {
                    ConfigManager<EffectualConfig> manager = (ConfigManager<EffectualConfig>) AutoConfig.getConfigHolder(EffectualConfig.class);
                    GuiRegistry registry = DefaultGuiProviders.apply(new GuiRegistry());
                    return new ConfigScreenProvider<>(manager, registry, parent).get();
                }
        );
    }
}