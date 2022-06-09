package org.optaplanner.examples.nurserostering.domain;

import java.time.DayOfWeek;
import java.util.EnumSet;

public enum WeekendDefinition {
    SATURDAY_SUNDAY("SaturdaySunday",
            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
    FRIDAY_SATURDAY_SUNDAY("FridaySaturdaySunday",
            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
    FRIDAY_SATURDAY_SUNDAY_MONDAY("FridaySaturdaySundayMonday",
            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY, DayOfWeek.MONDAY),
    SATURDAY_SUNDAY_MONDAY("SaturdaySundayMonday",
            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY, DayOfWeek.MONDAY);

    private EnumSet<DayOfWeek> dayOfWeekSet;
    private DayOfWeek firstDayOfWeekend;
    private DayOfWeek lastDayOfWeekend;

    public static WeekendDefinition valueOfCode(String code) {
        for (WeekendDefinition weekendDefinition : values()) {
            if (code.equalsIgnoreCase(weekendDefinition.getCode())) {
                return weekendDefinition;
            }
        }
        return null;
    }

    private String code;

    private WeekendDefinition(String code, DayOfWeek dayOfWeekend1, DayOfWeek dayOfWeekend2) {
        this.code = code;
        this.dayOfWeekSet = EnumSet.of(dayOfWeekend1, dayOfWeekend2);
        this.firstDayOfWeekend = dayOfWeekend1;
        this.lastDayOfWeekend = dayOfWeekend2;
    }

    private WeekendDefinition(String code, DayOfWeek dayOfWeekend1, DayOfWeek dayOfWeekend2, DayOfWeek dayOfWeekend3) {
        this.code = code;
        this.dayOfWeekSet = EnumSet.of(dayOfWeekend1, dayOfWeekend2, dayOfWeekend3);
        this.firstDayOfWeekend = dayOfWeekend1;
        this.lastDayOfWeekend = dayOfWeekend3;
    }

    private WeekendDefinition(String code, DayOfWeek dayOfWeekend1, DayOfWeek dayOfWeekend2, DayOfWeek dayOfWeekend3,
            DayOfWeek dayOfWeekend4) {
        this.code = code;
        this.dayOfWeekSet = EnumSet.of(dayOfWeekend1, dayOfWeekend2, dayOfWeekend3, dayOfWeekend4);
        this.firstDayOfWeekend = dayOfWeekend1;
        this.lastDayOfWeekend = dayOfWeekend4;
    }

    public String getCode() {
        return code;
    }

    public DayOfWeek getFirstDayOfWeekend() {
        return firstDayOfWeekend;
    }

    public DayOfWeek getLastDayOfWeekend() {
        return lastDayOfWeekend;
    }

    @Override
    public String toString() {
        return code;
    }

    public boolean isWeekend(DayOfWeek dayOfWeek) {
        return dayOfWeekSet.contains(dayOfWeek);
    }

    public int getWeekendLength() {
        return dayOfWeekSet.size();
    }

}
