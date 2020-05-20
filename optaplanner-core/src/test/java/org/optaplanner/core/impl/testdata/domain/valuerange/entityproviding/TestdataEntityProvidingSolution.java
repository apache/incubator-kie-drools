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

package org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataEntityProvidingSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataEntityProvidingSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataEntityProvidingSolution.class,
                TestdataEntityProvidingEntity.class);
    }

    private List<TestdataEntityProvidingEntity> entityList;

    private SimpleScore score;

    public TestdataEntityProvidingSolution() {
    }

    public TestdataEntityProvidingSolution(String code) {
        super(code);
    }

    @PlanningEntityCollectionProperty
    public List<TestdataEntityProvidingEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataEntityProvidingEntity> entityList) {
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

    @ProblemFactCollectionProperty
    public Collection<TestdataValue> getProblemFacts() {
        Set<TestdataValue> valueSet = new HashSet<>();
        for (TestdataEntityProvidingEntity entity : entityList) {
            valueSet.addAll(entity.getValueRange());
        }
        return valueSet;
    }

}
