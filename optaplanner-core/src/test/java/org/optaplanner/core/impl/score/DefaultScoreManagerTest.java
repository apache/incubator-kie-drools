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

package org.optaplanner.core.impl.score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class DefaultScoreManagerTest {

    @Test
    public void explainScore() {
        SolverFactory<TestdataSolution> solverFactory =
                SolverFactory.createFromXmlResource("org/optaplanner/core/api/solver/testdataSolverConfig.xml");
        ScoreManager<TestdataSolution> scoreManager = ScoreManager.create(solverFactory);
        assertThat(scoreManager).isNotNull();
        TestdataSolution solution = TestdataSolution.generateSolution();
        ScoreExplanation<TestdataSolution> scoreExplanation = scoreManager.explainScore(solution);
        assertThat(scoreExplanation).isNotNull();
        assertSoftly(softly -> {
            softly.assertThat(scoreExplanation.getScore()).isNotNull();
            softly.assertThat(scoreExplanation.getSummary()).isNotBlank();
            softly.assertThat(scoreExplanation.getConstraintMatchTotalMap()).isNotEmpty();
            softly.assertThat(scoreExplanation.getIndictmentMap()).isNotEmpty();
        });
    }

}
