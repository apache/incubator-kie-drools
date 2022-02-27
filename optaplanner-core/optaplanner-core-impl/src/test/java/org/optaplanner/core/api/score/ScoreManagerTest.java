/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public class ScoreManagerTest {

    public static final SolverFactory<TestdataSolution> SOLVER_FACTORY =
            SolverFactory.createFromXmlResource("org/optaplanner/core/api/solver/testdataSolverConfig.xml");

    @ParameterizedTest
    @EnumSource(ScoreManagerSource.class)
    void updateScore(ScoreManagerSource scoreManagerSource) {
        ScoreManager<TestdataSolution, ?> scoreManager = scoreManagerSource.createScoreManager(SOLVER_FACTORY);
        assertThat(scoreManager).isNotNull();
        TestdataSolution solution = TestdataSolution.generateSolution();
        assertThat(solution.getScore()).isNull();
        scoreManager.updateScore(solution);
        assertThat(solution.getScore()).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(ScoreManagerSource.class)
    void explainScore(ScoreManagerSource scoreManagerSource) {
        ScoreManager<TestdataSolution, ?> scoreManager = scoreManagerSource.createScoreManager(SOLVER_FACTORY);
        assertThat(scoreManager).isNotNull();
        TestdataSolution solution = TestdataSolution.generateSolution();
        ScoreExplanation<TestdataSolution, ?> scoreExplanation = scoreManager.explainScore(solution);
        assertThat(scoreExplanation).isNotNull();
        assertSoftly(softly -> {
            softly.assertThat(scoreExplanation.getScore()).isNotNull();
            softly.assertThat(scoreExplanation.getSummary()).isNotBlank();
            softly.assertThat(scoreExplanation.getConstraintMatchTotalMap()).isNotEmpty();
            softly.assertThat(scoreExplanation.getIndictmentMap()).isNotEmpty();
        });
    }

    public enum ScoreManagerSource {

        FROM_SOLVER_FACTORY(ScoreManager::create),
        FROM_SOLVER_MANAGER(solverFactory -> ScoreManager.create(SolverManager.create(solverFactory)));

        private final Function<SolverFactory, ScoreManager> scoreManagerConstructor;

        ScoreManagerSource(Function<SolverFactory, ScoreManager> scoreManagerConstructor) {
            this.scoreManagerConstructor = scoreManagerConstructor;
        }

        public <Solution_, Score_ extends Score<Score_>> ScoreManager<Solution_, Score_>
                createScoreManager(SolverFactory<Solution_> solverFactory) {
            return scoreManagerConstructor.apply(solverFactory);
        }

    }

}
