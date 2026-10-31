package net.frank.mc3ver;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

// Fängt Vanilla-Umbenennungen bei Versionswechseln ab: fehlende Texturen zeigt das Spiel sonst nur als lila-schwarze
// Kachel
public class ModelReferencesTest {

    private static final Path ASSETS = Paths.get("").toAbsolutePath().resolve("src/main/resources/assets");

    @Test
    void testVanillaAssetsOnTestClasspath() {
        assertNotNull(
                ModelReferencesTest.class.getResource("/assets/minecraft/textures/item/stick.png"),
                "Vanilla-Client-Assets müssen im Test-Classpath liegen, sonst prüft der Test nichts");
    }

    @Test
    void testAllModelTexturesAndParentsExist() throws IOException {
        List<String> missing = new ArrayList<>();
        try (Stream<Path> files = Files.walk(ASSETS.resolve("mc3ver/models"))) {
            for (Path file : files.filter(f -> f.toString().endsWith(".json")).toList()) {
                JsonObject model =
                        JsonParser.parseString(Files.readString(file)).getAsJsonObject();
                String name = ASSETS.relativize(file).toString().replace('\\', '/');
                if (model.has("parent")
                        && !model.get("parent").getAsString().startsWith("builtin/")
                        && !exists("models", model.get("parent").getAsString(), ".json")) {
                    missing.add(name + " -> parent " + model.get("parent").getAsString());
                }
                if (model.has("textures")) {
                    for (var entry : model.getAsJsonObject("textures").entrySet()) {
                        String texture = texturePath(entry.getValue());
                        if (texture != null && !texture.startsWith("#") && !exists("textures", texture, ".png")) {
                            missing.add(name + " -> " + entry.getKey() + " " + texture);
                        }
                    }
                }
            }
        }
        assertTrue(missing.isEmpty(), "Fehlende Modell-Referenzen:\n" + String.join("\n", missing));
    }

    private static String texturePath(JsonElement value) {
        if (value.isJsonPrimitive()) return value.getAsString();
        if (value.isJsonObject() && value.getAsJsonObject().has("sprite"))
            return value.getAsJsonObject().get("sprite").getAsString();
        return null;
    }

    // Sucht zuerst in den Mod-Ressourcen, dann im Vanilla-Client-Jar auf dem Classpath
    private static boolean exists(String kind, String id, String extension) throws IOException {
        String namespace = id.contains(":") ? id.substring(0, id.indexOf(':')) : "minecraft";
        String path = id.contains(":") ? id.substring(id.indexOf(':') + 1) : id;
        String relative = namespace + "/" + kind + "/" + path + extension;
        if (Files.exists(ASSETS.resolve(relative))) return true;
        try (InputStream in = ModelReferencesTest.class.getResourceAsStream("/assets/" + relative)) {
            return in != null;
        }
    }
}
