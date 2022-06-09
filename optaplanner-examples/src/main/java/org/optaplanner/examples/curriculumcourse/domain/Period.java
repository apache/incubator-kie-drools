package org.optaplanner.examples.curriculumcourse.domain;

import static java.util.Objects.requireNonNull;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Period")
public class Period extends AbstractPersistable implements Labeled {

    private Day day;
    private Timeslot timeslot;

    public Period() {
    }

    public Period(int id, Day day, Timeslot timeslot) {
        super(id);
        this.day = requireNonNull(day);
        day.getPeriodList().add(this);
        this.timeslot = requireNonNull(timeslot);
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    @Override
    public String getLabel() {
        return day.getLabel() + " " + timeslot.getLabel();
    }

    @Override
    public String toString() {
        return day + "-" + timeslot;
    }

}
