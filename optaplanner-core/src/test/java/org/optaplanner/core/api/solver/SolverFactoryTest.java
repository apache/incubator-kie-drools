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

package org.optaplanner.core.api.solver;

import java.io.IOException;

import org.junit.Test;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.junit.Assert.*;

public class SolverFactoryTest {

    @Test
    @SuppressWarnings("rawtypes")
    public void testdataSolverConfigWithoutGenericsForBackwardsCompatibility() {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml");
        Solver solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void testdataSolverConfig() {
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonExistingSolverConfig() {
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/nonExistingSolverConfig.xml");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void testdataSolverConfigWithClassLoader() throws ClassNotFoundException, IOException {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlResource(
                "divertThroughClassLoader/org/optaplanner/core/api/solver/classloaderTestdataSolverConfig.xml", classLoader);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void cloneSolverFactory() {
        SolverFactory<TestdataSolution> solverFactoryTemplate = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml");
        solverFactoryTemplate.getSolverConfig().setTerminationConfig(new TerminationConfig());
        SolverFactory<TestdataSolution> solverFactory1 = solverFactoryTemplate.cloneSolverFactory();
        SolverFactory<TestdataSolution> solverFactory2 = solverFactoryTemplate.cloneSolverFactory();
        assertNotSame(solverFactory1, solverFactory2);
        solverFactory1.getSolverConfig().getTerminationConfig().setMinutesSpentLimit(1L);
        solverFactory2.getSolverConfig().getTerminationConfig().setMinutesSpentLimit(2L);
        assertEquals((Long) 1L, solverFactory1.getSolverConfig().getTerminationConfig().getMinutesSpentLimit());
        assertEquals((Long) 2L, solverFactory2.getSolverConfig().getTerminationConfig().getMinutesSpentLimit());
        Solver<TestdataSolution> solver1 = solverFactory1.buildSolver();
        Solver<TestdataSolution> solver2 = solverFactory2.buildSolver();
        assertNotSame(solver1, solver2);
    }

}
