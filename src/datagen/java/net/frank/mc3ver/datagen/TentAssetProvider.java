package net.frank.mc3ver.datagen;

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
            writes.add(DataProvider.saveStable(output, read(file), blockstates.json(Mc3verMod.id(name(file)))));
        }
        for (Path file : templates("models")) {
            JsonObject model = TentModelShaper.shape(name(file), read(file).getAsJsonObject());
            writes.add(DataProvider.saveStable(output, model, models.json(Mc3verMod.id(name(file)))));
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    static JsonObject canvasBlockstate() {
        JsonArray multipart = new JsonArray();
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            String partName = part.getSerializedName();
            // Die unbenutzte Seitenwand teilt sich das Modell der linken Wand
            String model = part == TentCanvasBlock.CanvasPart.WALL_SIDE ? "wall_left" : partName;
            for (int i = 0; i < FACINGS.length; i++) {
                JsonObject when = new JsonObject();
                when.addProperty("facing", FACINGS[i]);
                when.addProperty("part", partName);
                JsonObject apply = new JsonObject();
                apply.addProperty("model", "mc3ver:block/tent_canvas_" + model);
                if (i > 0) apply.addProperty("y", i * 90);
                JsonObject entry = new JsonObject();
                entry.add("when", when);
                entry.add("apply", apply);
                multipart.add(entry);
            }
        }
        JsonObject state = new JsonObject();
        state.add("multipart", multipart);
        return state;
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
