package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;

// Haar und Zopf hängen am Kopf der Bewohnerin. Gerendert wird verzögert: ModelFeatureRenderer ruft erst beim Zeichnen
// setupAnim(state), das die Pose zurücksetzt. Der Kopf muss deshalb allein aus dem State dieselbe Pose bekommen wie
// der Vanilla-Kopf. Liegt im Paket client, weil VillagerWomanModel paketprivat ist.
public class VillagerWomanModelClientGameTest implements FabricClientGameTest {

    private static final float EPSILON = 1.0e-5f;

    @Override
    public void runTest(ClientGameTestContext context) {
        context.runOnClient(client -> {
            VillagerModel vanilla = new VillagerModel(client.getEntityModels().bakeLayer(ModelLayers.VILLAGER));
            for (VillagerWomanLook look : VillagerWomanLook.ALL) {
                VillagerWomanModel woman =
                        new VillagerWomanModel(client.getEntityModels().bakeLayer(look.layer()));
                checkLook(look.name(), vanilla, woman);
            }
        });
    }

    private static void checkLook(String look, VillagerModel vanilla, VillagerWomanModel woman) {
        {
            VillagerRenderState state = new VillagerRenderState();
            state.yRot = 50.0f;
            state.xRot = -20.0f;
            assertSameHead(look + ": gedrehter Kopf", vanilla, woman, state);

            // Kopfschütteln beim abgelehnten Handel
            state.isUnhappy = true;
            state.ageInTicks = 3.0f;
            assertSameHead(look + ": Kopfschütteln", vanilla, woman, state);
        }
    }

    private static void assertSameHead(
            String label, VillagerModel vanilla, VillagerWomanModel woman, VillagerRenderState state) {
        vanilla.setupAnim(state);
        woman.setupAnim(state);
        ModelPart expected = vanilla.getHead();
        ModelPart actual = woman.root().getChild("head");
        check(label, "xRot", expected.xRot, actual.xRot);
        check(label, "yRot", expected.yRot, actual.yRot);
        check(label, "zRot", expected.zRot, actual.zRot);
    }

    private static void check(String label, String axis, float expected, float actual) {
        if (Math.abs(expected - actual) > EPSILON) {
            throw new AssertionError(label + ": " + axis + " erwartet " + expected + ", war " + actual);
        }
    }
}
