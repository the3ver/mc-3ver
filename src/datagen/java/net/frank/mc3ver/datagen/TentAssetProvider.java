package net.frank.mc3ver.datagen;

import com.google.common.base.Splitter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.frank.mc3ver.Mc3verMod;
import net.frank.mc3ver.tent.TentCanvasBlock;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

/**
 * Blockstates und Blockmodelle des Zelts. Vorlagen liegen in src/datagen/resources/tent, die Modelle formt
 * {@link TentModelShaper} nach dem Dachquerschnitt. Der Blockstate der Zeltplane entsteht aus
 * {@link TentCanvasBlock.CanvasPart} (ein Modell je Teil, vier Drehungen).
 */
class TentAssetProvider implements DataProvider {

    private static final String[] FACINGS = {"north", "east", "south", "west"};

    private final PackOutput.PathProvider blockstates;
    private final PackOutput.PathProvider models;

    TentAssetProvider(FabricPackOutput output) {
        this.blockstates = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.models = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> writes = new ArrayList<>();
        writes.add(DataProvider.saveStable(output, canvasBlockstate(), blockstates.json(Mc3verMod.id("tent_canvas"))));
        for (Path file : templates("blockstates")) {
            JsonObject state = read(file).getAsJsonObject();
            String cellModel = CELL_MODEL_OF_BLOCK.get(name(file));
            if (cellModel != null) state = withGuyLine(state, TentModelShaper.guyLineFor(cellModel));
            writes.add(DataProvider.saveStable(output, state, blockstates.json(Mc3verMod.id(name(file)))));
        }
        for (Path file : templates("models")) {
            JsonObject model = TentModelShaper.shape(name(file), read(file).getAsJsonObject());
            writes.add(DataProvider.saveStable(output, model, models.json(Mc3verMod.id(name(file)))));
        }
        for (String name : TentModelShaper.GUY_LINE_MODELS) {
            writes.add(DataProvider.saveStable(
                    output, TentModelShaper.guyLineModel(name), models.json(Mc3verMod.id(name))));
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    // Ofen und Truhe stehen in einer Seitenzelle und tragen deren Abspannleine
    private static final Map<String, String> CELL_MODEL_OF_BLOCK =
            Map.of("tent_furnace", "tent_furnace_left", "tent_chest", "tent_chest_right");

    static JsonObject canvasBlockstate() {
        JsonArray multipart = new JsonArray();
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            String partName = part.getSerializedName();
            // Die unbenutzte Seitenwand teilt sich das Modell der linken Wand
            String model = part == TentCanvasBlock.CanvasPart.WALL_SIDE ? "wall_left" : partName;
            String guyLine = part == TentCanvasBlock.CanvasPart.WALL_SIDE
                    ? null
                    : TentModelShaper.guyLineFor("tent_canvas_" + model);
            for (int i = 0; i < FACINGS.length; i++) {
                multipart.add(
                        part(when("facing", FACINGS[i], "part", partName), "mc3ver:block/tent_canvas_" + model, i));
            }
            if (guyLine != null) addGuyLine(multipart, guyLine, "part", partName);
        }
        JsonObject state = new JsonObject();
        state.add("multipart", multipart);
        return state;
    }

    /** Wandelt einen variants-Blockstate in multipart und hängt die Abspannleine je Richtung an. */
    static JsonObject withGuyLine(JsonObject state, String guyLine) {
        JsonArray multipart = new JsonArray();
        for (var variant : state.getAsJsonObject("variants").entrySet()) {
            JsonObject when = new JsonObject();
            for (String condition : Splitter.on(',').split(variant.getKey())) {
                List<String> kv = Splitter.on('=').splitToList(condition);
                when.addProperty(kv.get(0), kv.get(1));
            }
            JsonObject entry = new JsonObject();
            entry.add("when", when);
            entry.add("apply", variant.getValue());
            multipart.add(entry);
        }
        addGuyLine(multipart, guyLine);
        JsonObject result = new JsonObject();
        result.add("multipart", multipart);
        return result;
    }

    private static void addGuyLine(JsonArray multipart, String guyLine, String... extraWhen) {
        for (int i = 0; i < FACINGS.length; i++) {
            JsonObject when = when("facing", FACINGS[i]);
            for (int k = 0; k < extraWhen.length; k += 2) when.addProperty(extraWhen[k], extraWhen[k + 1]);
            multipart.add(part(when, "mc3ver:block/" + guyLine, i));
        }
    }

    private static JsonObject when(String... keyValues) {
        JsonObject when = new JsonObject();
        for (int k = 0; k < keyValues.length; k += 2) when.addProperty(keyValues[k], keyValues[k + 1]);
        return when;
    }

    private static JsonObject part(JsonObject when, String model, int facingIndex) {
        JsonObject apply = new JsonObject();
        apply.addProperty("model", model);
        if (facingIndex > 0) apply.addProperty("y", facingIndex * 90);
        JsonObject entry = new JsonObject();
        entry.add("when", when);
        entry.add("apply", apply);
        return entry;
    }

    private static List<Path> templates(String kind) {
        try {
            Path dir =
                    Path.of(TentAssetProvider.class.getResource("/tent/" + kind).toURI());
            try (Stream<Path> files = Files.list(dir)) {
                return files.filter(f -> f.toString().endsWith(".json"))
                        .sorted()
                        .toList();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private static JsonElement read(Path file) {
        try {
            return JsonParser.parseString(Files.readString(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String name(Path file) {
        String fileName = file.getFileName().toString();
        return fileName.substring(0, fileName.length() - ".json".length());
    }

    @Override
    public String getName() {
        return "Zelt-Blockstates und -Modelle";
    }
}
