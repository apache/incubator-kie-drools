package org.optaplanner.examples.travelingtournament.score;

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
        return constraintFactory.forEach(Match.class)
                .ifExists(Match.class, equal(Match::getHomeTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .ifExists(Match.class, equal(Match::getHomeTeam),
                        equal(match -> getDayIndex(match) + 2, TravelingTournamentConstraintProvider::getDayIndex))
                .ifExists(Match.class, equal(Match::getHomeTeam),
                        equal(match -> getDayIndex(match) + 3, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("4 consecutive home matches");
    }

    private Constraint fourConsecutiveAwayMatches(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Match.class)
                .ifExists(Match.class, equal(Match::getAwayTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .ifExists(Match.class, equal(Match::getAwayTeam),
                        equal(match -> getDayIndex(match) + 2, TravelingTournamentConstraintProvider::getDayIndex))
                .ifExists(Match.class, equal(Match::getAwayTeam),
                        equal(match -> getDayIndex(match) + 3, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("4 consecutive away matches");
    }

    private Constraint repeatMatchOnTheNextDay(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Match.class)
                .ifExists(Match.class, equal(Match::getHomeTeam, Match::getAwayTeam),
                        equal(Match::getAwayTeam, Match::getHomeTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Repeat match on the next day");
    }

    private Constraint startToAwayHop(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Match.class)
                .ifNotExists(Day.class,
                        equal(match -> getDayIndex(match) - 1, Day::getIndex))
                .penalize(HardSoftScore.ONE_SOFT,
                        match -> match.getAwayTeam().getDistance(match.getHomeTeam()))
                .asConstraint("Start to away hop");
    }

    private Constraint homeToAwayHop(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Match.class)
                .join(Match.class, equal(Match::getHomeTeam, Match::getAwayTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize(HardSoftScore.ONE_SOFT,
                        (match, otherMatch) -> match.getHomeTeam().getDistance(otherMatch.getHomeTeam()))
                .asConstraint("Home to away hop");
    }

    private Constraint awayToAwayHop(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Match.class)
                .join(Match.class, equal(Match::getAwayTeam, Match::getAwayTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize(HardSoftScore.ONE_SOFT,
                        (match, otherMatch) -> match.getHomeTeam().getDistance(otherMatch.getHomeTeam()))
                .asConstraint("Away to away hop");
    }

    private Constraint awayToHomeHop(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Match.class)
                .join(Match.class, equal(Match::getAwayTeam, Match::getHomeTeam),
                        equal(match -> getDayIndex(match) + 1, TravelingTournamentConstraintProvider::getDayIndex))
                .penalize(HardSoftScore.ONE_SOFT,
                        (match, otherMatch) -> match.getHomeTeam().getDistance(match.getAwayTeam()))
                .asConstraint("Away to home hop");
    }

    private Constraint awayToEndHop(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Match.class)
                .ifNotExists(Day.class, equal(match -> getDayIndex(match) + 1, Day::getIndex))
                .penalize(HardSoftScore.ONE_SOFT,
                        match -> match.getHomeTeam().getDistance(match.getAwayTeam()))
                .asConstraint("Away to end hop");
    }

    private static int getDayIndex(Match match) {
        return match.getDay().getIndex();
    }

}
