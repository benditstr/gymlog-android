package de.th.nuernberg.bme.gymlog.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class StreakCalculatorTest {

    /** Mitternachts-Timestamp für ein festes Datum. */
    private long midnight(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private final long today = midnight(2026, Calendar.JUNE, 23);
    private final long oneDay = 24L * 60 * 60 * 1000;

    @Test
    public void calculateStreak_nullList_returnsZero() {
        assertEquals(0, StreakCalculator.calculateStreak(null, today));
    }

    @Test
    public void calculateStreak_emptyList_returnsZero() {
        assertEquals(0, StreakCalculator.calculateStreak(new ArrayList<>(), today));
    }

    @Test
    public void calculateStreak_threeConsecutiveDaysIncludingToday_returnsThree() {
        List<Long> dates = Arrays.asList(today, today - oneDay, today - 2 * oneDay);
        assertEquals(3, StreakCalculator.calculateStreak(dates, today));
    }

    @Test
    public void calculateStreak_gapBreaksStreak() {
        // heute vorhanden, vorgestern vorhanden, gestern fehlt → nur heute zählt
        List<Long> dates = Arrays.asList(today, today - 2 * oneDay);
        assertEquals(1, StreakCalculator.calculateStreak(dates, today));
    }

    @Test
    public void calculateStreak_todayMissingButYesterdayPresent_countsFromYesterday() {
        List<Long> dates = Arrays.asList(today - oneDay, today - 2 * oneDay);
        assertEquals(2, StreakCalculator.calculateStreak(dates, today));
    }

    @Test
    public void calculateStreak_todayAndYesterdayMissing_returnsZero() {
        List<Long> dates = Arrays.asList(today - 3 * oneDay);
        assertEquals(0, StreakCalculator.calculateStreak(dates, today));
    }

    @Test
    public void calculateStreak_onlyToday_returnsOne() {
        List<Long> dates = Arrays.asList(today);
        assertEquals(1, StreakCalculator.calculateStreak(dates, today));
    }

    @Test
    public void calculateStreak_duplicateDates_areDeduplicated() {
        List<Long> dates = Arrays.asList(today, today, today - oneDay);
        assertEquals(2, StreakCalculator.calculateStreak(dates, today));
    }

    @Test
    public void calculateStreak_unsortedInput_stillCounts() {
        List<Long> dates = Arrays.asList(today - 2 * oneDay, today, today - oneDay);
        assertEquals(3, StreakCalculator.calculateStreak(dates, today));
    }
}
