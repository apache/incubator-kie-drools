package org.optaplanner.examples.nurserostering.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ShiftDate")
public class ShiftDate extends AbstractPersistable implements Comparable<ShiftDate> {

    private static final DateTimeFormatter LABEL_FORMATTER = DateTimeFormatter.ofPattern("E d MMM");

    private int dayIndex; // TODO check if still needed/faster now that we use LocalDate instead of java.util.Date
    private LocalDate date;

    private List<Shift> shiftList;

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    public List<Shift> getShiftList() {
        return shiftList;
    }

    public void setShiftList(List<Shift> shiftList) {
        this.shiftList = shiftList;
    }

    public int getWeekendSundayIndex() {
        switch (date.getDayOfWeek()) {
            case MONDAY:
                return dayIndex - 1;
            case TUESDAY:
                return dayIndex - 2;
            case WEDNESDAY:
                return dayIndex - 3;
            case THURSDAY:
                return dayIndex + 3;
            case FRIDAY:
                return dayIndex + 2;
            case SATURDAY:
                return dayIndex + 1;
            case SUNDAY:
                return dayIndex;
            default:
                throw new IllegalArgumentException("The dayOfWeek (" + date.getDayOfWeek() + ") is not valid.");
        }
    }

    public String getLabel() {
        return date.format(LABEL_FORMATTER);
    }

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ISO_DATE);
    }

    @Override
    public int compareTo(ShiftDate o) {
        return this.getDate().compareTo(o.getDate());
    }
}
