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

import java.util.List;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.benchmark.quarkus.testdata.normal.constraints.TestdataQuarkusConstraintProvider;
import org.optaplanner.benchmark.quarkus.testdata.normal.domain.TestdataQuarkusEntity;
import org.optaplanner.benchmark.quarkus.testdata.normal.domain.TestdataQuarkusOtherEntity;
import org.optaplanner.benchmark.quarkus.testdata.normal.domain.TestdataQuarkusSolution;
import org.optaplanner.core.config.solver.SolverConfig;

import io.quarkus.test.QuarkusUnitTest;

public class OptaPlannerBenchmarkProcessorInheritedSolverBenchmarkTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.optaplanner.benchmark.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class, TestdataQuarkusOtherEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class)
                    .addAsResource("solverConfig.xml")
                    .addAsResource("solverBenchmarkConfigWithInheritedSolverBenchmark.xml", "solverBenchmarkConfig.xml"));

    @Inject
    SolverConfig solverConfig;

    @Inject
    PlannerBenchmarkConfig plannerBenchmarkConfig;

    @Test
    public void inheritClassesFromSolverConfig() {
        Assertions.assertEquals(TestdataQuarkusSolution.class, solverConfig.getSolutionClass());
        Assertions.assertEquals(2, solverConfig.getEntityClassList().size());
        Assertions.assertTrue(solverConfig.getEntityClassList().contains(TestdataQuarkusEntity.class));
        Assertions.assertTrue(solverConfig.getEntityClassList().contains(TestdataQuarkusOtherEntity.class));
        Assertions.assertEquals(5, plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig()
                .getSolverConfig().getTerminationConfig().getMillisecondsSpentLimit());
        Assertions.assertEquals(List.of(TestdataQuarkusEntity.class),
                plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig().getSolverConfig().getEntityClassList());

        SolverBenchmarkConfig childBenchmarkConfig = plannerBenchmarkConfig.getSolverBenchmarkConfigList().get(0);
        Assertions.assertEquals(TestdataQuarkusSolution.class,
                childBenchmarkConfig.getSolverConfig().getSolutionClass());
        Assertions.assertNull(childBenchmarkConfig.getSolverConfig().getEntityClassList()); // inherited from inherited solver config
        Assertions.assertEquals(TestdataQuarkusConstraintProvider.class,
                childBenchmarkConfig.getSolverConfig().getScoreDirectorFactoryConfig()
                        .getConstraintProviderClass());

        childBenchmarkConfig = plannerBenchmarkConfig.getSolverBenchmarkConfigList().get(1);
        Assertions.assertEquals(TestdataQuarkusConstraintProvider.class,
                childBenchmarkConfig.getSolverConfig().getSolutionClass());
        Assertions.assertNull(childBenchmarkConfig.getSolverConfig().getEntityClassList()); // inherited from inherited solver config
        Assertions.assertEquals(TestdataQuarkusConstraintProvider.class,
                childBenchmarkConfig.getSolverConfig().getScoreDirectorFactoryConfig()
                        .getConstraintProviderClass());
    }

}
