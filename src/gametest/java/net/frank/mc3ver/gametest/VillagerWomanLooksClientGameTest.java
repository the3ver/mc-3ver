package net.frank.mc3ver.gametest;

import java.util.UUID;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.frank.mc3ver.village.VillagerGenderLogic;

// Alle Aussehen der Bewohnerin nebeneinander, von vorn, von der Seite und schräg von hinten, zum Vergleichen.
// Aufruf: ./gradlew runClientGameTest, Screenshots unter build/run/clientGameTest/screenshots
public class VillagerWomanLooksClientGameTest implements FabricClientGameTest {

    private static final int Y = 150;
    private static final int LOOKS = 3;
    private static final double X = 3.5;

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext game = context.worldBuilder().create()) {
            game.getServer().runCommand("gamerule doDaylightCycle false");
            game.getServer().runCommand("gamerule doMobSpawning false");
            game.getServer().runCommand("time set 6000");
            game.getServer().runCommand("fill -12 " + (Y - 1) + " -12 12 " + (Y - 1) + " 12 grass_block");
            game.getServer().runCommand("fill -12 " + Y + " -12 12 " + (Y + 10) + " 12 air");
            game.getServer().runCommand("gamemode spectator @p");
            for (int look = 0; look < LOOKS; look++) {
                // Unterstes Bit 0: weiblich, die Bits darüber wählen das Aussehen
                UUID uuid = new UUID(9L, (long) look << 1);
                check(VillagerGenderLogic.isFemale(uuid), "UUID nicht weiblich");
                check(VillagerGenderLogic.look(uuid, LOOKS) == look, "UUID wählt falsches Aussehen");
                summon(X, 1.8 * (look - 1) + 0.5, uuid, game);
            }
            shot(context, game, "villager_women_front", -0.5, 0.3, 0.5);
            shot(context, game, "villager_women_side", 3.5, 0.3, -3.8);
            shot(context, game, "villager_women_back", 7.0, 0.6, -2.8);
        }
    }

    private static void shot(
            ClientGameTestContext context, TestSingleplayerContext game, String name, double x, double dy, double z) {
        game.getServer()
                .runCommand("tp @p " + x + " " + (Y + dy) + " " + z + " facing " + X + " " + (Y + 1.4) + " 0.5");
        game.getConnection().waitForChunksRender();
        context.waitTicks(20);
        context.takeScreenshot(TestScreenshotOptions.of(name).withSize(1280, 720));
    }

    private static void summon(double x, double z, UUID uuid, TestSingleplayerContext game) {
        int[] ints = {
            (int) (uuid.getMostSignificantBits() >> 32),
            (int) uuid.getMostSignificantBits(),
            (int) (uuid.getLeastSignificantBits() >> 32),
            (int) uuid.getLeastSignificantBits()
        };
        game.getServer()
                .runCommand("summon villager " + x + " " + Y + " " + z + " {NoAI:1b,Rotation:[90f,0f],UUID:[I;"
                        + ints[0] + "," + ints[1] + "," + ints[2] + "," + ints[3] + "]}");
    }

    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }
}
