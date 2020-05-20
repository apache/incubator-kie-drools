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

package org.optaplanner.core.impl.testdata.domain.shadow.inverserelation;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningSolution
public class TestdataInverseRelationSolution extends TestdataObject {

    public static SolutionDescriptor buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataInverseRelationSolution.class,
                TestdataInverseRelationEntity.class, TestdataInverseRelationValue.class);
    }

    private List<TestdataInverseRelationValue> valueList;
    private List<TestdataInverseRelationEntity> entityList;

    private SimpleScore score;

    public TestdataInverseRelationSolution() {
    }

    public TestdataInverseRelationSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataInverseRelationValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataInverseRelationValue> valueList) {
        this.valueList = valueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataInverseRelationEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataInverseRelationEntity> entityList) {
        this.entityList = entityList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
