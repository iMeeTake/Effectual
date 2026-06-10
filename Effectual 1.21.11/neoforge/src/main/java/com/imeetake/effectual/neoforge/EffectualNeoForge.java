package com.imeetake.effectual.neoforge;

import com.imeetake.effectual.Effectual;
import com.imeetake.effectual.EffectualClient;
import com.imeetake.effectual.EffectualConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Effectual.MOD_ID, dist = Dist.CLIENT)
public final class EffectualNeoForge {
    public EffectualNeoForge() {
        Effectual.init();
        EffectualClient.init();

        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (client, parent) -> AutoConfigClient.getConfigScreen(EffectualConfig.class, parent).get()
        );
    }
}
