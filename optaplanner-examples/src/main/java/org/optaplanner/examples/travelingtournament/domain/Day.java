package org.optaplanner.examples.travelingtournament.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Day extends AbstractPersistable implements Labeled {

    private int index;

    private Day nextDay;

    public Day() {
    }

    public Day(int id) {
        super(id);
        this.index = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Day getNextDay() {
        return nextDay;
    }

    public void setNextDay(Day nextDay) {
        this.nextDay = nextDay;
    }

    @Override
    public String getLabel() {
        return Integer.toString(index);
    }

    @Override
    public String toString() {
        return "Day-" + index;
    }

}
