/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.placer.EntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedEntityPlacerConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.monitoring.MonitoringConfig;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;
import org.optaplanner.core.config.solver.random.RandomType;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.AbstractFromConfigFactory;
import org.optaplanner.core.impl.constructionheuristic.DefaultConstructionHeuristicPhaseFactory;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.phase.PhaseFactory;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryFactory;
import org.optaplanner.core.impl.solver.change.DefaultProblemChangeDirector;
import org.optaplanner.core.impl.solver.random.DefaultRandomFactory;
import org.optaplanner.core.impl.solver.random.RandomFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecallerFactory;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.BasicPlumbingTermination;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.optaplanner.core.impl.solver.termination.TerminationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Tags;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see SolverFactory
 */
public final class DefaultSolverFactory<Solution_> implements SolverFactory<Solution_> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSolverFactory.class);
    private static final long DEFAULT_RANDOM_SEED = 0L;

    private final SolverConfig solverConfig;
    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final InnerScoreDirectorFactory<Solution_, ?> scoreDirectorFactory;

    public DefaultSolverFactory(SolverConfig solverConfig) {
        this.solverConfig = Objects.requireNonNull(solverConfig, "The solverConfig (" + solverConfig + ") cannot be null.");
        this.solutionDescriptor = buildSolutionDescriptor();
        // Caching score director factory as it potentially does expensive things (eg. Drools KieBase compilation).
        this.scoreDirectorFactory = buildScoreDirectorFactory();
    }

    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public <Score_ extends Score<Score_>> InnerScoreDirectorFactory<Solution_, Score_> getScoreDirectorFactory() {
        return (InnerScoreDirectorFactory<Solution_, Score_>) scoreDirectorFactory;
    }

    @Override
    public Solver<Solution_> buildSolver() {
        boolean daemon_ = Objects.requireNonNullElse(solverConfig.getDaemon(), false);

        SolverScope<Solution_> solverScope = new SolverScope<>();
        MonitoringConfig monitoringConfig = solverConfig.determineMetricConfig();
        solverScope.setMonitoringTags(Tags.empty());
        if (!monitoringConfig.getSolverMetricList().isEmpty()) {
            solverScope.setSolverMetricSet(EnumSet.copyOf(monitoringConfig.getSolverMetricList()));
        } else {
            solverScope.setSolverMetricSet(EnumSet.noneOf(SolverMetric.class));
        }

        EnvironmentMode environmentMode_ = solverConfig.determineEnvironmentMode();
        InnerScoreDirector<Solution_, ?> innerScoreDirector =
                scoreDirectorFactory.buildScoreDirector(true, environmentMode_.isAsserted());
        solverScope.setScoreDirector(innerScoreDirector);
        solverScope.setProblemChangeDirector(new DefaultProblemChangeDirector<>(innerScoreDirector));

        if ((solverScope.isMetricEnabled(SolverMetric.CONSTRAINT_MATCH_TOTAL_STEP_SCORE)
                || solverScope.isMetricEnabled(SolverMetric.CONSTRAINT_MATCH_TOTAL_BEST_SCORE)) &&
                !solverScope.getScoreDirector().isConstraintMatchEnabled()) {
            LOGGER.warn("The metrics [{}, {}] cannot function properly" +
                    " because ConstraintMatches are not supported on the ScoreDirector.",
                    SolverMetric.CONSTRAINT_MATCH_TOTAL_STEP_SCORE.getMeterId(),
                    SolverMetric.CONSTRAINT_MATCH_TOTAL_BEST_SCORE.getMeterId());
        }

        Integer moveThreadCount_ = new MoveThreadCountResolver().resolveMoveThreadCount(solverConfig.getMoveThreadCount());
        BestSolutionRecaller<Solution_> bestSolutionRecaller =
                BestSolutionRecallerFactory.create().buildBestSolutionRecaller(environmentMode_);
        HeuristicConfigPolicy<Solution_> configPolicy = new HeuristicConfigPolicy.Builder<>(environmentMode_,
                moveThreadCount_, solverConfig.getMoveThreadBufferSize(), solverConfig.getThreadFactoryClass(),
                scoreDirectorFactory).build();
        TerminationConfig terminationConfig_ =
                Objects.requireNonNullElseGet(solverConfig.getTerminationConfig(), TerminationConfig::new);
        BasicPlumbingTermination<Solution_> basicPlumbingTermination = new BasicPlumbingTermination<>(daemon_);
        Termination<Solution_> termination = TerminationFactory.<Solution_> create(terminationConfig_)
                .buildTermination(configPolicy, basicPlumbingTermination);
        List<Phase<Solution_>> phaseList = buildPhaseList(configPolicy, bestSolutionRecaller, termination);

        RandomFactory randomFactory = buildRandomFactory(environmentMode_);
        return new DefaultSolver<>(environmentMode_, randomFactory, bestSolutionRecaller, basicPlumbingTermination,
                termination, phaseList, solverScope,
                moveThreadCount_ == null ? SolverConfig.MOVE_THREAD_COUNT_NONE : Integer.toString(moveThreadCount_));
    }

    private SolutionDescriptor<Solution_> buildSolutionDescriptor() {
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
        SolutionDescriptor<Solution_> solutionDescriptor =
                SolutionDescriptor.buildSolutionDescriptor(solverConfig.determineDomainAccessType(),
                        (Class<Solution_>) solverConfig.getSolutionClass(),
                        solverConfig.getGizmoMemberAccessorMap(),
                        solverConfig.getGizmoSolutionClonerMap(),
                        solverConfig.getEntityClassList());
        EnvironmentMode environmentMode = solverConfig.determineEnvironmentMode();
        if (environmentMode.isAsserted()) {
            solutionDescriptor.setAssertModelForCloning(true);
        }
        return solutionDescriptor;
    }

    private InnerScoreDirectorFactory<Solution_, ?> buildScoreDirectorFactory() {
        EnvironmentMode environmentMode = solverConfig.determineEnvironmentMode();
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig_ =
                Objects.requireNonNullElseGet(solverConfig.getScoreDirectorFactoryConfig(),
                        ScoreDirectorFactoryConfig::new);
        ScoreDirectorFactoryFactory<Solution_, ?> scoreDirectorFactoryFactory =
                new ScoreDirectorFactoryFactory<>(scoreDirectorFactoryConfig_);
        return scoreDirectorFactoryFactory.buildScoreDirectorFactory(solverConfig.getClassLoader(), environmentMode,
                solutionDescriptor);
    }

    private RandomFactory buildRandomFactory(EnvironmentMode environmentMode_) {
        RandomFactory randomFactory;
        if (solverConfig.getRandomFactoryClass() != null) {
            if (solverConfig.getRandomType() != null || solverConfig.getRandomSeed() != null) {
                throw new IllegalArgumentException(
                        "The solverConfig with randomFactoryClass (" + solverConfig.getRandomFactoryClass()
                                + ") has a non-null randomType (" + solverConfig.getRandomType()
                                + ") or a non-null randomSeed (" + solverConfig.getRandomSeed() + ").");
            }
            randomFactory = ConfigUtils.newInstance(solverConfig, "randomFactoryClass", solverConfig.getRandomFactoryClass());
        } else {
            RandomType randomType_ = Objects.requireNonNullElse(solverConfig.getRandomType(), RandomType.JDK);
            Long randomSeed_ = solverConfig.getRandomSeed();
            if (solverConfig.getRandomSeed() == null && environmentMode_ != EnvironmentMode.NON_REPRODUCIBLE) {
                randomSeed_ = DEFAULT_RANDOM_SEED;
            }
            randomFactory = new DefaultRandomFactory(randomType_, randomSeed_);
        }
        return randomFactory;
    }

    private List<Phase<Solution_>> buildPhaseList(HeuristicConfigPolicy<Solution_> configPolicy,
            BestSolutionRecaller<Solution_> bestSolutionRecaller, Termination<Solution_> termination) {
        List<PhaseConfig> phaseConfigList_ = solverConfig.getPhaseConfigList();
        if (ConfigUtils.isEmptyCollection(phaseConfigList_)) {
            Collection<EntityDescriptor<Solution_>> genuineEntityDescriptors =
                    configPolicy.getSolutionDescriptor().getGenuineEntityDescriptors();
            Map<Class<?>, List<ListVariableDescriptor<Solution_>>> entityClassToListVariableDescriptorListMap =
                    configPolicy.getSolutionDescriptor()
                            .findListVariableDescriptors()
                            .stream()
                            .collect(Collectors.groupingBy(
                                    listVariableDescriptor -> listVariableDescriptor.getEntityDescriptor().getEntityClass(),
                                    Collectors.mapping(Function.identity(), Collectors.toList())));

            phaseConfigList_ = new ArrayList<>(genuineEntityDescriptors.size() + 1);
            for (EntityDescriptor<Solution_> genuineEntityDescriptor : genuineEntityDescriptors) {
                ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
                EntityPlacerConfig<?> entityPlacerConfig;

                if (entityClassToListVariableDescriptorListMap.containsKey(genuineEntityDescriptor.getEntityClass())) {
                    List<ListVariableDescriptor<Solution_>> listVariableDescriptorList =
                            entityClassToListVariableDescriptorListMap.get(genuineEntityDescriptor.getEntityClass());
                    if (listVariableDescriptorList.size() != 1) {
                        // TODO: Do multiple Construction Heuristics for each list variable descriptor?
                        throw new IllegalArgumentException(
                                "Construction Heuristic phase does not support multiple list variables ("
                                        + listVariableDescriptorList + ") for planning entity (" +
                                        genuineEntityDescriptor.getEntityClass() + ").");
                    }
                    entityPlacerConfig =
                            DefaultConstructionHeuristicPhaseFactory.buildListVariableQueuedValuePlacerConfig(configPolicy,
                                    listVariableDescriptorList.get(0));
                } else {
                    QueuedEntityPlacerConfig queuedEntityPlacerConfig = new QueuedEntityPlacerConfig();
                    queuedEntityPlacerConfig.setEntitySelectorConfig(AbstractFromConfigFactory
                            .getDefaultEntitySelectorConfigForEntity(configPolicy, genuineEntityDescriptor));
                    entityPlacerConfig = queuedEntityPlacerConfig;
                }

                constructionHeuristicPhaseConfig.setEntityPlacerConfig(entityPlacerConfig);
                phaseConfigList_.add(constructionHeuristicPhaseConfig);
            }
            phaseConfigList_.add(new LocalSearchPhaseConfig());
        }
        List<Phase<Solution_>> phaseList = new ArrayList<>(phaseConfigList_.size());
        int phaseIndex = 0;
        for (PhaseConfig phaseConfig : phaseConfigList_) {
            PhaseFactory<Solution_> phaseFactory = PhaseFactory.create(phaseConfig);
            Phase<Solution_> phase =
                    phaseFactory.buildPhase(phaseIndex, configPolicy, bestSolutionRecaller, termination);
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
                // A moveThreadCount beyond 4 is currently typically slower
                // TODO remove limitation after fixing https://issues.redhat.com/browse/PLANNER-2449
                if (resolvedMoveThreadCount > 4) {
                    resolvedMoveThreadCount = 4;
                }
                if (resolvedMoveThreadCount <= 1) {
                    // Fall back to single threaded solving with no move threads.
                    // To deliberately enforce 1 moveThread, set the moveThreadCount explicitly to 1.
                    return null;
                }
            } else {
                resolvedMoveThreadCount = ConfigUtils.resolvePoolSize("moveThreadCount", moveThreadCount,
                        SolverConfig.MOVE_THREAD_COUNT_NONE, SolverConfig.MOVE_THREAD_COUNT_AUTO);
            }
            if (resolvedMoveThreadCount < 1) {
                throw new IllegalArgumentException("The moveThreadCount (" + moveThreadCount
                        + ") resulted in a resolvedMoveThreadCount (" + resolvedMoveThreadCount
                        + ") that is lower than 1.");
            }
            if (resolvedMoveThreadCount > availableProcessorCount) {
                LOGGER.warn("The resolvedMoveThreadCount ({}) is higher "
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
