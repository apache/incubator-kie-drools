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

package org.drools.planner.examples.tsp;

import java.io.File;

import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.examples.cloudbalancing.persistence.CloudBalancingDaoImpl;
import org.drools.planner.examples.common.app.SolverPerformanceTest;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.tsp.persistence.TspDaoImpl;
import org.junit.Test;

public class TspPerformanceTest extends SolverPerformanceTest {

    @Override
    protected String createSolverConfigResource() {
        return "/org/drools/planner/examples/tsp/solver/tspSolverConfig.xml";
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new TspDaoImpl();
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void solveModel_a2_1() {
        File unsolvedDataFile = new File("data/tsp/unsolved/europe40.xml");
        runSpeedTest(unsolvedDataFile, "-218451");
    }

    @Test(timeout = 600000)
    public void solveModel_a2_1Debug() {
        File unsolvedDataFile = new File("data/tsp/unsolved/europe40.xml");
        runSpeedTest(unsolvedDataFile, "-219798", EnvironmentMode.DEBUG);
    }

}
