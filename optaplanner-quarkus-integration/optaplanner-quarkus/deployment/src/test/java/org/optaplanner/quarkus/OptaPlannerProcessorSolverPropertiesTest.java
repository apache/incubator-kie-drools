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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.quarkus.constraints.TestdataPlanningConstraintProvider;
import org.optaplanner.quarkus.domain.TestdataPlanningEntity;
import org.optaplanner.quarkus.domain.TestdataPlanningSolution;

import io.quarkus.test.QuarkusUnitTest;

public class OptaPlannerProcessorSolverPropertiesTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.optaplanner.solver.environment-mode",
                    "FULL_ASSERT")
            .overrideConfigKey("quarkus.optaplanner.solver.move-thread-count", "2")
            .overrideConfigKey("quarkus.optaplanner.solver.termination.spent-limit", "4h")
            .overrideConfigKey("quarkus.optaplanner.solver.termination.unimproved-spent-limit", "5h")
            .overrideConfigKey("quarkus.optaplanner.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataPlanningEntity.class, TestdataPlanningSolution.class,
                            TestdataPlanningConstraintProvider.class));

    @Inject
    SolverConfig solverConfig;
    @Inject
    SolverFactory<TestdataPlanningSolution> solverFactory;

    @Test
    public void solverProperties() {
        assertEquals(EnvironmentMode.FULL_ASSERT, solverConfig.getEnvironmentMode());
        assertEquals("2", solverConfig.getMoveThreadCount());

        assertNotNull(solverFactory);
    }

    @Test
    public void terminationProperties() {
        assertEquals(Duration.ofHours(4), solverConfig.getTerminationConfig().getSpentLimit());
        assertEquals(Duration.ofHours(5), solverConfig.getTerminationConfig().getUnimprovedSpentLimit());
        assertEquals(SimpleScore.of(0).toString(), solverConfig.getTerminationConfig().getBestScoreLimit());
    }
}
