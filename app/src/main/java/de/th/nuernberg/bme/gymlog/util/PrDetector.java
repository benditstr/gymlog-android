package de.th.nuernberg.bme.gymlog.util;

/**
 * Erkennt persönliche Rekorde (PR) beim Eintragen eines Satzes.
 * Reine Funktion ohne Android-Abhängigkeiten — JVM-Unit-testbar.
 */
public final class PrDetector {

    private PrDetector() {}

    /**
     * @param newWeight   Gewicht des neuen Satzes
     * @param previousMax bisheriges Maximalgewicht der Übung; {@code null} = noch kein Eintrag
     * @return {@code true}, wenn es der erste Eintrag ist oder das Gewicht den bisherigen
     *         Rekord echt übersteigt
     */
    public static boolean isNewPr(float newWeight, Float previousMax) {
        return previousMax == null || newWeight > previousMax;
    }
}
