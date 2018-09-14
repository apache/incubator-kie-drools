/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.domain.ScanAnnotatedClassesConfig;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.random.RandomType;
import org.optaplanner.core.config.solver.recaller.BestSolutionRecallerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.random.DefaultRandomFactory;
import org.optaplanner.core.impl.solver.random.RandomFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.solver.termination.BasicPlumbingTermination;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.ObjectUtils.*;

@XStreamAlias("solver")
public class SolverConfig extends AbstractConfig<SolverConfig> {

    public static final String MOVE_THREAD_COUNT_NONE = "NONE";
    public static final String MOVE_THREAD_COUNT_AUTO = "AUTO";
    protected static final long DEFAULT_RANDOM_SEED = 0L;

    private static final Logger logger = LoggerFactory.getLogger(SolverConfig.class);

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected EnvironmentMode environmentMode = null;
    protected Boolean daemon = null;
    protected RandomType randomType = null;
    protected Long randomSeed = null;
    protected Class<? extends RandomFactory> randomFactoryClass = null;
    protected String moveThreadCount = null;
    protected Integer moveThreadBufferSize = null;
    protected Class<? extends ThreadFactory> threadFactoryClass = null;

    @XStreamAlias("scanAnnotatedClasses")
    protected ScanAnnotatedClassesConfig scanAnnotatedClassesConfig = null;
    protected Class<?> solutionClass = null;
    @XStreamImplicit(itemFieldName = "entityClass")
    protected List<Class<?>> entityClassList = null;

    @XStreamAlias("scoreDirectorFactory")
    protected ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = null;

    @XStreamAlias("termination")
    private TerminationConfig terminationConfig;

    @XStreamImplicit()
    protected List<PhaseConfig> phaseConfigList = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public SolverConfig() {
    }

    public SolverConfig(SolverConfig inheritedConfig) {
        inherit(inheritedConfig);
        if (environmentMode == EnvironmentMode.PRODUCTION) {
            environmentMode = EnvironmentMode.NON_REPRODUCIBLE;
        }
    }

    public EnvironmentMode getEnvironmentMode() {
        return environmentMode;
    }

    public void setEnvironmentMode(EnvironmentMode environmentMode) {
        this.environmentMode = environmentMode;
    }

    public Boolean getDaemon() {
        return daemon;
    }

    public void setDaemon(Boolean daemon) {
        this.daemon = daemon;
    }

    public RandomType getRandomType() {
        return randomType;
    }

    public void setRandomType(RandomType randomType) {
        this.randomType = randomType;
    }

