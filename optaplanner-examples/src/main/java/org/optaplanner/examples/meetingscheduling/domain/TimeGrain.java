package org.optaplanner.examples.meetingscheduling.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

public class TimeGrain extends AbstractPersistable implements Labeled {

    /**
     * Time granularity is 15 minutes (which is often recommended when dealing with humans for practical purposes).
     */
    public static final int GRAIN_LENGTH_IN_MINUTES = 15;

    private int grainIndex; // unique

    private Day day;
    private int startingMinuteOfDay;

    public int getGrainIndex() {
        return grainIndex;
    }

    public void setGrainIndex(int grainIndex) {
        this.grainIndex = grainIndex;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public int getStartingMinuteOfDay() {
        return startingMinuteOfDay;
    }

    public void setStartingMinuteOfDay(int startingMinuteOfDay) {
        this.startingMinuteOfDay = startingMinuteOfDay;
    }

    public LocalDate getDate() {
        return day.toDate();
    }

    public LocalTime getTime() {
        return LocalTime.of(startingMinuteOfDay / 60, startingMinuteOfDay % 60);
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(getDate(), getTime());
    }

    public String getTimeString() {
        int hourOfDay = startingMinuteOfDay / 60;
        int minuteOfHour = startingMinuteOfDay % 60;
        return (hourOfDay < 10 ? "0" : "") + hourOfDay
                + ":" + (minuteOfHour < 10 ? "0" : "") + minuteOfHour;
    }

    public String getDateTimeString() {
        return day.getDateString() + " " + getTimeString();
    }

    @Override
    public String getLabel() {
        return getTimeString();
    }

    @Override
    public String toString() {
        return grainIndex + "(" + getDateTimeString() + ")";
    }
}
