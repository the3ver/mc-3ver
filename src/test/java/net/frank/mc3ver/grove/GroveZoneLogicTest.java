package net.frank.mc3ver.grove;

import static org.junit.jupiter.api.Assertions.*;

import net.frank.mc3ver.grove.GroveZoneLogic.Density;
import net.frank.mc3ver.grove.GroveZoneLogic.Zone;
import org.junit.jupiter.api.Test;

class GroveZoneLogicTest {

    private static final double FAR = 1000;

    @Test
    void zoneFollowsDepthInsideTheGrove() {
        assertEquals(Zone.EDGE, GroveZoneLogic.zoneAt(0, FAR));
        assertEquals(Zone.EDGE, GroveZoneLogic.zoneAt(8, FAR));
        assertEquals(Zone.GROUPS, GroveZoneLogic.zoneAt(16, FAR));
        assertEquals(Zone.GROUPS, GroveZoneLogic.zoneAt(24, FAR));
        assertEquals(Zone.FOREST, GroveZoneLogic.zoneAt(32, FAR));
        assertEquals(Zone.FOREST, GroveZoneLogic.zoneAt(GroveShapeLogic.MAX_DEPTH, FAR));
    }

    @Test
    void heartAroundOldTreeWinsOverEveryOtherZone() {
        assertEquals(Zone.HEART, GroveZoneLogic.zoneAt(64, 19.9));
        assertEquals(Zone.HEART, GroveZoneLogic.zoneAt(0, 0));
        assertEquals(Zone.FOREST, GroveZoneLogic.zoneAt(64, 20));
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
    void fernsGrowOnlyInTheDenseForest() {
        assertTrue(GroveZoneLogic.densityOf(Zone.FOREST).fernPatchesPerChunk() > 0);
        for (Zone zone : new Zone[] {Zone.EDGE, Zone.GROUPS, Zone.HEART}) {
            assertEquals(0f, GroveZoneLogic.densityOf(zone).fernPatchesPerChunk(), zone.name());
        }
    }

    @Test
    void flowersThinOutTowardsTheForest() {
        float edge = GroveZoneLogic.densityOf(Zone.EDGE).flowerPatchesPerChunk();
        float groups = GroveZoneLogic.densityOf(Zone.GROUPS).flowerPatchesPerChunk();
        float forest = GroveZoneLogic.densityOf(Zone.FOREST).flowerPatchesPerChunk();
        assertTrue(edge > groups && groups > forest && forest > 0, edge + " > " + groups + " > " + forest);
        assertTrue(GroveZoneLogic.densityOf(Zone.HEART).flowerPatchesPerChunk() > 0, "Blumen am alten Baum");
    }

    @Test
    void bushesFillTheGapsBetweenGroups() {
        float groups = GroveZoneLogic.densityOf(Zone.GROUPS).bushesPerChunk();
        assertTrue(groups > GroveZoneLogic.densityOf(Zone.EDGE).bushesPerChunk());
        assertTrue(groups > GroveZoneLogic.densityOf(Zone.FOREST).bushesPerChunk());
        assertEquals(0f, GroveZoneLogic.densityOf(Zone.HEART).bushesPerChunk(), "Herz bleibt frei");
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

    private static double expectedTrees(Zone zone) {
        Density d = GroveZoneLogic.densityOf(zone);
        return d.clustersPerChunk() * (d.minClusterSize() + d.maxClusterSize()) / 2.0;
    }
}
