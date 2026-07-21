package com.imeetake.effectual.fabric;

import com.imeetake.effectual.Effectual;
import net.fabricmc.api.ModInitializer;

public final class EffectualFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Effectual.init();
    }
}
