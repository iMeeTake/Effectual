package com.imeetake.effectual.forge;

import com.imeetake.effectual.Effectual;
import com.imeetake.effectual.forge.client.EffectualForgeClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Effectual.MOD_ID)
public final class EffectualForge {
    public EffectualForge() {
        ModLoadingContext.get().registerDisplayTest(IExtensionPoint.DisplayTest.IGNORE_SERVER_VERSION);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> EffectualForgeClient::init);
    }
}
