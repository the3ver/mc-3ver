package net.frank.mc3ver.client.mixin;

import net.frank.mc3ver.village.BakerTitleLogic;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Handelsmenü: "Bäckerin – Meisterin" statt "Bäckerin – Meister"
@Mixin(MerchantScreen.class)
abstract class MerchantScreenMixin {

    @ModifyArg(
            method = "extractLabels",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
    private String mc3ver$femaleLevelName(String levelKey) {
        Object contents = ((Screen) (Object) this).getTitle().getContents();
        String titleKey = contents instanceof TranslatableContents translatable ? translatable.getKey() : "";
        return BakerTitleLogic.levelKey(titleKey, levelKey);
    }
}
