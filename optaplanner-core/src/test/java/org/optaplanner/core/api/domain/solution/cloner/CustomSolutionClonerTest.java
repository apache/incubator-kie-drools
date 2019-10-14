/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.api.domain.solution.cloner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.clone.customcloner.TestdataCorrectlyClonedSolution;
import org.optaplanner.core.impl.testdata.domain.clone.customcloner.TestdataEntitiesNotClonedSolution;
import org.optaplanner.core.impl.testdata.domain.clone.customcloner.TestdataScoreNotClonedSolution;
import org.optaplanner.core.impl.testdata.domain.clone.customcloner.TestdataScoreNotEqualSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class CustomSolutionClonerTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void clonedUsingCustomCloner() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataCorrectlyClonedSolution.class, TestdataEntity.class);
        solverConfig.setEnvironmentMode(EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT);

        TestdataCorrectlyClonedSolution solution = new TestdataCorrectlyClonedSolution();
        TestdataCorrectlyClonedSolution solved = PlannerTestUtils.solve(solverConfig, solution);
        assertTrue("Custom solution cloner was not used", solved.isClonedByCustomCloner());
    }

    @Test
    public void scoreNotCloned() {
        // RHBRMS-1430 Possible NPE when custom cloner doesn't clone score
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataScoreNotClonedSolution.class, TestdataEntity.class);
        solverConfig.setEnvironmentMode(EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT);

        TestdataScoreNotClonedSolution solution = new TestdataScoreNotClonedSolution();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Cloning corruption: the original's score ");
        PlannerTestUtils.solve(solverConfig, solution);
    }

    @Test
    public void scoreNotEqual() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataScoreNotEqualSolution.class, TestdataEntity.class);
        solverConfig.setEnvironmentMode(EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT);

        TestdataScoreNotEqualSolution solution = new TestdataScoreNotEqualSolution();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Cloning corruption: the original's score ");
        PlannerTestUtils.solve(solverConfig, solution);
    }

    @Test
    public void entitiesNotCloned() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataEntitiesNotClonedSolution.class, TestdataEntity.class);
        solverConfig.setEnvironmentMode(EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT);

        TestdataEntitiesNotClonedSolution solution = new TestdataEntitiesNotClonedSolution();

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Cloning corruption: the same entity ");
        PlannerTestUtils.solve(solverConfig, solution);
    }

}
