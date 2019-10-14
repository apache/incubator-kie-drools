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
package org.optaplanner.core.impl.phase;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mockito;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.mockito.Mockito.*;

public class PhaseLifecycleTest {

    @Test
    public void verifyEventCounts() {
        PhaseLifecycleListener listener = mock(PhaseLifecycleListener.class);
        // prepare solver
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        // prepare solution
        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        final int entitiesCount = 17;
        ArrayList<TestdataEntity> entities = new ArrayList<>(entitiesCount);
        for (int i = 0; i < entitiesCount; i++) {
            entities.add(new TestdataEntity(String.valueOf(i)));
        }
        solution.setEntityList(entities);

        // add listener mock and solve
        ((DefaultSolver<TestdataSolution>) solver).addPhaseLifecycleListener(listener);
        TestdataSolution solvedSolution = solver.solve(solution);

        // step count = number of uninitialized entities (CH) + LS step count limit
        final int stepCount = entitiesCount + PlannerTestUtils.TERMINATION_STEP_COUNT_LIMIT;
        final int phaseCount = solverConfig.getPhaseConfigList().size();
        PlannerAssert.verifyPhaseLifecycle(listener, 1, phaseCount, stepCount);

        // forget previous invocations
        Mockito.<PhaseLifecycleListener<?>>reset(listener);

        // uninitialize 1 entity and solve again
        solvedSolution.getEntityList().get(0).setValue(null);
        solver.solve(solvedSolution);
        PlannerAssert.verifyPhaseLifecycle(listener, 1, phaseCount, 1 + PlannerTestUtils.TERMINATION_STEP_COUNT_LIMIT);

        // forget previous invocations
        Mockito.<PhaseLifecycleListener<?>>reset(listener);

        // remove listener and solve again
        ((DefaultSolver<TestdataSolution>) solver).removePhaseLifecycleListener(listener);
        solver.solve(solution);
        PlannerAssert.verifyPhaseLifecycle(listener, 0, 0, 0);
    }

}
