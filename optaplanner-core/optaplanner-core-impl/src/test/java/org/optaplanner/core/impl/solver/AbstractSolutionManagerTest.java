/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.solver;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.DefaultConstraintJustification;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolutionManagerTest;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public abstract class AbstractSolutionManagerTest {

    protected abstract ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig();

    @Test
    void indictmentsPresentOnFreshExplanation() {
        // Create the environment.
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = buildScoreDirectorFactoryConfig();
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setSolutionClass(TestdataSolution.class);
        solverConfig.setEntityClassList(Collections.singletonList(TestdataEntity.class));
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        SolutionManager<TestdataSolution, SimpleScore> solutionManager =
                SolutionManagerTest.SolutionManagerSource.FROM_SOLVER_FACTORY.createSolutionManager(solverFactory);

        // Prepare the solution.
        int entityCount = 3;
        TestdataSolution solution = TestdataSolution.generateSolution(2, entityCount);
        ScoreExplanation<TestdataSolution, SimpleScore> scoreExplanation = solutionManager.explain(solution);

        // Check for expected results.
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(scoreExplanation.getScore())
                    .isEqualTo(SimpleScore.of(-entityCount));
            softly.assertThat(scoreExplanation.getConstraintMatchTotalMap())
                    .isNotEmpty();
            softly.assertThat(scoreExplanation.getIndictmentMap())
                    .isNotEmpty();
            List<DefaultConstraintJustification> constraintJustificationList = (List) scoreExplanation.getJustificationList();
            softly.assertThat(constraintJustificationList)
                    .isNotEmpty();
            softly.assertThat(scoreExplanation.getJustificationList(DefaultConstraintJustification.class))
                    .containsExactlyElementsOf(constraintJustificationList);
        });
    }

}
