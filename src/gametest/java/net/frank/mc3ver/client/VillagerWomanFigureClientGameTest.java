package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.util.RandomSource;

// Erwachsene Bewohnerinnen zeichnen Figur (Taille, Brust, kurze Nase) und Hände in den Hüften, Bewohner und Babys
// nicht. Hält sie Ware, bleiben die Arme verschränkt.
// Ein Modell zeichnet alle Dorfbewohner nacheinander, das Umschalten muss deshalb in beide Richtungen greifen.
public class VillagerWomanFigureClientGameTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        context.runOnClient(client -> {
            for (ModelLayerLocation layer :
                    new ModelLayerLocation[] {ModelLayers.VILLAGER, ModelLayers.VILLAGER_NO_HAT}) {
                VillagerModel model = new VillagerModel(client.getEntityModels().bakeLayer(layer));
                ModelPart root = model.root();

                model.setupAnim(state(true, false, false));
                check(layer + ": Bewohnerin", root, true, true);
                model.setupAnim(state(false, false, false));
                check(layer + ": Bewohner", root, false, false);
                model.setupAnim(state(true, true, false));
                check(layer + ": Baby", root, false, false);
                // Mit Ware in der Hand: Figur bleibt, Arme verschränkt
                model.setupAnim(state(true, false, true));
                check(layer + ": Bewohnerin mit Ware", root, true, false);
                model.setupAnim(state(false, false, true));
                check(layer + ": Bewohner mit Ware", root, false, false);
                checkBustHeight(layer + ": Brust", root);
            }
        });
    }

    // Oberkante der Brust bei y 2.5: tiefer wirkt sie wie ein Schild um den Hals
    private static void checkBustHeight(String label, ModelPart root) {
        ModelPart bust = root.getChild("body").getChild("jacket").getChild(VillagerWomanFigure.BUST);
        // Die Cube-Liste ist privat; getRandomCube erreicht bei 64 Zügen sicher beide Stufen
        RandomSource random = RandomSource.create(1L);
        float top = Float.MAX_VALUE;
        for (int i = 0; i < 64; i++) {
            top = Math.min(top, bust.getRandomCube(random).minY);
        }
        if (top != 2.5f) throw new AssertionError(label + ": Oberkante erwartet 2.5, war " + top);
    }

    private static VillagerRenderState state(boolean female, boolean baby, boolean holding) {
        VillagerRenderState state = new VillagerRenderState();
        state.setData(VillagerWomanClient.FEMALE, female);
        state.isBaby = baby;
        if (holding) state.heldItem.newLayer();
        return state;
    }

    private static void check(String label, ModelPart root, boolean figure, boolean hipArms) {
        ModelPart body = root.getChild("body");
        ModelPart jacket = body.getChild("jacket");
        ModelPart head = root.getChild("head");
        boolean figureOk = body.skipDraw == figure
                && jacket.skipDraw == figure
                && body.getChild(VillagerWomanFigure.FIGURE).visible == figure
                && jacket.getChild(VillagerWomanFigure.FIGURE).visible == figure
                && jacket.getChild(VillagerWomanFigure.BUST).visible == figure
                && head.getChild("nose").visible != figure
                && head.getChild(VillagerWomanFigure.NOSE).visible == figure;
        if (!figureOk) throw new AssertionError(label + ": Figur erwartet " + figure);
        boolean armsOk = root.getChild("arms").visible != hipArms
                && root.getChild(VillagerWomanFigure.HIP_ARMS).visible == hipArms;
        if (!armsOk) throw new AssertionError(label + ": Hände in den Hüften erwartet " + hipArms);
    }
}
