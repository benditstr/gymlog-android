package de.th.nuernberg.bme.gymlog.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SetValidatorTest {

    // ── Weight ────────────────────────────────────────────────

    @Test
    public void isWeightValid_nullAndBlank_returnsFalse() {
        assertFalse(SetValidator.isWeightValid(null));
        assertFalse(SetValidator.isWeightValid(""));
        assertFalse(SetValidator.isWeightValid("   "));
    }

    @Test
    public void isWeightValid_zeroAndNegative_returnsFalse() {
        assertFalse(SetValidator.isWeightValid("0"));
        assertFalse(SetValidator.isWeightValid("0.0"));
        assertFalse(SetValidator.isWeightValid("-5"));
    }

    @Test
    public void isWeightValid_validWeights_returnsTrue() {
        assertTrue(SetValidator.isWeightValid("80"));
        assertTrue(SetValidator.isWeightValid("80.5"));
        assertTrue(SetValidator.isWeightValid("0.5"));
        assertTrue(SetValidator.isWeightValid("  100  "));
        // KI-08: Komma-Dezimaltrennzeichen wird normalisiert (Komma→Punkt) → gültig
        assertTrue(SetValidator.isWeightValid("12,5"));
    }

    @Test
    public void isWeightValid_nonNumeric_returnsFalse() {
        assertFalse(SetValidator.isWeightValid("abc"));
    }

    // ── Reps ─────────────────────────────────────────────────

    @Test
    public void isRepsValid_nullAndBlank_returnsFalse() {
        assertFalse(SetValidator.isRepsValid(null));
        assertFalse(SetValidator.isRepsValid(""));
    }

    @Test
    public void isRepsValid_zeroAndNegative_returnsFalse() {
        assertFalse(SetValidator.isRepsValid("0"));
        assertFalse(SetValidator.isRepsValid("-3"));
    }

    @Test
    public void isRepsValid_validReps_returnsTrue() {
        assertTrue(SetValidator.isRepsValid("10"));
        assertTrue(SetValidator.isRepsValid("1"));
        assertTrue(SetValidator.isRepsValid("100"));
    }

    @Test
    public void isRepsValid_decimal_returnsFalse() {
        assertFalse(SetValidator.isRepsValid("10.5"));
    }
}