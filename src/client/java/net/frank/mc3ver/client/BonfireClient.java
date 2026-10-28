package net.frank.mc3ver.client;

import net.frank.mc3ver.bonfire.BonfireBlock;
import net.frank.mc3ver.bonfire.BonfireLogic;
import net.frank.mc3ver.bonfire.ModBonfire;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

final class BonfireClient {

    private BonfireClient() {}

    static void register() {
        BlockEntityRenderers.register(ModBonfire.BONFIRE_BLOCK_ENTITY, context -> new BonfireRenderer());

        // Rauchpartikel des Signalfeuers in der Farbe des Rauchs einfärben
        BonfireBlock.smokeSpawner = (level, type, x, y, z, xd, yd, zd, color) -> {
            Particle particle = Minecraft.getInstance().particleEngine.createParticle(type, x, y, z, xd, yd, zd);
            if (particle instanceof SingleQuadParticle quad && color != BonfireLogic.SmokeColor.NONE) {
                int rgb = color.smokeRgb();
                quad.setColor(((rgb >> 16) & 0xFF) / 255.0f, ((rgb >> 8) & 0xFF) / 255.0f, (rgb & 0xFF) / 255.0f);
            }
        };
    }
}
