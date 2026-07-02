/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.partitionedsearch;

import static org.optaplanner.core.config.partitionedsearch.PartitionedSearchPhaseConfig.ACTIVE_THREAD_COUNT_AUTO;
import static org.optaplanner.core.config.partitionedsearch.PartitionedSearchPhaseConfig.ACTIVE_THREAD_COUNT_UNLIMITED;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.partitionedsearch.PartitionedSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.phase.AbstractPhaseFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPartitionedSearchPhaseFactory<Solution_>
        extends AbstractPhaseFactory<Solution_, PartitionedSearchPhaseConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPartitionedSearchPhaseFactory.class);

    public DefaultPartitionedSearchPhaseFactory(PartitionedSearchPhaseConfig phaseConfig) {
        super(phaseConfig);
    }

    @Override
    public PartitionedSearchPhase<Solution_> buildPhase(int phaseIndex, HeuristicConfigPolicy<Solution_> solverConfigPolicy,
            BestSolutionRecaller<Solution_> bestSolutionRecaller, Termination<Solution_> solverTermination) {
        HeuristicConfigPolicy<Solution_> phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        ThreadFactory threadFactory = solverConfigPolicy.buildThreadFactory(ChildThreadType.PART_THREAD);
        Termination<Solution_> phaseTermination = buildPhaseTermination(phaseConfigPolicy, solverTermination);
        Integer resolvedActiveThreadCount = resolveActiveThreadCount(phaseConfig.getRunnablePartThreadLimit());
        List<PhaseConfig> phaseConfigList_ = phaseConfig.getPhaseConfigList();
        if (ConfigUtils.isEmptyCollection(phaseConfigList_)) {
            phaseConfigList_ = Arrays.asList(new ConstructionHeuristicPhaseConfig(), new LocalSearchPhaseConfig());
        }

        DefaultPartitionedSearchPhase.Builder<Solution_> builder = new DefaultPartitionedSearchPhase.Builder<>(phaseIndex,
                solverConfigPolicy.getLogIndentation(), phaseTermination, buildSolutionPartitioner(), threadFactory,
                resolvedActiveThreadCount, phaseConfigList_,
                phaseConfigPolicy.createChildThreadConfigPolicy(ChildThreadType.PART_THREAD));

        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            builder.setAssertStepScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            builder.setAssertExpectedStepScore(true);
            builder.setAssertShadowVariablesAreNotStaleAfterStep(true);
        }
        return builder.build();
    }

    private SolutionPartitioner<Solution_> buildSolutionPartitioner() {
        if (phaseConfig.getSolutionPartitionerClass() != null) {
            SolutionPartitioner<?> solutionPartitioner =
                    ConfigUtils.newInstance(phaseConfig, "solutionPartitionerClass", phaseConfig.getSolutionPartitionerClass());
            ConfigUtils.applyCustomProperties(solutionPartitioner, "solutionPartitionerClass",
                    phaseConfig.getSolutionPartitionerCustomProperties(), "solutionPartitionerCustomProperties");
            return (SolutionPartitioner<Solution_>) solutionPartitioner;
        } else {
            if (phaseConfig.getSolutionPartitionerCustomProperties() != null) {
                throw new IllegalStateException(
                        "If there is no solutionPartitionerClass (" + phaseConfig.getSolutionPartitionerClass()
                                + "), then there can be no solutionPartitionerCustomProperties ("
                                + phaseConfig.getSolutionPartitionerCustomProperties() + ") either.");
            }
            // TODO Implement generic partitioner
            throw new UnsupportedOperationException();
        }
    }

    protected Integer resolveActiveThreadCount(String runnablePartThreadLimit) {
        int availableProcessorCount = getAvailableProcessors();
        Integer resolvedActiveThreadCount;
        final boolean threadLimitNullOrAuto =
                runnablePartThreadLimit == null || runnablePartThreadLimit.equals(ACTIVE_THREAD_COUNT_AUTO);
        if (threadLimitNullOrAuto) {
            // Leave one for the Operating System and 1 for the solver thread, take the rest
            resolvedActiveThreadCount = Math.max(1, availableProcessorCount - 2);
        } else if (runnablePartThreadLimit.equals(ACTIVE_THREAD_COUNT_UNLIMITED)) {
            resolvedActiveThreadCount = null;
        } else {
            resolvedActiveThreadCount = ConfigUtils.resolvePoolSize("runnablePartThreadLimit",
                    runnablePartThreadLimit, ACTIVE_THREAD_COUNT_AUTO, ACTIVE_THREAD_COUNT_UNLIMITED);
            if (resolvedActiveThreadCount < 1) {
                throw new IllegalArgumentException("The runnablePartThreadLimit (" + runnablePartThreadLimit
                        + ") resulted in a resolvedActiveThreadCount (" + resolvedActiveThreadCount
                        + ") that is lower than 1.");
            }
            if (resolvedActiveThreadCount > availableProcessorCount) {
                LOGGER.debug("The resolvedActiveThreadCount ({}) is higher than "
                        + "the availableProcessorCount ({}), so the JVM will "
                        + "round-robin the CPU instead.", resolvedActiveThreadCount, availableProcessorCount);
            }
        }
        return resolvedActiveThreadCount;
    }

    protected int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }
}
