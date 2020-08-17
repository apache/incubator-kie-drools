/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.random.RandomType;
import org.optaplanner.core.config.solver.recaller.BestSolutionRecallerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.phase.PhaseFactory;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryFactory;
import org.optaplanner.core.impl.solver.random.DefaultRandomFactory;
import org.optaplanner.core.impl.solver.random.RandomFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.BasicPlumbingTermination;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see SolverFactory
 */
public final class DefaultSolverFactory<Solution_> implements SolverFactory<Solution_> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSolverFactory.class);
    private static final long DEFAULT_RANDOM_SEED = 0L;

    private final SolverConfig solverConfig;

    public DefaultSolverFactory(SolverConfig solverConfig) {
        if (solverConfig == null) {
            throw new IllegalStateException("The solverConfig (" + solverConfig + ") cannot be null.");
        }
        this.solverConfig = solverConfig;
    }

    public InnerScoreDirectorFactory<Solution_> getScoreDirectorFactory() {
        return buildScoreDirectorFactory(solverConfig.determineEnvironmentMode());
    }

    @Override
    public Solver<Solution_> buildSolver() {
        EnvironmentMode environmentMode_ = solverConfig.determineEnvironmentMode();
        boolean daemon_ = defaultIfNull(solverConfig.getDaemon(), false);

        RandomFactory randomFactory = buildRandomFactory(environmentMode_);
        Integer moveThreadCount_ = new MoveThreadCountResolver().resolveMoveThreadCount(solverConfig.getMoveThreadCount());
        InnerScoreDirectorFactory<Solution_> scoreDirectorFactory = buildScoreDirectorFactory(environmentMode_);
        boolean constraintMatchEnabledPreference = environmentMode_.isAsserted();
        SolverScope<Solution_> solverScope = new SolverScope<>();
        solverScope.setScoreDirector(scoreDirectorFactory.buildScoreDirector(true, constraintMatchEnabledPreference));

        BestSolutionRecaller<Solution_> bestSolutionRecaller = new BestSolutionRecallerConfig()
                .buildBestSolutionRecaller(environmentMode_);
        HeuristicConfigPolicy configPolicy = new HeuristicConfigPolicy(environmentMode_,
                moveThreadCount_, solverConfig.getMoveThreadBufferSize(), solverConfig.getThreadFactoryClass(),
                scoreDirectorFactory);
        TerminationConfig terminationConfig_ = solverConfig.getTerminationConfig() == null ? new TerminationConfig()
                : solverConfig.getTerminationConfig();
        BasicPlumbingTermination basicPlumbingTermination = new BasicPlumbingTermination(daemon_);
        Termination termination = terminationConfig_.buildTermination(configPolicy, basicPlumbingTermination);
        List<Phase<Solution_>> phaseList = buildPhaseList(configPolicy, bestSolutionRecaller, termination);
        return new DefaultSolver<>(environmentMode_, randomFactory,
                bestSolutionRecaller, basicPlumbingTermination, termination, phaseList, solverScope);
    }

    /**
     * @param environmentMode never null
     * @return never null
     */
    public InnerScoreDirectorFactory<Solution_> buildScoreDirectorFactory(EnvironmentMode environmentMode) {
        SolutionDescriptor<Solution_> solutionDescriptor = buildSolutionDescriptor();
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig_ = solverConfig.getScoreDirectorFactoryConfig() == null
                ? new ScoreDirectorFactoryConfig()
                : solverConfig.getScoreDirectorFactoryConfig();
        ScoreDirectorFactoryFactory<Solution_> scoreDirectorFactoryFactory =
                new ScoreDirectorFactoryFactory<>(scoreDirectorFactoryConfig_);
        return scoreDirectorFactoryFactory.buildScoreDirectorFactory(solverConfig.getClassLoader(), environmentMode,
                solutionDescriptor);
    }

    /**
     * @return never null
     */
    public SolutionDescriptor<Solution_> buildSolutionDescriptor() {
        if (solverConfig.getSolutionClass() == null) {
            throw new IllegalArgumentException("The solver configuration must have a solutionClass (" +
                    solverConfig.getSolutionClass() +
                    "). If you're using the Quarkus extension or Spring Boot starter, it should have been filled in " +
                    "already.");
        }
        if (ConfigUtils.isEmptyCollection(solverConfig.getEntityClassList())) {
            throw new IllegalArgumentException("The solver configuration must have at least 1 entityClass (" +
                    solverConfig.getEntityClassList() + "). If you're using the Quarkus extension or Spring Boot starter, " +
                    "it should have been filled in already.");
        }
        return SolutionDescriptor.buildSolutionDescriptor((Class<Solution_>) solverConfig.getSolutionClass(),
                solverConfig.getEntityClassList());
    }

    protected RandomFactory buildRandomFactory(EnvironmentMode environmentMode_) {
        RandomFactory randomFactory;
        if (solverConfig.getRandomFactoryClass() != null) {
            if (solverConfig.getRandomType() != null || solverConfig.getRandomSeed() != null) {
                throw new IllegalArgumentException(
                        "The solverConfig with randomFactoryClass (" + solverConfig.getRandomFactoryClass()
                                + ") has a non-null randomType (" + solverConfig.getRandomType()
                                + ") or a non-null randomSeed (" + solverConfig.getRandomSeed() + ").");
            }
            randomFactory = ConfigUtils.newInstance(this, "randomFactoryClass", solverConfig.getRandomFactoryClass());
        } else {
            RandomType randomType_ = defaultIfNull(solverConfig.getRandomType(), RandomType.JDK);
            Long randomSeed_ = solverConfig.getRandomSeed();
            if (solverConfig.getRandomSeed() == null && environmentMode_ != EnvironmentMode.NON_REPRODUCIBLE) {
                randomSeed_ = DEFAULT_RANDOM_SEED;
            }
            randomFactory = new DefaultRandomFactory(randomType_, randomSeed_);
        }
        return randomFactory;
    }

    protected List<Phase<Solution_>> buildPhaseList(HeuristicConfigPolicy configPolicy,
            BestSolutionRecaller<Solution_> bestSolutionRecaller, Termination termination) {
        List<PhaseConfig> phaseConfigList_ = solverConfig.getPhaseConfigList();
        if (ConfigUtils.isEmptyCollection(phaseConfigList_)) {
            phaseConfigList_ = Arrays.asList(
                    new ConstructionHeuristicPhaseConfig(),
                    new LocalSearchPhaseConfig());
        }
        List<Phase<Solution_>> phaseList = new ArrayList<>(phaseConfigList_.size());
        int phaseIndex = 0;
        for (PhaseConfig<?> phaseConfig : phaseConfigList_) {
            PhaseFactory<Solution_> phaseFactory = PhaseFactory.create(phaseConfig);
            Phase<Solution_> phase = phaseFactory.buildPhase(phaseIndex, configPolicy, bestSolutionRecaller, termination);
            phaseList.add(phase);
            phaseIndex++;
        }
        return phaseList;
    }

    // Required for testability as final classes cannot be mocked.
    protected static class MoveThreadCountResolver {

        protected Integer resolveMoveThreadCount(String moveThreadCount) {
            int availableProcessorCount = getAvailableProcessors();
            Integer resolvedMoveThreadCount;
            if (moveThreadCount == null || moveThreadCount.equals(SolverConfig.MOVE_THREAD_COUNT_NONE)) {
                return null;
            } else if (moveThreadCount.equals(SolverConfig.MOVE_THREAD_COUNT_AUTO)) {
                // Leave one for the Operating System and 1 for the solver thread, take the rest
                resolvedMoveThreadCount = (availableProcessorCount - 2);
                if (resolvedMoveThreadCount <= 1) {
                    // Fall back to single threaded solving with no move threads.
                    // To deliberately enforce 1 moveThread, set the moveThreadCount explicitly to 1.
                    return null;
                }
            } else {
                resolvedMoveThreadCount = ConfigUtils.resolveThreadPoolSizeScript(
                        "moveThreadCount", moveThreadCount, SolverConfig.MOVE_THREAD_COUNT_NONE,
                        SolverConfig.MOVE_THREAD_COUNT_AUTO);
            }
            if (resolvedMoveThreadCount < 1) {
                throw new IllegalArgumentException("The moveThreadCount (" + moveThreadCount
                        + ") resulted in a resolvedMoveThreadCount (" + resolvedMoveThreadCount
                        + ") that is lower than 1.");
            }
            if (resolvedMoveThreadCount > availableProcessorCount) {
                logger.warn("The resolvedMoveThreadCount ({}) is higher "
                        + "than the availableProcessorCount ({}), which is counter-efficient.",
                        resolvedMoveThreadCount, availableProcessorCount);
                // Still allow it, to reproduce issues of a high-end server machine on a low-end developer machine
            }
            return resolvedMoveThreadCount;
        }

        protected int getAvailableProcessors() {
            return Runtime.getRuntime().availableProcessors();
        }
    }
}
