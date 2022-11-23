package org.optaplanner.examples.travelingtournament.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

@PlanningEntity
public class Match extends AbstractPersistableJackson {

    private Team homeTeam;
    private Team awayTeam;

    private Day day;

    public Match() { // For Jackson.
    }

    public Match(long id, Team homeTeam, Team awayTeam) {
        super(id);
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    @PlanningVariable(valueRangeProviderRefs = { "dayRange" })
    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return homeTeam + "+" + awayTeam;
    }

}
