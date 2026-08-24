package net.frank.mc3ver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DoubleJumpLogicTest {

	@Test
	void testShouldAllowDoubleJump_WhenInAirAndKeyPressedFirstTime_ReturnsTrue() {
		boolean canDoubleJump = true;
		boolean isJumpKeyDown = true;
		boolean jumpWasDown = false;
		boolean isFlying = false;
		boolean isFallFlying = false;
		boolean isSpectator = false;

		boolean allowed = DoubleJumpLogic.shouldAllowDoubleJump(
			canDoubleJump, isJumpKeyDown, jumpWasDown, isFlying, isFallFlying, isSpectator
		);

		assertTrue(allowed, "Doppelsprung sollte erlaubt sein, wenn Spieler in der Luft ist und Taste neu gedrückt wird");
	}

	@Test
	void testShouldNotAllowDoubleJump_WhenFlyingOrSpectating() {
		// Im Kreativflug sollte kein Doppelsprung getriggert werden
		assertFalse(DoubleJumpLogic.shouldAllowDoubleJump(true, true, false, true, false, false));
		// Im Zuschauermodus (Spectator) ebenfalls nicht
		assertFalse(DoubleJumpLogic.shouldAllowDoubleJump(true, true, false, false, false, true));
		// Mit Elytra (FallFlying) ebenfalls nicht
		assertFalse(DoubleJumpLogic.shouldAllowDoubleJump(true, true, false, false, true, false));
	}

	@Test
	void testShouldResetDoubleJump_WhenGroundOrWaterOrClimbable() {
		// Am Boden
		assertTrue(DoubleJumpLogic.shouldResetDoubleJump(true, false, false, false, false));
		// Im Wasser
		assertTrue(DoubleJumpLogic.shouldResetDoubleJump(false, true, false, false, false));
		// In Lava
		assertTrue(DoubleJumpLogic.shouldResetDoubleJump(false, false, true, false, false));
		// Auf Leiter / Ranke
		assertTrue(DoubleJumpLogic.shouldResetDoubleJump(false, false, false, false, true));
		// Auf Reittier
		assertTrue(DoubleJumpLogic.shouldResetDoubleJump(false, false, false, true, false));
		// In der Luft ohne alles
		assertFalse(DoubleJumpLogic.shouldResetDoubleJump(false, false, false, false, false));
	}

	@Test
	void testCalculateNewVelocity_AppliesUpwardJumpAndForwardBoost() {
		double currentVx = 0.1;
		double currentVz = 0.0;
		double lookX = 1.0;
		double lookZ = 0.0;
		double jumpY = 0.52;
		double forwardBoost = 0.15;

		double[] newVelocity = DoubleJumpLogic.calculateNewVelocity(
			currentVx, currentVz, lookX, lookZ, jumpY, forwardBoost
		);

		assertEquals(3, newVelocity.length);
		assertEquals(0.25, newVelocity[0], 0.0001, "X-Geschwindigkeit sollte Vorwärtsschub beinhalten");
		assertEquals(0.52, newVelocity[1], 0.0001, "Y-Geschwindigkeit sollte Sprungkraft sein");
		assertEquals(0.0, newVelocity[2], 0.0001, "Z-Geschwindigkeit sollte unverändert bleiben");
	}
}
