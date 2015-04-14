/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.domain.solution.cloner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataUtils;

import static org.junit.Assert.assertTrue;

public class FieldAccessingSolutionClonerTest extends AbstractSolutionClonerTest {

    @Override
    protected <Sol extends Solution> FieldAccessingSolutionCloner<Sol> createSolutionCloner(
            SolutionDescriptor solutionDescriptor) {
        return new FieldAccessingSolutionCloner<Sol>(solutionDescriptor);
    }

    @PlanningSolution
    public static abstract class AbstractTestSolution implements Solution<SimpleScore> {
        List<TestVehicle> vehicles;
        private SimpleScore score;
        List<TestWorkload> workloads;

        @Override
        public SimpleScore getScore() {
            return score;
        }

        @Override
        public void setScore(SimpleScore score) {
            this.score = score;
        }

        @Override
        public Collection<?> getProblemFacts() {
            return vehicles;
        }

        @PlanningEntityCollectionProperty
        @ValueRangeProvider(id = "workloadsRange")
        public List<TestWorkload> getWorkloads() {
            return workloads;
        }

        @ValueRangeProvider(id = "vehiclesRange")
        public List<TestVehicle> getVehicles() {
            return vehicles;
        }

    }

    public static class TestSolution extends AbstractTestSolution {
    }

    @PlanningEntity
    public static abstract class AbstractTestWorkload implements TestStandstill {
        String id;
        private TestStandstill previousStandstill;

        @PlanningVariable(graphType = PlanningVariableGraphType.CHAINED,
                valueRangeProviderRefs = {"vehiclesRange", "workloadsRange"})
        public TestStandstill getPreviousStandstill() {
            return previousStandstill;
        }

        public void setPreviousStandstill(
                TestStandstill previousStandstill) {
            this.previousStandstill = previousStandstill;
        }

        public String toString() {
            return getClass().getSimpleName() + "{id=" + id + "}";
        }
    }

    public static class TestWorkload extends AbstractTestWorkload {

    }

    public static class TestVehicle implements TestStandstill {
        String id;

        public String toString() {
            return getClass().getSimpleName() + "{id=" + id + "}";
        }
    }

    public interface TestStandstill {

    }

    @Test
    public void testCloneSolutionWithInheritedVariables() {
        SolutionDescriptor descriptor = TestdataUtils.buildSolutionDescriptor(AbstractTestSolution.class);
        FieldAccessingSolutionCloner cloner = new FieldAccessingSolutionCloner(descriptor);

        TestSolution solution = createTestSolution();

        TestSolution clone = (TestSolution) cloner.cloneSolution(solution);
        assertTrue(clone != null);
        assertTrue(clone != solution); // a clone has been created that is not the same instance as the original
        assertTrue(clone.workloads != solution.workloads); // the collection of workloads must be a clone...
        assertTrue(clone.workloads.size() == solution.workloads.size()); // ... of same size
        assertTrue(clone.workloads.get(0) != solution.workloads.get(0)); // ... containing clones (deep cloned)
        assertTrue(
                clone.workloads.get(0).getPreviousStandstill() == clone.workloads.get(1)); // ... and correct cross-refs
        assertTrue(clone.workloads.get(1).getPreviousStandstill() == clone.vehicles.get(0));
    }

    private TestSolution createTestSolution() {
        TestSolution s = new TestSolution();
        s.vehicles = new ArrayList<TestVehicle>();
        s.vehicles.add(new TestVehicle());
        s.workloads = new ArrayList<TestWorkload>();
        s.workloads.add(new TestWorkload());
        s.workloads.add(new TestWorkload());
        s.workloads.get(0).setPreviousStandstill(s.workloads.get(1));
        s.workloads.get(1).setPreviousStandstill(s.vehicles.get(0));

        s.workloads.get(0).id = "W1";
        s.workloads.get(1).id = "W2";
        s.vehicles.get(0).id = "V1";
        return s;
    }
}
