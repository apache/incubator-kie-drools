package org.optaplanner.benchmark.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.solver.DivertingClassLoader;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.phase.custom.NoChangeCustomPhaseCommand;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class PlannerBenchmarkFactoryTest {

    private static File benchmarkTestDir;
    private static File benchmarkOutputTestDir;

    @BeforeAll
    static void setup() throws IOException {
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
    void createFromSolverConfigXmlResource() {
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                "org/optaplanner/core/config/solver/testdataSolverConfig.xml");
        TestdataSolution solution = new TestdataSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        assertThat(benchmarkFactory.buildPlannerBenchmark(solution)).isNotNull();

        benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                "org/optaplanner/core/config/solver/testdataSolverConfig.xml", benchmarkOutputTestDir);
        assertThat(benchmarkFactory.buildPlannerBenchmark(solution)).isNotNull();
    }

    @Test
    void createFromSolverConfigXmlResource_classLoader() {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                "divertThroughClassLoader/org/optaplanner/core/api/solver/classloaderTestdataSolverConfig.xml", classLoader);
        TestdataSolution solution = new TestdataSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        assertThat(benchmarkFactory.buildPlannerBenchmark(solution)).isNotNull();

        benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                "divertThroughClassLoader/org/optaplanner/core/api/solver/classloaderTestdataSolverConfig.xml",
                benchmarkOutputTestDir, classLoader);
        assertThat(benchmarkFactory.buildPlannerBenchmark(solution)).isNotNull();
    }

    @Test
    void problemIsNotASolutionInstance() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(
                PlannerBenchmarkConfig.createFromSolverConfig(solverConfig));
        assertThatIllegalArgumentException().isThrownBy(
                () -> benchmarkFactory.buildPlannerBenchmark("This is not a solution instance."));
    }

    @Test
    void problemIsNull() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(
                PlannerBenchmarkConfig.createFromSolverConfig(solverConfig));
        TestdataSolution solution = new TestdataSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        assertThatIllegalArgumentException().isThrownBy(() -> benchmarkFactory.buildPlannerBenchmark(solution, null));
    }

    // ************************************************************************
    // Static creation methods: XML
    // ************************************************************************

    @Test
    void createFromXmlResource() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfig.xml");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertThat(plannerBenchmark).isNotNull();
        assertThat(plannerBenchmark.benchmark()).exists();
    }

    @Test
    void createFromXmlResource_classLoader() {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "divertThroughClassLoader/org/optaplanner/benchmark/api/classloaderTestdataBenchmarkConfig.xml", classLoader);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertThat(plannerBenchmark).isNotNull();
        assertThat(plannerBenchmark.benchmark()).exists();
    }

    @Test
    void createFromXmlResource_nonExisting() {
        final String nonExistingBenchmarkConfigResource = "org/optaplanner/benchmark/api/nonExistingBenchmarkConfig.xml";
        assertThatIllegalArgumentException()
                .isThrownBy(() -> PlannerBenchmarkFactory.createFromXmlResource(nonExistingBenchmarkConfigResource))
                .withMessageContaining(nonExistingBenchmarkConfigResource);
    }

    @Test
    void createFromInvalidXmlResource_failsShowingBothResourceAndReason() {
        final String invalidXmlBenchmarkConfigResource = "org/optaplanner/benchmark/api/invalidBenchmarkConfig.xml";
        assertThatIllegalArgumentException()
                .isThrownBy(() -> PlannerBenchmarkFactory.createFromXmlResource(invalidXmlBenchmarkConfigResource))
                .withMessageContaining(invalidXmlBenchmarkConfigResource)
                .withStackTraceContaining("invalidElementThatShouldNotBeHere");
    }

    @Test
    void createFromInvalidXmlFile_failsShowingBothPathAndReason() throws IOException {
        final String invalidXmlBenchmarkConfigResource = "org/optaplanner/benchmark/api/invalidBenchmarkConfig.xml";
        File file = new File(benchmarkTestDir, "invalidBenchmarkConfig.xml");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(invalidXmlBenchmarkConfigResource)) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        assertThatIllegalArgumentException()
                .isThrownBy(() -> PlannerBenchmarkFactory.createFromXmlFile(file))
                .withMessageContaining(file.toString())
                .withStackTraceContaining("invalidElementThatShouldNotBeHere");
    }

    @Test
    void createFromXmlResource_uninitializedBestSolution() {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfig.xml");
        SolverBenchmarkConfig solverBenchmarkConfig = benchmarkConfig.getSolverBenchmarkConfigList().get(0);
        CustomPhaseConfig phaseConfig = new CustomPhaseConfig();
        phaseConfig.setCustomPhaseCommandClassList(Collections.singletonList(NoChangeCustomPhaseCommand.class));
        solverBenchmarkConfig.getSolverConfig().setPhaseConfigList(Collections.singletonList(phaseConfig));
        PlannerBenchmark plannerBenchmark = PlannerBenchmarkFactory.create(benchmarkConfig).buildPlannerBenchmark();
        assertThat(plannerBenchmark).isNotNull();
        assertThat(plannerBenchmark.benchmark()).exists();
    }

    @Test
    void createFromXmlResource_subSingleCount() {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfig.xml");
        SolverBenchmarkConfig solverBenchmarkConfig = benchmarkConfig.getSolverBenchmarkConfigList().get(0);
        solverBenchmarkConfig.setSubSingleCount(3);
        PlannerBenchmark plannerBenchmark = PlannerBenchmarkFactory.create(benchmarkConfig).buildPlannerBenchmark();
        assertThat(plannerBenchmark).isNotNull();
        assertThat(plannerBenchmark.benchmark()).exists();
    }

    @Test
    void createFromXmlFile() throws IOException {
        File file = new File(benchmarkTestDir, "testdataBenchmarkConfig.xml");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfig.xml")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlFile(file);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertThat(plannerBenchmark).isNotNull();
        assertThat(plannerBenchmark.benchmark()).exists();
    }

    @Test
    void createFromXmlFile_classLoader() throws IOException {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        File file = new File(benchmarkTestDir, "classloaderTestdataBenchmarkConfig.xml");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/benchmark/api/classloaderTestdataBenchmarkConfig.xml")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlFile(file, classLoader);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertThat(plannerBenchmark).isNotNull();
        assertThat(plannerBenchmark.benchmark()).exists();
    }

    // ************************************************************************
    // Static creation methods: Freemarker
    // ************************************************************************

    @Test
    void createFromFreemarkerXmlResource() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlResource(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfigTemplate.xml.ftl");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertThat(plannerBenchmark).isNotNull();
        assertThat(plannerBenchmark.benchmark()).exists();
    }

    @Test
    void createFromFreemarkerXmlResource_classLoader() {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlResource(
                "divertThroughClassLoader/org/optaplanner/benchmark/api/classloaderTestdataBenchmarkConfigTemplate.xml.ftl",
                classLoader);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertThat(plannerBenchmark).isNotNull();
        assertThat(plannerBenchmark.benchmark()).exists();
    }

    @Test
    void createFromFreemarkerXmlResource_nonExisting() {
        assertThatIllegalArgumentException().isThrownBy(() -> PlannerBenchmarkFactory.createFromFreemarkerXmlResource(
                "org/optaplanner/benchmark/api/nonExistingBenchmarkConfigTemplate.xml.ftl"));
    }

    @Test
    void createFromFreemarkerXmlFile() throws IOException {
        File file = new File(benchmarkTestDir, "testdataBenchmarkConfigTemplate.xml.ftl");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/benchmark/api/testdataBenchmarkConfigTemplate.xml.ftl")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlFile(file);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertThat(plannerBenchmark).isNotNull();
        assertThat(plannerBenchmark.benchmark()).exists();
    }

    @Test
    void createFromFreemarkerXmlFile_classLoader() throws IOException {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        File file = new File(benchmarkTestDir, "classloaderTestdataBenchmarkConfigTemplate.xml.ftl");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/benchmark/api/classloaderTestdataBenchmarkConfigTemplate.xml.ftl")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlFile(file,
                classLoader);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        assertThat(plannerBenchmark).isNotNull();
        assertThat(plannerBenchmark.benchmark()).exists();
    }

    // ************************************************************************
    // Instance methods
    // ************************************************************************

    @Test
    void buildPlannerBenchmark() {
        PlannerBenchmarkConfig benchmarkConfig = new PlannerBenchmarkConfig();
        SolverBenchmarkConfig inheritedSolverConfig = new SolverBenchmarkConfig();
        inheritedSolverConfig.setSolverConfig(new SolverConfig()
                .withSolutionClass(TestdataSolution.class)
                .withEntityClasses(TestdataEntity.class)
                .withConstraintProviderClass(TestdataConstraintProvider.class));
        benchmarkConfig.setInheritedSolverBenchmarkConfig(inheritedSolverConfig);

        benchmarkConfig.setSolverBenchmarkConfigList(Arrays.asList(
                new SolverBenchmarkConfig(), new SolverBenchmarkConfig(), new SolverBenchmarkConfig()));

        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(benchmarkConfig);

        TestdataSolution solution1 = new TestdataSolution("s1");
        solution1.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        solution1.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        TestdataSolution solution2 = new TestdataSolution("s2");
        solution2.setEntityList(Arrays.asList(new TestdataEntity("e11"), new TestdataEntity("e12"), new TestdataEntity("e13")));
        solution2.setValueList(Arrays.asList(new TestdataValue("v11"), new TestdataValue("v12")));

        DefaultPlannerBenchmark plannerBenchmark =
                (DefaultPlannerBenchmark) benchmarkFactory.buildPlannerBenchmark(solution1, solution2);
        assertThat(plannerBenchmark).isNotNull();
        assertThat(plannerBenchmark.getPlannerBenchmarkResult().getSolverBenchmarkResultList().size()).isEqualTo(3);
        assertThat(plannerBenchmark.getPlannerBenchmarkResult().getUnifiedProblemBenchmarkResultList().size()).isEqualTo(2);
    }

    public static class TestdataConstraintProvider implements ConstraintProvider {
        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
            return new Constraint[0];
        }
    }

}
