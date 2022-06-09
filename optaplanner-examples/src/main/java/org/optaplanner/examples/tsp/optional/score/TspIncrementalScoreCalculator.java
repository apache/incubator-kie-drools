package org.optaplanner.examples.tsp.optional.score;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;

public class TspIncrementalScoreCalculator implements IncrementalScoreCalculator<TspSolution, SimpleLongScore> {

    private Domicile domicile;

    private long score;

    @Override
    public void resetWorkingSolution(TspSolution tspSolution) {
        domicile = tspSolution.getDomicile();
        score = 0L;
        for (Visit visit : tspSolution.getVisitList()) {
            insert(visit);
        }
    }

    @Override
    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(Object entity) {
        insert((Visit) entity);
    }

    @Override
    public void beforeVariableChanged(Object entity, String variableName) {
        retract((Visit) entity);
    }

    @Override
    public void afterVariableChanged(Object entity, String variableName) {
        insert((Visit) entity);
    }

    @Override
    public void beforeEntityRemoved(Object entity) {
        retract((Visit) entity);
    }

    @Override
    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    private void insert(Visit visit) {
        Standstill previousStandstill = visit.getPreviousStandstill();
        if (previousStandstill != null) {
            score -= visit.getDistanceFromPreviousStandstill();
            // HACK: This counts too much, but the insert/retracts balance each other out
            score += previousStandstill.getDistanceTo(domicile);
            score -= visit.getDistanceTo(domicile);
        }
    }

    private void retract(Visit visit) {
        Standstill previousStandstill = visit.getPreviousStandstill();
        if (previousStandstill != null) {
            score += visit.getDistanceFromPreviousStandstill();
            // HACK: This counts too much, but the insert/retracts balance each other out
            score -= previousStandstill.getDistanceTo(domicile);
            score += visit.getDistanceTo(domicile);
        }
    }

    @Override
    public SimpleLongScore calculateScore() {
        return SimpleLongScore.of(score);
    }

}
