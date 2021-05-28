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

package org.optaplanner.benchmark.quarkus;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.quarkus.testdata.normal.constraints.TestdataQuarkusConstraintProvider;
import org.optaplanner.benchmark.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.benchmark.quarkus.testdata.normal.domain.TestdataQuarkusSolution;
import org.optaplanner.core.config.solver.SolverConfig;

import io.quarkus.test.QuarkusUnitTest;

public class OptaPlannerBenchmarkProcessorMissingSpentLimitTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class));

    @Inject
    SolverConfig solverConfig;
    @Inject
    OptaPlannerBenchmarkRuntimeConfig runtimeConfig;

    @Test
    public void benchmark() throws ExecutionException, InterruptedException {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            new OptaPlannerBenchmarkBeanProvider().benchmarkFactory(new PlannerBenchmarkConfig(),
                    runtimeConfig, solverConfig);
        }, "Property quarkus.optaplanner.benchmark.solver.termination.spent-limit is required if the inherited solver config does not have termination configured.");

    }

}
