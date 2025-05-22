package com.imeetake.effectual;

import com.imeetake.effectual.effects.Bubbles.BubbleBreathEffect;
import com.imeetake.effectual.effects.Bubbles.BubbleChestEffect;
import com.imeetake.effectual.effects.Bubbles.BubblePotsEffect;
import com.imeetake.effectual.effects.CaveDust.CaveDustEffect;
import com.imeetake.effectual.effects.Firefly.FireflyEffect;
import com.imeetake.effectual.effects.GoldGlow.LanternGlowEffect;
import com.imeetake.effectual.effects.MouthSteam.MouthSteamEffect;
import com.imeetake.effectual.effects.PlayerRunEffect.PlayerRunEffect;
import com.imeetake.effectual.effects.SoulGlow.SoulLanternGlowEffect;
import com.imeetake.effectual.effects.Sparks.CampfireImprovements;
import com.imeetake.effectual.effects.Sparks.FireEntitySparks;
import com.imeetake.effectual.effects.Sparks.FireImprovements;
import com.imeetake.effectual.effects.SparksCart.SparksCartEffect;
import com.imeetake.effectual.effects.SparksSoul.SoulCampfireImprovements;
import com.imeetake.effectual.effects.SparksSoul.SoulFireImprovements;
import com.imeetake.effectual.effects.SteamEffect.SteamEffect;
import com.imeetake.effectual.effects.WaterDrip.WaterDripEffect;

public class EffectRegistry {
    public static void registerEffects() {
        MouthSteamEffect.register();
        FireImprovements.register();
        CampfireImprovements.register();
        LanternGlowEffect.register();
        SoulFireImprovements.register();
        SoulCampfireImprovements.register();
        SoulLanternGlowEffect.register();
        FireflyEffect.register();
        BubbleBreathEffect.register();
        CaveDustEffect.register();
        PlayerRunEffect.register();
        SparksCartEffect.register();
        SteamEffect.register();
        BubbleChestEffect.register();
        WaterDripEffect.register();
        BubblePotsEffect.register();
        FireEntitySparks.register();
    }
}
