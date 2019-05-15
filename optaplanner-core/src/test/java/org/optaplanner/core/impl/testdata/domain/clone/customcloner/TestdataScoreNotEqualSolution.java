/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.testdata.domain.clone.customcloner;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution(solutionCloner = TestdataScoreNotEqualSolution.class)
public class TestdataScoreNotEqualSolution implements SolutionCloner<TestdataScoreNotEqualSolution> {

    @PlanningScore
    private SimpleScore score;
    @PlanningEntityProperty
    private TestdataEntity entity = new TestdataEntity();

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> valueRange() {
        // solver will never get to this point due to cloning corruption
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TestdataScoreNotEqualSolution cloneSolution(TestdataScoreNotEqualSolution original) {
        TestdataScoreNotEqualSolution clone = new TestdataScoreNotEqualSolution();
        clone.entity.setValue(original.entity.getValue());
        if (original.score != null) {
            clone.score = SimpleScore.ofUninitialized(original.score.getInitScore() - 1, original.score.getScore() - 1);
        } else {
            clone.score = SimpleScore.of(0);
        }
        if (clone.score.equals(original.score)) {
            throw new IllegalStateException("The cloned score should be intentionally unequal to the original score");
        }
        return clone;
    }

}
