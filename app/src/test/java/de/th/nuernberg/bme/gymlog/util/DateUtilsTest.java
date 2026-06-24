package de.th.nuernberg.bme.gymlog.util;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateUtilsTest {

    @Test
    public void normalizeToMidnight_setsTimeComponentsToZero() {
        Calendar input = Calendar.getInstance();
        input.set(2024, Calendar.MARCH, 15, 14, 30, 45);
        input.set(Calendar.MILLISECOND, 500);

        long normalized = DateUtils.normalizeToMidnight(input.getTimeInMillis());

        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(normalized);
        assertEquals(0, result.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, result.get(Calendar.MINUTE));
        assertEquals(0, result.get(Calendar.SECOND));
        assertEquals(0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void normalizeToMidnight_preservesYearMonthDay() {
        Calendar input = Calendar.getInstance();
        input.set(2024, Calendar.JUNE, 20, 23, 59, 59);

        long normalized = DateUtils.normalizeToMidnight(input.getTimeInMillis());

        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(normalized);
        assertEquals(2024, result.get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, result.get(Calendar.MONTH));
        assertEquals(20, result.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void normalizeToMidnight_sameDayDifferentTimes_produceSameTimestamp() {
        Calendar morning = Calendar.getInstance();
        morning.set(2024, Calendar.JANUARY, 10, 7, 0, 0);
        morning.set(Calendar.MILLISECOND, 0);

        Calendar evening = Calendar.getInstance();
        evening.set(2024, Calendar.JANUARY, 10, 22, 30, 0);
        evening.set(Calendar.MILLISECOND, 0);

        assertEquals(
            DateUtils.normalizeToMidnight(morning.getTimeInMillis()),
            DateUtils.normalizeToMidnight(evening.getTimeInMillis())
        );
    }

    @Test
    public void tomorrowMidnight_isExactlyOneDayAfterTodayMidnight()

    {
        long today = DateUtils.todayMidnight();
        long tomorrow = DateUtils.tomorrowMidnight();
        assertEquals(24L * 60 * 60 * 1000, tomorrow - today);
    }

    @Test
    public void todayMidnight_isInThePast() {
        assertTrue(DateUtils.todayMidnight() <= System.currentTimeMillis());
    }
}