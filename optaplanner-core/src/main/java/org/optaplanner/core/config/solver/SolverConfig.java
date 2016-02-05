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
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.domain.ScanAnnotatedClassesConfig;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.random.RandomType;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.BasicPlumbingTermination;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.random.DefaultRandomFactory;
import org.optaplanner.core.impl.solver.random.RandomFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@XStreamAlias("solver")
public class SolverConfig extends AbstractConfig<SolverConfig> {

    protected static final long DEFAULT_RANDOM_SEED = 0L;

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected EnvironmentMode environmentMode = null;
    protected Boolean daemon = null;
    protected RandomType randomType = null;
    protected Long randomSeed = null;
    protected Class<? extends RandomFactory> randomFactoryClass = null;

    @XStreamAlias("scanAnnotatedClasses")
    protected ScanAnnotatedClassesConfig scanAnnotatedClassesConfig = null;
    protected Class<? extends Solution> solutionClass = null;
    @XStreamImplicit(itemFieldName = "entityClass")
    protected List<Class<?>> entityClassList = null;

    @XStreamAlias("scoreDirectorFactory")
    protected ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = null;

    @XStreamAlias("termination")
    private TerminationConfig terminationConfig;

    @XStreamImplicit()
    protected List<PhaseConfig> phaseConfigList = null;

    public SolverConfig() {
    }

