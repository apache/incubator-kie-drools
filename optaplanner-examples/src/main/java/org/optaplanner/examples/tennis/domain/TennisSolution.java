package org.optaplanner.examples.tennis.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningSolution
public class TennisSolution extends AbstractPersistable {

    private List<Team> teamList;
    private List<Day> dayList;
    private List<UnavailabilityPenalty> unavailabilityPenaltyList;

    private List<TeamAssignment> teamAssignmentList;

    private HardMediumSoftScore score;

    public TennisSolution() {
    }

    public TennisSolution(long id) {
        super(id);
    }

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    public List<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
    }

    @ProblemFactCollectionProperty
    public List<Day> getDayList() {
        return dayList;
    }

    public void setDayList(List<Day> dayList) {
        this.dayList = dayList;
    }

    @ProblemFactCollectionProperty
    public List<UnavailabilityPenalty> getUnavailabilityPenaltyList() {
        return unavailabilityPenaltyList;
    }

    public void setUnavailabilityPenaltyList(List<UnavailabilityPenalty> unavailabilityPenaltyList) {
        this.unavailabilityPenaltyList = unavailabilityPenaltyList;
    }

    @PlanningEntityCollectionProperty
    public List<TeamAssignment> getTeamAssignmentList() {
        return teamAssignmentList;
    }

    public void setTeamAssignmentList(List<TeamAssignment> teamAssignmentList) {
        this.teamAssignmentList = teamAssignmentList;
    }

    @PlanningScore
    public HardMediumSoftScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

}
