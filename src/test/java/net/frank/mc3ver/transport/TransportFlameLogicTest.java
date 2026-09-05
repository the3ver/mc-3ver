package net.frank.mc3ver.transport;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TransportFlameLogicTest {

    @Test
    void testGetRandomColorReturnsValidRgbColor() {
        Random random = new Random(42);
        int colorRgb = TransportFlameLogic.getRandomFlameColorRgb(random);
        
        assertTrue(TransportFlameLogic.isValidFlameColorRgb(colorRgb), "Color must be one of the predefined 16 flame colors");
        assertTrue(TransportFlameLogic.ALL_FLAME_COLORS.contains(colorRgb), "Color list must contain the generated color");
        assertEquals(16, TransportFlameLogic.ALL_FLAME_COLORS.size(), "There should be exactly 16 distinct flame colors");
    }

    @Test
    void testFlameTargetDataAndFormatting() {
        java.util.UUID flameId = java.util.UUID.randomUUID();
        TransportFlameLogic.FlameTarget target = new TransportFlameLogic.FlameTarget(
            120, 64, -350, "minecraft:overworld", 0x3AB3DA, flameId
        );

        assertEquals(120, target.x());
        assertEquals(64, target.y());
        assertEquals(-350, target.z());
        assertEquals("minecraft:overworld", target.dimension());
        assertEquals(0x3AB3DA, target.colorRgb());
        assertEquals(flameId, target.flameId());

        assertEquals("X: 120, Y: 64, Z: -350", target.formatCoordinates());
        assertEquals("Overworld", target.formatDimensionName());

        TransportFlameLogic.FlameTarget netherTarget = new TransportFlameLogic.FlameTarget(
            0, 100, 0, "minecraft:the_nether", 0xB02E26, flameId
        );
        assertEquals("Nether", netherTarget.formatDimensionName());

        TransportFlameLogic.FlameTarget endTarget = new TransportFlameLogic.FlameTarget(
            0, 100, 0, "minecraft:the_end", 0x8932B8, flameId
        );
        assertEquals("The End", endTarget.formatDimensionName());
    }

    @Test
    void testChannelingProgressLifecycle() {
        int requiredTicks = TransportFlameLogic.CHANNELING_TICKS;
        assertEquals(50, requiredTicks, "Channeling must require 50 ticks (2.5 seconds)");

        TransportFlameLogic.ChannelingSession session = new TransportFlameLogic.ChannelingSession();
        assertFalse(session.isComplete());
        assertEquals(0.0f, session.getProgress());

        // Tick 25 times while holding use button
        for (int i = 0; i < 25; i++) {
            boolean finished = session.tick(true);
            assertFalse(finished);
        }
        assertEquals(25, session.getTicksUsed());
        assertEquals(0.5f, session.getProgress(), 0.001f);
        assertFalse(session.isComplete());

        // Cancel prematurely
        session.tick(false);
        assertEquals(0, session.getTicksUsed(), "Releasing use key must reset progress");
        assertEquals(0.0f, session.getProgress());
        assertFalse(session.isComplete());

        // Now hold for full 50 ticks
        boolean finished = false;
        for (int i = 0; i < requiredTicks; i++) {
            finished = session.tick(true);
        }
        assertTrue(finished, "Session must report finished when required ticks are reached");
        assertTrue(session.isComplete());
        assertEquals(1.0f, session.getProgress(), 0.001f);
    }

    @Test
    void testTeleportDecisionLogic() {
        java.util.UUID flameId = java.util.UUID.randomUUID();
        TransportFlameLogic.FlameTarget target = new TransportFlameLogic.FlameTarget(
            100, 64, 200, "minecraft:overworld", 0x3AB3DA, flameId
        );

        // Null target
        assertEquals(TransportFlameLogic.TeleportOutcome.INVALID_TARGET,
            TransportFlameLogic.evaluateTeleport(null, true, 0, 64, 0, "minecraft:overworld"));

        // Flame not existing in world anymore
        assertEquals(TransportFlameLogic.TeleportOutcome.FLAME_EXTINGUISHED,
            TransportFlameLogic.evaluateTeleport(target, false, 0, 64, 0, "minecraft:overworld"));

        // Player already at destination
        assertEquals(TransportFlameLogic.TeleportOutcome.ALREADY_AT_TARGET,
            TransportFlameLogic.evaluateTeleport(target, true, 100, 64, 200, "minecraft:overworld"));

        // Valid teleport across dimensions
        assertEquals(TransportFlameLogic.TeleportOutcome.SUCCESS,
            TransportFlameLogic.evaluateTeleport(target, true, 100, 64, 200, "minecraft:the_nether"));

        // Valid teleport within same dimension from far away
        assertEquals(TransportFlameLogic.TeleportOutcome.SUCCESS,
            TransportFlameLogic.evaluateTeleport(target, true, 500, 70, -300, "minecraft:overworld"));
    }

    @Test
    void testArrivalCoordinatesCalculation() {
        java.util.UUID flameId = java.util.UUID.randomUUID();
        TransportFlameLogic.FlameTarget target = new TransportFlameLogic.FlameTarget(
            15, 64, -80, "minecraft:overworld", 0x3AB3DA, flameId
        );

        double[] pos = TransportFlameLogic.calculateArrivalCoordinates(target);
        assertEquals(15.5, pos[0], 0.001);
        assertEquals(64.0, pos[1], 0.001);
        assertEquals(-79.5, pos[2], 0.001);
    }

    @Test
    void testDyedItemColorCompatibility() {
        int color = 0x3AB3DA;
        net.minecraft.world.item.component.DyedItemColor dyedColor = new net.minecraft.world.item.component.DyedItemColor(color);
        assertEquals(color, dyedColor.rgb());
    }

    @Test
    void testTransportMapMaxStackSize() {
        assertEquals(64, TransportFlameLogic.TRANSPORT_MAP_MAX_STACK_SIZE,
            "Transport map should have a max stack size of 64 so up to 64 maps can fit into a bundle");
    }

    @Test
    void testTransportMapBundleWeight() {
        org.apache.commons.lang3.math.Fraction itemWeight = org.apache.commons.lang3.math.Fraction.getFraction(1, TransportFlameLogic.TRANSPORT_MAP_MAX_STACK_SIZE);
        assertEquals(org.apache.commons.lang3.math.Fraction.getFraction(1, 64), itemWeight,
            "Each transport map must occupy exactly 1/64 of a bundle");
        
        org.apache.commons.lang3.math.Fraction fullBundleWeight = itemWeight.multiplyBy(org.apache.commons.lang3.math.Fraction.getFraction(64, 1));
        assertEquals(org.apache.commons.lang3.math.Fraction.ONE, fullBundleWeight,
            "64 transport maps must fill exactly one bundle");
    }

    @Test
    void testDecidePlacementAction_WhenPlayerAlreadyHasMapToSamePosition_KeepsExistingMap() {
        java.util.UUID flameId = java.util.UUID.randomUUID();
        TransportFlameLogic.FlameTarget existing = new TransportFlameLogic.FlameTarget(
            100, 64, 200, "minecraft:overworld", 0x3AB3DA, flameId
        );

        TransportFlameLogic.PlacementDecision decision = TransportFlameLogic.decidePlacementAction(
            100, 64, 200, "minecraft:overworld",
            java.util.List.of(existing),
            (dim, pos) -> true, // Flame exists
            new java.util.Random(1)
        );

        assertEquals(TransportFlameLogic.PlacementActionType.KEEP_EXISTING_MAP, decision.actionType(),
            "If player already has a map to this exact location, keep existing map");
    }

    @Test
    void testDecidePlacementAction_WhenPlayerHasExtinguishedOldMap_RelinksOldMap() {
        java.util.UUID flameId = java.util.UUID.randomUUID();
        TransportFlameLogic.FlameTarget oldMap = new TransportFlameLogic.FlameTarget(
            100, 64, 200, "minecraft:overworld", 0x3AB3DA, flameId
        );

        TransportFlameLogic.PlacementDecision decision = TransportFlameLogic.decidePlacementAction(
            150, 70, 250, "minecraft:overworld",
            java.util.List.of(oldMap),
            (dim, pos) -> false, // Flame at old location does NOT exist (it was broken/extinguished)
            new java.util.Random(1)
        );

        assertEquals(TransportFlameLogic.PlacementActionType.RELINK_OLD_MAP, decision.actionType(),
            "Must relink extinguished old map instead of generating a new map");
        assertEquals(oldMap, decision.targetToRelink(), "Target to relink must be the old map");
        assertEquals(150, decision.newTarget().x());
        assertEquals(70, decision.newTarget().y());
        assertEquals(250, decision.newTarget().z());
        assertEquals("minecraft:overworld", decision.newTarget().dimension());
        assertEquals(0x3AB3DA, decision.newTarget().colorRgb(), "Must preserve original color");
        assertEquals(flameId, decision.newTarget().flameId(), "Must preserve original flameId");
    }

    @Test
    void testDecidePlacementAction_WhenPlayerHasOnlyActiveMapsToOtherFlames_CreatesNewMap() {
        java.util.UUID flameId = java.util.UUID.randomUUID();
        TransportFlameLogic.FlameTarget activeMap = new TransportFlameLogic.FlameTarget(
            100, 64, 200, "minecraft:overworld", 0x3AB3DA, flameId
        );

        TransportFlameLogic.PlacementDecision decision = TransportFlameLogic.decidePlacementAction(
            150, 70, 250, "minecraft:overworld",
            java.util.List.of(activeMap),
            (dim, pos) -> true, // Flame at (100, 64, 200) is ALIVE and existing!
            new java.util.Random(42)
        );

        assertEquals(TransportFlameLogic.PlacementActionType.CREATE_NEW_MAP, decision.actionType(),
            "Must create a new map because existing map belongs to an active flame");
        assertNull(decision.targetToRelink());
        assertEquals(150, decision.newTarget().x());
        assertEquals(70, decision.newTarget().y());
        assertEquals(250, decision.newTarget().z());
        assertEquals("minecraft:overworld", decision.newTarget().dimension());
    }

    @Test
    void testDecidePlacementAction_WhenPlayerHasBothActiveAndExtinguishedMaps_RelinksOnlyExtinguishedMap() {
        java.util.UUID activeFlameId = java.util.UUID.randomUUID();
        TransportFlameLogic.FlameTarget activeMap = new TransportFlameLogic.FlameTarget(
            100, 64, 200, "minecraft:overworld", 0x3AB3DA, activeFlameId
        );

        java.util.UUID extinguishedFlameId = java.util.UUID.randomUUID();
        TransportFlameLogic.FlameTarget extinguishedMap = new TransportFlameLogic.FlameTarget(
            300, 70, 400, "minecraft:overworld", 0xB02E26, extinguishedFlameId
        );

        TransportFlameLogic.PlacementDecision decision = TransportFlameLogic.decidePlacementAction(
            500, 64, 500, "minecraft:overworld",
            java.util.List.of(activeMap, extinguishedMap),
            (dim, pos) -> pos.getX() == 100, // Flame at 100 exists, flame at 300 does not
            new java.util.Random(42)
        );

        assertEquals(TransportFlameLogic.PlacementActionType.RELINK_OLD_MAP, decision.actionType());
        assertEquals(extinguishedMap, decision.targetToRelink(), "Must pick the extinguished map to relink, not the active map");
        assertEquals(500, decision.newTarget().x());
        assertEquals(0xB02E26, decision.newTarget().colorRgb(), "Must preserve extinguished map's color");
        assertEquals(extinguishedFlameId, decision.newTarget().flameId(), "Must preserve extinguished map's flameId");
    }
}
