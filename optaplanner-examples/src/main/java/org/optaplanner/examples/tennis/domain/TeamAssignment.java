package org.optaplanner.examples.tennis.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity
public class TeamAssignment extends AbstractPersistable {

    private Day day;
    private int indexInDay;
    private boolean pinned;

    // planning variable
    private Team team;

    public TeamAssignment() {
    }

    public TeamAssignment(long id, Day day, int indexInDay) {
        super(id);
        this.day = day;
        this.indexInDay = indexInDay;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public int getIndexInDay() {
        return indexInDay;
    }

    public void setIndexInDay(int indexInDay) {
        this.indexInDay = indexInDay;
    }

    @PlanningPin
    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    @PlanningVariable
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return "Day-" + day.getDateIndex() + "(" + indexInDay + ")";
    }

}
