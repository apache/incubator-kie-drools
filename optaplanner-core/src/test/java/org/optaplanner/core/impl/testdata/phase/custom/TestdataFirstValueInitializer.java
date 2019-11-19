package org.optaplanner.core.impl.testdata.phase.custom;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class TestdataFirstValueInitializer implements CustomPhaseCommand<TestdataSolution> {

    @Override
    public void changeWorkingSolution(ScoreDirector<TestdataSolution> scoreDirector) {
        TestdataSolution solution = scoreDirector.getWorkingSolution();
        TestdataValue firstValue = solution.getValueList().get(0);

        for (TestdataEntity entity : solution.getEntityList()) {
            scoreDirector.beforeVariableChanged(entity, "value");
            entity.setValue(firstValue);
            scoreDirector.afterVariableChanged(entity, "value");
        }

        scoreDirector.triggerVariableListeners();
        Score<?> score = scoreDirector.calculateScore();

        if (!score.isSolutionInitialized()) {
            throw new IllegalStateException("The solution (" + TestdataEntity.class.getSimpleName()
                                                    + ") was not fully initialized by CustomSolverPhase: ("
                                                    + this.getClass().getCanonicalName() + ")");
        }
    }
}
