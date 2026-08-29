package net.frank.mc3ver.wand;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WindWandLogicTest {

    @Test
    void testConstants() {
        assertEquals(20, WindWandLogic.COOLDOWN_TICKS, "Cooldown should be 20 ticks (1 second)");
        assertEquals(256, WindWandLogic.MAX_DURABILITY, "Max durability should be 256");
        assertEquals(1.5f, WindWandLogic.PROJECTILE_SPEED, 0.001f, "Default projectile speed should be 1.5");
        assertEquals(0.05f, WindWandLogic.DEFAULT_INACCURACY, 0.001f, "Default inaccuracy spread should be 0.05");
    }

    @Test
    void testCalculateVelocityWithSpread() {
        // Look straight forward along Z (0, 0, 1) with speed 1.5 and no spread
        double[] vel1 = WindWandLogic.calculateVelocity(0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.5f);
        assertEquals(0.0, vel1[0], 0.0001);
        assertEquals(0.0, vel1[1], 0.0001);
        assertEquals(1.5, vel1[2], 0.0001);

        // Look straight along X (1, 0, 0) with spread (+0.1, -0.05, +0.02) and speed 2.0
        double[] vel2 = WindWandLogic.calculateVelocity(1.0, 0.0, 0.0, 0.1, -0.05, 0.02, 2.0f);
        assertEquals((1.0 + 0.1) * 2.0, vel2[0], 0.0001);
        assertEquals((-0.05) * 2.0, vel2[1], 0.0001);
        assertEquals((0.02) * 2.0, vel2[2], 0.0001);

        // Non-unit look vector (0, 3, 4) with length 5 -> normalized (0, 0.6, 0.8)
        double[] vel3 = WindWandLogic.calculateVelocity(0.0, 3.0, 4.0, 0.0, 0.0, 0.0, 10.0f);
        assertEquals(0.0, vel3[0], 0.0001);
        assertEquals(6.0, vel3[1], 0.0001);
        assertEquals(8.0, vel3[2], 0.0001);
    }

    @Test
    void testDurabilityLogic() {
        assertEquals(1, WindWandLogic.calculateNewDamage(0, 1));
        assertEquals(50, WindWandLogic.calculateNewDamage(49, 1));

        assertEquals(256, WindWandLogic.calculateRemainingDurability(0, 256));
        assertEquals(206, WindWandLogic.calculateRemainingDurability(50, 256));
        assertEquals(0, WindWandLogic.calculateRemainingDurability(256, 256));
        assertEquals(0, WindWandLogic.calculateRemainingDurability(300, 256));

        org.junit.jupiter.api.Assertions.assertFalse(WindWandLogic.shouldBreak(0, 256));
        org.junit.jupiter.api.Assertions.assertFalse(WindWandLogic.shouldBreak(255, 256));
        org.junit.jupiter.api.Assertions.assertTrue(WindWandLogic.shouldBreak(256, 256));
        org.junit.jupiter.api.Assertions.assertTrue(WindWandLogic.shouldBreak(257, 256));
    }

    @Test
    void testCanUse() {
        org.junit.jupiter.api.Assertions.assertTrue(WindWandLogic.canUse(false, false));
        org.junit.jupiter.api.Assertions.assertFalse(WindWandLogic.canUse(true, false), "Cannot use while cooldown is active");
        org.junit.jupiter.api.Assertions.assertFalse(WindWandLogic.canUse(false, true), "Cannot use while spectator");
        org.junit.jupiter.api.Assertions.assertFalse(WindWandLogic.canUse(true, true), "Cannot use while cooldown and spectator");
    }
}
