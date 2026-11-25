package net.frank.mc3ver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

/** Liest Ressourcen der Mod aus src/main/resources für Ressourcentests. */
public final class TestResources {

    public static final Path RESOURCES = Paths.get("").toAbsolutePath().resolve("src/main/resources");

    private TestResources() {}

    public static JsonObject json(String path) throws IOException {
        return JsonParser.parseString(Files.readString(RESOURCES.resolve(path))).getAsJsonObject();
    }

    public static BufferedImage image(String path) throws IOException {
        return ImageIO.read(RESOURCES.resolve(path).toFile());
    }
}
