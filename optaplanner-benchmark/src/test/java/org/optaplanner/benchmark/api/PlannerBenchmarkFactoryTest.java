/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.api;

import java.io.IOException;

import org.junit.Test;
import org.optaplanner.core.api.solver.ClassLoaderTest;

import static org.junit.Assert.assertNotNull;

public class PlannerBenchmarkFactoryTest extends ClassLoaderTest {

    @Test
    public void testdataPlannerBenchmarkConfig() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "org/optaplanner/benchmark/api/testdataPlannerBenchmarkConfig.xml");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonExistingPlannerBenchmarkConfig() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "org/optaplanner/benchmark/api/nonExistingPlannerBenchmarkConfig.xml");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
    }

    @Test
    public void testdataPlannerBenchmarkConfigWithClassLoader() throws ClassNotFoundException, IOException {
        ClassLoader classLoader = mockDivertingClassLoader();
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "divertThroughClassLoader/org/optaplanner/benchmark/api/classloaderTestdataPlannerBenchmarkConfig.xml", classLoader);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
    }

}
