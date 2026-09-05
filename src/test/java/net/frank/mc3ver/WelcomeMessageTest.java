package net.frank.mc3ver;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WelcomeMessageTest {

	@Test
	void testModNameAndVersion() {
		assertEquals("WorldExplorerMod", Mc3verMod.MOD_NAME);
		assertEquals("0.4.0", Mc3verMod.MOD_VERSION);
	}

	@Test
	void testCreateWelcomeComponent_ReturnsTranslatableComponentWithKeyAndArgs() {
		Component component = WelcomeMessageHandler.createWelcomeComponent(Mc3verMod.MOD_NAME, Mc3verMod.MOD_VERSION);
		assertNotNull(component);
		assertInstanceOf(TranslatableContents.class, component.getContents());
		
		TranslatableContents contents = (TranslatableContents) component.getContents();
		assertEquals("message.mc3ver.welcome", contents.getKey());
		assertArrayEquals(new Object[]{"WorldExplorerMod", "0.4.0"}, contents.getArgs());
	}
}
