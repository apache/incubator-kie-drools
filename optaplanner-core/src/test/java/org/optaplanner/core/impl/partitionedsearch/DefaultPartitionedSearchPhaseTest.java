/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.partitionedsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.partitionedsearch.PartitionedSearchPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.partitionedsearch.scope.PartitionedSearchPhaseScope;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.assertEquals;

public class DefaultPartitionedSearchPhaseTest {

    @Test
    public void partCount() {
        final int partSize = 3;
        final int partCount = 7;
        SolverFactory<TestdataSolution> solverFactory = createSolverFactory();
        setPartSize(solverFactory.getSolverConfig(), partSize);
        DefaultSolver<TestdataSolution> solver = (DefaultSolver<TestdataSolution>) solverFactory.buildSolver();
        PartitionedSearchPhase<TestdataSolution> phase
                = (PartitionedSearchPhase<TestdataSolution>) solver.getPhaseList().get(0);
        phase.addPhaseLifecycleListener(new PhaseLifecycleListenerAdapter<TestdataSolution>() {
            @Override
            public void phaseStarted(AbstractPhaseScope<TestdataSolution> phaseScope) {
                assertEquals(Integer.valueOf(partCount), ((PartitionedSearchPhaseScope) phaseScope).getPartCount());
            }
        });
        solver.solve(createSolution(partCount * partSize, 2));
    }

    private static SolverFactory<TestdataSolution> createSolverFactory() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils
                .buildSolverFactory(TestdataSolution.class, TestdataEntity.class);
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        PartitionedSearchPhaseConfig partitionedSearchPhaseConfig = new PartitionedSearchPhaseConfig();
        partitionedSearchPhaseConfig.setSolutionPartitionerClass(TestdataSolutionPartitioner.class);
        solverConfig.setPhaseConfigList(Arrays.asList(partitionedSearchPhaseConfig));
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setStepCountLimit(1);
        localSearchPhaseConfig.setTerminationConfig(terminationConfig);
        partitionedSearchPhaseConfig.setPhaseConfigList(
                Arrays.asList(constructionHeuristicPhaseConfig, localSearchPhaseConfig));
        return solverFactory;
    }

    private static TestdataSolution createSolution(int entities, int values) {
        TestdataSolution solution = new TestdataSolution();
        solution.setEntityList(IntStream.range(0, entities)
                .mapToObj(i -> new TestdataEntity(Character.toString((char) (65 + i))))
                .collect(Collectors.toList())
        );
        solution.setValueList(IntStream.range(0, values)
                .mapToObj(i -> new TestdataValue(Integer.toString(i)))
                .collect(Collectors.toList())
        );
        return solution;
    }

    private static void setPartSize(SolverConfig solverConfig, int partSize) {
        PartitionedSearchPhaseConfig phaseConfig
                = (PartitionedSearchPhaseConfig) solverConfig.getPhaseConfigList().get(0);
        Map<String, String> map = new HashMap<>();
        map.put("partSize", Integer.toString(partSize));
        phaseConfig.setSolutionPartitionerCustomProperties(map);
    }

    public static class TestdataSolutionPartitioner implements SolutionPartitioner<TestdataSolution> {

        /**
         * {@link PartitionedSearchPhaseConfig#solutionPartitionerCustomProperties Custom property}.
         */
        private int partSize = 1;

        public void setPartSize(int partSize) {
            this.partSize = partSize;
        }

        @Override
        public List<TestdataSolution> splitWorkingSolution(
                ScoreDirector<TestdataSolution> scoreDirector, Integer runnablePartThreadLimit) {
            TestdataSolution workingSolution = scoreDirector.getWorkingSolution();
            List<TestdataEntity> allEntities = workingSolution.getEntityList();
            if (allEntities.size() % partSize > 0) {
                throw new IllegalStateException("This partitioner can only make equally sized partitions.");
            }
            List<TestdataSolution> partitions = new ArrayList<>();
            for (int i = 0; i < allEntities.size() / partSize; i++) {
                List<TestdataEntity> partitionEntitites = new ArrayList<>(
                        allEntities.subList(i * partSize, (i + 1) * partSize)
                );
                TestdataSolution partition = new TestdataSolution();
                partition.setEntityList(partitionEntitites);
                partition.setValueList(workingSolution.getValueList());
                partitions.add(partition);
            }
            return partitions;
        }

    }

}
