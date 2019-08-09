/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.domain.score.lavish;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningSolution
public class TestdataLavishSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataLavishSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataLavishSolution.class, TestdataLavishEntity.class);
    }

    public static TestdataLavishSolution generateSolution() {
        return generateSolution(2, 5, 3, 7);
    }

    public static TestdataLavishSolution generateSolution(int valueListSize, int entityListSize) {
        return generateSolution(2, valueListSize, 3, entityListSize);
    }

    public static TestdataLavishSolution generateSolution(int valueGroupListSize, int valueListSize,
            int entityGroupListSize, int entityListSize) {
        TestdataLavishSolution solution = new TestdataLavishSolution("Generated Solution 0");
        List<TestdataLavishValueGroup> valueGroupList = new ArrayList<>(valueGroupListSize);
        for (int i = 0; i < valueGroupListSize; i++) {
            TestdataLavishValueGroup valueGroup = new TestdataLavishValueGroup("Generated ValueGroup " + i);
            valueGroupList.add(valueGroup);
        }
        solution.setValueGroupList(valueGroupList);
        List<TestdataLavishValue> valueList = new ArrayList<>(valueListSize);
        for (int i = 0; i < valueListSize; i++) {
            TestdataLavishValueGroup valueGroup = valueGroupList.get(i % valueGroupListSize);
            TestdataLavishValue value = new TestdataLavishValue("Generated Value " + i, valueGroup);
            valueList.add(value);
        }
        solution.setValueList(valueList);
        solution.setExtraList(new ArrayList<>());
        List<TestdataLavishEntityGroup> entityGroupList = new ArrayList<>(entityGroupListSize);
        for (int i = 0; i < entityGroupListSize; i++) {
            TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("Generated EntityGroup " + i);
            entityGroupList.add(entityGroup);
        }
        solution.setEntityGroupList(entityGroupList);
        List<TestdataLavishEntity> entityList = new ArrayList<>(entityListSize);
        for (int i = 0; i < entityListSize; i++) {
            TestdataLavishEntityGroup entityGroup = entityGroupList.get(i % entityGroupListSize);
            TestdataLavishValue value = valueList.get(i % valueListSize);
            TestdataLavishEntity entity = new TestdataLavishEntity("Generated Entity " + i, entityGroup, value);
            entityList.add(entity);
        }
        solution.setEntityList(entityList);
        return solution;
    }

    @ProblemFactCollectionProperty
    private List<TestdataLavishValueGroup> valueGroupList;
    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    private List<TestdataLavishValue> valueList;
    @ProblemFactCollectionProperty
    private List<TestdataLavishExtra> extraList;
    @ProblemFactCollectionProperty
    private List<TestdataLavishEntityGroup> entityGroupList;
    @PlanningEntityCollectionProperty
    private List<TestdataLavishEntity> entityList;

    @PlanningScore
    private SimpleScore score;

    public TestdataLavishSolution() {
    }

    public TestdataLavishSolution(String code) {
        super(code);
    }

    public TestdataLavishValueGroup getFirstValueGroup() {
        return valueGroupList.get(0);
    }

    public TestdataLavishValue getFirstValue() {
        return valueList.get(0);
    }

    public TestdataLavishEntityGroup getFirstEntityGroup() {
        return entityGroupList.get(0);
    }

    public TestdataLavishEntity getFirstEntity() {
        return entityList.get(0);
    }

    // ************************************************************************
    // Getter/setters
    // ************************************************************************

    public List<TestdataLavishValueGroup> getValueGroupList() {
        return valueGroupList;
    }

    public void setValueGroupList(List<TestdataLavishValueGroup> valueGroupList) {
        this.valueGroupList = valueGroupList;
    }

    public List<TestdataLavishValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataLavishValue> valueList) {
        this.valueList = valueList;
    }

    public List<TestdataLavishExtra> getExtraList() {
        return extraList;
    }

    public void setExtraList(List<TestdataLavishExtra> extraList) {
        this.extraList = extraList;
    }

    public List<TestdataLavishEntityGroup> getEntityGroupList() {
        return entityGroupList;
    }

    public void setEntityGroupList(List<TestdataLavishEntityGroup> entityGroupList) {
        this.entityGroupList = entityGroupList;
    }

    public List<TestdataLavishEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataLavishEntity> entityList) {
        this.entityList = entityList;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

}
