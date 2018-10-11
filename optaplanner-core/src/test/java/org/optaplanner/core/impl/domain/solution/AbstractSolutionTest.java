/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.solution;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

import static org.junit.Assert.assertEquals;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class AbstractSolutionTest {

    @Test
    public void getScore() {
        TestdataAbstractSolutionBasedSolution solution = new TestdataAbstractSolutionBasedSolution();
        solution.setScore(null);
        assertEquals(null, solution.getScore());
        solution.setScore(SimpleScore.of(-10));
        assertEquals(SimpleScore.of(-10), solution.getScore());
        solution.setScore(SimpleScore.of(-2));
        assertEquals(SimpleScore.of(-2), solution.getScore());
    }

    @Test
    public void getProblemFacts() {
        TestdataAbstractSolutionBasedSolution solution = new TestdataAbstractSolutionBasedSolution();
        TestdataValue singleValue = new TestdataValue("sv1");
        solution.setSingleValue(singleValue);
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        List<TestdataValue> valueList = Arrays.asList(v1, v2, v3);
        solution.setValueList(valueList);
        List<TestdataEntity> entityList = Arrays.asList(
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"));
        solution.setEntityList(entityList);
        TestdataEntity singleEntity = new TestdataEntity("se1");
        solution.setSingleEntity(singleEntity);
        SimpleScore score = SimpleScore.of(-10);
        solution.setScore(score);
        assertCollectionContainsExactly(solution.getProblemFactList(), singleValue, v1, v2, v3);
    }

    @Test
    public void getProblemFactsWithNullField() {
        TestdataAbstractSolutionBasedSolution solution = new TestdataAbstractSolutionBasedSolution();
        solution.setSingleValue(null);
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        List<TestdataValue> valueList = Arrays.asList(v1, v2, v3);
        solution.setValueList(valueList);
        solution.setEntityList(null);
        solution.setSingleEntity(null);
        solution.setScore(null);
        assertCollectionContainsExactly(solution.getProblemFactList(), v1, v2, v3);
    }

    public static class TestdataAbstractSolutionBasedSolution extends AbstractSolution<SimpleScore> {

        public static SolutionDescriptor buildSolutionDescriptor() {
            return SolutionDescriptor.buildSolutionDescriptor(TestdataAbstractSolutionBasedSolution.class, TestdataEntity.class);
        }

        private TestdataValue singleValue;
        private List<TestdataValue> valueList;
        private List<TestdataEntity> entityList;
        private TestdataEntity singleEntity;

        public TestdataValue getSingleValue() {
            return singleValue;
        }

        public void setSingleValue(TestdataValue singleValue) {
            this.singleValue = singleValue;
        }

        @ValueRangeProvider(id = "valueRange")
        public List<TestdataValue> getValueList() {
            return valueList;
        }

        public void setValueList(List<TestdataValue> valueList) {
            this.valueList = valueList;
        }

        @PlanningEntityCollectionProperty
        public List<TestdataEntity> getEntityList() {
            return entityList;
        }

        public void setEntityList(List<TestdataEntity> entityList) {
            this.entityList = entityList;
        }

        @PlanningEntityProperty
        public TestdataEntity getSingleEntity() {
            return singleEntity;
        }

        public void setSingleEntity(TestdataEntity singleEntity) {
            this.singleEntity = singleEntity;
        }

    }

}
