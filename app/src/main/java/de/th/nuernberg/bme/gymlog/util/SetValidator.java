package de.th.nuernberg.bme.gymlog.util;

public class SetValidator {

    private SetValidator() {}

    public static boolean isWeightValid(String weightStr) {
        if (weightStr == null || weightStr.trim().isEmpty()) return false;
        try {
            float weight = Float.parseFloat(weightStr.trim().replace(',', '.'));
            return weight > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isRepsValid(String repsStr) {
        if (repsStr == null || repsStr.trim().isEmpty()) return false;
        try {
            int reps = Integer.parseInt(repsStr.trim());
            return reps > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}