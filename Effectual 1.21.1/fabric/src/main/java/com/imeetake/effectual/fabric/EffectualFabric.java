package com.imeetake.effectual.fabric;

import net.fabricmc.api.ModInitializer;

import com.imeetake.effectual.Effectual;

public final class EffectualFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Effectual.init();
    }
}
