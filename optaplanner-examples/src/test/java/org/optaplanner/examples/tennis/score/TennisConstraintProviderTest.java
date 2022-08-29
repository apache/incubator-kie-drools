package org.optaplanner.examples.tennis.score;

import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
import org.optaplanner.examples.tennis.domain.Day;
import org.optaplanner.examples.tennis.domain.Team;
import org.optaplanner.examples.tennis.domain.TeamAssignment;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.domain.UnavailabilityPenalty;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class TennisConstraintProviderTest
        extends AbstractConstraintProviderTest<TennisConstraintProvider, TennisSolution> {

    private static final Day DAY0 = new Day(0, 0);
    private static final Day DAY1 = new Day(1, 1);
    private static final Day DAY2 = new Day(2, 2);
    private static final Team TEAM0 = new Team(0, "A");
    private static final Team TEAM1 = new Team(1, "B");
    private static final Team TEAM2 = new Team(2, "C");

    @ConstraintProviderTest
    void oneAssignmentPerDayPerTeam(ConstraintVerifier<TennisConstraintProvider, TennisSolution> constraintVerifier) {
        TeamAssignment assignment1 = new TeamAssignment(0, DAY0, 0);
        assignment1.setTeam(TEAM0);
        TeamAssignment assignment2 = new TeamAssignment(1, DAY0, 1);
        assignment2.setTeam(TEAM0);
        TeamAssignment assignment3 = new TeamAssignment(2, DAY0, 2);
        assignment3.setTeam(TEAM0);
        TeamAssignment assignment4 = new TeamAssignment(3, DAY1, 0);
        assignment4.setTeam(TEAM1);
        TeamAssignment assignment5 = new TeamAssignment(4, DAY2, 1);
        assignment5.setTeam(TEAM1);

        constraintVerifier.verifyThat(TennisConstraintProvider::oneAssignmentPerDatePerTeam)
                .given(assignment1, assignment2, assignment3, assignment4, assignment5, TEAM0, TEAM1, TEAM2)
                .penalizesBy(3); // TEAM0 by 2, TEAM1 by 1.
    }

    @ConstraintProviderTest
    void unavailabilityPenalty(ConstraintVerifier<TennisConstraintProvider, TennisSolution> constraintVerifier) {
        TeamAssignment assignment1 = new TeamAssignment(0, DAY0, 0);
        assignment1.setTeam(TEAM0);
        TeamAssignment assignment2 = new TeamAssignment(1, DAY1, 0);
        assignment2.setTeam(TEAM1);
        TeamAssignment assignment3 = new TeamAssignment(2, DAY1, 1);
        assignment3.setTeam(TEAM1);
        TeamAssignment assignment4 = new TeamAssignment(3, DAY2, 0);
        assignment4.setTeam(TEAM1);

        UnavailabilityPenalty unavailabilityPenalty1 = new UnavailabilityPenalty(0, TEAM0, DAY0);
        UnavailabilityPenalty unavailabilityPenalty2 = new UnavailabilityPenalty(1, TEAM1, DAY1);

        constraintVerifier.verifyThat(TennisConstraintProvider::unavailabilityPenalty)
                .given(assignment1, assignment2, assignment3, assignment4, unavailabilityPenalty1, unavailabilityPenalty2)
                .penalizesBy(2); // TEAM0 by 1, TEAM1 by 1.
    }

    @ConstraintProviderTest
    void fairAssignmentCountPerTeam(ConstraintVerifier<TennisConstraintProvider, TennisSolution> constraintVerifier) {
        TeamAssignment assignment1 = new TeamAssignment(0, DAY0, 0);
        assignment1.setTeam(TEAM0);
        TeamAssignment assignment2 = new TeamAssignment(1, DAY1, 0);
        assignment2.setTeam(TEAM1);
        TeamAssignment assignment3 = new TeamAssignment(2, DAY2, 0);
        assignment3.setTeam(TEAM2);
        TeamAssignment assignment4 = new TeamAssignment(3, DAY0, 0);
        assignment4.setTeam(TEAM2);

        constraintVerifier.verifyThat(TennisConstraintProvider::fairAssignmentCountPerTeam)
                .given(assignment1, assignment2, assignment3)
                .penalizesBy(1732);
        // Team 2 twice while everyone else just once = more unfair.
        constraintVerifier.verifyThat(TennisConstraintProvider::fairAssignmentCountPerTeam)
                .given(assignment1, assignment2, assignment3, assignment4)
                .penalizesBy(2449);
    }

    @ConstraintProviderTest
    void evenlyConfrontationCount(ConstraintVerifier<TennisConstraintProvider, TennisSolution> constraintVerifier) {
        TeamAssignment assignment1 = new TeamAssignment(0, DAY0, 0);
        assignment1.setTeam(TEAM0);
        TeamAssignment assignment2 = new TeamAssignment(1, DAY0, 0);
        assignment2.setTeam(TEAM1);
        TeamAssignment assignment3 = new TeamAssignment(2, DAY0, 0);
        assignment3.setTeam(TEAM2);
        TeamAssignment assignment4 = new TeamAssignment(3, DAY0, 0);
        assignment4.setTeam(TEAM2);

        constraintVerifier.verifyThat(TennisConstraintProvider::evenlyConfrontationCount)
                .given(assignment1, assignment2, assignment3)
                .penalizesBy(1732);
        // Team 2 twice while everyone else just once = more unfair.
        constraintVerifier.verifyThat(TennisConstraintProvider::evenlyConfrontationCount)
                .given(assignment1, assignment2, assignment3, assignment4)
                .penalizesBy(3000);
    }

    @Override
    protected ConstraintVerifier<TennisConstraintProvider, TennisSolution> createConstraintVerifier() {
        return ConstraintVerifier.build(new TennisConstraintProvider(), TennisSolution.class, TeamAssignment.class);
    }
}
