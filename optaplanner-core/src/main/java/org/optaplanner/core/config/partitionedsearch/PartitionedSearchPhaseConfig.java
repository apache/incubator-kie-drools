/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.partitionedsearch;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.config.util.KeyAsElementMapConverter;
import org.optaplanner.core.impl.partitionedsearch.DefaultPartitionedSearchPhase;
import org.optaplanner.core.impl.partitionedsearch.PartitionedSearchPhase;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XStreamAlias("partitionedSearch")
public class PartitionedSearchPhaseConfig extends PhaseConfig<PartitionedSearchPhaseConfig> {

    public static final String ACTIVE_THREAD_COUNT_AUTO = "AUTO";
    public static final String ACTIVE_THREAD_COUNT_UNLIMITED = "UNLIMITED";

    private static final Logger logger = LoggerFactory.getLogger(PartitionedSearchPhaseConfig.class);

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected Class<? extends SolutionPartitioner<?>> solutionPartitionerClass = null;
    @XStreamConverter(KeyAsElementMapConverter.class)
    protected Map<String, String> solutionPartitionerCustomProperties = null;

    /** @deprecated Use {@link SolverConfig#threadFactoryClass} instead.*/
    @Deprecated // TODO remove in 8.0
    protected Class<? extends ThreadFactory> threadFactoryClass = null;
    protected String runnablePartThreadLimit = null;

    @XStreamImplicit()
    protected List<PhaseConfig> phaseConfigList = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public Class<? extends SolutionPartitioner<?>> getSolutionPartitionerClass() {
        return solutionPartitionerClass;
    }

    public void setSolutionPartitionerClass(Class<? extends SolutionPartitioner<?>> solutionPartitionerClass) {
        this.solutionPartitionerClass = solutionPartitionerClass;
    }

    public Map<String, String> getSolutionPartitionerCustomProperties() {
        return solutionPartitionerCustomProperties;
    }

    public void setSolutionPartitionerCustomProperties(Map<String, String> solutionPartitionerCustomProperties) {
        this.solutionPartitionerCustomProperties = solutionPartitionerCustomProperties;
    }

    /**
     * @deprecated Use {@link SolverConfig#getThreadFactoryClass} instead
     */
    @Deprecated
    public Class<? extends ThreadFactory> getThreadFactoryClass() {
        return threadFactoryClass;
    }

    /**
     * @deprecated Use {@link SolverConfig#setThreadFactoryClass} instead.
     */
    @Deprecated
    public void setThreadFactoryClass(Class<? extends ThreadFactory> threadFactoryClass) {
        this.threadFactoryClass = threadFactoryClass;
    }

    /**
     * Similar to a thread pool size, but instead of limiting the number of {@link Thread}s,
     * it limits the number of {@link java.lang.Thread.State#RUNNABLE runnable} {@link Thread}s to avoid consuming all
     * CPU resources (which would starve UI, Servlets and REST threads).
     * <p/>
     * The number of {@link Thread}s is always equal to the number of partitions returned by
     * {@link SolutionPartitioner#splitWorkingSolution(ScoreDirector, Integer)},
     * because otherwise some partitions would never run (especially with {@link Solver#terminateEarly() asynchronous termination}).
     * If this limit (or {@link Runtime#availableProcessors()}) is lower than the number of partitions,
     * this results in a slower score calculation speed per partition {@link Solver}.
     * <p/>
     * Defaults to {@value #ACTIVE_THREAD_COUNT_AUTO} which consumes the majority
     * but not all of the CPU cores on multi-core machines, to prevent a livelock that hangs other processes
     * (such as your IDE, REST servlets threads or SSH connections) on the machine.
     * <p/>
     * Use {@value #ACTIVE_THREAD_COUNT_UNLIMITED} to give it all CPU cores.
     * This is useful if you're handling the CPU consumption on an OS level.
     * @return null, a number, {@value #ACTIVE_THREAD_COUNT_AUTO}, {@value #ACTIVE_THREAD_COUNT_UNLIMITED}
     * or a JavaScript calculation using {@value org.optaplanner.core.config.util.ConfigUtils#AVAILABLE_PROCESSOR_COUNT}.
     */
    public String getRunnablePartThreadLimit() {
        return runnablePartThreadLimit;
    }

    public void setRunnablePartThreadLimit(String runnablePartThreadLimit) {
        this.runnablePartThreadLimit = runnablePartThreadLimit;
    }

    public List<PhaseConfig> getPhaseConfigList() {
        return phaseConfigList;
    }

