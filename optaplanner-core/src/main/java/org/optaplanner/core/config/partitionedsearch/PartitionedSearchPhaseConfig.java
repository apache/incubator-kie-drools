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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.partitionedsearch.DefaultPartitionedSearchPhase;
import org.optaplanner.core.impl.partitionedsearch.PartitionedSearchPhase;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XStreamAlias("paritionedSearch")
public class PartitionedSearchPhaseConfig extends PhaseConfig<PartitionedSearchPhaseConfig> {

    public static final String THREAD_POOL_SIZE_AUTO = "AUTO";

    private static final Logger logger = LoggerFactory.getLogger(PartitionedSearchPhaseConfig.class);

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected String threadPoolSize = null;


    @XStreamImplicit()
    protected List<PhaseConfig> phaseConfigList = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    /**
     * If there aren't enough processors available, CPU's will be shared by threads in a round-robin matter,.
     * resulting in a slower score calculation speed per partition {@link Solver}.
     * @return null, {@value #THREAD_POOL_SIZE_AUTO}
     * or a JavaScript calculation using {@value ConfigUtils#AVAILABLE_PROCESSOR_COUNT}.
     */
    public String getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(String threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
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
        DefaultPartitionedSearchPhase phase = new DefaultPartitionedSearchPhase();
//        configurePhase(phase, phaseIndex, phaseConfigPolicy, bestSolutionRecaller, solverTermination);
//        phase.setDecider(buildDecider(phaseConfigPolicy,
//                phase.getTermination()));
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

    private ExecutorService buildExecutorService() {
        // TODO Support plugging in a ThreadFactory (and potentially an ExecutorService even)
        int availableProcessorCount = Runtime.getRuntime().availableProcessors();
        int resolvedThreadPoolSize;
        if (threadPoolSize == null || threadPoolSize.equals(THREAD_POOL_SIZE_AUTO)) {
            resolvedThreadPoolSize = availableProcessorCount <= 1 ? 1 : availableProcessorCount - 1;
        } else {
            resolvedThreadPoolSize = ConfigUtils.resolveThreadPoolSizeScript(
                    "threadPoolSize", threadPoolSize, THREAD_POOL_SIZE_AUTO);
        }
        if (resolvedThreadPoolSize < 1) {
            throw new IllegalArgumentException("The threadPoolSize (" + threadPoolSize
                    + ") resulted in a resolvedThreadPoolSize (" + resolvedThreadPoolSize
                    + ") that is lower than 1.");
        }
        if (resolvedThreadPoolSize > availableProcessorCount) {
            logger.warn("Because the resolvedThreadPoolSize (" + resolvedThreadPoolSize
                    + ") is higher than the availableProcessorCount (" + availableProcessorCount
                    + "), it is reduced to availableProcessorCount.");
        }
        return Executors.newFixedThreadPool(resolvedThreadPoolSize);
    }

    @Override
    public void inherit(PartitionedSearchPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        phaseConfigList = ConfigUtils.inheritMergeableListConfig(
                phaseConfigList, inheritedConfig.getPhaseConfigList());
    }

}
