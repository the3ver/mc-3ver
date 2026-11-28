package net.frank.mc3ver.gametest;

import java.util.Locale;
import java.util.UUID;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.frank.mc3ver.client.VillagerWomanFigure;
import net.frank.mc3ver.client.VillagerWomanFigure.ArmStyle;
import net.frank.mc3ver.client.VillagerWomanFigure.BustStyle;
import net.frank.mc3ver.client.VillagerWomanFigure.NoseStyle;

// Vorschläge zur Brust der Bewohnerin, alle mit Händen in den Hüften: je Vorschlag schräg von vorn und im Profil.
// Von vorn: Bewohner, Bewohnerin, Bäuerin. Im Profil: Bewohnerin, Bäuerin, Bewohner.
public class VillagerWomanBustClientGameTest implements FabricClientGameTest {

    private static final int Y = 150;
    private static final double X = 3.5;
    private static final double PROFILE_Z = 8.5;
    private static final String FARMER =
            "VillagerData:{profession:\"minecraft:farmer\",level:2,type:\"minecraft:plains\"},";

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext game = context.worldBuilder().create()) {
            game.getServer().runCommand("gamerule doDaylightCycle false");
            game.getServer().runCommand("gamerule doMobSpawning false");
            game.getServer().runCommand("time set 6000");
            game.getServer().runCommand("fill -12 " + (Y - 1) + " -12 12 " + (Y - 1) + " 12 grass_block");
            game.getServer().runCommand("fill -12 " + Y + " -12 12 " + (Y + 10) + " 12 air");
            game.getServer().runCommand("gamemode spectator @p");
            summon(-1.6, 90, new UUID(30L, 1L), "", game);
            summon(0.0, 90, new UUID(30L, 0L), "", game);
            summon(1.6, 90, new UUID(30L, 4L), FARMER, game);
            summon(PROFILE_Z - 1.6, 0, new UUID(31L, 0L), "", game);
            summon(PROFILE_Z, 0, new UUID(31L, 4L), FARMER, game);
            summon(PROFILE_Z + 1.6, 0, new UUID(31L, 1L), "", game);
            context.runOnClient(client -> VillagerWomanFigure.armStyle = ArmStyle.HIPS);
            for (BustStyle style : new BustStyle[] {BustStyle.NONE, BustStyle.ROUND, BustStyle.COMBINED}) {
                context.runOnClient(client -> VillagerWomanFigure.bustStyle = style);
                String name = "bust_" + style.name().toLowerCase(Locale.ROOT);
                shot(context, game, name + "_diagonal", 0.0, -3.2, 0.0);
                shot(context, game, name + "_profile", -0.5, PROFILE_Z, PROFILE_Z);
            }
            // Nase: mit tiefer Wölbung ohne Striche, schräg, im Profil und frontal nah am Gesicht
            context.runOnClient(client -> VillagerWomanFigure.bustStyle = BustStyle.LOW);
            for (NoseStyle nose : NoseStyle.values()) {
                context.runOnClient(client -> VillagerWomanFigure.noseStyle = nose);
                String name = "nose_" + nose.name().toLowerCase(Locale.ROOT);
                shot(context, game, name + "_diagonal", 0.0, -3.2, 0.0);
                shot(context, game, name + "_profile", -0.5, PROFILE_Z, PROFILE_Z);
                shot(context, game, name + "_face", 1.2, 0.0, 0.0);
            }
            context.runOnClient(client -> {
                VillagerWomanFigure.noseStyle = NoseStyle.VANILLA;
                VillagerWomanFigure.armStyle = ArmStyle.CROSSED;
                VillagerWomanFigure.bustStyle = BustStyle.NONE;
            });
        }
    }

    private static void shot(
            ClientGameTestContext context, TestSingleplayerContext game, String name, double x, double z, double tz) {
        game.getServer()
                .runCommand("tp @p " + x + " " + (Y + 0.6) + " " + z + " facing " + X + " " + (Y + 1.1) + " " + tz);
        game.getConnection().waitForChunksRender();
        context.waitTicks(10);
        context.takeScreenshot(TestScreenshotOptions.of(name).withSize(1280, 720));
    }

    private static void summon(double z, int yaw, UUID uuid, String nbt, TestSingleplayerContext game) {
        int[] ints = {
            (int) (uuid.getMostSignificantBits() >> 32),
            (int) uuid.getMostSignificantBits(),
            (int) (uuid.getLeastSignificantBits() >> 32),
            (int) uuid.getLeastSignificantBits()
        };
        game.getServer()
                .runCommand("summon villager " + X + " " + Y + " " + z + " {NoAI:1b,Rotation:[" + yaw + "f,0f]," + nbt
                        + "UUID:[I;" + ints[0] + "," + ints[1] + "," + ints[2] + "," + ints[3] + "]}");
    }
}
