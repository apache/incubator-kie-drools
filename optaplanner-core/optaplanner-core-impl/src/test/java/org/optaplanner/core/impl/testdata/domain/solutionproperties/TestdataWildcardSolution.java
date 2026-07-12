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

package org.optaplanner.core.impl.testdata.domain.solutionproperties;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataWildcardSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataWildcardSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataWildcardSolution.class, TestdataEntity.class);
    }

    private List<? extends TestdataValue> extendsValueList;
    private List<? super TestdataValue> supersValueList;
    private List<? extends TestdataEntity> extendsEntityList;

    private SimpleScore score;

    public TestdataWildcardSolution() {
    }

    public TestdataWildcardSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<? extends TestdataValue> getExtendsValueList() {
        return extendsValueList;
    }

    public void setExtendsValueList(List<? extends TestdataValue> extendsValueList) {
        this.extendsValueList = extendsValueList;
    }

    @ProblemFactCollectionProperty
    public List<? super TestdataValue> getSupersValueList() {
        return supersValueList;
    }

    public void setSupersValueList(List<? super TestdataValue> supersValueList) {
        this.supersValueList = supersValueList;
    }

    @PlanningEntityCollectionProperty
    public List<? extends TestdataEntity> getExtendsEntityList() {
        return extendsEntityList;
    }

    public void setExtendsEntityList(List<? extends TestdataEntity> extendsEntityList) {
        this.extendsEntityList = extendsEntityList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

}
