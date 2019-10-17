/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.core.api.solver.DivertingClassLoader;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.phase.custom.NoChangeCustomPhaseCommand;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.KieContainerHelper;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class PlannerBenchmarkFactoryTest {

    private static File benchmarkTestDir;
    private static File benchmarkOutputTestDir;

    @BeforeClass
    public static void setup() throws IOException {
        benchmarkTestDir = new File("target/test/benchmarkTest/");
        benchmarkTestDir.mkdirs();
        new File(benchmarkTestDir, "input.xml").createNewFile();
        benchmarkOutputTestDir = new File(benchmarkTestDir, "output/");
        benchmarkOutputTestDir.mkdir();
    }

    // ************************************************************************
    // Static creation methods: SolverConfig
    // ************************************************************************

    @Test
    public void createFromSolverConfigXmlResource() {
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                "org/optaplanner/core/config/solver/testdataSolverConfig.xml");
        TestdataSolution solution = new TestdataSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        assertNotNull(benchmarkFactory.buildPlannerBenchmark(solution));


        benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                "org/optaplanner/core/config/solver/testdataSolverConfig.xml", benchmarkOutputTestDir);
        assertNotNull(benchmarkFactory.buildPlannerBenchmark(solution));
    }

    @Test
    public void createFromSolverConfigXmlResource_classLoader() {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                "divertThroughClassLoader/org/optaplanner/core/api/solver/classloaderTestdataSolverConfig.xml", classLoader);
        TestdataSolution solution = new TestdataSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        assertNotNull(benchmarkFactory.buildPlannerBenchmark(solution));


        benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                "divertThroughClassLoader/org/optaplanner/core/api/solver/classloaderTestdataSolverConfig.xml", benchmarkOutputTestDir, classLoader);
        assertNotNull(benchmarkFactory.buildPlannerBenchmark(solution));
    }

    @Test(expected = IllegalArgumentException.class)
    public void problemIsNotASolutionInstance() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(
                PlannerBenchmarkConfig.createFromSolverConfig(solverConfig));
        benchmarkFactory.buildPlannerBenchmark("This is not a solution instance.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void problemIsNull() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(
                PlannerBenchmarkConfig.createFromSolverConfig(solverConfig));
        TestdataSolution solution = new TestdataSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        benchmarkFactory.buildPlannerBenchmark(solution, null);
    }

    @Test
    @Deprecated
    public void createFromSolverFactory() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverFactory(
                solverFactory);
        TestdataSolution solution = new TestdataSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark(solution);
        benchmark.benchmark();
    }

    // ************************************************************************
    // Static creation methods: XML
    // ************************************************************************

    @Test
    public void createFromXmlResource() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfig.xml");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    @Test
    public void createFromXmlResource_classLoader() {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "divertThroughClassLoader/org/optaplanner/benchmark/api/classloaderTestdataBenchmarkConfig.xml", classLoader);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFromXmlResource_nonExisting() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "org/optaplanner/benchmark/api/nonExistingBenchmarkConfig.xml");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    @Test
    public void createFromXmlResource_uninitializedBestSolution() {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfig.xml");
        SolverBenchmarkConfig solverBenchmarkConfig = benchmarkConfig.getSolverBenchmarkConfigList().get(0);
        CustomPhaseConfig phaseConfig = new CustomPhaseConfig();
        phaseConfig.setCustomPhaseCommandClassList(Collections.singletonList(NoChangeCustomPhaseCommand.class));
        solverBenchmarkConfig.getSolverConfig().setPhaseConfigList(Collections.singletonList(phaseConfig));
        PlannerBenchmark plannerBenchmark = PlannerBenchmarkFactory.create(benchmarkConfig).buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    @Test
    public void createFromXmlResource_subSingleCount() {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfig.xml");
        SolverBenchmarkConfig solverBenchmarkConfig = benchmarkConfig.getSolverBenchmarkConfigList().get(0);
        solverBenchmarkConfig.setSubSingleCount(3);
        PlannerBenchmark plannerBenchmark = PlannerBenchmarkFactory.create(benchmarkConfig).buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    @Test
    public void createFromXmlFile() throws IOException {
        File file = new File(benchmarkTestDir, "testdataBenchmarkConfig.xml");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfig.xml")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlFile(file);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    @Test
    public void createFromXmlFile_classLoader() throws IOException {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        File file = new File(benchmarkTestDir, "classloaderTestdataBenchmarkConfig.xml");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/benchmark/api/classloaderTestdataBenchmarkConfig.xml")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlFile(file, classLoader);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    // ************************************************************************
    // Static creation methods: Freemarker
    // ************************************************************************

    @Test
    public void createFromFreemarkerXmlResource() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlResource(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfigTemplate.xml.ftl");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    @Test
    public void createFromFreemarkerXmlResource_classLoader() {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlResource(
                "divertThroughClassLoader/org/optaplanner/benchmark/api/classloaderTestdataBenchmarkConfigTemplate.xml.ftl", classLoader);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFromFreemarkerXmlResource_nonExisting() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlResource(
                "org/optaplanner/benchmark/api/nonExistingBenchmarkConfigTemplate.xml.ftl");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    @Test
    public void createFromFreemarkerXmlFile() throws IOException {
        File file = new File(benchmarkTestDir, "testdataBenchmarkConfigTemplate.xml.ftl");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfigTemplate.xml.ftl")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlFile(file);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    @Test
    public void createFromFreemarkerXmlFile_classLoader() throws IOException {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        File file = new File(benchmarkTestDir, "classloaderTestdataBenchmarkConfigTemplate.xml.ftl");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/benchmark/api/classloaderTestdataBenchmarkConfigTemplate.xml.ftl")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlFile(file, classLoader);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    // ************************************************************************
    // Static creation methods: PlannerBenchmarkConfig
    // ************************************************************************

    // ************************************************************************
    // Static creation methods: KieContainer
    // ************************************************************************

    @Test
    public void createFromReleaseId() throws IOException {
        ReleaseId releaseId = deployTestingKjar();
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromKieContainerXmlResource(
                releaseId, "testdata/kjar/benchmarkConfig.solver");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    @Test
    public void createFromKieContainer() throws IOException {
        ReleaseId releaseId = deployTestingKjar();
        KieContainer kieContainer = KieServices.Factory.get().newKieContainer(releaseId);
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromKieContainerXmlResource(
                kieContainer, "testdata/kjar/benchmarkConfig.solver");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertNotNull(plannerBenchmark);
        plannerBenchmark.benchmark();
    }

    private ReleaseId deployTestingKjar() throws IOException {
        KieContainerHelper kieContainerHelper = new KieContainerHelper();

        ReleaseId releaseId = kieContainerHelper.deployTestdataBenchmarkKjar(
                "buildSolverWithReleaseId",
                "org/optaplanner/benchmark/api/kieContainerNamedKsessionKmodule.xml",
                "org/optaplanner/benchmark/api/testdataKieContainerBenchmarkConfig.xml");
        return releaseId;
    }

}
