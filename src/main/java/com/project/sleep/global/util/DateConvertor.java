package com.project.sleep.global.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class DateConvertor {

    private static LocalDate weekStart(LocalDate date, DayOfWeek firstDayOfWeek) {
        return date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
    }

    private static LocalDate weekEndExclusive(LocalDate date) {
        return weekStart(date, DayOfWeek.SATURDAY).plusWeeks(1);
    }

    public static LocalDate weekStart(LocalDate date) {
        return weekStart(date, DayOfWeek.SUNDAY);
    }

    public static LocalDate weekEnd(LocalDate date) {
        return weekEndExclusive(date);
    }

    public static LocalDate monthStart(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate monthEndInclusive(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }
}
