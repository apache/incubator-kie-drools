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

package org.optaplanner.quarkus;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.impl.solver.DefaultSolverManager;
import org.optaplanner.quarkus.constraints.TestdataPlanningConstraintProvider;
import org.optaplanner.quarkus.domain.TestdataPlanningEntity;
import org.optaplanner.quarkus.domain.TestdataPlanningSolution;

import io.quarkus.test.QuarkusUnitTest;

public class OptaPlannerProcessorSolveTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.optaplanner.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataPlanningEntity.class,
                            TestdataPlanningSolution.class, TestdataPlanningConstraintProvider.class));

    @Inject
    SolverFactory<TestdataPlanningSolution> solverFactory;
    @Inject
    SolverManager<TestdataPlanningSolution, Long> solverManager;
    @Inject
    ScoreManager<TestdataPlanningSolution> scoreManager;

    @Test
    public void singletonSolverFactory() {
        assertNotNull(solverFactory);
        assertNotNull(scoreManager);
        // TODO with optaplanner 8.0, once SolverFactory.getScoreDirectorFactory() doesn't create a new instance every time
        // assertSame(solverFactory.getScoreDirectorFactory(), ((DefaultScoreManager<TestdataPlanningSolution>) scoreManager).getScoreDirectorFactory());
        assertNotNull(solverManager);
        // There is only one SolverFactory instance
        assertSame(solverFactory, ((DefaultSolverManager<TestdataPlanningSolution, Long>) solverManager).getSolverFactory());
    }

    @Test
    public void solve() throws ExecutionException, InterruptedException {
        TestdataPlanningSolution problem = new TestdataPlanningSolution();
        problem.setValueList(IntStream.range(1, 3)
                .mapToObj(i -> "v" + i)
                .collect(Collectors.toList()));
        problem.setEntityList(IntStream.range(1, 3)
                .mapToObj(i -> new TestdataPlanningEntity())
                .collect(Collectors.toList()));
        SolverJob<TestdataPlanningSolution, Long> solverJob = solverManager.solve(1L, problem);
        TestdataPlanningSolution solution = solverJob.getFinalBestSolution();
        assertNotNull(solution);
        assertTrue(solution.getScore().getScore() >= 0);
    }

}
