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

package org.drools.planner.examples.nqueens.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.planner.config.XmlSolverFactory;
import org.drools.planner.config.constructionheuristic.ConstructionHeuristicSolverPhaseConfig;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.move.MoveSelectorConfig;
import org.drools.planner.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.drools.planner.config.localsearch.LocalSearchSolverPhaseConfig;
import org.drools.planner.config.phase.SolverPhaseConfig;
import org.drools.planner.config.score.director.ScoreDirectorFactoryConfig;
import org.drools.planner.config.solver.SolverConfig;
import org.drools.planner.config.termination.TerminationConfig;
import org.drools.planner.core.Solver;
import org.drools.planner.core.constructionheuristic.greedyFit.decider.ConstructionHeuristicPickEarlyType;
import org.drools.planner.examples.common.app.CommonApp;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.nqueens.domain.NQueens;
import org.drools.planner.examples.nqueens.domain.Queen;
import org.drools.planner.examples.nqueens.persistence.NQueensDaoImpl;
import org.drools.planner.examples.nqueens.swingui.NQueensPanel;

public class NQueensApp extends CommonApp {

    public static final String SOLVER_CONFIG
            = "/org/drools/planner/examples/nqueens/solver/nqueensSolverConfig.xml";

    public static void main(String[] args) {
        new NQueensApp().init();
    }

    @Override
    protected Solver createSolver() {
        XmlSolverFactory solverFactory = new XmlSolverFactory();
        solverFactory.configure(SOLVER_CONFIG);
        return solverFactory.buildSolver();
    }

    protected Solver createSolverByApi() {
        // Not recommended! It is highly recommended to use XmlSolverFactory with an XML configuration instead.
        SolverConfig solverConfig = new SolverConfig();

        solverConfig.setSolutionClass(NQueens.class);
        Set<Class<?>> planningEntityClassSet = new HashSet<Class<?>>();
        planningEntityClassSet.add(Queen.class);
        solverConfig.setPlanningEntityClassSet(planningEntityClassSet);

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = solverConfig.getScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setScoreDefinitionType(ScoreDirectorFactoryConfig.ScoreDefinitionType.SIMPLE);
        scoreDirectorFactoryConfig.setScoreDrlList(
                Arrays.asList("/org/drools/planner/examples/nqueens/solver/nQueensScoreRules.drl"));

        TerminationConfig terminationConfig = solverConfig.getTerminationConfig();
        terminationConfig.setScoreAttained("0");
        List<SolverPhaseConfig> solverPhaseConfigList = new ArrayList<SolverPhaseConfig>();
        ConstructionHeuristicSolverPhaseConfig constructionHeuristicSolverPhaseConfig
                = new ConstructionHeuristicSolverPhaseConfig();
        constructionHeuristicSolverPhaseConfig.setConstructionHeuristicType(
                ConstructionHeuristicSolverPhaseConfig.ConstructionHeuristicType.FIRST_FIT_DECREASING);
        constructionHeuristicSolverPhaseConfig.setConstructionHeuristicPickEarlyType(
                ConstructionHeuristicPickEarlyType.FIRST_LAST_STEP_SCORE_EQUAL_OR_IMPROVING);
        solverPhaseConfigList.add(constructionHeuristicSolverPhaseConfig);
        LocalSearchSolverPhaseConfig localSearchSolverPhaseConfig = new LocalSearchSolverPhaseConfig();
        ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        changeMoveSelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        localSearchSolverPhaseConfig.setMoveSelectorConfigList(
                Arrays.<MoveSelectorConfig>asList(changeMoveSelectorConfig));
        localSearchSolverPhaseConfig.getAcceptorConfig().setSolutionTabuSize(1000);
        solverPhaseConfigList.add(localSearchSolverPhaseConfig);
        solverConfig.setSolverPhaseConfigList(solverPhaseConfigList);
        return solverConfig.buildSolver();
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new NQueensPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new NQueensDaoImpl();
    }

}
