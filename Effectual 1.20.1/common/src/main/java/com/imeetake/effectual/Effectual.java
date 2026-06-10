package com.imeetake.effectual;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;

public class Effectual {
    public static final String MOD_ID = "effectual";

    public static void init() {
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            EffectualConfig.init();
            ModParticles.register();
            EffectualClient.init();
        });
    }
}
