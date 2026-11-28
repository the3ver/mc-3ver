package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;

// Erwachsene Bewohnerinnen zeichnen die Figur mit Taille statt der geraden Quader, Bewohner und Babys nicht.
// Ein Modell zeichnet alle Dorfbewohner nacheinander, das Umschalten muss deshalb in beide Richtungen greifen.
public class VillagerWomanFigureClientGameTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        context.runOnClient(client -> {
            for (ModelLayerLocation layer :
                    new ModelLayerLocation[] {ModelLayers.VILLAGER, ModelLayers.VILLAGER_NO_HAT}) {
                VillagerModel model = new VillagerModel(client.getEntityModels().bakeLayer(layer));
                ModelPart body = model.root().getChild("body");
                ModelPart jacket = body.getChild("jacket");

                model.setupAnim(state(true, false));
                check(layer + ": Bewohnerin", body, jacket, true);
                model.setupAnim(state(false, false));
                check(layer + ": Bewohner", body, jacket, false);
                model.setupAnim(state(true, true));
                check(layer + ": Baby", body, jacket, false);
            }
        });
    }

    private static VillagerRenderState state(boolean female, boolean baby) {
        VillagerRenderState state = new VillagerRenderState();
        state.setData(VillagerWomanClient.FEMALE, female);
        state.isBaby = baby;
        return state;
    }

    private static void check(String label, ModelPart body, ModelPart jacket, boolean figure) {
        boolean ok = body.skipDraw == figure
                && jacket.skipDraw == figure
                && body.getChild(VillagerWomanFigure.FIGURE).visible == figure
                && jacket.getChild(VillagerWomanFigure.FIGURE).visible == figure;
        if (!ok) throw new AssertionError(label + ": Figur erwartet " + figure);
    }
}
