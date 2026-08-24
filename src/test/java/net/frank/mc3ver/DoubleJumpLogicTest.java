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

	@Test
	void testDoubleJumpState_OnlyTriggersOnSecondPressInAir() {
		DoubleJumpLogic.DoubleJumpState state = new DoubleJumpLogic.DoubleJumpState();

		// Tick 1: Spieler steht am Boden und drückt Leertaste für normalen Sprung
		boolean jump1 = state.update(true, false, false, false, false, false, false, false, true, false);
		assertFalse(jump1, "Erster Sprung am Boden darf keinen Doppelsprung auslösen");

		// Tick 2: Spieler verlässt Boden, hält Leertaste noch gedrückt
		boolean jump2 = state.update(false, false, false, false, false, false, false, false, true, true);
		assertFalse(jump2, "Halten der Leertaste in der Luft darf keinen Doppelsprung auslösen");

		// Tick 3: Spieler lässt Leertaste in der Luft los
		boolean jump3 = state.update(false, false, false, false, false, false, false, false, false, true);
		assertFalse(jump3, "Loslassen der Leertaste löst keinen Sprung aus, lädt ihn aber scharf");

		// Tick 4: Spieler drückt Leertaste zum 2. Mal in der Luft
		boolean jump4 = state.update(false, false, false, false, false, false, false, false, true, false);
		assertTrue(jump4, "Zweites Drücken in der Luft MUSS Doppelsprung auslösen");

		// Tick 5: Spieler hält Leertaste nach Doppelsprung weiter gedrückt
		boolean jump5 = state.update(false, false, false, false, false, false, false, false, true, true);
		assertFalse(jump5, "Weiteres Halten darf keinen weiteren Doppelsprung auslösen");

		// Tick 6: Spieler landet wieder am Boden
		state.update(true, false, false, false, false, false, false, false, false, false);
		assertFalse(state.isPrimed(), "Nach Landung darf der Sprung nicht mehr scharf sein");
	}

	@Test
	void testDoubleJumpState_CannotDoubleJumpMultipleTimesInAirWithoutTouchingGround() {
		DoubleJumpLogic.DoubleJumpState state = new DoubleJumpLogic.DoubleJumpState();

		// Am Boden: Doppelsprung ist geladen/bereit
		state.update(true, false, false, false, false, false, false, false, true, false);

		// In der Luft: Taste loslassen
		state.update(false, false, false, false, false, false, false, false, false, true);

		// 1. Doppelsprung in der Luft ausführen
		boolean firstDoubleJump = state.update(false, false, false, false, false, false, false, false, true, false);
		assertTrue(firstDoubleJump, "1. Doppelsprung in der Luft muss erfolgreich auslösen");

		// Weiterhin in der Luft: Taste erneut loslassen
		state.update(false, false, false, false, false, false, false, false, false, true);

		// Versuch eines 2. Doppelsprungs (Triple Jump) in derselben Flugphase
		boolean secondDoubleJump = state.update(false, false, false, false, false, false, false, false, true, false);
		assertFalse(secondDoubleJump, "2. Doppelsprung in der Luft OHNE Bodenkontakt darf NICHT auslösen");

		// Weiterhin in der Luft: Taste nochmals loslassen und drücken
		state.update(false, false, false, false, false, false, false, false, false, true);
		boolean thirdDoubleJump = state.update(false, false, false, false, false, false, false, false, true, false);
		assertFalse(thirdDoubleJump, "3. Doppelsprung in der Luft OHNE Bodenkontakt darf ebenfalls NICHT auslösen");

		// Jetzt landet der Spieler auf festem Boden
		state.update(true, false, false, false, false, false, false, false, false, false);

		// Neuer Sprungzyklus: In der Luft Taste loslassen
		state.update(false, false, false, false, false, false, false, false, false, false);

		// Nach Bodenkontakt muss Doppelsprung wieder möglich sein
		boolean jumpAfterLanding = state.update(false, false, false, false, false, false, false, false, true, false);
		assertTrue(jumpAfterLanding, "Nach Bodenkontakt muss Doppelsprung wieder funktionieren");
	}

	@Test
	void testDoubleJumpState_WalkingOffLedge_CanOnlyDoubleJumpOnce() {
		DoubleJumpLogic.DoubleJumpState state = new DoubleJumpLogic.DoubleJumpState();

		// Am Boden: Spieler geht ohne zu springen
		state.update(true, false, false, false, false, false, false, false, false, false);

		// Spieler läuft über die Kante (in der Luft, Leertaste nicht gedrückt)
		state.update(false, false, false, false, false, false, false, false, false, false);
		assertTrue(state.isPrimed(), "Nach Kantensturz muss Doppelsprung direkt scharf sein");

		// 1. Drücken der Leertaste in der Luft
		boolean jump1 = state.update(false, false, false, false, false, false, false, false, true, false);
		assertTrue(jump1, "Doppelsprung beim Fallen von der Kante muss funktionieren");

		// Erneutes Loslassen und Drücken in der Luft
		state.update(false, false, false, false, false, false, false, false, false, true);
		boolean jump2 = state.update(false, false, false, false, false, false, false, false, true, false);
		assertFalse(jump2, "Kein weiterer Sprung in der Luft nach Kantensturz erlaubt");
	}
}
