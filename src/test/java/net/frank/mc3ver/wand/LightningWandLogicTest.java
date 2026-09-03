package net.frank.mc3ver.wand;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LightningWandLogicTest {

    @Test
    void testConstants() {
        assertEquals(30, LightningWandLogic.COOLDOWN_TICKS, "Cooldown should be 30 ticks (1.5 seconds)");
        assertEquals(256, LightningWandLogic.MAX_DURABILITY, "Max durability should be 256");
        assertEquals(20.0, LightningWandLogic.RANGE, 0.001, "Range should be 20 blocks");
        assertEquals(8.0, LightningWandLogic.CHAIN_RADIUS, 0.001, "Chain bounce radius should be 8 blocks");
        assertEquals(4, LightningWandLogic.MAX_CHAINS, "Maximum chain targets should be 4");
        assertEquals(8.0f, LightningWandLogic.BASE_DAMAGE, 0.001f, "Base damage should be 8.0 (4 hearts)");
        assertEquals(0.20f, LightningWandLogic.DAMAGE_DECAY, 0.001f, "Damage decay per bounce should be 20%");
        assertEquals(100, LightningWandLogic.MAX_CHARGE_TICKS, "Max charge ticks should be 100 (5 seconds)");
        assertEquals(3.0f, LightningWandLogic.MAX_DAMAGE_MULTIPLIER, 0.001f, "Max damage multiplier should be 3.0x");
        assertEquals(1, LightningWandLogic.MIN_DURABILITY_COST, "Min durability cost should be 1");
        assertEquals(5, LightningWandLogic.MAX_DURABILITY_COST, "Max durability cost should be 5");
        assertEquals(5.0f, LightningWandLogic.THUNDER_HIT_DAMAGE, 0.001f, "Thunder hit damage should be 5.0");
    }

    @Test
    void testCanUse() {
        org.junit.jupiter.api.Assertions.assertTrue(LightningWandLogic.canUse(false, false));
        org.junit.jupiter.api.Assertions.assertFalse(LightningWandLogic.canUse(true, false), "Cannot use while cooldown is active");
        org.junit.jupiter.api.Assertions.assertFalse(LightningWandLogic.canUse(false, true), "Cannot use while spectator");
        org.junit.jupiter.api.Assertions.assertFalse(LightningWandLogic.canUse(true, true), "Cannot use while cooldown and spectator");
    }

    @Test
    void testCalculateDamageWithDecay() {
        assertEquals(8.0f, LightningWandLogic.calculateDamage(8.0f, 0.20f, 0), 0.001f);
        assertEquals(6.4f, LightningWandLogic.calculateDamage(8.0f, 0.20f, 1), 0.001f);
        assertEquals(5.12f, LightningWandLogic.calculateDamage(8.0f, 0.20f, 2), 0.001f);
        assertEquals(4.096f, LightningWandLogic.calculateDamage(8.0f, 0.20f, 3), 0.001f);
        assertEquals(1.0f, LightningWandLogic.calculateDamage(8.0f, 0.20f, 100), 0.001f, "Damage should not fall below 1.0");
    }

    @Test
    void testIsWithinRange() {
        org.junit.jupiter.api.Assertions.assertTrue(LightningWandLogic.isWithinRange(0.0, 0.0, 0.0, 3.0, 0.0, 4.0, 5.0));
        org.junit.jupiter.api.Assertions.assertFalse(LightningWandLogic.isWithinRange(0.0, 0.0, 0.0, 3.0, 0.0, 4.01, 5.0));
        org.junit.jupiter.api.Assertions.assertTrue(LightningWandLogic.isWithinRange(10.0, 20.0, 30.0, 10.0, 25.0, 30.0, 8.0));
        org.junit.jupiter.api.Assertions.assertFalse(LightningWandLogic.isWithinRange(10.0, 20.0, 30.0, 10.0, 29.0, 30.0, 8.0));
    }

    @Test
    void testDurabilityLogic() {
        assertEquals(1, LightningWandLogic.calculateNewDamage(0, 1));
        assertEquals(50, LightningWandLogic.calculateNewDamage(49, 1));

        assertEquals(256, LightningWandLogic.calculateRemainingDurability(0, 256));
        assertEquals(206, LightningWandLogic.calculateRemainingDurability(50, 256));
        assertEquals(0, LightningWandLogic.calculateRemainingDurability(256, 256));

        org.junit.jupiter.api.Assertions.assertFalse(LightningWandLogic.shouldBreak(255, 256));
        org.junit.jupiter.api.Assertions.assertTrue(LightningWandLogic.shouldBreak(256, 256));
        org.junit.jupiter.api.Assertions.assertTrue(LightningWandLogic.shouldBreak(257, 256));
    }

    @Test
    void testIsRayAimingAtTarget() {
        // Direct hit along Z
        org.junit.jupiter.api.Assertions.assertTrue(
            LightningWandLogic.isRayAimingAtTarget(0, 1.6, 0, 0, 0, 1, 0, 1.6, 10, 20.0, 1.0)
        );

        // Within tolerance (0.8 blocks off to the side)
        org.junit.jupiter.api.Assertions.assertTrue(
            LightningWandLogic.isRayAimingAtTarget(0, 1.6, 0, 0, 0, 1, 0.8, 1.6, 10, 20.0, 1.0)
        );

        // Outside tolerance (2.5 blocks off to the side)
        org.junit.jupiter.api.Assertions.assertFalse(
            LightningWandLogic.isRayAimingAtTarget(0, 1.6, 0, 0, 0, 1, 2.5, 1.6, 10, 20.0, 1.0)
        );

        // Behind player
        org.junit.jupiter.api.Assertions.assertFalse(
            LightningWandLogic.isRayAimingAtTarget(0, 1.6, 0, 0, 0, 1, 0, 1.6, -5, 20.0, 1.0)
        );

        // Beyond max range
        org.junit.jupiter.api.Assertions.assertFalse(
            LightningWandLogic.isRayAimingAtTarget(0, 1.6, 0, 0, 0, 1, 0, 1.6, 25, 20.0, 1.0)
        );
    }

    @Test
    void testCalculateAimDistance() {
        assertEquals(10.0, LightningWandLogic.calculateAimDistance(0, 1.6, 0, 0, 0, 1, 0, 1.6, 10, 20.0, 1.0), 0.001);
        assertEquals(10.0, LightningWandLogic.calculateAimDistance(0, 1.6, 0, 0, 0, 1, 0.8, 1.6, 10, 20.0, 1.0), 0.001);
        assertEquals(-1.0, LightningWandLogic.calculateAimDistance(0, 1.6, 0, 0, 0, 1, 2.5, 1.6, 10, 20.0, 1.0), 0.001);
        assertEquals(-1.0, LightningWandLogic.calculateAimDistance(0, 1.6, 0, 0, 0, 1, 0, 1.6, -5, 20.0, 1.0), 0.001);
    }

    @Test
    void testShouldIgniteBlock() {
        org.junit.jupiter.api.Assertions.assertTrue(LightningWandLogic.shouldIgniteBlock(true, true));
        org.junit.jupiter.api.Assertions.assertFalse(LightningWandLogic.shouldIgniteBlock(true, false));
        org.junit.jupiter.api.Assertions.assertFalse(LightningWandLogic.shouldIgniteBlock(false, true));
        org.junit.jupiter.api.Assertions.assertFalse(LightningWandLogic.shouldIgniteBlock(false, false));
    }

    @Test
    void testCalculateChargeRatio() {
        assertEquals(0.0f, LightningWandLogic.calculateChargeRatio(0, 100), 0.001f);
        assertEquals(0.5f, LightningWandLogic.calculateChargeRatio(50, 100), 0.001f);
        assertEquals(1.0f, LightningWandLogic.calculateChargeRatio(100, 100), 0.001f);
        assertEquals(1.0f, LightningWandLogic.calculateChargeRatio(150, 100), 0.001f, "Charge ratio should be capped at 1.0");
        assertEquals(0.0f, LightningWandLogic.calculateChargeRatio(-10, 100), 0.001f, "Charge ratio should not be negative");
    }

    @Test
    void testCalculateChargedDamage() {
        assertEquals(8.0f, LightningWandLogic.calculateChargedDamage(8.0f, 3.0f, 0, 100), 0.001f);
        assertEquals(16.0f, LightningWandLogic.calculateChargedDamage(8.0f, 3.0f, 50, 100), 0.001f);
        assertEquals(24.0f, LightningWandLogic.calculateChargedDamage(8.0f, 3.0f, 100, 100), 0.001f);
        assertEquals(24.0f, LightningWandLogic.calculateChargedDamage(8.0f, 3.0f, 150, 100), 0.001f);
        assertEquals(8.0f, LightningWandLogic.calculateChargedDamage(8.0f, 3.0f, -5, 100), 0.001f);
    }

    @Test
    void testCalculateDurabilityCost() {
        assertEquals(1, LightningWandLogic.calculateDurabilityCost(1, 5, 0, 100));
        assertEquals(3, LightningWandLogic.calculateDurabilityCost(1, 5, 50, 100));
        assertEquals(5, LightningWandLogic.calculateDurabilityCost(1, 5, 100, 100));
        assertEquals(5, LightningWandLogic.calculateDurabilityCost(1, 5, 150, 100));
        assertEquals(1, LightningWandLogic.calculateDurabilityCost(1, 5, -10, 100));
    }

    @Test
    void testCalculateExtraDamage() {
        assertEquals(3.0f, LightningWandLogic.calculateExtraDamage(8.0f, 5.0f), 0.001f);
        assertEquals(19.0f, LightningWandLogic.calculateExtraDamage(24.0f, 5.0f), 0.001f);
        assertEquals(0.0f, LightningWandLogic.calculateExtraDamage(5.0f, 5.0f), 0.001f);
        assertEquals(0.0f, LightningWandLogic.calculateExtraDamage(3.0f, 5.0f), 0.001f);
    }
}
