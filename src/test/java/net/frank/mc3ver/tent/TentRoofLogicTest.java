package net.frank.mc3ver.tent;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TentRoofLogicTest {

    private static final Path MODELS =
            Paths.get("").toAbsolutePath().resolve("src/main/generated/assets/mc3ver/models/block");

    @Test
    void roofRisesFromLowSideWallsToARidgeAboveTheTentVolume() {
        assertEquals(TentRoofLogic.WALL_HEIGHT, TentRoofLogic.roofTopAt(0), 1e-9);
        assertEquals(TentRoofLogic.WALL_HEIGHT, TentRoofLogic.roofTopAt(48), 1e-9);
        assertEquals(TentRoofLogic.RIDGE_HEIGHT, TentRoofLogic.roofTopAt(24), 1e-9);
        assertEquals(TentRoofLogic.roofTopAt(10), TentRoofLogic.roofTopAt(38), 1e-9, "symmetrisch");
        assertTrue(TentRoofLogic.roofTopAt(10) < TentRoofLogic.roofTopAt(11), "steigt zur Mitte hin");
        assertTrue(TentRoofLogic.RIDGE_HEIGHT > 32, "First ragt über die zwei Blockebenen hinaus");
        assertTrue(TentRoofLogic.WALL_HEIGHT < 16, "Seitenwände sind niedriger als ein Block");
    }

    @Test
    void slopeIsSteeperThanFortyFiveDegrees() {
        assertEquals(28.0 / 24.0, TentRoofLogic.slope(), 1e-9);
        assertEquals(49.4, TentRoofLogic.angleDegrees(), 0.05);
    }

    @Test
    void planeRunsFromEaveToRidge() {
        double run = 24 + TentRoofLogic.EAVE;
        assertEquals(Math.hypot(run, run * TentRoofLogic.slope()), TentRoofLogic.planeLength(), 1e-9);
    }

    @Test
    void standingPlayerFitsUnderTheRidgeInTheMiddleColumn() {
        // Spieler 0,6 Blöcke breit in der Mitte der Mittelspalte, 1,8 Blöcke hoch
        double playerTop = 1.8 * 16;
        double edge = 24 - 0.3 * 16;
        assertTrue(TentRoofLogic.roofTopAt(edge) - TentRoofLogic.THICKNESS > playerTop);
    }

    @Test
    void furnitureStaysUnderTheRoof() throws Exception {
        // Möbel stehen in den unteren Seitenzellen; kein Element darf durch die Dachschräge stoßen
        for (String name : Set.of(
                "tent_furnace_left",
                "tent_chest_right",
                "tent_chest_right_open",
                "tent_canvas_wall_crafting_table_right")) {
            double cellX = name.contains("left") ? 0 : 32;
            JsonObject model = JsonParser.parseString(Files.readString(MODELS.resolve(name + ".json")))
                    .getAsJsonObject();
            for (JsonElement el : model.getAsJsonArray("elements")) {
                JsonObject e = el.getAsJsonObject();
                if (e.has("rotation")) continue; // aufgeklappter Truhendeckel liegt ohnehin tiefer als die Schräge
                JsonArray from = e.getAsJsonArray("from");
                JsonArray to = e.getAsJsonArray("to");
                double top = to.get(1).getAsDouble();
                double roof = Math.min(
                        TentRoofLogic.roofTopAt(cellX + from.get(0).getAsDouble()),
                        TentRoofLogic.roofTopAt(cellX + to.get(0).getAsDouble()));
                assertTrue(top <= roof + 1e-9, name + ": Element " + e.get("from") + " ragt bis " + top + " > " + roof);
            }
        }
    }

    @Test
    void ridgeNeedsFreeSpaceAboveTheMiddleRow() {
        ExplorerTentLogic.BlockCoord ridge =
                ExplorerTentLogic.toWorldPos(100, 64, 200, ExplorerTentLogic.Facing.EAST, 0, 2, 3);
        ExplorerTentLogic.BlockCoord besideRidge =
                ExplorerTentLogic.toWorldPos(100, 64, 200, ExplorerTentLogic.Facing.EAST, 1, 2, 3);

        ExplorerTentLogic.PlacementResult blocked =
                ExplorerTentLogic.validatePlacement(100, 64, 200, ExplorerTentLogic.Facing.EAST, worldBlockedAt(ridge));
        assertEquals(ExplorerTentLogic.PlacementStatus.OBSTRUCTED, blocked.status());
        assertEquals(ridge, blocked.failedCoord());

        assertEquals(
                ExplorerTentLogic.PlacementStatus.SUCCESS,
                ExplorerTentLogic.validatePlacement(
                                100, 64, 200, ExplorerTentLogic.Facing.EAST, worldBlockedAt(besideRidge))
                        .status(),
                "Über den Seitenspalten reicht das Dach nicht in die dritte Ebene");
    }

    private static ExplorerTentLogic.WorldView worldBlockedAt(ExplorerTentLogic.BlockCoord blocked) {
        return new ExplorerTentLogic.WorldView() {
            @Override
            public boolean isSolid(int x, int y, int z) {
                return y < 64;
            }

            @Override
            public boolean isReplaceable(int x, int y, int z) {
                return y >= 64 && !(x == blocked.x() && y == blocked.y() && z == blocked.z());
            }
        };
    }
}
