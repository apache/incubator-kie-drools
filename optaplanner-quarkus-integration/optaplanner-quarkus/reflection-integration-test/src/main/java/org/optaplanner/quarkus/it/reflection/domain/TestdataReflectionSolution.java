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

package org.optaplanner.quarkus.it.reflection.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class TestdataReflectionSolution {

    @ValueRangeProvider(id = "fieldValueRange")
    private List<String> fieldValueList;

    private List<String> methodValueList;

    @PlanningEntityCollectionProperty
    private List<TestdataReflectionEntity> entityList;

    @PlanningScore
    private HardSoftScore score;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public List<String> getFieldValueList() {
        return fieldValueList;
    }

    public void setFieldValueList(List<String> fieldValueList) {
        this.fieldValueList = fieldValueList;
    }

    public List<String> getMethodValueList() {
        return methodValueList;
    }

    public void setMethodValueList(List<String> methodValueList) {
        this.methodValueList = methodValueList;
    }

    @ValueRangeProvider(id = "methodValueRange")
    public List<String> readMethodValueList() {
        return methodValueList;
    }

    public List<TestdataReflectionEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataReflectionEntity> entityList) {
        this.entityList = entityList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}
