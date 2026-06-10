package com.imeetake.effectual.neoforge;

import com.imeetake.effectual.Effectual;
import com.imeetake.effectual.neoforge.client.EffectualNeoForgeClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = Effectual.MOD_ID, dist = Dist.CLIENT)
public final class EffectualNeoForge {
    public EffectualNeoForge() {
        EffectualNeoForgeClient.init();
    }
}
