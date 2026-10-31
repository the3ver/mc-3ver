package net.frank.mc3ver.peace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PacifistLogicTest {

    @Test
    void toggle_gesperrt_wenn_cooldown() {
        assertEquals(PacifistLogic.ToggleResult.LOCKED, PacifistLogic.toggle(false, true), "Cooldown aktiv => LOCKED");
        assertEquals(
                PacifistLogic.ToggleResult.LOCKED, PacifistLogic.toggle(true, true), "Pazifist + Cooldown => LOCKED");
    }

    @Test
    void toggle_aktiviert_wenn_nicht_pazifist() {
        assertEquals(
                PacifistLogic.ToggleResult.ACTIVATED,
                PacifistLogic.toggle(false, false),
                "Nicht pazifistisch, kein Cooldown => ACTIVATED");
    }

    @Test
    void toggle_deaktiviert_wenn_pazifist() {
        assertEquals(
                PacifistLogic.ToggleResult.DEACTIVATED,
                PacifistLogic.toggle(true, false),
                "Pazifistisch, kein Cooldown => DEACTIVATED");
    }

    @Test
    void shouldBlockDamage_alle_kombinationen() {
        assertTrue(PacifistLogic.shouldBlockDamage(true, true), "Pazifist + Feind => blockieren");
        assertFalse(PacifistLogic.shouldBlockDamage(false, true), "Kein Pazifist + Feind => nicht blockieren");
        assertFalse(PacifistLogic.shouldBlockDamage(true, false), "Pazifist + kein Feind => nicht blockieren");
        assertFalse(PacifistLogic.shouldBlockDamage(false, false), "Kein Pazifist + kein Feind => nicht blockieren");
    }

    @Test
    void breaksPeace_alle_kombinationen() {
        assertTrue(PacifistLogic.breaksPeace(true, true), "Pazifist greift Monster an => Frieden gebrochen");
        assertFalse(PacifistLogic.breaksPeace(false, true), "Kein Pazifist greift Monster an => kein Bruch");
        assertFalse(PacifistLogic.breaksPeace(true, false), "Pazifist greift Nicht-Monster an => kein Bruch");
        assertFalse(PacifistLogic.breaksPeace(false, false), "Kein Pazifist, kein Monster => kein Bruch");
    }

    @Test
    void canBeTargetedByMonsters_kehrwert_von_isPacifist() {
        assertFalse(PacifistLogic.canBeTargetedByMonsters(true), "Pazifist darf nicht angegriffen werden");
        assertTrue(PacifistLogic.canBeTargetedByMonsters(false), "Nicht-Pazifist ist angreifbar");
    }

    @Test
    void konstanten_haben_erwartete_werte() {
        assertEquals(20, PacifistLogic.TOGGLE_COOLDOWN_TICKS, "Toggle-Cooldown muss 20 Ticks betragen");
        assertEquals(600, PacifistLogic.BROKEN_PEACE_LOCK_TICKS, "Friedensbruch-Sperre muss 600 Ticks betragen");
        assertEquals(0xF2F2F2, PacifistLogic.EFFECT_COLOR, "Effektfarbe muss 0xF2F2F2 sein");
    }
}
