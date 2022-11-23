package org.optaplanner.examples.tennis.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

public class UnavailabilityPenalty extends AbstractPersistableJackson {

    private Team team;
    private Day day;

    public UnavailabilityPenalty() {
    }

    public UnavailabilityPenalty(long id, Team team, Day day) {
        super(id);
        this.team = team;
        this.day = day;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

}
