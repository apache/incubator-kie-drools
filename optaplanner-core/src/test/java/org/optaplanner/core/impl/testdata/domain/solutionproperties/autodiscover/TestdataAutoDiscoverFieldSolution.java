/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover;

import java.util.List;

import org.optaplanner.core.api.domain.autodiscover.AutoDiscoverMemberType;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfiguration;

@PlanningSolution(autoDiscoverMemberType = AutoDiscoverMemberType.FIELD)
public class TestdataAutoDiscoverFieldSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataAutoDiscoverFieldSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataAutoDiscoverFieldSolution.class, TestdataEntity.class);
    }

    private TestdataConstraintConfiguration constraintConfiguration;
    private TestdataObject singleProblemFact;
    @ValueRangeProvider(id = "valueRange")
    private List<TestdataValue> problemFactList;

    private List<TestdataEntity> entityList;
    private TestdataEntity otherEntity;

    private SimpleScore score;

    public TestdataAutoDiscoverFieldSolution() {
    }

    public TestdataAutoDiscoverFieldSolution(String code) {
        super(code);
    }

    public TestdataAutoDiscoverFieldSolution(String code, TestdataObject singleProblemFact,
            List<TestdataValue> problemFactList, List<TestdataEntity> entityList,
            TestdataEntity otherEntity) {
        super(code);
        this.singleProblemFact = singleProblemFact;
        this.problemFactList = problemFactList;
        this.entityList = entityList;
        this.otherEntity = otherEntity;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

}
