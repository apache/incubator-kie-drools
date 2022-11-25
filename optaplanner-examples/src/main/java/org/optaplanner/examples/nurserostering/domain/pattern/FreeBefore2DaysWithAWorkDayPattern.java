package org.optaplanner.examples.nurserostering.domain.pattern;

import java.time.DayOfWeek;

public class FreeBefore2DaysWithAWorkDayPattern extends Pattern {

    private DayOfWeek freeDayOfWeek;

    public FreeBefore2DaysWithAWorkDayPattern() {
    }

    public FreeBefore2DaysWithAWorkDayPattern(long id, String code) {
        super(id, code);
    }

    public FreeBefore2DaysWithAWorkDayPattern(long id, String code, DayOfWeek freeDayOfWeek) {
        this(id, code);
        this.freeDayOfWeek = freeDayOfWeek;
    }

    public DayOfWeek getFreeDayOfWeek() {
        return freeDayOfWeek;
    }

    public void setFreeDayOfWeek(DayOfWeek freeDayOfWeek) {
        this.freeDayOfWeek = freeDayOfWeek;
    }

    @Override
    public String toString() {
        return "Free on " + freeDayOfWeek + " followed by a work day within 2 days";
    }

}
