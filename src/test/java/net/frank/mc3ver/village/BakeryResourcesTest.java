package net.frank.mc3ver.village;

import static net.frank.mc3ver.TestResources.json;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import org.junit.jupiter.api.Test;

class BakeryResourcesTest {

    private static final Path GENERATED = Paths.get("").toAbsolutePath().resolve("src/main/generated");

    @Test
    void everyVillageStyleHasABakeryWithOvenChestAndBothJigsaws() throws Exception {
        for (String style : BakeryLayoutLogic.STYLES) {
            CompoundTag structure = structure(style);
            ListTag palette = structure.getListOrEmpty("palette");
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < palette.size(); i++)
                ids.add(palette.getCompoundOrEmpty(i).getStringOr("id", ""));
            assertTrue(ids.contains("mc3ver:baking_oven"), style + ": kein Backofen");
            assertTrue(ids.contains("minecraft:chest"), style + ": keine Truhe");
            assertTrue(ids.contains("minecraft:bricks"), style + ": kein Schornstein");

            Set<String> pools = new HashSet<>();
            Set<String> lootTables = new HashSet<>();
            ListTag blocks = structure.getListOrEmpty("blocks");
            for (int i = 0; i < blocks.size(); i++) {
                CompoundTag entity = blocks.getCompoundOrEmpty(i).getCompoundOrEmpty("nbt");
                if (entity.getStringOr("id", "").equals("minecraft:jigsaw")) pools.add(entity.getStringOr("pool", ""));
                if (entity.contains("LootTable")) lootTables.add(entity.getStringOr("LootTable", ""));
            }
            String village = "minecraft:village/" + style + "/";
            assertEquals(Set.of(village + "streets", village + "villagers"), pools, style);
            assertEquals(Set.of("mc3ver:chests/village/bakery"), lootTables, style);

            ListTag size = structure.getListOrEmpty("size");
            assertEquals(BakeryLayoutLogic.SIZE_X, size.getIntOr(0, 0), style);
            assertEquals(BakeryLayoutLogic.CHIMNEY_TOP + 1, size.getIntOr(1, 0), style);
            assertEquals(BakeryLayoutLogic.SIZE_Z, size.getIntOr(2, 0), style);
        }
    }

    @Test
    void chestHoldsBakedGoodsAndPearCake() throws Exception {
        Set<String> items = new HashSet<>();
        for (JsonElement pool :
                json("data/mc3ver/loot_table/chests/village/bakery.json").getAsJsonArray("pools")) {
            for (JsonElement entry : pool.getAsJsonObject().getAsJsonArray("entries")) {
                items.add(entry.getAsJsonObject().get("name").getAsString());
            }
        }
        assertTrue(
                items.containsAll(Set.of("minecraft:bread", "minecraft:wheat", "mc3ver:pear_cake")), items.toString());
    }

    private static CompoundTag structure(String style) throws Exception {
        Path file = GENERATED.resolve("data/mc3ver/structure/village/" + style + "/houses/" + style + "_bakery.nbt");
        return NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
    }
}
