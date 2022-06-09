package org.optaplanner.examples.curriculumcourse.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Day")
public class Day extends AbstractPersistable {

    private static final String[] WEEKDAYS = { "Mo", "Tu", "We", "Th", "Fr", "Sat", "Sun" };

    private int dayIndex;

    private List<Period> periodList;

    public Day() {
    }

    public Day(int dayIndex, Period... periods) {
        super(dayIndex);
        this.dayIndex = dayIndex;
        this.periodList = Arrays.stream(periods)
                .collect(Collectors.toList());
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public List<Period> getPeriodList() {
        return periodList;
    }

    public void setPeriodList(List<Period> periodList) {
        this.periodList = periodList;
    }

    public String getLabel() {
        String weekday = WEEKDAYS[dayIndex % WEEKDAYS.length];
        if (dayIndex > WEEKDAYS.length) {
            return "Day " + dayIndex;
        }
        return weekday;
    }

    @Override
    public String toString() {
        return Integer.toString(dayIndex);
    }

}
