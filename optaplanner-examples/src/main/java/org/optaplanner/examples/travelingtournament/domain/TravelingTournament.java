package org.optaplanner.examples.travelingtournament.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningSolution
public class TravelingTournament extends AbstractPersistable {

    private List<Day> dayList;
    private List<Team> teamList;

    private List<Match> matchList;

    private HardSoftScore score;

    public TravelingTournament() {
    }

    public TravelingTournament(long id) {
        super(id);
    }

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    public List<Day> getDayList() {
        return dayList;
    }

    public void setDayList(List<Day> dayList) {
        this.dayList = dayList;
    }

    @ProblemFactCollectionProperty
    public List<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
    }

    @PlanningEntityCollectionProperty
    public List<Match> getMatchList() {
        return matchList;
    }

    public void setMatchList(List<Match> matchSets) {
        this.matchList = matchSets;
    }

    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @JsonIgnore
    public int getN() {
        return teamList.size();
    }

}
