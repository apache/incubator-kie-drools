package org.optaplanner.examples.curriculumcourse.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Timeslot.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Timeslot extends AbstractPersistable implements Labeled {

    private static final String[] TIMES = { "08:00", "09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00", "17:00",
            "18:00" };

    private int timeslotIndex;

    public Timeslot() {
    }

    public Timeslot(int timeslotIndex) {
        super(timeslotIndex);
        this.timeslotIndex = timeslotIndex;
    }

    public int getTimeslotIndex() {
        return timeslotIndex;
    }

    public void setTimeslotIndex(int timeslotIndex) {
        this.timeslotIndex = timeslotIndex;
    }

    @Override
    public String getLabel() {
        String time = TIMES[timeslotIndex % TIMES.length];
        if (timeslotIndex > TIMES.length) {
            return "Timeslot " + timeslotIndex;
        }
        return time;
    }

    @Override
    public String toString() {
        return Integer.toString(timeslotIndex);
    }

}
