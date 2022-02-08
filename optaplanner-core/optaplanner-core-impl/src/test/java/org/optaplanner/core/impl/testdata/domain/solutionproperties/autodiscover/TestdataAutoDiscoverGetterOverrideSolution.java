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

@PlanningSolution(autoDiscoverMemberType = AutoDiscoverMemberType.GETTER)
public class TestdataAutoDiscoverGetterOverrideSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataAutoDiscoverGetterOverrideSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(
                TestdataAutoDiscoverGetterOverrideSolution.class, TestdataEntity.class);
    }

    private TestdataObject singleProblemFactField;
    private List<TestdataValue> problemFactListField;
    private List<String> listProblemFactField;

    private List<TestdataEntity> entityListField;
    private TestdataEntity otherEntityField;

    private SimpleScore score;

    public TestdataAutoDiscoverGetterOverrideSolution() {
    }

    public TestdataAutoDiscoverGetterOverrideSolution(String code) {
        super(code);
    }

    public TestdataAutoDiscoverGetterOverrideSolution(String code, TestdataObject singleProblemFact,
            List<TestdataValue> problemFactList, List<TestdataEntity> entityList,
            TestdataEntity otherEntity, List<String> listFact) {
        super(code);
        this.singleProblemFactField = singleProblemFact;
        this.problemFactListField = problemFactList;
        this.entityListField = entityList;
        this.otherEntityField = otherEntity;
        this.listProblemFactField = listFact;
    }

    public TestdataObject getSingleProblemFact() {
        return singleProblemFactField;
    }

    @ValueRangeProvider(id = "valueRange")
    public List<TestdataValue> getProblemFactList() {
        return problemFactListField;
    }

    @ProblemFactProperty // would have been autodiscovered as @ProblemFactCollectionProperty
    public List<String> getListProblemFact() {
        return listProblemFactField;
    }

    public List<TestdataEntity> getEntityList() {
        return entityListField;
    }

    public TestdataEntity getOtherEntity() {
        return otherEntityField;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

}
