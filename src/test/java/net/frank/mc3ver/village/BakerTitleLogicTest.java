package net.frank.mc3ver.village;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BakerTitleLogicTest {

    @Test
    void bakerGetsTheFemaleLevelName() {
        assertEquals(
                "mc3ver.merchant.level.female.5",
                BakerTitleLogic.levelKey("entity.mc3ver.villager.baker", "merchant.level.5"));
        assertEquals(
                "mc3ver.merchant.level.female.3",
                BakerTitleLogic.levelKey("entity.mc3ver.villager.baker", "merchant.level.3"));
    }

    @Test
    void otherTradersKeepTheVanillaLevelName() {
        assertEquals(
                "merchant.level.5", BakerTitleLogic.levelKey("entity.minecraft.villager.farmer", "merchant.level.5"));
        assertEquals("merchant.level.2", BakerTitleLogic.levelKey("", "merchant.level.2"));
    }

    @Test
    void otherKeysStayUntouched() {
        assertEquals("merchant.trades", BakerTitleLogic.levelKey("entity.mc3ver.villager.baker", "merchant.trades"));
    }
}
