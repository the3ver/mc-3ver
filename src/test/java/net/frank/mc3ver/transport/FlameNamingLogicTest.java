package net.frank.mc3ver.transport;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FlameNamingLogicTest {

    @Test
    void testSanitizeTrimsAndKeepsNormalNames() {
        assertEquals("Basis am See", TransportFlameLogic.sanitizeFlameName("  Basis am See  "));
        assertEquals("Höhle Ä", TransportFlameLogic.sanitizeFlameName("Höhle Ä"));
    }

    @Test
    void testSanitizeRejectsBlankNames() {
        assertNull(TransportFlameLogic.sanitizeFlameName(null));
        assertNull(TransportFlameLogic.sanitizeFlameName(""));
        assertNull(TransportFlameLogic.sanitizeFlameName("   "));
        assertNull(TransportFlameLogic.sanitizeFlameName("\n\t"));
    }

    @Test
    void testSanitizeStripsControlAndFormattingCharacters() {
        assertEquals("Turm", TransportFlameLogic.sanitizeFlameName("§cTu\u0000rm"));
        assertEquals("Turm", TransportFlameLogic.sanitizeFlameName("§cTurm"));
    }

    @Test
    void testSanitizeCutsToMaxLength() {
        String longName = "x".repeat(TransportFlameLogic.MAX_FLAME_NAME_LENGTH + 10);
        assertEquals(TransportFlameLogic.MAX_FLAME_NAME_LENGTH, TransportFlameLogic.sanitizeFlameName(longName).length());
    }

    @Test
    void testPlacerMayNameUnnamedFlameAgainAfterClosingScreen() {
        UUID placer = UUID.randomUUID();
        // Screen ohne Namen geschlossen: Name bleibt null, also darf der Aufsteller erneut benennen
        assertTrue(TransportFlameLogic.canNameFlame(null, placer, placer));
    }

    @Test
    void testNameIsFinalOnceSet() {
        UUID placer = UUID.randomUUID();
        assertFalse(TransportFlameLogic.canNameFlame("Basis", placer, placer));
    }

    @Test
    void testOnlyPlacerMayNameTheFlame() {
        UUID placer = UUID.randomUUID();
        assertFalse(TransportFlameLogic.canNameFlame(null, placer, UUID.randomUUID()));
    }

    @Test
    void testFlamesWithoutKnownPlacerMayBeNamedByAnyone() {
        assertTrue(TransportFlameLogic.canNameFlame(null, null, UUID.randomUUID()));
    }
}
