package de.th.nuernberg.bme.gymlog.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PrDetectorTest {

    @Test
    public void isNewPr_firstEntryNoPreviousMax_returnsTrue() {
        assertTrue(PrDetector.isNewPr(100f, null));
    }

    @Test
    public void isNewPr_heavierThanPrevious_returnsTrue() {
        assertTrue(PrDetector.isNewPr(100f, 90f));
    }

    @Test
    public void isNewPr_lighterThanPrevious_returnsFalse() {
        assertFalse(PrDetector.isNewPr(90f, 100f));
    }

    @Test
    public void isNewPr_equalToPrevious_returnsFalse() {
        assertFalse(PrDetector.isNewPr(100f, 100f));
    }
}
