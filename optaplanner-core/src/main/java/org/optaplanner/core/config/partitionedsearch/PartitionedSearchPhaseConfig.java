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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.partitionedsearch.DefaultPartitionedSearchPhase;
import org.optaplanner.core.impl.partitionedsearch.PartitionedSearchPhase;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
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


    protected Class<? extends ThreadFactory> threadFactoryClass = null;
    protected String activeThreadCount = null;

    private Class<SolutionPartitioner> solutionPartitionerClass = null;

    @XStreamImplicit()
    protected List<PhaseConfig> phaseConfigList = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public Class<? extends ThreadFactory> getThreadFactoryClass() {
        return threadFactoryClass;
    }

    public void setThreadFactoryClass(Class<? extends ThreadFactory> threadFactoryClass) {
        this.threadFactoryClass = threadFactoryClass;
    }

    /**
     * If there aren't enough processors available, CPU's will be shared by threads in a round-robin matter,.
     * resulting in a slower score calculation speed per partition {@link Solver}.
     * @return null, a number, {@value #ACTIVE_THREAD_COUNT_AUTO}, {@value #ACTIVE_THREAD_COUNT_UNLIMITED}
     * or a JavaScript calculation using {@value ConfigUtils#AVAILABLE_PROCESSOR_COUNT}.
     */
    public String getActiveThreadCount() {
        return activeThreadCount;
    }

    public void setActiveThreadCount(String activeThreadCount) {
        this.activeThreadCount = activeThreadCount;
    }

    public Class<SolutionPartitioner> getSolutionPartitionerClass() {
        return solutionPartitionerClass;
    }

    public void setSolutionPartitionerClass(Class<SolutionPartitioner> solutionPartitionerClass) {
        this.solutionPartitionerClass = solutionPartitionerClass;
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
        DefaultPartitionedSearchPhase phase = new DefaultPartitionedSearchPhase();
        configurePhase(phase, phaseIndex, phaseConfigPolicy, bestSolutionRecaller, solverTermination);
        phase.setThreadPoolExecutor(buildThreadPoolExecutor());
        phase.setActiveThreadCount(resolvedActiveThreadCount());
        phase.setSolutionPartitioner(buildSolutionPartitioner());
        List<PhaseConfig> phaseConfigList_ = phaseConfigList;
        if (ConfigUtils.isEmptyCollection(phaseConfigList_)) {
            phaseConfigList_ = Arrays.asList(
                    new ConstructionHeuristicPhaseConfig(),
                    new LocalSearchPhaseConfig());
        }
        phase.setPhaseConfigList(phaseConfigList_);
        phase.setConfigPolicy(phaseConfigPolicy);


        // TODO check if any asserts should happen in EnvironmentMode
//        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
//        if (environmentMode.isNonIntrusiveFullAsserted()) {
//            phase.setAssertStepScoreFromScratch(true);
//        }
//        if (environmentMode.isIntrusiveFastAsserted()) {
//            phase.setAssertExpectedStepScore(true);
//            phase.setAssertShadowVariablesAreNotStaleAfterStep(true);
//        }
        return phase;
    }

    private ThreadPoolExecutor buildThreadPoolExecutor() {
        ThreadFactory threadFactory;
        if (threadFactoryClass != null) {
            threadFactory = ConfigUtils.newInstance(this, "threadFactoryClass", threadFactoryClass);
        } else {
            threadFactory = Executors.defaultThreadFactory();
        }
        // Based on Executors.newCachedThreadPool(...)
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                threadFactory);
    }

    private Integer resolvedActiveThreadCount() {
        int availableProcessorCount = Runtime.getRuntime().availableProcessors();
        Integer resolvedActiveThreadCount;
        if (activeThreadCount == null || activeThreadCount.equals(ACTIVE_THREAD_COUNT_AUTO)) {
            // Leave one for the Operating System and 1 for the solver thread, take the rest
            resolvedActiveThreadCount = availableProcessorCount <= 2 ? 1 : availableProcessorCount - 2;
        } else if (activeThreadCount.equals(ACTIVE_THREAD_COUNT_UNLIMITED)) {
            resolvedActiveThreadCount = null;
        } else {
            resolvedActiveThreadCount = ConfigUtils.resolveThreadPoolSizeScript(
                    "activeThreadCount", activeThreadCount, ACTIVE_THREAD_COUNT_AUTO, ACTIVE_THREAD_COUNT_UNLIMITED);
            if (resolvedActiveThreadCount < 1) {
                throw new IllegalArgumentException("The activeThreadCount (" + activeThreadCount
                        + ") resulted in a resolvedActiveThreadCount (" + resolvedActiveThreadCount
                        + ") that is lower than 1.");
            }
            if (resolvedActiveThreadCount > availableProcessorCount) {
                logger.debug("The resolvedActiveThreadCount (" + resolvedActiveThreadCount
                        + ") is higher than the availableProcessorCount (" + availableProcessorCount
                        + "), so the JVM will round-robin the CPU instead.");
            }
        }
        return resolvedActiveThreadCount;
    }

    private SolutionPartitioner buildSolutionPartitioner() {
        if (solutionPartitionerClass != null) {
            return ConfigUtils.newInstance(this, "solutionPartitionerClass", solutionPartitionerClass);
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void inherit(PartitionedSearchPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        threadFactoryClass = ConfigUtils.inheritOverwritableProperty(threadFactoryClass,
                inheritedConfig.getThreadFactoryClass());
        activeThreadCount = ConfigUtils.inheritOverwritableProperty(activeThreadCount,
                inheritedConfig.getActiveThreadCount());
        solutionPartitionerClass = ConfigUtils.inheritOverwritableProperty(solutionPartitionerClass,
                inheritedConfig.getSolutionPartitionerClass());
        phaseConfigList = ConfigUtils.inheritMergeableListConfig(
                phaseConfigList, inheritedConfig.getPhaseConfigList());
    }

}
