package org.optaplanner.constraint.drl;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.solutionproperties.TestdataNoProblemFactPropertySolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class SolutionDescriptorTest {

    @Test
    void noProblemFactPropertyWithDroolsScoreCalculation() {
        assertThatIllegalStateException().isThrownBy(() -> buildSolverFactoryWithDroolsScoreDirector(
                TestdataNoProblemFactPropertySolution.class, TestdataEntity.class));
    }

    private static <Solution_> SolverFactory<Solution_> buildSolverFactoryWithDroolsScoreDirector(
            Class<Solution_> solutionClass, Class<?>... entityClasses) {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(solutionClass, entityClasses);
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig =
                solverConfig.getScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setEasyScoreCalculatorClass(null);
        scoreDirectorFactoryConfig.setScoreDrlList(
                Collections.singletonList("org/optaplanner/constraint/drl/dummySimpleScoreDroolsConstraints.drl"));
        return SolverFactory.create(solverConfig);
    }

}
