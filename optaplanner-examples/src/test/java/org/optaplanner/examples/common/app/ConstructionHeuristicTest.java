/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.examples.common.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.business.ProblemFileComparator;
import org.optaplanner.examples.common.persistence.SolutionDao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.*;

@RunWith(Parameterized.class)
public abstract class ConstructionHeuristicTest extends LoggingTest {


    protected static Collection<Object[]> buildParameters(SolutionDao solutionDao, String... unsolvedFileNames) {
        ConstructionHeuristicPhaseConfig.ConstructionHeuristicType[] types
                = ConstructionHeuristicPhaseConfig.ConstructionHeuristicType.values();
        List<Object[]> filesAsParameters = new ArrayList<Object[]>(unsolvedFileNames.length * types.length);
        File dataDir = solutionDao.getDataDir();
        File unsolvedDataDir = new File(dataDir, "unsolved");
        for (String unsolvedFileName : unsolvedFileNames) {
            File unsolvedFile = new File(unsolvedDataDir, unsolvedFileName);
            if (!unsolvedFile.exists()) {
                throw new IllegalStateException("The directory unsolvedFile (" + unsolvedFile.getAbsolutePath()
                        + ") does not exist.");
            }
            for (ConstructionHeuristicPhaseConfig.ConstructionHeuristicType type : types) {
                filesAsParameters.add(new Object[]{unsolvedFile, type});
            }
        }
        return filesAsParameters;
    }

    protected SolutionDao solutionDao;
    protected File dataFile;
    protected ConstructionHeuristicPhaseConfig.ConstructionHeuristicType constructionHeuristicType;

    protected ConstructionHeuristicTest(File dataFile,
            ConstructionHeuristicPhaseConfig.ConstructionHeuristicType constructionHeuristicType) {
        this.dataFile = dataFile;
        this.constructionHeuristicType = constructionHeuristicType;
    }

    @Before
    public void setUp() {
        solutionDao = createSolutionDao();
    }

    protected abstract String createSolverConfigResource();

    protected abstract SolutionDao createSolutionDao();

    protected Solution readPlanningProblem() {
        return solutionDao.readSolution(dataFile);
    }

    @Test
    public void runConstructionHeuristic() {
        SolverFactory solverFactory = buildSolverFactory();
        Solution planningProblem = readPlanningProblem();
        Solver solver = solverFactory.buildSolver();

        solver.solve(planningProblem);
        Solution bestSolution = solver.getBestSolution();
        assertNotNull(bestSolution);
    }

    protected SolverFactory buildSolverFactory() {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(createSolverConfigResource());
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        solverConfig.setTerminationConfig(new TerminationConfig());
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(constructionHeuristicType);
        solverConfig.setPhaseConfigList(Arrays.<PhaseConfig>asList(constructionHeuristicPhaseConfig));
        return solverFactory;
    }

}
