package com.imeetake.effectual;

import com.imeetake.effectual.effects.AirTrail.AirTrailEffect;
import com.imeetake.effectual.effects.Bubbles.BubbleBreathEffect;
import com.imeetake.effectual.effects.Bubbles.BubbleChestEffect;
import com.imeetake.effectual.effects.Bubbles.BubblePotsEffect;
import com.imeetake.effectual.effects.CauldronFill.CauldronFillEffect;
import com.imeetake.effectual.effects.CaveDust.CaveDustEffect;
import com.imeetake.effectual.effects.Firefly.FireflyEffect;
import com.imeetake.effectual.effects.GoldGlow.LanternGlowEffect;
import com.imeetake.effectual.effects.GoldGlow.TorchGlowEffect;
import com.imeetake.effectual.effects.Levitation.LevitationAuraEffect;
import com.imeetake.effectual.effects.MouthSteam.MouthSteamEffect;
import com.imeetake.effectual.effects.PlayerRunEffect.PlayerRunEffect;
import com.imeetake.effectual.effects.SoulGlow.SoulLanternGlowEffect;
import com.imeetake.effectual.effects.SoulGlow.SoulTorchGlowEffect;
import com.imeetake.effectual.effects.Sparks.CampfireImprovements;
import com.imeetake.effectual.effects.Sparks.FireEntitySparksEffect;
import com.imeetake.effectual.effects.Sparks.FireImprovements;
import com.imeetake.effectual.effects.Sparks.FurnaceSparksEffect;
import com.imeetake.effectual.effects.SparksCart.SparksCartEffect;
import com.imeetake.effectual.effects.SparksSoul.SoulCampfireImprovements;
import com.imeetake.effectual.effects.SparksSoul.SoulFireImprovements;
import com.imeetake.effectual.effects.SpeedAura.SpeedAuraEffect;
import com.imeetake.effectual.effects.SteamEffect.SteamEffect;
import com.imeetake.effectual.effects.StripEffect.StripEffect;
import com.imeetake.effectual.effects.WaterDrip.WaterDripEffect;
import com.imeetake.effectual.effects.WitherDecay.WitherDecayEffect;

public class EffectRegistry {
    public static void registerEffects() {
        MouthSteamEffect.register();
        FireImprovements.register();
        CampfireImprovements.register();
        TorchGlowEffect.register();
        LanternGlowEffect.register();
        SoulFireImprovements.register();
        SoulCampfireImprovements.register();
        SoulTorchGlowEffect.register();
        SoulLanternGlowEffect.register();
        FurnaceSparksEffect.register();
        FireflyEffect.register();
        BubbleBreathEffect.register();
        CaveDustEffect.register();
        PlayerRunEffect.register();
        SparksCartEffect.register();
        SteamEffect.register();
        BubbleChestEffect.register();
        WaterDripEffect.register();
        BubblePotsEffect.register();
        FireEntitySparksEffect.register();
        AirTrailEffect.register();
        SpeedAuraEffect.register();
        LevitationAuraEffect.register();
        WitherDecayEffect.register();
        CauldronFillEffect.register();
        StripEffect.register();
    }
}
