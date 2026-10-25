package net.frank.mc3ver.peace;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PacifistEffect extends MobEffect {

    public PacifistEffect() {
        super(MobEffectCategory.BENEFICIAL, PacifistLogic.EFFECT_COLOR);
    }
}
