package com.imeetake.effectual;

import com.imeetake.effectual.effects.AirTrail.AirTrailEffect;
import com.imeetake.effectual.effects.Bubbles.BubbleBreathEffect;
import com.imeetake.effectual.effects.Bubbles.BubbleChestEffect;
import com.imeetake.effectual.effects.Bubbles.BubblePotsEffect;
import com.imeetake.effectual.effects.CauldronFill.CauldronFillEffect;
import com.imeetake.effectual.effects.CaveDust.CaveDustEffect;
import com.imeetake.effectual.effects.GoldGlow.LanternGlowEffect;
import com.imeetake.effectual.effects.GoldGlow.TorchGlowEffect;
import com.imeetake.effectual.effects.MetalSparks.SparksCartEffect;
import com.imeetake.effectual.effects.MouthSteam.MouthSteamEffect;
import com.imeetake.effectual.effects.PlayerRunEffect.PlayerRunEffect;
import com.imeetake.effectual.effects.SoulGlow.SoulLanternGlowEffect;
import com.imeetake.effectual.effects.SoulGlow.SoulTorchGlowEffect;
import com.imeetake.effectual.effects.Sparks.*;
import com.imeetake.effectual.effects.SparksSoul.SoulFireImprovements;
import com.imeetake.effectual.effects.SteamEffect.SteamEffect;
import com.imeetake.effectual.effects.StripEffect.StripEffect;
import com.imeetake.effectual.effects.WaterDrip.WaterDripEffect;
import com.imeetake.effectual.effects.WitherDecay.WitherDecayEffect;

public class EffectRegistry {

    public static void register() {
        BubbleBreathEffect.register();
        BubbleChestEffect.register();
        BubblePotsEffect.register();
        CauldronFillEffect.register();
        CaveDustEffect.register();
        PlayerRunEffect.register();
        SteamEffect.register();
        StripEffect.register();
        WitherDecayEffect.register();
        MouthSteamEffect.register();
        WaterDripEffect.register();
        AirTrailEffect.register();
        TorchGlowEffect.register();
        LanternGlowEffect.register();
        SoulTorchGlowEffect.register();
        SoulLanternGlowEffect.register();
        FurnaceSparksEffect.register();
        FireImprovements.register();
        SparksCartEffect.register();
        SoulFireImprovements.register();
    }
}