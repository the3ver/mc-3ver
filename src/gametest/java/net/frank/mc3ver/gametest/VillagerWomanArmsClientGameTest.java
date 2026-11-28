package net.frank.mc3ver.gametest;

import java.util.UUID;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.frank.mc3ver.client.VillagerWomanFigure;
import net.frank.mc3ver.client.VillagerWomanFigure.ArmStyle;

// Vorschläge zur Armhaltung der Bewohnerin: je Haltung ein Screenshot von vorn und einer schräg,
// links ein Bewohner zum Vergleich, daneben Bewohnerin ohne Beruf und Bäuerin.
public class VillagerWomanArmsClientGameTest implements FabricClientGameTest {

    private static final int Y = 150;
    private static final double X = 3.5;
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
            summon(-1.6, new UUID(20L, 1L), "", game);
            summon(0.0, new UUID(20L, 0L), "", game);
            summon(1.6, new UUID(20L, 4L), FARMER, game);
            for (ArmStyle style : ArmStyle.values()) {
                context.runOnClient(client -> VillagerWomanFigure.armStyle = style);
                String name = "arms_" + style.name().toLowerCase(java.util.Locale.ROOT);
                shot(context, game, name + "_front", -0.8, 0.0);
                shot(context, game, name + "_diagonal", 0.0, -3.2);
            }
            context.runOnClient(client -> VillagerWomanFigure.armStyle = ArmStyle.CROSSED);
        }
    }

    private static void shot(
            ClientGameTestContext context, TestSingleplayerContext game, String name, double x, double z) {
        game.getServer()
                .runCommand("tp @p " + x + " " + (Y + 0.6) + " " + z + " facing " + X + " " + (Y + 1.1) + " 0.0");
        game.getConnection().waitForChunksRender();
        context.waitTicks(10);
        context.takeScreenshot(TestScreenshotOptions.of(name).withSize(1280, 720));
    }

    private static void summon(double z, UUID uuid, String nbt, TestSingleplayerContext game) {
        int[] ints = {
            (int) (uuid.getMostSignificantBits() >> 32),
            (int) uuid.getMostSignificantBits(),
            (int) (uuid.getLeastSignificantBits() >> 32),
            (int) uuid.getLeastSignificantBits()
        };
        game.getServer()
                .runCommand("summon villager " + X + " " + Y + " " + z + " {NoAI:1b,Rotation:[90f,0f]," + nbt
                        + "UUID:[I;" + ints[0] + "," + ints[1] + "," + ints[2] + "," + ints[3] + "]}");
    }
}
