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

import java.util.Arrays;
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

@PlanningSolution(solutionCloner = TestdataCorrectlyClonedSolution.class)
public class TestdataCorrectlyClonedSolution implements SolutionCloner<TestdataCorrectlyClonedSolution> {

    private boolean clonedByCustomCloner = false;
    @PlanningScore
    private SimpleScore score;
    @PlanningEntityProperty
    private TestdataEntity entity = new TestdataEntity("A");

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> valueRange() {
        // two values needed to allow for at least one doable move, otherwise the second step ends in an infinite loop
        return Arrays.asList(new TestdataValue("1"), new TestdataValue("2"));
    }

    @Override
    public TestdataCorrectlyClonedSolution cloneSolution(TestdataCorrectlyClonedSolution original) {
        TestdataCorrectlyClonedSolution clone = new TestdataCorrectlyClonedSolution();
        clone.clonedByCustomCloner = true;
        // score is immutable so no need to create a new instance
        clone.score = original.score;
        clone.entity.setValue(original.entity.getValue());
        return clone;
    }

    public boolean isClonedByCustomCloner() {
        return clonedByCustomCloner;
    }

}
