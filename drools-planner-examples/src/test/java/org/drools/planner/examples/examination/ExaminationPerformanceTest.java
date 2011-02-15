/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.examples.examination;

import java.io.File;

import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.examples.common.app.SolverPerformanceTest;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.examination.persistence.ExaminationDaoImpl;
import org.junit.Test;

public class ExaminationPerformanceTest extends SolverPerformanceTest {

    @Override
    protected String createSolverConfigResource() {
        return "/org/drools/planner/examples/examination/solver/examinationSolverConfig.xml";
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new ExaminationDaoImpl();
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 60000)
    public void solveComp_set1_initializedScore7925soft() {
        File unsolvedDataFile = new File("data/examination/unsolved/exam_comp_set1_initialized.xml");
        runSpeedTest(unsolvedDataFile, "0hard/-7925soft");
    }

    @Test(timeout = 60000)
    public void solveDebugComp_set1_initializedScore8072soft() {
        File unsolvedDataFile = new File("data/examination/unsolved/exam_comp_set1_initialized.xml");
        runSpeedTest(unsolvedDataFile, "0hard/-8072soft", EnvironmentMode.DEBUG);
    }

}
