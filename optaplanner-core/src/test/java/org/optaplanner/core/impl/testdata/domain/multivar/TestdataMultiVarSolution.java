/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.testdata.domain.multivar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataUtil;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataMultiVarSolution extends TestdataObject implements Solution<SimpleScore> {

    public static SolutionDescriptor buildSolutionDescriptor() {
        return TestdataUtil.buildSolutionDescriptor(TestdataMultiVarSolution.class, TestdataMultiVarEntity.class);
    }

    private List<TestdataValue> valueList;
    private List<TestdataOtherValue> otherValueList;
    private List<TestdataMultiVarEntity> multiVarEntityList;

    private SimpleScore score;

    public TestdataMultiVarSolution() {
    }

    public TestdataMultiVarSolution(String code) {
        super(code);
    }

    public List<TestdataValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataValue> valueList) {
        this.valueList = valueList;
    }

    public List<TestdataOtherValue> getOtherValueList() {
        return otherValueList;
    }

    public void setOtherValueList(List<TestdataOtherValue> otherValueList) {
        this.otherValueList = otherValueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataMultiVarEntity> getMultiVarEntityList() {
        return multiVarEntityList;
    }

    public void setMultiVarEntityList(List<TestdataMultiVarEntity> multiVarEntityList) {
        this.multiVarEntityList = multiVarEntityList;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Collection<Object> getProblemFacts() {
        List<Object> problemFacts = new ArrayList<Object>(valueList.size() + otherValueList.size());
        problemFacts.addAll(valueList);
        problemFacts.addAll(otherValueList);
        return problemFacts;
    }

}
