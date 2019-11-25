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

import org.junit.Test;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.spring.boot.autoconfigure.score.constraintprovider.TestdataSpringConstraintProvider;
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
    public void withSolverConfigXml_withApplicationProperties() {
        contextRunner
                .withPropertyValues("optaplanner.solver.termination.spent-limit=PT1S")
                .run(context -> {
                    assertNotNull(context.getBean(SolverManager.class));
                });
    }

    @Test
    public void withSolverConfigXml_withoutApplicationProperties() {
        contextRunner
                .run(context -> {
                    assertNotNull(context.getBean(SolverManager.class));
                });
    }

    @Test
    public void withoutSolverConfigXml_withApplicationProperties() {
        contextRunner
                .withPropertyValues("optaplanner.solver.termination.spent-limit=PT1S")
                .withClassLoader(new FilteredClassLoader(new ClassPathResource("solverConfig.xml")))
                .run(context -> {
                    assertNotNull(context.getBean(SolverManager.class));
                });
    }

    @Test
    public void withoutSolverConfigXml_withoutApplicationProperties() {
        contextRunner
                .withClassLoader(new FilteredClassLoader(
                        new ClassPathResource("solverConfig.xml")))
                .run(context -> {
                    assertNotNull(context.getBean(SolverManager.class));
                });
    }

    @Configuration
    @EntityScan(basePackageClasses = {TestdataSpringSolution.class, TestdataSpringConstraintProvider.class})
    @AutoConfigurationPackage
    public static class TestConfiguration {

    }

}
