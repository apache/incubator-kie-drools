package org.optaplanner.core.impl.testdata.phase.event;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public class TestdataStepScoreListener extends PhaseLifecycleListenerAdapter<TestdataSolution> {

    private List<Integer> scores = new ArrayList<>();

    @Override
    public void stepEnded(AbstractStepScope<TestdataSolution> stepScope) {
        TestdataSolution solution = stepScope.getWorkingSolution();

        if (solution.getScore() != null) {
            scores.add(solution.getScore().getScore());
        }
    }

    public List<Integer> getScores() {
        return scores;
    }
}
