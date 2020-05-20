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
package org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover;

import java.util.List;

import org.optaplanner.core.api.domain.autodiscover.AutoDiscoverMemberType;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution(autoDiscoverMemberType = AutoDiscoverMemberType.FIELD)
public class TestdataAutoDiscoverFieldOverrideSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataAutoDiscoverFieldOverrideSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataAutoDiscoverFieldOverrideSolution.class,
                TestdataEntity.class);
    }

    private TestdataObject singleProblemFact;
    @ValueRangeProvider(id = "valueRange")
    private List<TestdataValue> problemFactList;
    @ProblemFactProperty // would have been autodiscovered as @ProblemFactCollectionProperty
    private List<String> listProblemFact;

    private List<TestdataEntity> entityList;
    private TestdataEntity otherEntity;

    private SimpleScore score;

    public TestdataAutoDiscoverFieldOverrideSolution() {
    }

    public TestdataAutoDiscoverFieldOverrideSolution(String code) {
        super(code);
    }

    public TestdataAutoDiscoverFieldOverrideSolution(String code, TestdataObject singleProblemFact,
            List<TestdataValue> problemFactList, List<TestdataEntity> entityList,
            TestdataEntity otherEntity, List<String> listFact) {
        super(code);
        this.singleProblemFact = singleProblemFact;
        this.problemFactList = problemFactList;
        this.entityList = entityList;
        this.otherEntity = otherEntity;
        this.listProblemFact = listFact;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

}
