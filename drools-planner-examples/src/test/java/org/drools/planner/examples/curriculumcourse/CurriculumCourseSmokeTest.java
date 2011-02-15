/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.curriculumcourse;

import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.XmlSolverConfigurer;
import org.drools.planner.config.localsearch.LocalSearchSolverConfig;
import org.drools.planner.config.localsearch.termination.TerminationConfig;
import org.drools.planner.core.Solver;
import org.drools.planner.core.score.DefaultHardAndSoftScore;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.app.LoggingTest;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.curriculumcourse.persistence.CurriculumCourseDaoImpl;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class CurriculumCourseSmokeTest extends LoggingTest {

    public static final String SOLVER_CONFIG
            = "/org/drools/planner/examples/curriculumcourse/solver/curriculumCourseSolverConfig.xml";
    public static final String UNSOLVED_DATA
            = "/org/drools/planner/examples/curriculumcourse/data/testComp01.xml";

    @Test @Ignore
    public void solveComp01() {
        XmlSolverConfigurer configurer = new XmlSolverConfigurer();
        configurer.configure(SOLVER_CONFIG);
        configurer.getConfig().setEnvironmentMode(EnvironmentMode.DEBUG);
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setMaximumStepCount(50);
        ((LocalSearchSolverConfig) configurer.getConfig()).setTerminationConfig(terminationConfig);

        Solver solver = configurer.buildSolver();
        SolutionDao solutionDao = new CurriculumCourseDaoImpl();
        Solution startingSolution = solutionDao.readSolution(getClass().getResourceAsStream(UNSOLVED_DATA));
        solver.setStartingSolution(startingSolution);
        solver.solve();
        Solution bestSolution = solver.getBestSolution();
        assertNotNull(bestSolution);
        Score bestScore = solver.getBestSolution().getScore();
        assertTrue(bestScore.compareTo(DefaultHardAndSoftScore.valueOf(0, -500)) > 0);
    }

}
