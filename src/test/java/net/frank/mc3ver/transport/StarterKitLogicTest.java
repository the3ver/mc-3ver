package net.frank.mc3ver.transport;

import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StarterKitLogicTest {

    @Test
    void testStarterKitIsEmpty() {
        Map<String, Integer> items = StarterKitLogic.getStarterItems();
        assertTrue(items.isEmpty(), "Starter kit should be empty in release");
    }
}
