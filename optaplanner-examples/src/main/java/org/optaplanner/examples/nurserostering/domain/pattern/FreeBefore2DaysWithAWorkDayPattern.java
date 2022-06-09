package org.optaplanner.examples.nurserostering.domain.pattern;

import java.time.DayOfWeek;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("FreeBefore2DaysWithAWorkDayPattern")
public class FreeBefore2DaysWithAWorkDayPattern extends Pattern {

    private DayOfWeek freeDayOfWeek;

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
