package net.frank.mc3ver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WelcomeMessageTest {

	@Test
	void testFormatWelcomeMessage_ContainsModNameAndVersion() {
		String message = WelcomeMessageHandler.getWelcomeText("MC 3ver", "0.1.0");
		assertNotNull(message);
		assertTrue(message.contains("MC 3ver"), "Nachricht sollte den Mod-Namen enthalten");
		assertTrue(message.contains("v0.1.0"), "Nachricht sollte die Versionsnummer enthalten");
		assertTrue(message.contains("Doppelsprung"), "Nachricht sollte Hinweis auf Doppelsprung enthalten");
	}
}
