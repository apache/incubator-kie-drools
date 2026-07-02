/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.test.api.score.stream.testdata;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public final class TestdataConstraintVerifierExtendedSolution extends TestdataConstraintVerifierSolution {

    public static TestdataConstraintVerifierExtendedSolution generateSolution(int valueListSize, int entityListSize) {
        TestdataConstraintVerifierExtendedSolution solution =
                new TestdataConstraintVerifierExtendedSolution("Generated Solution 0");
        List<TestdataValue> valueList = new ArrayList<>();
        List<String> secondValueList = new ArrayList<>();
        for (int i = 0; i < valueListSize; i++) {
            TestdataValue value = new TestdataValue("Generated Value " + i);
            valueList.add(value);
            secondValueList.add(value.getCode());
        }
        solution.setValueList(valueList);
        solution.setStringValueList(secondValueList);
        List<TestdataConstraintVerifierFirstEntity> entityList = new ArrayList<>();
        List<TestdataConstraintVerifierSecondEntity> secondEntityList = new ArrayList<>();
        for (int i = 0; i < entityListSize; i++) {
            if (i % 2 == 0) {
                TestdataValue value = valueList.get(i % valueListSize);
                TestdataConstraintVerifierFirstEntity entity =
                        new TestdataConstraintVerifierFirstEntity("Generated Entity " + i, value);
                entityList.add(entity);
            } else {
                String value = secondValueList.get(i / valueListSize);
                TestdataConstraintVerifierSecondEntity entity =
                        new TestdataConstraintVerifierSecondEntity("Generated Entity " + i, value);
                secondEntityList.add(entity);
            }

        }
        solution.setEntityList(entityList);
        solution.setSecondEntityList(secondEntityList);
        return solution;
    }

    private List<String> stringValueList;
    private List<TestdataConstraintVerifierSecondEntity> secondEntityList;

    public TestdataConstraintVerifierExtendedSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "stringValueRange")
    @ProblemFactCollectionProperty
    public List<String> getStringValueList() {
        return stringValueList;
    }

    public void setStringValueList(List<String> stringValueList) {
        this.stringValueList = stringValueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataConstraintVerifierSecondEntity> getSecondEntityList() {
        return secondEntityList;
    }

    public void setSecondEntityList(List<TestdataConstraintVerifierSecondEntity> secondEntityList) {
        this.secondEntityList = secondEntityList;
    }
}
