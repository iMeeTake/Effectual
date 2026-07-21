package com.imeetake.effectual;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "effectual")
public class EffectualConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    private static EffectualConfig INSTANCE;

    public static void init() {
        AutoConfig.register(EffectualConfig.class, GsonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(EffectualConfig.class).getConfig();
    }

    public static EffectualConfig get() {
        return INSTANCE;
    }

    @ConfigEntry.Gui.Tooltip
    public boolean mouthSteam = false;

    @ConfigEntry.Gui.Tooltip
    public boolean dynamicBreathSpeed = true;

    @ConfigEntry.Gui.Tooltip
    public boolean bubbleBreath = true;

    @ConfigEntry.Gui.Tooltip
    public boolean bubbleChests = true;

    @ConfigEntry.Gui.Tooltip
    public boolean bubblePots = true;

    @ConfigEntry.Gui.Tooltip
    public boolean waterDrip = true;

    @ConfigEntry.Gui.Tooltip
    public boolean steamEffect = true;

    @ConfigEntry.Gui.Tooltip
    public boolean blockPlaceEffect = true;

    @ConfigEntry.Gui.Tooltip
    public boolean stripEffect = true;

    @ConfigEntry.Gui.Tooltip
    public boolean fireImprovements = true;

    @ConfigEntry.Gui.Tooltip
    public boolean campfireImprovements = true;

    @ConfigEntry.Gui.Tooltip
    public boolean torchImprovements = true;

    @ConfigEntry.Gui.Tooltip
    public boolean lanternImprovements = true;

    @ConfigEntry.Gui.Tooltip
    public boolean fireEntitySparks = true;

    @ConfigEntry.Gui.Tooltip
    public boolean metalHitSparks = true;

    @ConfigEntry.Gui.Tooltip
    public boolean furnaceSparks = true;

    @ConfigEntry.Gui.Tooltip
    public boolean cauldronFillEffect = true;

    @ConfigEntry.Gui.Tooltip
    public boolean caveDust = true;

    @ConfigEntry.Gui.Tooltip
    public int caveDustFrequency = 5;

    @ConfigEntry.Gui.Tooltip
    public boolean runDust = true;

    @ConfigEntry.Gui.Tooltip
    public boolean snowFootprints = true;

    @ConfigEntry.Gui.Tooltip
    public boolean snowResidue = true;

    @ConfigEntry.Gui.Tooltip
    public boolean airTrail = true;

    @ConfigEntry.Gui.Tooltip
    public boolean witherDecay = true;

    @ConfigEntry.Gui.Tooltip
    public boolean minecartSparks = true;
}
