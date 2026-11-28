package net.frank.mc3ver.gametest;

import java.util.UUID;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.frank.mc3ver.village.VillagerGenderLogic;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

// Alle Aussehen der Bewohnerin nebeneinander, von vorn, schräg von vorn und schräg von hinten, zum Vergleichen.
// Dazu die Figur neben einem Bewohner: ohne Beruf, mit Beruf, Bäuerin mit Ware (Arme verschränkt).
// Aufruf: ./gradlew runClientGameTest, Screenshots unter build/run/clientGameTest/screenshots
public class VillagerWomanLooksClientGameTest implements FabricClientGameTest {

    private static final int Y = 150;
    private static final int LOOKS = 3;
    private static final double X = 3.5;
    private static final double FIGURES_Z = -8.0;
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
            for (int look = 0; look < LOOKS; look++) {
                // Unterstes Bit 0: weiblich, die Bits darüber wählen das Aussehen
                UUID uuid = new UUID(9L, (long) look << 1);
                check(VillagerGenderLogic.isFemale(uuid), "UUID nicht weiblich");
                check(VillagerGenderLogic.look(uuid, LOOKS) == look, "UUID wählt falsches Aussehen");
                summon(X, 1.8 * (look - 1) + 0.5, uuid, 90, "", game);
            }
            // Figur: Bewohner und Bewohnerin ohne und mit Beruf von vorn, dazu beide im Profil
            summon(X, FIGURES_Z - 2.4, new UUID(10L, 1L), 90, "", game);
            summon(X, FIGURES_Z - 0.8, new UUID(10L, 0L), 90, "", game);
            summon(X, FIGURES_Z + 0.8, new UUID(10L, 3L), 90, FARMER, game);
            summon(X, FIGURES_Z + 2.4, new UUID(10L, 4L), 90, FARMER, game);
            // Bäuerin mit Ware: Arme bleiben verschränkt
            game.getServer().runOnServer(server -> {
                Entity farmer = server.overworld().getEntity(new UUID(10L, 4L));
                ((Villager) farmer).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.EMERALD));
            });
            summon(X, PROFILE_Z - 1.0, new UUID(11L, 1L), 0, "", game);
            summon(X, PROFILE_Z + 1.0, new UUID(11L, 0L), 0, "", game);

            shot(context, game, "villager_women_front", -1.0, 0.6, 0.5, 0.5);
            shot(context, game, "villager_women_diagonal", -0.5, 0.6, -3.5, 0.5);
            shot(context, game, "villager_women_back", 7.0, 0.6, -2.8, 0.5);
            shot(context, game, "villager_figure_front", -1.0, 0.6, FIGURES_Z, FIGURES_Z);
            shot(context, game, "villager_figure_profile", -1.5, 0.6, PROFILE_Z, PROFILE_Z);
            shot(context, game, "villager_figure_back", 8.0, 0.8, PROFILE_Z - 4.0, PROFILE_Z);
        }
    }

    private static void shot(
            ClientGameTestContext context,
            TestSingleplayerContext game,
            String name,
            double x,
            double dy,
            double z,
            double targetZ) {
        game.getServer()
                .runCommand("tp @p " + x + " " + (Y + dy) + " " + z + " facing " + X + " " + (Y + 1.2) + " " + targetZ);
        game.getConnection().waitForChunksRender();
        context.waitTicks(20);
        context.takeScreenshot(TestScreenshotOptions.of(name).withSize(1280, 720));
    }

    private static void summon(double x, double z, UUID uuid, int yaw, String nbt, TestSingleplayerContext game) {
        int[] ints = {
            (int) (uuid.getMostSignificantBits() >> 32),
            (int) uuid.getMostSignificantBits(),
            (int) (uuid.getLeastSignificantBits() >> 32),
            (int) uuid.getLeastSignificantBits()
        };
        game.getServer()
                .runCommand("summon villager " + x + " " + Y + " " + z + " {NoAI:1b,Rotation:[" + yaw + "f,0f]," + nbt
                        + "UUID:[I;" + ints[0] + "," + ints[1] + "," + ints[2] + "," + ints[3] + "]}");
    }

    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }
}
