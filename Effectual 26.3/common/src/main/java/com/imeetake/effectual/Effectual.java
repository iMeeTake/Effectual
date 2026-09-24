package com.imeetake.effectual;

public class Effectual {
    public static final String MOD_ID = "effectual";

    public static void init() {
        EffectualConfig.init();
        ModParticles.register();
    }
}
