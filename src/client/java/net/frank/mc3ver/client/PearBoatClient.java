package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.frank.mc3ver.Mc3verMod;
import net.frank.mc3ver.tree.PearTreeEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;

final class PearBoatClient {

    static final ModelLayerLocation PEAR_BOAT_LAYER = new ModelLayerLocation(Mc3verMod.id("boat/pear"), "main");
    static final ModelLayerLocation PEAR_CHEST_BOAT_LAYER = new ModelLayerLocation(Mc3verMod.id("chest_boat/pear"), "main");

    private PearBoatClient() {}

    static void register() {
        ModelLayerRegistry.registerModelLayer(PEAR_BOAT_LAYER, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(PEAR_CHEST_BOAT_LAYER, BoatModel::createChestBoatModel);
        EntityRendererRegistry.register(PearTreeEntities.PEAR_BOAT_ENTITY_TYPE, context -> new BoatRenderer(context, PEAR_BOAT_LAYER));
        EntityRendererRegistry.register(PearTreeEntities.PEAR_CHEST_BOAT_ENTITY_TYPE, context -> new BoatRenderer(context, PEAR_CHEST_BOAT_LAYER));
    }
}
