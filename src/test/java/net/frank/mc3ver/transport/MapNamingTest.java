package net.frank.mc3ver.transport;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;

public class MapNamingTest {

    private static final String OVERWORLD = "minecraft:overworld";

    // Ersetzt eine Karte im Inventar oder am Boden; merkt sich nur den gesetzten Namen
    private static final class FakeMap implements TransportMapInventory.MapItemRef {
        private final TransportFlameLogic.FlameTarget target;
        String name;

        FakeMap(TransportFlameLogic.FlameTarget target) {
            this.target = target;
        }

        @Override
        public TransportFlameLogic.FlameTarget getTarget() {
            return target;
        }

        @Override
        public void modify(Consumer<ItemStack> change) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void rename(String newName) {
            name = newName;
        }
    }

    private static TransportFlameLogic.FlameTarget at(int x, String dimension) {
        return new TransportFlameLogic.FlameTarget(x, 64, 0, dimension, 0xF9FFFE, UUID.randomUUID());
    }

    @Test
    void namesEveryMapPointingAtTheFlame() {
        FakeMap carried = new FakeMap(at(5, OVERWORLD));
        FakeMap dropped = new FakeMap(at(5, OVERWORLD));
        TransportMapInventory.nameMapsFor(List.of(carried, dropped), 5, 64, 0, OVERWORLD, "Schlucht");
        assertEquals("Schlucht", carried.name);
        assertEquals("Schlucht", dropped.name);
    }

    @Test
    void leavesMapsOfOtherFlamesAndBlankMapsAlone() {
        FakeMap otherPos = new FakeMap(at(6, OVERWORLD));
        FakeMap otherDimension = new FakeMap(at(5, "minecraft:the_nether"));
        FakeMap blank = new FakeMap(null);
        TransportMapInventory.nameMapsFor(List.of(otherPos, otherDimension, blank), 5, 64, 0, OVERWORLD, "Schlucht");
        assertNull(otherPos.name);
        assertNull(otherDimension.name);
        assertNull(blank.name);
    }
}
