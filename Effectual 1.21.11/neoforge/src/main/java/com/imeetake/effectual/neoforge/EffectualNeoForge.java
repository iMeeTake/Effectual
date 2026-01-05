package com.imeetake.effectual.neoforge;

import com.imeetake.effectual.Effectual;
import com.imeetake.effectual.EffectualConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Effectual.MOD_ID)
public final class EffectualNeoForge {
    public EffectualNeoForge() {
        // Run our common setup.
        Effectual.init();

        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (client, parent) -> AutoConfigClient.getConfigScreen(EffectualConfig.class, parent).get()
        );
    }
}
