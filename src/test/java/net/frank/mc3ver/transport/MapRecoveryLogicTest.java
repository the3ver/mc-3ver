package net.frank.mc3ver.transport;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MapRecoveryLogicTest {

    private static final String OVERWORLD = "minecraft:overworld";

    @Test
    void testPlayerWithLinkedMapGetsNoDuplicate() {
        assertEquals(TransportFlameLogic.MapRecoveryOutcome.ALREADY_HAS_MAP, TransportFlameLogic.decideMapRecovery(true, true));
        assertEquals(TransportFlameLogic.MapRecoveryOutcome.ALREADY_HAS_MAP, TransportFlameLogic.decideMapRecovery(true, false));
    }

    @Test
    void testRecoveryCostsPaper() {
        assertEquals(TransportFlameLogic.MapRecoveryOutcome.NEEDS_PAPER, TransportFlameLogic.decideMapRecovery(false, false));
        assertEquals(TransportFlameLogic.MapRecoveryOutcome.GRANT_MAP, TransportFlameLogic.decideMapRecovery(false, true));
    }

    @Test
    void testCarriesMapForMatchesOnlyThisFlame() {
        UUID id = UUID.randomUUID();
        List<TransportFlameLogic.FlameTarget> targets = Arrays.asList(
            null,
            new TransportFlameLogic.FlameTarget(1, 64, 1, OVERWORLD, 0xF9FFFE, id)
        );
        assertTrue(TransportFlameLogic.carriesMapFor(targets, 1, 64, 1, OVERWORLD));
        assertFalse(TransportFlameLogic.carriesMapFor(targets, 2, 64, 1, OVERWORLD));
        assertFalse(TransportFlameLogic.carriesMapFor(targets, 1, 64, 1, "minecraft:the_nether"));
        assertFalse(TransportFlameLogic.carriesMapFor(List.of(), 1, 64, 1, OVERWORLD));
    }

    @Test
    void testResolveFlameTargetKeepsStoredIdentity() {
        UUID id = UUID.randomUUID();
        TransportFlameLogic.FlameTarget stored = new TransportFlameLogic.FlameTarget(4, 70, 4, OVERWORLD, 0x169C9C, id);
        assertSame(stored, TransportFlameLogic.resolveFlameTarget(stored, 4, 70, 4, OVERWORLD, new Random(1)));

        TransportFlameLogic.FlameTarget moved = TransportFlameLogic.resolveFlameTarget(stored, 5, 70, 4, OVERWORLD, new Random(1));
        assertTrue(moved.isAt(5, 70, 4, OVERWORLD));
        assertEquals(0x169C9C, moved.colorRgb());
        assertEquals(id, moved.flameId());
    }

    @Test
    void testResolveFlameTargetCreatesIdentityForOldFlames() {
        TransportFlameLogic.FlameTarget fresh = TransportFlameLogic.resolveFlameTarget(null, 4, 70, 4, OVERWORLD, new Random(1));
        assertTrue(fresh.isAt(4, 70, 4, OVERWORLD));
        assertTrue(TransportFlameLogic.isValidFlameColorRgb(fresh.colorRgb()));
        assertNotNull(fresh.flameId());
    }

    @Test
    void testHoldCompletesAfterRecoveryTicks() {
        assertEquals(30, TransportFlameLogic.RECOVERY_HOLD_TICKS, "Holding must take 1.5 seconds");
        TransportFlameLogic.FlameHoldTracker tracker = new TransportFlameLogic.FlameHoldTracker();
        UUID player = UUID.randomUUID();

        float progress = 0;
        long tick = 100;
        for (; tick < 130; tick += 4) {
            progress = tracker.update(player, "flame", tick);
            assertTrue(progress < 1.0f, "Not complete before 30 ticks, tick " + tick);
        }
        assertEquals(1.0f, tracker.update(player, "flame", 130));
        assertEquals(0.0f, tracker.update(player, "flame", 134), "After completion the next hold starts over");
    }

    @Test
    void testLettingGoResetsHold() {
        TransportFlameLogic.FlameHoldTracker tracker = new TransportFlameLogic.FlameHoldTracker();
        UUID player = UUID.randomUUID();
        tracker.update(player, "flame", 0);
        tracker.update(player, "flame", 4);
        assertEquals(8f / 30f, tracker.update(player, "flame", 8), 0.001f);

        float afterPause = tracker.update(player, "flame", 8 + TransportFlameLogic.HOLD_MAX_GAP_TICKS + 1);
        assertEquals(0.0f, afterPause, "A pause longer than the gap starts a new hold");
    }

    @Test
    void testSwitchingFlameOrResetStartsOver() {
        TransportFlameLogic.FlameHoldTracker tracker = new TransportFlameLogic.FlameHoldTracker();
        UUID player = UUID.randomUUID();
        tracker.update(player, "a", 0);
        tracker.update(player, "a", 4);
        assertEquals(0.0f, tracker.update(player, "b", 8));

        tracker.update(player, "b", 12);
        tracker.reset(player);
        assertEquals(0.0f, tracker.update(player, "b", 16));
    }

    @Test
    void testHoldsArePerPlayer() {
        TransportFlameLogic.FlameHoldTracker tracker = new TransportFlameLogic.FlameHoldTracker();
        UUID alice = UUID.randomUUID();
        UUID bob = UUID.randomUUID();
        tracker.update(alice, "flame", 0);
        assertEquals(0.0f, tracker.update(bob, "flame", 4));
        assertEquals(8f / 30f, tracker.update(alice, "flame", 8), 0.001f, "Bob does not interrupt Alice");
    }

    @Test
    void testProgressBar() {
        assertEquals("□□□□", TransportFlameLogic.progressBar(0.0f, 4));
        assertEquals("■■□□", TransportFlameLogic.progressBar(0.5f, 4));
        assertEquals("■■■■", TransportFlameLogic.progressBar(1.5f, 4));
    }
}
