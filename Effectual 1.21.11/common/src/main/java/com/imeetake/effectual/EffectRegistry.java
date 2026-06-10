package com.imeetake.effectual;

import com.imeetake.effectual.effects.AirTrail.AirTrailEffect;
import com.imeetake.effectual.effects.Bubbles.BubbleBreathEffect;
import com.imeetake.effectual.effects.Bubbles.BubbleChestEffect;
import com.imeetake.effectual.effects.Bubbles.BubblePotsEffect;
import com.imeetake.effectual.effects.CauldronFill.CauldronFillEffect;
import com.imeetake.effectual.effects.CaveDust.CaveDustEffect;
import com.imeetake.effectual.effects.Glow.LightSourceGlowEffect;
import com.imeetake.effectual.effects.MetalSparks.SparksCartEffect;
import com.imeetake.effectual.effects.MouthSteam.MouthSteamEffect;
import com.imeetake.effectual.effects.PlayerRunEffect.PlayerRunEffect;
import com.imeetake.effectual.effects.SnowFootprints.SnowFootprintEffect;
import com.imeetake.effectual.effects.SnowFootprints.SnowResidueEffect;
import com.imeetake.effectual.effects.Sparks.FireImprovements;
import com.imeetake.effectual.effects.Sparks.FurnaceSparksEffect;
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
        LightSourceGlowEffect.register();
        FurnaceSparksEffect.register();
        FireImprovements.register();
        SparksCartEffect.register();
        SnowFootprintEffect.register();
        SnowResidueEffect.register();
    }
}
