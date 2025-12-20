package com.imeetake.effectual.fabric;

import com.imeetake.effectual.EffectualConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import me.shedaniel.autoconfig.gui.DefaultGuiProviders;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigManager<EffectualConfig> manager = (ConfigManager<EffectualConfig>) AutoConfig.getConfigHolder(EffectualConfig.class);
            GuiRegistry registry = DefaultGuiProviders.apply(new GuiRegistry());
            return new ConfigScreenProvider<>(manager, registry, parent).get();
        };
    }
}