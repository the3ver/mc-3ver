package net.frank.mc3ver.sonic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SonicSwordLogicTest {

    private static final double EPS = 1.0e-6;

    @Test
    void diamondBonusPlusSwordBonusMakesEightAndAHalfDamage() {
        // Diamant gibt +3, der Spieler schlägt mit 1
        float bonus = SonicSwordLogic.attackDamageBonus(3.0f);
        assertEquals(4.5f, bonus, EPS);
        assertEquals(8.5f, SonicSwordLogic.PLAYER_BASE_DAMAGE + 3.0f + bonus, EPS);
    }

    @Test
    void chargingNeedsNoCooldownAndNoSpectator() {
        assertTrue(SonicSwordLogic.canCharge(false, false));
        assertFalse(SonicSwordLogic.canCharge(true, false));
        assertFalse(SonicSwordLogic.canCharge(false, true));
    }

    @Test
    void shortClickDoesNotFire() {
        assertFalse(SonicSwordLogic.fires(0));
        assertFalse(SonicSwordLogic.fires(SonicSwordLogic.MIN_CHARGE_TICKS - 1));
        assertTrue(SonicSwordLogic.fires(SonicSwordLogic.MIN_CHARGE_TICKS));
    }

    @Test
    void chargeGrowsUntilFullAndStaysThere() {
        int full = SonicSwordLogic.WAVE_FULL_CHARGE_TICKS;
        assertEquals(0.0f, SonicSwordLogic.charge(SonicSwordLogic.MIN_CHARGE_TICKS - 1, full), EPS);
        assertEquals(0.25f, SonicSwordLogic.charge(10, 40), EPS);
        assertEquals(0.5f, SonicSwordLogic.charge(20, 40), EPS);
        assertEquals(1.0f, SonicSwordLogic.charge(40, 40), EPS);
        assertEquals(1.0f, SonicSwordLogic.charge(500, 40), EPS);
    }

    @Test
    void chargeTicksNeverGoNegative() {
        assertEquals(15, SonicSwordLogic.chargeTicks(100, 115));
        assertEquals(0, SonicSwordLogic.chargeTicks(115, 100));
    }

    @Test
    void fullWaveParalysesThreeSecondsWithMediumDamage() {
        SonicSwordLogic.Wave weak = SonicSwordLogic.wave(0.0f);
        SonicSwordLogic.Wave full = SonicSwordLogic.wave(1.0f);
        assertEquals(3.0, weak.radius(), EPS);
        assertEquals(6.0, full.radius(), EPS);
        assertEquals(1.5f, weak.damage(), EPS);
        assertEquals(4.0f, full.damage(), EPS);
        assertEquals(20, weak.paralysisTicks());
        assertEquals(60, full.paralysisTicks());
    }

    @Test
    void beamHasLittleDamageButStrongKnockback() {
        SonicSwordLogic.Beam weak = SonicSwordLogic.beam(0.0f);
        SonicSwordLogic.Beam full = SonicSwordLogic.beam(1.0f);
        assertEquals(8.0, weak.range(), EPS);
        assertEquals(16.0, full.range(), EPS);
        assertEquals(1.0f, weak.damage(), EPS);
        assertEquals(2.0f, full.damage(), EPS);
        assertEquals(0.8, weak.knockback(), EPS);
        assertEquals(2.5, full.knockback(), EPS);
        // Der Strahl schubst, die Welle verletzt
        assertTrue(full.damage() < SonicSwordLogic.wave(1.0f).damage());
    }

    @Test
    void beamHitsTargetsAlongTheLineOnly() {
        // Blick entlang +X
        assertEquals(5.0, SonicSwordLogic.beamHitDistance(1, 0, 0, 5, 0, 0, 10, 1), EPS);
        assertEquals(5.0, SonicSwordLogic.beamHitDistance(1, 0, 0, 5, 0.9, 0, 10, 1), EPS);
        assertEquals(-1, SonicSwordLogic.beamHitDistance(1, 0, 0, 5, 1.5, 0, 10, 1), EPS);
        assertEquals(-1, SonicSwordLogic.beamHitDistance(1, 0, 0, -2, 0, 0, 10, 1), EPS, "hinter dem Spieler");
        assertEquals(-1, SonicSwordLogic.beamHitDistance(1, 0, 0, 11, 0, 0, 10, 1), EPS, "zu weit");
        assertEquals(10.0, SonicSwordLogic.beamHitDistance(1, 0, 0, 10, 0, 0, 10, 1), EPS, "Grenze zählt");
    }

    @Test
    void beamWorksDiagonally() {
        double d = Math.sqrt(0.5);
        assertEquals(Math.sqrt(8), SonicSwordLogic.beamHitDistance(d, 0, d, 2, 0, 2, 10, 1), EPS);
        assertEquals(-1, SonicSwordLogic.beamHitDistance(d, 0, d, 4, 0, 0, 10, 1), EPS);
    }

    @Test
    void knockbackResistanceDampensThePush() {
        assertEquals(2.0, SonicSwordLogic.knockbackAfterResistance(2.0, 0.0), EPS);
        assertEquals(1.0, SonicSwordLogic.knockbackAfterResistance(2.0, 0.5), EPS);
        assertEquals(0.0, SonicSwordLogic.knockbackAfterResistance(2.0, 1.0), EPS);
        assertEquals(0.0, SonicSwordLogic.knockbackAfterResistance(2.0, 1.4), EPS);
    }
}
