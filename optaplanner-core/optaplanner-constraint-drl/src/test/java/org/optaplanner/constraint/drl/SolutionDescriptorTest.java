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
