package net.frank.mc3ver.teleport;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

// Dimensionen werden in Items und Blockdaten als Text gespeichert, z. B. "minecraft:the_nether"
public final class Dimensions {

    private Dimensions() {}

    public static String id(Level level) {
        return level.dimension().identifier().toString();
    }

    public static String translationKey(String dimensionId) {
        return "dimension.mc3ver." + Identifier.parse(dimensionId).getPath();
    }

    // Übersetzter Name; Dimensionen anderer Mods ohne Übersetzung zeigen ihre ID
    public static Component name(String dimensionId) {
        return Component.translatableWithFallback(translationKey(dimensionId), dimensionId);
    }

    // null, wenn es die Dimension nicht (mehr) gibt
    public static ServerLevel resolve(MinecraftServer server, String dimensionId) {
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse(dimensionId)));
    }
}
