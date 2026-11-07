package net.frank.mc3ver.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.frank.mc3ver.Mc3verMod;
import net.frank.mc3ver.sonic.ModSonicSword;
import net.frank.mc3ver.sonic.SonicSwordLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;

// Linksklick halten lädt den Schallstrahl, Loslassen feuert. Der Balken unter dem Fadenkreuz zeigt
// die Ladung von Strahl (Linksklick) und Welle (Rechtsklick).
final class SonicSwordClient {

    private static final int BAR_WIDTH = 32;
    private static final int BAR_HEIGHT = 3;
    private static final int BAR_OFFSET_Y = 10;
    private static final int COLOR_BACK = 0xC0200828;
    private static final int COLOR_CHARGING = 0xFF8A2BE2;
    private static final int COLOR_READY = 0xFFFF4FB8;
    private static final int COLOR_FULL = 0xFFFFA0E0;

    private static boolean beamCharging = false;
    private static int beamTicks = 0;

    private SonicSwordClient() {}

    static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(SonicSwordClient::tick);
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CROSSHAIR,
                Mc3verMod.id("sonic_sword_charge"),
                (graphics, delta) -> drawBar(graphics));
    }

    private static void tick(Minecraft client) {
        LocalPlayer player = client.player;
        boolean holding = player != null
                && client.gui.screen() == null
                && ModSonicSword.holdsSonicSword(player)
                && client.options.keyAttack.isDown()
                && !player.isUsingItem();

        if (holding && !beamCharging) {
            if (!SonicSwordLogic.canCharge(
                    player.getCooldowns().isOnCooldown(player.getMainHandItem()), player.isSpectator())) {
                return;
            }
            beamCharging = true;
            beamTicks = 0;
            ClientPlayNetworking.send(new ModSonicSword.BeamChargePayload(true));
        } else if (holding) {
            beamTicks++;
        } else if (beamCharging) {
            beamCharging = false;
            beamTicks = 0;
            ClientPlayNetworking.send(new ModSonicSword.BeamChargePayload(false));
        }
    }

    private static void drawBar(GuiGraphicsExtractor graphics) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !ModSonicSword.holdsSonicSword(player)) return;

        int ticks;
        int fullTicks;
        if (beamCharging) {
            ticks = beamTicks;
            fullTicks = SonicSwordLogic.BEAM_FULL_CHARGE_TICKS;
        } else if (player.isUsingItem() && player.getUseItem().is(ModSonicSword.SONIC_SWORD)) {
            ticks = player.getTicksUsingItem();
            fullTicks = SonicSwordLogic.WAVE_FULL_CHARGE_TICKS;
        } else {
            return;
        }

        int x = (graphics.guiWidth() - BAR_WIDTH) / 2;
        int y = graphics.guiHeight() / 2 + BAR_OFFSET_Y;
        float progress = Math.min(1.0f, (float) ticks / fullTicks);
        int filled = Math.round(BAR_WIDTH * progress);
        int color = progress >= 1.0f ? COLOR_FULL : SonicSwordLogic.fires(ticks) ? COLOR_READY : COLOR_CHARGING;

        graphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, COLOR_BACK);
        graphics.fill(x, y, x + filled, y + BAR_HEIGHT, color);
    }
}
