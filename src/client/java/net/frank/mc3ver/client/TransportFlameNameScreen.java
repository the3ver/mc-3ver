package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frank.mc3ver.transport.TransportFlameLogic;
import net.frank.mc3ver.transport.TransportFlameNaming;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

// Eingabe-Screen beim Aufstellen einer Transportflamme. Abbrechen oder Esc lässt die Flamme unbenannt,
// ein Rechtsklick auf die Flamme öffnet den Screen dann erneut.
public class TransportFlameNameScreen extends Screen {

    private final BlockPos pos;
    private EditBox nameBox;
    private Button doneButton;

    public TransportFlameNameScreen(BlockPos pos) {
        super(Component.translatable("screen.mc3ver.flame_name.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        nameBox = new EditBox(font, centerX - 100, centerY - 10, 200, 20, Component.translatable("screen.mc3ver.flame_name.title"));
        nameBox.setMaxLength(TransportFlameLogic.MAX_FLAME_NAME_LENGTH);
        nameBox.setHint(Component.translatable("screen.mc3ver.flame_name.hint"));
        nameBox.setResponder(value -> updateDoneButton());
        addRenderableWidget(nameBox);

        doneButton = addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> submit())
            .bounds(centerX - 100, centerY + 20, 98, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
            .bounds(centerX + 2, centerY + 20, 98, 20).build());

        setInitialFocus(nameBox);
        updateDoneButton();
    }

    private void updateDoneButton() {
        if (doneButton != null) {
            doneButton.active = TransportFlameLogic.sanitizeFlameName(nameBox.getValue()) != null;
        }
    }

    private void submit() {
        String name = TransportFlameLogic.sanitizeFlameName(nameBox.getValue());
        if (name == null) return;
        ClientPlayNetworking.send(new TransportFlameNaming.SetNamePayload(pos, name));
        onClose();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isConfirmation() && nameBox.isFocused()) {
            submit();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, title, width / 2, height / 2 - 40, 0xFFFFFFFF);
        graphics.centeredText(font, Component.translatable("screen.mc3ver.flame_name.info"), width / 2, height / 2 - 26, 0xFFA0A0A0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
