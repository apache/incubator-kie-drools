/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.common;

import java.util.Collections;
import java.util.Objects;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.ScoreManagerTest.ScoreManagerSource;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.testdata.domain.TestdataConstraintProvider;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public abstract class AbstractScoreManagerTest {

    private final ConstraintStreamImplType constraintStreamImplType;

    protected AbstractScoreManagerTest(ConstraintStreamImplType constraintStreamImplType) {
        this.constraintStreamImplType = Objects.requireNonNull(constraintStreamImplType);
    }

    @Test
    void indictmentsPresentOnFreshExplanation() {
        // Create the environment.
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setConstraintProviderClass(TestdataConstraintProvider.class);
        scoreDirectorFactoryConfig.setConstraintStreamImplType(constraintStreamImplType);
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setSolutionClass(TestdataSolution.class);
        solverConfig.setEntityClassList(Collections.singletonList(TestdataEntity.class));
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        ScoreManager<TestdataSolution, SimpleScore> scoreManager =
                ScoreManagerSource.FROM_SOLVER_FACTORY.createScoreManager(solverFactory);

        // Prepare the solution.
        int entityCount = 3;
        TestdataSolution solution = TestdataSolution.generateSolution(2, entityCount);
        ScoreExplanation<TestdataSolution, SimpleScore> scoreExplanation = scoreManager.explainScore(solution);

        // Check for expected results.
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(scoreExplanation.getScore())
                    .isEqualTo(SimpleScore.of(-entityCount));
            softly.assertThat(scoreExplanation.getConstraintMatchTotalMap())
                    .isNotEmpty();
            softly.assertThat(scoreExplanation.getIndictmentMap())
                    .isNotEmpty();
        });
    }

}
