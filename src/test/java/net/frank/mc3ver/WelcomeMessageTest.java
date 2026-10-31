package net.frank.mc3ver;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.junit.jupiter.api.Test;

public class WelcomeMessageTest {

    @Test
    void testModNameAndVersion() {
        assertEquals("WorldExplorerMod", Mc3verMod.MOD_NAME);
        assertEquals(System.getProperty("mc3ver.version"), Mc3verMod.MOD_VERSION);
    }

    // Bug: Im Spiel teilen sich alle Mods einen Classloader; die erste fabric.mod.json war die von
    // fabric-dimensions-v1, die Welcome-Nachricht zeigte deshalb "v5.1.20+5af7744b5d"
    @Test
    void findModVersionPicksOurModAmongOthers() {
        String other = "{\"id\": \"fabric-dimensions-v1\", \"version\": \"5.1.20+5af7744b5d\"}";
        String ours = "{\"id\": \"mc3ver\", \"version\": \"0.8.2\"}";
        assertEquals("0.8.2", Mc3verMod.findModVersion(java.util.List.of(other, ours)));
    }

    @Test
    void findModVersionFailsWithoutOurMod() {
        String other = "{\"id\": \"fabric-dimensions-v1\", \"version\": \"5.1.20+5af7744b5d\"}";
        assertThrows(IllegalStateException.class, () -> Mc3verMod.findModVersion(java.util.List.of(other)));
    }

    @Test
    void testCreateWelcomeComponent_ReturnsTranslatableComponentWithKeyAndArgs() {
        Component component = WelcomeMessageHandler.createWelcomeComponent(Mc3verMod.MOD_NAME, Mc3verMod.MOD_VERSION);
        assertNotNull(component);
        assertInstanceOf(TranslatableContents.class, component.getContents());

        TranslatableContents contents = (TranslatableContents) component.getContents();
        assertEquals("message.mc3ver.welcome", contents.getKey());
        assertArrayEquals(new Object[] {"WorldExplorerMod", Mc3verMod.MOD_VERSION}, contents.getArgs());
    }
}
