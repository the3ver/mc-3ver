package net.frank.mc3ver.village;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VillagerGenderLogicTest {

    @Test
    void evenLowestBitIsFemaleOddIsNot() {
        assertTrue(VillagerGenderLogic.isFemale(new UUID(7L, 2L)));
        assertFalse(VillagerGenderLogic.isFemale(new UUID(7L, 3L)));
    }

    @Test
    void sameUuidAlwaysGivesTheSameAnswer() {
        UUID uuid = UUID.fromString("3f2b8c1e-9d4a-4e7b-a1c2-5d6e7f8091a2");
        assertEquals(VillagerGenderLogic.isFemale(uuid), VillagerGenderLogic.isFemale(uuid));
    }

    @Test
    void aboutHalfOfRandomVillagersAreFemale() {
        Random random = new Random(42);
        int female = 0;
        int total = 10_000;
        for (int i = 0; i < total; i++) {
            UUID uuid = new UUID(random.nextLong(), random.nextLong());
            if (VillagerGenderLogic.isFemale(uuid)) female++;
        }
        assertTrue(female > total * 0.45 && female < total * 0.55, "Anteil weiblich: " + female + "/" + total);
    }

    @Test
    void lookUsesTheBitsAboveTheGenderBit() {
        assertEquals(0, VillagerGenderLogic.look(new UUID(7L, 0L), 3));
        assertEquals(1, VillagerGenderLogic.look(new UUID(7L, 2L), 3));
        assertEquals(2, VillagerGenderLogic.look(new UUID(7L, 4L), 3));
        assertEquals(0, VillagerGenderLogic.look(new UUID(7L, 6L), 3));
        assertEquals(1, VillagerGenderLogic.look(new UUID(7L, -2L), 3));
    }

    @Test
    void looksAreSpreadEvenlyAmongWomen() {
        Random random = new Random(7);
        int[] counts = new int[3];
        for (int i = 0; i < 9_000; i++) {
            UUID uuid = new UUID(random.nextLong(), random.nextLong() & ~1L);
            counts[VillagerGenderLogic.look(uuid, 3)]++;
        }
        for (int count : counts) assertTrue(count > 2_700 && count < 3_300, "Verteilung: " + count);
    }
}
