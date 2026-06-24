package de.th.nuernberg.bme.gymlog.util;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Berechnet die aktuelle Trainings-Streak (aufeinanderfolgende Tage mit mindestens
 * einem Satz). Reine Funktion ohne Android-Abhängigkeiten — JVM-Unit-testbar.
 *
 * <p>Die Streak zählt auch dann, wenn heute noch kein Eintrag existiert, aber
 * gestern lückenlos trainiert wurde (der Tag ist ja noch nicht vorbei).
 */
public final class StreakCalculator {

    private static final long ONE_DAY_MS = 24L * 60 * 60 * 1000;

    private StreakCalculator() {}

    /**
     * @param datesDescending Liste von Datums-Timestamps (Unix-ms), beliebig sortiert;
     *                        werden intern auf Mitternacht normalisiert und dedupliziert
     * @param today           aktueller Zeitpunkt (Unix-ms)
     * @return Anzahl aufeinanderfolgender Trainingstage bis heute/gestern; 0 wenn keine
     */
    public static int calculateStreak(List<Long> datesDescending, long today) {
        if (datesDescending == null || datesDescending.isEmpty()) {
            return 0;
        }

        Set<Long> days = new HashSet<>();
        for (Long d : datesDescending) {
            if (d != null) {
                days.add(DateUtils.normalizeToMidnight(d));
            }
        }

        long todayMid = DateUtils.normalizeToMidnight(today);
        long yesterdayMid = minusOneDay(todayMid);

        long cursor;
        if (days.contains(todayMid)) {
            cursor = todayMid;
        } else if (days.contains(yesterdayMid)) {
            cursor = yesterdayMid;
        } else {
            return 0;
        }

        int streak = 0;
        while (days.contains(cursor)) {
            streak++;
            cursor = minusOneDay(cursor);
        }
        return streak;
    }

    /** DST-sicheres Zurückgehen um einen Kalendertag (auf Mitternacht normalisiert). */
    private static long minusOneDay(long midnight) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(midnight - ONE_DAY_MS);
        return DateUtils.normalizeToMidnight(cal.getTimeInMillis());
    }
}
