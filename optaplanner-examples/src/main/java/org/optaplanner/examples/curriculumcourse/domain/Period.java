package org.optaplanner.examples.curriculumcourse.domain;

import static java.util.Objects.requireNonNull;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Period.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Period extends AbstractPersistable implements Labeled {

    private Day day;
    private Timeslot timeslot;

    public Period() {
    }

    public Period(long id, Day day, Timeslot timeslot) {
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
    @JsonIgnore
    public String getLabel() {
        return day.getLabel() + " " + timeslot.getLabel();
    }

    @Override
    public String toString() {
        return day + "-" + timeslot;
    }

}
