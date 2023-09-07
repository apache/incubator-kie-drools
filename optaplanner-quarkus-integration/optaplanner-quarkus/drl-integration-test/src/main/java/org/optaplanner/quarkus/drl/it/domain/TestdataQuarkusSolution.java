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

package org.optaplanner.quarkus.drl.it.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

@PlanningSolution
public class TestdataQuarkusSolution {

    private List<String> leftValueList;
    private List<String> rightValueList;
    private List<TestdataQuarkusEntity> entityList;

    private SimpleScore score;

    @ValueRangeProvider(id = "leftValueRange")
    @ProblemFactCollectionProperty
    public List<String> getLeftValueList() {
        return leftValueList;
    }

    public void setLeftValueList(List<String> valueList) {
        this.leftValueList = valueList;
    }

    @ValueRangeProvider(id = "rightValueRange")
    @ProblemFactCollectionProperty
    public List<String> getRightValueList() {
        return rightValueList;
    }

    public void setRightValueList(List<String> valueList) {
        this.rightValueList = valueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataQuarkusEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataQuarkusEntity> entityList) {
        this.entityList = entityList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

}
