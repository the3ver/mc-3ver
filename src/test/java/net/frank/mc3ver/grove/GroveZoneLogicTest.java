package net.frank.mc3ver.grove;

import static org.junit.jupiter.api.Assertions.*;

import net.frank.mc3ver.grove.GroveCenterLogic.GroveCenter;
import net.frank.mc3ver.grove.GroveZoneLogic.Density;
import net.frank.mc3ver.grove.GroveZoneLogic.Zone;
import org.junit.jupiter.api.Test;

class GroveZoneLogicTest {

    private static final long SEED = 12345L;
    // Mittelpunkt (312, 200), alter Baum (297, 202), siehe GroveCenterLogicTest
    private static final GroveCenter CENTER = GroveCenterLogic.centerInCell(SEED, 0, 0);
    private static final double FAR = 1000;

    @Test
    void zoneBoundariesFollowCenterDistance() {
        assertEquals(Zone.FOREST, GroveZoneLogic.zoneAt(0, FAR));
        assertEquals(Zone.FOREST, GroveZoneLogic.zoneAt(55.9, FAR));
        assertEquals(Zone.GROUPS, GroveZoneLogic.zoneAt(56, FAR));
        assertEquals(Zone.GROUPS, GroveZoneLogic.zoneAt(95.9, FAR));
        assertEquals(Zone.EDGE, GroveZoneLogic.zoneAt(96, FAR));
        assertEquals(Zone.EDGE, GroveZoneLogic.zoneAt(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    @Test
    void heartAroundOldTreeWinsOverEveryOtherZone() {
        assertEquals(Zone.HEART, GroveZoneLogic.zoneAt(0, 19.9));
        assertEquals(Zone.HEART, GroveZoneLogic.zoneAt(FAR, 0));
        assertEquals(Zone.FOREST, GroveZoneLogic.zoneAt(0, 20));
    }

    @Test
    void zoneForUsesTheAcceptedCenterAndItsTree() {
        assertEquals(Zone.HEART, zoneOnlyAround(CENTER, CENTER.treeX(), CENTER.treeZ()));
        assertEquals(Zone.HEART, zoneOnlyAround(CENTER, CENTER.x(), CENTER.z()));
        assertEquals(Zone.HEART, zoneOnlyAround(CENTER, CENTER.treeX(), CENTER.treeZ() + 15));
        assertEquals(Zone.FOREST, zoneOnlyAround(CENTER, CENTER.x() + 30, CENTER.z()));
        assertEquals(Zone.GROUPS, zoneOnlyAround(CENTER, CENTER.x() + 70, CENTER.z()));
        assertEquals(Zone.EDGE, zoneOnlyAround(CENTER, CENTER.x() + 120, CENTER.z()));
    }

    @Test
    void zoneForMeasuresHeartFromTheTreeNotTheCenter() {
        // 25 Blöcke vom Mittelpunkt in Gegenrichtung zum Baum: außerhalb des Herzens, also Wald
        int awayX = CENTER.x() + Integer.signum(CENTER.x() - CENTER.treeX()) * 25;
        assertEquals(Zone.FOREST, zoneOnlyAround(CENTER, awayX, CENTER.z()));
    }

    @Test
    void zoneForIsEdgeWhenNoCenterIsInTheGrove() {
        assertEquals(Zone.EDGE, GroveZoneLogic.zoneFor(SEED, CENTER.x(), CENTER.z(), c -> false));
    }

    @Test
    void zoneForSkipsRejectedCloserCenters() {
        GroveCenter neighbour = GroveCenterLogic.centerInCell(SEED, 1, 0);
        // Am eigenen Mittelpunkt, aber nur der Nachbar zählt: der ist mehr als eine Zellbreite/2 weg
        assertEquals(Zone.EDGE, zoneOnlyAround(neighbour, CENTER.x(), CENTER.z()));
        assertEquals(Zone.HEART, GroveZoneLogic.zoneFor(SEED, CENTER.treeX(), CENTER.treeZ(), c -> true));
    }

    @Test
    void densityGrowsTowardsTheMiddleAndStopsAtTheHeart() {
        double edge = expectedTrees(Zone.EDGE);
        double groups = expectedTrees(Zone.GROUPS);
        double forest = expectedTrees(Zone.FOREST);
        assertTrue(edge > 0, "Rand hat einzelne Bäume");
        assertTrue(groups > edge, "Gruppen dichter als Rand");
        assertTrue(forest > groups, "Wald dichter als Gruppen");
        assertEquals(0, expectedTrees(Zone.HEART));
    }

    @Test
    void groupsStandTogetherOthersAlone() {
        Density groups = GroveZoneLogic.densityOf(Zone.GROUPS);
        assertEquals(3, groups.minClusterSize());
        assertEquals(5, groups.maxClusterSize());
        assertTrue(groups.clusterRadius() > 0);
        assertEquals(1, GroveZoneLogic.densityOf(Zone.EDGE).maxClusterSize());
        assertEquals(1, GroveZoneLogic.densityOf(Zone.FOREST).maxClusterSize());
    }

    @Test
    void groundPatchesOnlyInTheForest() {
        assertTrue(GroveZoneLogic.densityOf(Zone.FOREST).groundPatchChance() > 0);
        assertEquals(0f, GroveZoneLogic.densityOf(Zone.GROUPS).groundPatchChance());
        assertEquals(0f, GroveZoneLogic.densityOf(Zone.EDGE).groundPatchChance());
        assertEquals(0f, GroveZoneLogic.densityOf(Zone.HEART).groundPatchChance());
    }

    @Test
    void countForRoundsByChance() {
        assertEquals(0, GroveZoneLogic.countFor(0.5f, 0.5f));
        assertEquals(1, GroveZoneLogic.countFor(0.5f, 0.49f));
        assertEquals(9, GroveZoneLogic.countFor(9f, 0f));
        assertEquals(9, GroveZoneLogic.countFor(9f, 0.99f));
        assertEquals(3, GroveZoneLogic.countFor(2.25f, 0.2f));
        assertEquals(2, GroveZoneLogic.countFor(2.25f, 0.25f));
        assertEquals(0, GroveZoneLogic.countFor(0f, 0f));
    }

    private static Zone zoneOnlyAround(GroveCenter accepted, int x, int z) {
        return GroveZoneLogic.zoneFor(SEED, x, z, c -> c.equals(accepted));
    }

    private static double expectedTrees(Zone zone) {
        Density d = GroveZoneLogic.densityOf(zone);
        return d.clustersPerChunk() * (d.minClusterSize() + d.maxClusterSize()) / 2.0;
    }
}
