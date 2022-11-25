package org.optaplanner.examples.examination.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Period extends AbstractPersistable implements Labeled {

    private String startDateTimeString;
    private int periodIndex;
    private int dayIndex;
    private int duration; // in minutes
    private int penalty;
    private boolean frontLoadLast;

    public String getStartDateTimeString() {
        return startDateTimeString;
    }

    public void setStartDateTimeString(String startDateTimeString) {
        this.startDateTimeString = startDateTimeString;
    }

    public int getPeriodIndex() {
        return periodIndex;
    }

    public void setPeriodIndex(int periodIndex) {
        this.periodIndex = periodIndex;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public boolean isFrontLoadLast() {
        return frontLoadLast;
    }

    public void setFrontLoadLast(boolean frontLoadLast) {
        this.frontLoadLast = frontLoadLast;
    }

    @Override
    public String getLabel() {
        return startDateTimeString;
    }

    @Override
    public String toString() {
        return startDateTimeString;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public Period withId(long id) {
        this.setId(id);
        return this;
    }

    public Period withStartDateTimeString(String startDateTimeString) {
        this.setStartDateTimeString(startDateTimeString);
        return this;
    }

    public Period withPeriodIndex(int periodIndex) {
        this.setPeriodIndex(periodIndex);
        return this;
    }

    public Period withDayIndex(int dayIndex) {
        this.setDayIndex(dayIndex);
        return this;
    }

    public Period withDuration(int duration) {
        this.setDuration(duration);
        return this;
    }

    public Period withPenalty(int penalty) {
        this.setPenalty(penalty);
        return this;
    }

    public Period withFrontLoadLast(boolean frontLoadLast) {
        this.setFrontLoadLast(frontLoadLast);
        return this;
    }
}
