package net.frank.mc3ver.village;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BakerJobLogicTest {

    @Test
    void womenTakeTheBakingOven() {
        assertTrue(BakerJobLogic.canTakeJob(true, true));
    }

    @Test
    void menLeaveTheBakingOven() {
        assertFalse(BakerJobLogic.canTakeJob(false, true));
    }

    @Test
    void otherJobSitesStayOpenToEveryone() {
        assertTrue(BakerJobLogic.canTakeJob(false, false));
        assertTrue(BakerJobLogic.canTakeJob(true, false));
    }
}
