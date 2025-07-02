package com.imeetake.effectual;


import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = "effectual")
@Config(name = "effectual", wrapperName = "EffectualConfig")
public class EffectualConfigModel {
    public boolean mouthSteam = true;
    public boolean dynamicBreathSpeed = true;
    public boolean bubbleBreath = true;
    public boolean bubbleChests = true;
    public boolean bubblePots = true;
    public boolean waterDrip = true;
    public boolean goldShine = true;
    public boolean steamEffect = true;
    public boolean blockPlaceEffect = true;
    public boolean fireImprovements = true;
    public boolean campfireImprovements = true;
    public boolean lanternImprovements = true;
    public boolean fireEntitySparks = true;
    public boolean metalHitSparks = true;
    public boolean caveDust = true;
    public int caveDustFrequency = 5;
    public boolean fireflyEffect = true;
    public int fireflySpawnRate = 5;
    public boolean runDust = true;
    public boolean airTrail = true;
    public boolean speedAura = true;
    public boolean levitationAura = true;
    public boolean witherDecay = true;
    public boolean minecartSparks = true;
}