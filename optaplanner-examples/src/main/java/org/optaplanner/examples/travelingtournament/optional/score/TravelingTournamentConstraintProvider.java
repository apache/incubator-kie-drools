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

package org.optaplanner.examples.travelingtournament.optional.score;

import static org.optaplanner.core.api.score.stream.Joiners.equal;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.travelingtournament.domain.Day;
import org.optaplanner.examples.travelingtournament.domain.Match;

public final class TravelingTournamentConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                fourConsecutiveHomeMatches(constraintFactory),
                fourConsecutiveAwayMatches(constraintFactory),
                repeatMatchOnTheNextDay(constraintFactory),
                startToAwayHop(constraintFactory),
                homeToAwayHop(constraintFactory),
                awayToAwayHop(constraintFactory),
                awayToHomeHop(constraintFactory),
                awayToEndHop(constraintFactory)
        };
    }

    private Constraint fourConsecutiveHomeMatches(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Match.class)
                .ifExists(Match.class, equal(Match::getHomeTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .ifExists(Match.class, equal(Match::getHomeTeam),
                        equal(match -> getDayIndex(match) + 2, TravelingTournamentConstraintProvider::getDayIndex))
                .ifExists(Match.class, equal(Match::getHomeTeam),
                        equal(match -> getDayIndex(match) + 3, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize("4 consecutive home matches", HardSoftScore.ONE_HARD);
    }

    private Constraint fourConsecutiveAwayMatches(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Match.class)
                .ifExists(Match.class, equal(Match::getAwayTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .ifExists(Match.class, equal(Match::getAwayTeam),
                        equal(match -> getDayIndex(match) + 2, TravelingTournamentConstraintProvider::getDayIndex))
                .ifExists(Match.class, equal(Match::getAwayTeam),
                        equal(match -> getDayIndex(match) + 3, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize("4 consecutive away matches", HardSoftScore.ONE_HARD);
    }

    private Constraint repeatMatchOnTheNextDay(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Match.class)
                .ifExists(Match.class, equal(Match::getHomeTeam, Match::getAwayTeam),
                        equal(Match::getAwayTeam, Match::getHomeTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize("Repeat match on the next day", HardSoftScore.ONE_HARD);
    }

    private Constraint startToAwayHop(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Match.class)
                .ifNotExists(Day.class,
                        equal(match -> getDayIndex(match) - 1, Day::getIndex))
                .penalize("Start to away hop", HardSoftScore.ONE_SOFT,
                        match -> match.getAwayTeam().getDistance(match.getHomeTeam()));
    }

    private Constraint homeToAwayHop(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Match.class)
                .join(Match.class, equal(Match::getHomeTeam, Match::getAwayTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize("Home to away hop", HardSoftScore.ONE_SOFT,
                        (match, otherMatch) -> match.getHomeTeam().getDistance(otherMatch.getHomeTeam()));
    }

    private Constraint awayToAwayHop(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Match.class)
                .join(Match.class, equal(Match::getAwayTeam, Match::getAwayTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize("Away to away hop", HardSoftScore.ONE_SOFT,
                        (match, otherMatch) -> match.getHomeTeam().getDistance(otherMatch.getHomeTeam()));
    }

    private Constraint awayToHomeHop(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Match.class)
                .join(Match.class, equal(Match::getAwayTeam, Match::getHomeTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize("Away to home hop", HardSoftScore.ONE_SOFT,
                        (match, otherMatch) -> match.getHomeTeam().getDistance(match.getAwayTeam()));
    }

    private Constraint awayToEndHop(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Match.class)
                .ifNotExists(Day.class, equal(match -> getDayIndex(match) + 1, Day::getIndex))
                .penalize("Away to end hop", HardSoftScore.ONE_SOFT,
                        match -> match.getHomeTeam().getDistance(match.getAwayTeam()));
    }

    private static int getDayIndex(Match match) {
        return match.getDay().getIndex();
    }

}
