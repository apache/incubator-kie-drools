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

package org.optaplanner.core.impl.testdata.domain.setbased;

import java.util.Collection;
import java.util.Set;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataUtil;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataSetBasedSolution extends TestdataObject implements Solution<SimpleScore> {

    public static SolutionDescriptor buildSolutionDescriptor() {
        return TestdataUtil.buildSolutionDescriptor(TestdataSetBasedSolution.class, TestdataSetBasedEntity.class);
    }

    private Set<TestdataValue> valueSet;
    private Set<TestdataSetBasedEntity> entitySet;

    private SimpleScore score;

    public TestdataSetBasedSolution() {
    }

    public TestdataSetBasedSolution(String code) {
        super(code);
    }

    public Set<TestdataValue> getValueSet() {
        return valueSet;
    }

    public void setValueSet(Set<TestdataValue> valueSet) {
        this.valueSet = valueSet;
    }

    @PlanningEntityCollectionProperty
    public Set<TestdataSetBasedEntity> getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(Set<TestdataSetBasedEntity> entitySet) {
        this.entitySet = entitySet;
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

    public Collection<? extends Object> getProblemFacts() {
        return valueSet;
    }

}
