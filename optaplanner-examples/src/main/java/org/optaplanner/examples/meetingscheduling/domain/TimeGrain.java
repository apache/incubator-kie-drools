package org.optaplanner.examples.meetingscheduling.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

public class TimeGrain extends AbstractPersistable
        implements Comparable<TimeGrain>, Labeled {

    private static final Comparator<TimeGrain> COMPARATOR = Comparator.comparing(TimeGrain::getDay)
            .thenComparingInt(TimeGrain::getStartingMinuteOfDay);

    /**
     * Time granularity is 15 minutes (which is often recommended when dealing with humans for practical purposes).
     */
    public static final int GRAIN_LENGTH_IN_MINUTES = 15;

    private int grainIndex;
    private Day day;
    private int startingMinuteOfDay;

    public TimeGrain() {
    }

    public TimeGrain(long id, int grainIndex, Day day, int startingMinuteOfDay) {
        super(id);
        this.grainIndex = grainIndex;
        this.day = day;
        this.startingMinuteOfDay = startingMinuteOfDay;
    }

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

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;

        TimeGrain timeGrain = (TimeGrain) other;

        if (startingMinuteOfDay != timeGrain.startingMinuteOfDay)
            return false;
        return Objects.equals(day, timeGrain.day);
    }

    @Override
    public int hashCode() {
        int result = day != null ? day.hashCode() : 0;
        result = 31 * result + startingMinuteOfDay;
        return result;
    }

    @Override
    public int compareTo(TimeGrain other) {
        return COMPARATOR.compare(this, other);
    }
}