    public void setPhaseConfigList(List<PhaseConfig> phaseConfigList) {
        this.phaseConfigList = phaseConfigList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public PartitionedSearchPhase buildPhase(int phaseIndex, HeuristicConfigPolicy solverConfigPolicy,
            BestSolutionRecaller bestSolutionRecaller, Termination solverTermination) {
        HeuristicConfigPolicy phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        ThreadFactory threadFactory;
        if (threadFactoryClass != null) {
            threadFactory = ConfigUtils.newInstance(this, "threadFactoryClass", threadFactoryClass);
        } else {
            threadFactory = solverConfigPolicy.buildThreadFactory(ChildThreadType.PART_THREAD);
        }
        DefaultPartitionedSearchPhase phase = new DefaultPartitionedSearchPhase(
                phaseIndex, solverConfigPolicy.getLogIndentation(), bestSolutionRecaller,
                buildPhaseTermination(phaseConfigPolicy, solverTermination),
                buildSolutionPartitioner(), threadFactory, resolvedActiveThreadCount());
        List<PhaseConfig> phaseConfigList_ = phaseConfigList;
        if (ConfigUtils.isEmptyCollection(phaseConfigList_)) {
            phaseConfigList_ = Arrays.asList(
                    new ConstructionHeuristicPhaseConfig(),
                    new LocalSearchPhaseConfig());
        }
        phase.setPhaseConfigList(phaseConfigList_);
        phase.setConfigPolicy(phaseConfigPolicy.createChildThreadConfigPolicy(ChildThreadType.PART_THREAD));
        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            phase.setAssertStepScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            phase.setAssertExpectedStepScore(true);
            phase.setAssertShadowVariablesAreNotStaleAfterStep(true);
        }
        return phase;
    }

    private SolutionPartitioner buildSolutionPartitioner() {
        if (solutionPartitionerClass != null) {
            SolutionPartitioner<?> solutionPartitioner = ConfigUtils.newInstance(this,
                    "solutionPartitionerClass", solutionPartitionerClass);
            ConfigUtils.applyCustomProperties(solutionPartitioner, "solutionPartitionerClass",
                    solutionPartitionerCustomProperties, "solutionPartitionerCustomProperties");
            return solutionPartitioner;
        } else {
            if (solutionPartitionerCustomProperties != null) {
                throw new IllegalStateException("If there is no solutionPartitionerClass (" + solutionPartitionerClass
                        + "), then there can be no solutionPartitionerCustomProperties ("
                        + solutionPartitionerCustomProperties + ") either.");
            }
            // TODO Implement generic partitioner
            throw new UnsupportedOperationException();
        }
    }

    private Integer resolvedActiveThreadCount() {
        int availableProcessorCount = Runtime.getRuntime().availableProcessors();
        Integer resolvedActiveThreadCount;
        if (runnablePartThreadLimit == null || runnablePartThreadLimit.equals(ACTIVE_THREAD_COUNT_AUTO)) {
            // Leave one for the Operating System and 1 for the solver thread, take the rest
            resolvedActiveThreadCount = Math.max(1, availableProcessorCount - 2);
        } else if (runnablePartThreadLimit.equals(ACTIVE_THREAD_COUNT_UNLIMITED)) {
            resolvedActiveThreadCount = null;
        } else {
            resolvedActiveThreadCount = ConfigUtils.resolveThreadPoolSizeScript(
                    "runnablePartThreadLimit", runnablePartThreadLimit, ACTIVE_THREAD_COUNT_AUTO, ACTIVE_THREAD_COUNT_UNLIMITED);
            if (resolvedActiveThreadCount < 1) {
                throw new IllegalArgumentException("The runnablePartThreadLimit (" + runnablePartThreadLimit
                        + ") resulted in a resolvedActiveThreadCount (" + resolvedActiveThreadCount
                        + ") that is lower than 1.");
            }
            if (resolvedActiveThreadCount > availableProcessorCount) {
                logger.debug("The resolvedActiveThreadCount ({}) is higher than "
                        + "the availableProcessorCount ({}), so the JVM will "
                        + "round-robin the CPU instead.", resolvedActiveThreadCount, availableProcessorCount);
            }
        }
        return resolvedActiveThreadCount;
    }

    @Override
    public void inherit(PartitionedSearchPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        solutionPartitionerClass = ConfigUtils.inheritOverwritableProperty(solutionPartitionerClass,
                inheritedConfig.getSolutionPartitionerClass());
        solutionPartitionerCustomProperties = ConfigUtils.inheritMergeableMapProperty(
                solutionPartitionerCustomProperties, inheritedConfig.getSolutionPartitionerCustomProperties());
        threadFactoryClass = ConfigUtils.inheritOverwritableProperty(threadFactoryClass,
                inheritedConfig.getThreadFactoryClass());
        runnablePartThreadLimit = ConfigUtils.inheritOverwritableProperty(runnablePartThreadLimit,
                inheritedConfig.getRunnablePartThreadLimit());
        phaseConfigList = ConfigUtils.inheritMergeableListConfig(
                phaseConfigList, inheritedConfig.getPhaseConfigList());
    }

}
