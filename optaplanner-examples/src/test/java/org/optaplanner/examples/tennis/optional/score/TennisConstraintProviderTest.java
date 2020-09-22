/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.tennis.optional.score;

import org.junit.jupiter.api.Test;
import org.optaplanner.examples.tennis.domain.Day;
import org.optaplanner.examples.tennis.domain.Team;
import org.optaplanner.examples.tennis.domain.TeamAssignment;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.domain.UnavailabilityPenalty;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

public class TennisConstraintProviderTest {

    private static final Day DAY0 = new Day(0, 0);
    private static final Day DAY1 = new Day(1, 1);
    private static final Day DAY2 = new Day(2, 2);
    private static final Team TEAM0 = new Team(0, "A");
    private static final Team TEAM1 = new Team(1, "B");
    private static final Team TEAM2 = new Team(2, "C");

    private final ConstraintVerifier<TennisConstraintProvider, TennisSolution> constraintVerifier =
            ConstraintVerifier.build(new TennisConstraintProvider(), TennisSolution.class, TeamAssignment.class);

    @Test
    public void oneAssignmentPerDayPerTeam() {
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

    @Test
    public void unavailabilityPenalty() {
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

    @Test
    public void fairAssignmentCountPerTeam() {
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

    @Test
    public void evenlyConfrontationCount() {
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

}