    public SolverConfig(SolverConfig inheritedConfig) {
        inherit(inheritedConfig);
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

    public ScanAnnotatedClassesConfig getScanAnnotatedClassesConfig() {
        return scanAnnotatedClassesConfig;
    }

    public void setScanAnnotatedClassesConfig(ScanAnnotatedClassesConfig scanAnnotatedClassesConfig) {
        this.scanAnnotatedClassesConfig = scanAnnotatedClassesConfig;
    }

    public Class<? extends Solution> getSolutionClass() {
        return solutionClass;
    }

    public void setSolutionClass(Class<? extends Solution> solutionClass) {
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
     * @return never null
     * @deprecated Use {@link SolverFactory#buildSolver()} or {@link #buildSolver(SolverConfigContext)} instead.
     */
    @Deprecated
    public <Solution_ extends Solution> Solver<Solution_> buildSolver() {
        return buildSolver(new SolverConfigContext());
    }

    /**
     * @param classLoader sometimes null
     * @return never null
     * @deprecated Use {@link SolverFactory#buildSolver()} or {@link #buildSolver(SolverConfigContext)} instead.
     */
    @Deprecated
    public <Solution_ extends Solution> Solver<Solution_> buildSolver(ClassLoader classLoader) {
        SolverConfigContext configContext = new SolverConfigContext();
        configContext.setClassLoader(classLoader);
        return buildSolver(configContext);
    }

    /**
     * @param configContext never null
     * @return never null
     */
    public <Solution_ extends Solution> Solver<Solution_> buildSolver(SolverConfigContext configContext) {
        configContext.validate();
        DefaultSolver<Solution_> solver = new DefaultSolver<Solution_>();
        EnvironmentMode environmentMode_ = determineEnvironmentMode();
        solver.setEnvironmentMode(environmentMode_);
        boolean daemon_ = defaultIfNull(daemon, false);
        BasicPlumbingTermination basicPlumbingTermination = new BasicPlumbingTermination(daemon_);
        solver.setBasicPlumbingTermination(basicPlumbingTermination);

        solver.setRandomFactory(buildRandomFactory(environmentMode_));
        SolutionDescriptor solutionDescriptor = buildSolutionDescriptor(configContext);
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig_
                = scoreDirectorFactoryConfig == null ? new ScoreDirectorFactoryConfig()
                : scoreDirectorFactoryConfig;
        InnerScoreDirectorFactory scoreDirectorFactory = scoreDirectorFactoryConfig_.buildScoreDirectorFactory(
                configContext, environmentMode_, solutionDescriptor);
        solver.setConstraintMatchEnabledPreference(environmentMode_.isAsserted());
        solver.setScoreDirectorFactory(scoreDirectorFactory);

        HeuristicConfigPolicy configPolicy = new HeuristicConfigPolicy(environmentMode_, scoreDirectorFactory);
        TerminationConfig terminationConfig_ = terminationConfig == null ? new TerminationConfig()
                : terminationConfig;
        Termination termination = terminationConfig_.buildTermination(configPolicy, basicPlumbingTermination);
        solver.setTermination(termination);
        BestSolutionRecaller bestSolutionRecaller = buildBestSolutionRecaller(environmentMode_);
        solver.setBestSolutionRecaller(bestSolutionRecaller);
        solver.setPhaseList(buildPhaseList(configPolicy, bestSolutionRecaller, termination));
        return solver;
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
            if (randomSeed == null && environmentMode_ != EnvironmentMode.PRODUCTION) {
                randomSeed_ = DEFAULT_RANDOM_SEED;
            }
            randomFactory = new DefaultRandomFactory(randomType_, randomSeed_);
        }
        return randomFactory;
    }

    protected SolutionDescriptor buildSolutionDescriptor(SolverConfigContext configContext) {
        if (scanAnnotatedClassesConfig != null) {
            if (solutionClass != null || entityClassList != null) {
                throw new IllegalArgumentException("The solver configuration with scanAnnotatedClasses ("
                        + scanAnnotatedClassesConfig + ") cannot also have a solutionClass (" + solutionClass
                        + ") or an entityClass (" + entityClassList + ").\n"
                        + "  Please decide between automatic scanning or manual referencing.");
            }
            return scanAnnotatedClassesConfig.buildSolutionDescriptor(configContext);
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
            return SolutionDescriptor.buildSolutionDescriptor(solutionClass, entityClassList);
        }
    }

    protected BestSolutionRecaller buildBestSolutionRecaller(EnvironmentMode environmentMode) {
        BestSolutionRecaller bestSolutionRecaller = new BestSolutionRecaller();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            bestSolutionRecaller.setAssertInitialScoreFromScratch(true);
            bestSolutionRecaller.setAssertShadowVariablesAreNotStale(true);
            bestSolutionRecaller.setAssertBestScoreIsUnmodified(true);
        }
        return bestSolutionRecaller;
    }

    protected List<Phase> buildPhaseList(HeuristicConfigPolicy configPolicy, BestSolutionRecaller bestSolutionRecaller, Termination termination) {
        List<PhaseConfig> phaseConfigList_ = phaseConfigList;
        if (ConfigUtils.isEmptyCollection(phaseConfigList_)) {
            phaseConfigList_ = new ArrayList<PhaseConfig>(2);
            phaseConfigList_.add(new ConstructionHeuristicPhaseConfig());
            phaseConfigList_.add(new LocalSearchPhaseConfig());
        }
        List<Phase> phaseList = new ArrayList<Phase>(phaseConfigList_.size());
        int phaseIndex = 0;
        for (PhaseConfig phaseConfig : phaseConfigList_) {
            Phase phase = phaseConfig.buildPhase(phaseIndex, configPolicy,
                    bestSolutionRecaller, termination);
            phaseList.add(phase);
            phaseIndex++;
        }
        return phaseList;
    }

    public void inherit(SolverConfig inheritedConfig) {
        environmentMode = ConfigUtils.inheritOverwritableProperty(environmentMode, inheritedConfig.getEnvironmentMode());
        daemon = ConfigUtils.inheritOverwritableProperty(daemon, inheritedConfig.getDaemon());
        randomType = ConfigUtils.inheritOverwritableProperty(randomType, inheritedConfig.getRandomType());
        randomSeed = ConfigUtils.inheritOverwritableProperty(randomSeed, inheritedConfig.getRandomSeed());
        randomFactoryClass = ConfigUtils.inheritOverwritableProperty(
                randomFactoryClass, inheritedConfig.getRandomFactoryClass());
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
