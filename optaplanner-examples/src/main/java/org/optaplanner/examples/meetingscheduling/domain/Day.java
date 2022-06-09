package org.optaplanner.examples.meetingscheduling.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

public class Day extends AbstractPersistable implements Labeled {

    private int dayOfYear;
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("E", Locale.ENGLISH);

    public int getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public String getDateString() {
        return DAY_FORMATTER.format(toDate());
    }

    public LocalDate toDate() {
        return LocalDate.ofYearDay(LocalDate.now().getYear(), dayOfYear);
    }

    @Override
    public String getLabel() {
        return getDateString();
    }

    @Override
    public String toString() {
        return getDateString();
    }
}
