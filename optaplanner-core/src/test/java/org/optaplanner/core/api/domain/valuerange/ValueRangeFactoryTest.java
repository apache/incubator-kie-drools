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

package org.optaplanner.core.api.domain.valuerange;

import java.util.Arrays;

import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.testdata.domain.valuerange.TestdataValueRangeEntity;
import org.optaplanner.core.impl.testdata.domain.valuerange.TestdataValueRangeSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class ValueRangeFactoryTest {

    @Test
    public void solve() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataValueRangeSolution.class, TestdataValueRangeEntity.class);

        TestdataValueRangeSolution solution = new TestdataValueRangeSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataValueRangeEntity("e1"), new TestdataValueRangeEntity("e2")));

        solution = PlannerTestUtils.solve(solverConfig, solution);;
        assertNotNull(solution);
    }

}
