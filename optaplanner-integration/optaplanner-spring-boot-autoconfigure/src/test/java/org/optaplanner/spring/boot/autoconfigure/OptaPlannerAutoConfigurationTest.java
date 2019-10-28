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

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.spring.boot.autoconfigure.score.constraintprovider.SpringBootTestDataConstraintProvider;
import org.optaplanner.spring.boot.autoconfigure.testdata.SpringBootTestDataSolution;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.*;

public class OptaPlannerAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner;

    public OptaPlannerAutoConfigurationTest() {
        this.contextRunner = new ApplicationContextRunner()
                .withUserConfiguration(TestConfiguration.class)
                .withConfiguration(AutoConfigurations.of(OptaPlannerAutoConfiguration.class));
    }

    @Test @Ignore("The @EntityScan annotation is ignored in tests") // TODO FIXME
    public void emptyProperties() {
        contextRunner.run(context -> {
            assertNotNull(context.getBean(SolverManager.class));
        });
    }

    @Configuration
    // TODO this annotation is ignored
    @EntityScan(basePackageClasses = {SpringBootTestDataSolution.class, SpringBootTestDataConstraintProvider.class})
    public static class TestConfiguration {

    }

}
