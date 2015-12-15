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

package org.optaplanner.examples.meetingscheduling.app;

import java.io.File;

import org.junit.Test;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.meetingscheduling.persistence.MeetingSchedulingDao;

public class MeetingSchedulingPerformanceTest extends SolverPerformanceTest {

    @Override
    protected String createSolverConfigResource() {
        return MeetingSchedulingApp.SOLVER_CONFIG;
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new MeetingSchedulingDao();
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void solveModel_200computers_600processes() {
        File unsolvedDataFile = new File("data/meetingscheduling/unsolved/50meetings-160timegrains-5rooms.xml");
        runSpeedTest(unsolvedDataFile, "-19hard/-115medium/-4046soft");
    }

    @Test(timeout = 600000)
    public void solveModel_200computers_600processesFastAssert() {
        File unsolvedDataFile = new File("data/meetingscheduling/unsolved/50meetings-160timegrains-5rooms.xml");
        runSpeedTest(unsolvedDataFile, "-29hard/-70medium/-3399soft", EnvironmentMode.FAST_ASSERT);
    }

}