    public Long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(Long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public Class<? extends RandomFactory> getRandomFactoryClass() {
        return randomFactoryClass;
    }

    public void setRandomFactoryClass(Class<? extends RandomFactory> randomFactoryClass) {
        this.randomFactoryClass = randomFactoryClass;
    }

    public String getMoveThreadCount() {
        return moveThreadCount;
    }

    public void setMoveThreadCount(String moveThreadCount) {
        this.moveThreadCount = moveThreadCount;
    }

    public Integer getMoveThreadBufferSize() {
        return moveThreadBufferSize;
    }

    public void setMoveThreadBufferSize(Integer moveThreadBufferSize) {
        this.moveThreadBufferSize = moveThreadBufferSize;
    }

    public Class<? extends ThreadFactory> getThreadFactoryClass() {
        return threadFactoryClass;
    }

    public void setThreadFactoryClass(Class<? extends ThreadFactory> threadFactoryClass) {
        this.threadFactoryClass = threadFactoryClass;
    }

    public ScanAnnotatedClassesConfig getScanAnnotatedClassesConfig() {
        return scanAnnotatedClassesConfig;
    }

    public void setScanAnnotatedClassesConfig(ScanAnnotatedClassesConfig scanAnnotatedClassesConfig) {
        this.scanAnnotatedClassesConfig = scanAnnotatedClassesConfig;
    }

    public Class<?> getSolutionClass() {
        return solutionClass;
    }

    public void setSolutionClass(Class<?> solutionClass) {
        this.solutionClass = solutionClass;
    }

    public List<Class<?>> getEntityClassList() {
        return entityClassList;
    }

    public void setEntityClassList(List<Class<?>> entityClassList) {
        this.entityClassList = entityClassList;
    }

    public ScoreDirectorFactoryConfig getScoreDirectorFactoryConfig() {
        return scoreDirectorFactoryConfig;
    }

    public void setScoreDirectorFactoryConfig(ScoreDirectorFactoryConfig scoreDirectorFactoryConfig) {
        this.scoreDirectorFactoryConfig = scoreDirectorFactoryConfig;
    }

    public TerminationConfig getTerminationConfig() {
        return terminationConfig;
    }

    public void setTerminationConfig(TerminationConfig terminationConfig) {
        this.terminationConfig = terminationConfig;
    }

    public List<PhaseConfig> getPhaseConfigList() {
        return phaseConfigList;
    }

    public void setPhaseConfigList(List<PhaseConfig> phaseConfigList) {
        this.phaseConfigList = phaseConfigList;
    }

    // ************************************************************************
    // Smart getters
    // ************************************************************************

    public EnvironmentMode determineEnvironmentMode() {
        if (environmentMode == EnvironmentMode.PRODUCTION) {
            environmentMode = EnvironmentMode.NON_REPRODUCIBLE;
        }
        return defaultIfNull(environmentMode, EnvironmentMode.REPRODUCIBLE);
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public void offerRandomSeedFromSubSingleIndex(long subSingleIndex) {
        if (environmentMode == null || environmentMode.isReproducible()) {
            if (randomFactoryClass == null && randomSeed == null) {
                randomSeed = subSingleIndex;
            }
        }
    }

    /**
     * @param configContext never null
     * @return never null
     */
    public <Solution_> Solver<Solution_> buildSolver(SolverConfigContext configContext) {
        configContext.validate();
        EnvironmentMode environmentMode_ = determineEnvironmentMode();
        boolean daemon_ = defaultIfNull(daemon, false);

        RandomFactory randomFactory = buildRandomFactory(environmentMode_);
        Integer moveThreadCount_ = resolveMoveThreadCount();
        SolutionDescriptor<Solution_> solutionDescriptor = buildSolutionDescriptor(configContext);
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig_
                = scoreDirectorFactoryConfig == null ? new ScoreDirectorFactoryConfig()
                : scoreDirectorFactoryConfig;
        InnerScoreDirectorFactory<Solution_> scoreDirectorFactory = scoreDirectorFactoryConfig_.buildScoreDirectorFactory(
                configContext, environmentMode_, solutionDescriptor);
        boolean constraintMatchEnabledPreference = environmentMode_.isAsserted();
        DefaultSolverScope<Solution_> solverScope = new DefaultSolverScope<>();
        solverScope.setScoreDirector(scoreDirectorFactory.buildScoreDirector(true, constraintMatchEnabledPreference));

        BestSolutionRecaller<Solution_> bestSolutionRecaller = new BestSolutionRecallerConfig()
                .buildBestSolutionRecaller(environmentMode_);
        HeuristicConfigPolicy configPolicy = new HeuristicConfigPolicy(environmentMode_,
                moveThreadCount_, moveThreadBufferSize, threadFactoryClass,
                scoreDirectorFactory);
        TerminationConfig terminationConfig_ = terminationConfig == null ? new TerminationConfig()
                : terminationConfig;
        BasicPlumbingTermination basicPlumbingTermination = new BasicPlumbingTermination(daemon_);
        Termination termination = terminationConfig_.buildTermination(configPolicy, basicPlumbingTermination);
        List<Phase<Solution_>> phaseList = buildPhaseList(configPolicy, bestSolutionRecaller, termination);
        return new DefaultSolver<>(environmentMode_, randomFactory,
                bestSolutionRecaller, basicPlumbingTermination, termination, phaseList, solverScope);
    }

    protected RandomFactory buildRandomFactory(EnvironmentMode environmentMode_) {
        RandomFactory randomFactory;
        if (randomFactoryClass != null) {
            if (randomType != null || randomSeed != null) {
                throw new IllegalArgumentException(
                        "The solverConfig with randomFactoryClass (" + randomFactoryClass
                                + ") has a non-null randomType (" + randomType
                                + ") or a non-null randomSeed (" + randomSeed + ").");
            }
            randomFactory = ConfigUtils.newInstance(this, "randomFactoryClass", randomFactoryClass);
        } else {
            RandomType randomType_ = defaultIfNull(randomType, RandomType.JDK);
            Long randomSeed_ = randomSeed;
            if (randomSeed == null && environmentMode_ != EnvironmentMode.NON_REPRODUCIBLE) {
                randomSeed_ = DEFAULT_RANDOM_SEED;
            }
            randomFactory = new DefaultRandomFactory(randomType_, randomSeed_);
        }
        return randomFactory;
    }

    protected int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    protected Integer resolveMoveThreadCount() {
        int availableProcessorCount = getAvailableProcessors();
        Integer resolvedMoveThreadCount;
        if (moveThreadCount == null || moveThreadCount.equals(MOVE_THREAD_COUNT_NONE)) {
            return null;
        } else if (moveThreadCount.equals(MOVE_THREAD_COUNT_AUTO)) {
            // Leave one for the Operating System and 1 for the solver thread, take the rest
            resolvedMoveThreadCount = (availableProcessorCount - 2);
            if (resolvedMoveThreadCount <= 1) {
                // Fall back to single threaded solving with no move threads.
                // To deliberately enforce 1 moveThread, set the moveThreadCount explicitly to 1.
                return null;
            }
        } else {
            resolvedMoveThreadCount = ConfigUtils.resolveThreadPoolSizeScript(
                    "moveThreadCount", moveThreadCount, MOVE_THREAD_COUNT_NONE, MOVE_THREAD_COUNT_AUTO);
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

    public <Solution_> SolutionDescriptor<Solution_> buildSolutionDescriptor(SolverConfigContext configContext) {
        ScoreDefinition deprecatedScoreDefinition = scoreDirectorFactoryConfig == null ? null
                : scoreDirectorFactoryConfig.buildDeprecatedScoreDefinition();
        if (scanAnnotatedClassesConfig != null) {
            if (solutionClass != null || entityClassList != null) {
                throw new IllegalArgumentException("The solver configuration with scanAnnotatedClasses ("
                        + scanAnnotatedClassesConfig + ") cannot also have a solutionClass (" + solutionClass
                        + ") or an entityClass (" + entityClassList + ").\n"
                        + "  Please decide between automatic scanning or manual referencing.");
            }
            return scanAnnotatedClassesConfig.buildSolutionDescriptor(configContext, deprecatedScoreDefinition);
        } else {
            if (solutionClass == null) {
                throw new IllegalArgumentException("The solver configuration must have a solutionClass (" + solutionClass
                        + "), if it has no scanAnnotatedClasses (" + scanAnnotatedClassesConfig + ").");
            }
            if (ConfigUtils.isEmptyCollection(entityClassList)) {
                throw new IllegalArgumentException(
                        "The solver configuration must have at least 1 entityClass (" + entityClassList
                        + "), if it has no scanAnnotatedClasses (" + scanAnnotatedClassesConfig + ").");
            }
            return SolutionDescriptor.buildSolutionDescriptor((Class<Solution_>) solutionClass, entityClassList, deprecatedScoreDefinition);
        }
    }

    protected <Solution_> List<Phase<Solution_>> buildPhaseList(HeuristicConfigPolicy configPolicy,
            BestSolutionRecaller bestSolutionRecaller, Termination termination) {
        List<PhaseConfig> phaseConfigList_ = phaseConfigList;
        if (ConfigUtils.isEmptyCollection(phaseConfigList_)) {
            phaseConfigList_ = Arrays.asList(
                    new ConstructionHeuristicPhaseConfig(),
                    new LocalSearchPhaseConfig());
        }
        List<Phase<Solution_>> phaseList = new ArrayList<>(phaseConfigList_.size());
        int phaseIndex = 0;
        for (PhaseConfig phaseConfig : phaseConfigList_) {
            Phase<Solution_> phase = phaseConfig.buildPhase(phaseIndex, configPolicy,
                    bestSolutionRecaller, termination);
            phaseList.add(phase);
            phaseIndex++;
        }
        return phaseList;
    }

    @Override
    public void inherit(SolverConfig inheritedConfig) {
        environmentMode = ConfigUtils.inheritOverwritableProperty(environmentMode, inheritedConfig.getEnvironmentMode());
        daemon = ConfigUtils.inheritOverwritableProperty(daemon, inheritedConfig.getDaemon());
        randomType = ConfigUtils.inheritOverwritableProperty(randomType, inheritedConfig.getRandomType());
        randomSeed = ConfigUtils.inheritOverwritableProperty(randomSeed, inheritedConfig.getRandomSeed());
        randomFactoryClass = ConfigUtils.inheritOverwritableProperty(
                randomFactoryClass, inheritedConfig.getRandomFactoryClass());
        moveThreadCount = ConfigUtils.inheritOverwritableProperty(moveThreadCount,
                inheritedConfig.getMoveThreadCount());
        moveThreadBufferSize = ConfigUtils.inheritOverwritableProperty(moveThreadBufferSize,
                inheritedConfig.getMoveThreadBufferSize());
        threadFactoryClass = ConfigUtils.inheritOverwritableProperty(threadFactoryClass,
                inheritedConfig.getThreadFactoryClass());
        scanAnnotatedClassesConfig = ConfigUtils.inheritConfig(scanAnnotatedClassesConfig, inheritedConfig.getScanAnnotatedClassesConfig());
        solutionClass = ConfigUtils.inheritOverwritableProperty(solutionClass, inheritedConfig.getSolutionClass());
        entityClassList = ConfigUtils.inheritMergeableListProperty(
                entityClassList, inheritedConfig.getEntityClassList());
        scoreDirectorFactoryConfig = ConfigUtils.inheritConfig(scoreDirectorFactoryConfig, inheritedConfig.getScoreDirectorFactoryConfig());
        terminationConfig = ConfigUtils.inheritConfig(terminationConfig, inheritedConfig.getTerminationConfig());
        phaseConfigList = ConfigUtils.inheritMergeableListConfig(
                phaseConfigList, inheritedConfig.getPhaseConfigList());
    }

}
