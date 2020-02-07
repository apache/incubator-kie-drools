/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.spring.boot.autoconfigure;

import java.time.Duration;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.solver.DefaultSolverManager;
import org.optaplanner.spring.boot.autoconfigure.solver.TestdataSpringConstraintProvider;
import org.optaplanner.spring.boot.autoconfigure.testdata.TestdataSpringEntity;
import org.optaplanner.spring.boot.autoconfigure.testdata.TestdataSpringSolution;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.*;

public class OptaPlannerAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner;

    public OptaPlannerAutoConfigurationTest() {
        this.contextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(OptaPlannerAutoConfiguration.class))
                .withUserConfiguration(TestConfiguration.class);
    }

    @Test
    public void solverConfigXml_none() {
        contextRunner
                .withClassLoader(new FilteredClassLoader(new ClassPathResource("solverConfig.xml")))
                .run(context -> {
                    SolverConfig solverConfig = context.getBean(SolverConfig.class);
                    assertNotNull(solverConfig);
                    assertEquals(TestdataSpringSolution.class, solverConfig.getSolutionClass());
                    assertEquals(Collections.singletonList(TestdataSpringEntity.class), solverConfig.getEntityClassList());
                    assertEquals(TestdataSpringConstraintProvider.class, solverConfig.getScoreDirectorFactoryConfig().getConstraintProviderClass());
                    // No termination defined
                    assertNull(solverConfig.getTerminationConfig());
                    SolverFactory solverFactory = context.getBean(SolverFactory.class);
                    assertNotNull(solverFactory);
                    assertNotNull(solverFactory.buildSolver());
                });
    }

    @Test
    public void solverConfigXml_default() {
        contextRunner
                .run(context -> {
                    SolverConfig solverConfig = context.getBean(SolverConfig.class);
                    assertNotNull(solverConfig);
                    assertEquals(TestdataSpringSolution.class, solverConfig.getSolutionClass());
                    assertEquals(Collections.singletonList(TestdataSpringEntity.class), solverConfig.getEntityClassList());
                    assertEquals(TestdataSpringConstraintProvider.class, solverConfig.getScoreDirectorFactoryConfig().getConstraintProviderClass());
                    // Properties defined in solverConfig.xml
                    assertEquals(2L, solverConfig.getTerminationConfig().getSecondsSpentLimit().longValue());
                    SolverFactory solverFactory = context.getBean(SolverFactory.class);
                    assertNotNull(solverFactory);
                    assertNotNull(solverFactory.buildSolver());
                });
    }

    @Test
    public void solverConfigXml_property() {
        contextRunner
                .withPropertyValues("optaplanner.solver-config-xml=org/optaplanner/spring/boot/autoconfigure/customSpringBootSolverConfig.xml")
                .run(context -> {
                    SolverConfig solverConfig = context.getBean(SolverConfig.class);
                    assertNotNull(solverConfig);
                    assertEquals(TestdataSpringSolution.class, solverConfig.getSolutionClass());
                    assertEquals(Collections.singletonList(TestdataSpringEntity.class), solverConfig.getEntityClassList());
                    assertEquals(TestdataSpringConstraintProvider.class, solverConfig.getScoreDirectorFactoryConfig().getConstraintProviderClass());
                    // Properties defined in customSpringBootSolverConfig.xml
                    assertEquals(3L, solverConfig.getTerminationConfig().getMinutesSpentLimit().longValue());
                    SolverFactory solverFactory = context.getBean(SolverFactory.class);
                    assertNotNull(solverFactory);
                    assertNotNull(solverFactory.buildSolver());
                });
    }

    @Test(expected = IllegalStateException.class)
    public void scanAnnotatedClasses() {
        contextRunner
                .withPropertyValues("optaplanner.solver-config-xml=org/optaplanner/spring/boot/autoconfigure/illegalScanAnnotatedSpringBootSolverConfig.xml")
                .run(context -> {
                    context.getBean(SolverConfig.class);
                });
    }

    @Test
    public void solverProperties() {
        contextRunner
                .withPropertyValues("optaplanner.solver.environment-mode=FULL_ASSERT")
                .run(context -> {
                    SolverConfig solverConfig = context.getBean(SolverConfig.class);
                    assertEquals(EnvironmentMode.FULL_ASSERT, solverConfig.getEnvironmentMode());
                    assertNotNull(context.getBean(SolverFactory.class));
                });
        contextRunner
                .withPropertyValues("optaplanner.solver.move-thread-count=2")
                .run(context -> {
                    SolverConfig solverConfig = context.getBean(SolverConfig.class);
                    assertEquals("2", solverConfig.getMoveThreadCount());
                    assertNotNull(context.getBean(SolverFactory.class));
                });
    }

    @Test
    public void terminationProperties() {
        contextRunner
                .withPropertyValues("optaplanner.solver.termination.spent-limit=4h")
                .run(context -> {
                    TerminationConfig terminationConfig = context.getBean(SolverConfig.class).getTerminationConfig();
                    assertEquals(Duration.ofHours(4), terminationConfig.getSpentLimit());
                    assertNotNull(context.getBean(SolverFactory.class));
                });
        contextRunner
                .withPropertyValues("optaplanner.solver.termination.unimproved-spent-limit=5h")
                .run(context -> {
                    TerminationConfig terminationConfig = context.getBean(SolverConfig.class).getTerminationConfig();
                    assertEquals(Duration.ofHours(5), terminationConfig.getUnimprovedSpentLimit());
                    assertNotNull(context.getBean(SolverFactory.class));
                });
        contextRunner
                .withPropertyValues("optaplanner.solver.termination.best-score-limit=6")
                .run(context -> {
                    TerminationConfig terminationConfig = context.getBean(SolverConfig.class).getTerminationConfig();
                    assertEquals(SimpleScore.of(6).toString(), terminationConfig.getBestScoreLimit());
                    assertNotNull(context.getBean(SolverFactory.class));
                });
    }

    @Test
    public void singletonSolverFactory() {
        contextRunner
                .run(context -> {
                    SolverFactory<TestdataSpringSolution> solverFactory = context.getBean(SolverFactory.class);
                    assertNotNull(solverFactory);
                    ScoreManager<TestdataSpringSolution> scoreManager = context.getBean(ScoreManager.class);
                    assertNotNull(scoreManager);
                    // TODO in 8.0, once SolverFactory.getScoreDirectorFactory() doesn't create a new instance every time
                    // assertSame(solverFactory.getScoreDirectorFactory(), ((DefaultScoreManager<TestdataSpringSolution>) scoreManager).getScoreDirectorFactory());
                    SolverManager<TestdataSpringSolution, Long> solverManager = context.getBean(SolverManager.class);
                    assertNotNull(solverManager);
                    // There is only one SolverFactory instance
                    assertSame(solverFactory, ((DefaultSolverManager<TestdataSpringSolution, Long>) solverManager).getSolverFactory());
                });
    }

    @Test
    public void solve() {
        contextRunner
                .withClassLoader(new FilteredClassLoader(new ClassPathResource("solverConfig.xml")))
                .withPropertyValues("optaplanner.solver.termination.best-score-limit=0")
                .run(context -> {
                    SolverManager solverManager = context.getBean(SolverManager.class);
                    TestdataSpringSolution problem = new TestdataSpringSolution();
                    problem.setValueList(IntStream.range(1, 3)
                            .mapToObj(i -> "v" + i)
                            .collect(Collectors.toList()));
                    problem.setEntityList(IntStream.range(1, 3)
                            .mapToObj(i -> new TestdataSpringEntity())
                            .collect(Collectors.toList()));
                    SolverJob<TestdataSpringSolution, Long> solverJob = solverManager.solve(1L, problem);
                    TestdataSpringSolution solution = solverJob.getFinalBestSolution();
                    assertNotNull(solution);
                    assertTrue(solution.getScore().getScore() >= 0);
                });
    }

    @Configuration
    @EntityScan(basePackageClasses = {TestdataSpringSolution.class, TestdataSpringConstraintProvider.class})
    @AutoConfigurationPackage
    public static class TestConfiguration {

    }

}
