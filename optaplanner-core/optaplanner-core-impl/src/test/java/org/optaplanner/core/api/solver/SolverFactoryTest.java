package org.optaplanner.core.api.solver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class SolverFactoryTest {

    private static File solverTestDir;

    @BeforeAll
    static void setup() {
        solverTestDir = new File("target/test/solverTest/");
        solverTestDir.mkdirs();
    }

    @Test
    void createFromXmlResource() {
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertThat(solver).isNotNull();
    }

    @Test
    @SuppressWarnings("rawtypes")
    void createFromXmlResource_noGenericsForBackwardsCompatibility() {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml");
        Solver solver = solverFactory.buildSolver();
        assertThat(solver).isNotNull();
    }

    @Test
    void createFromNonExistingXmlResource_failsShowingResource() {
        final String xmlSolverConfigResource = "org/optaplanner/core/api/solver/nonExistingSolverConfig.xml";
        assertThatIllegalArgumentException().isThrownBy(() -> SolverFactory.createFromXmlResource(xmlSolverConfigResource))
                .withMessageContaining(xmlSolverConfigResource);
    }

    @Test
    void createFromNonExistingXmlFile_failsShowingPath() {
        final File xmlSolverConfigFile = new File(solverTestDir, "nonExistingSolverConfig.xml");
        assertThatIllegalArgumentException().isThrownBy(() -> SolverFactory.createFromXmlFile(xmlSolverConfigFile))
                .withMessageContaining(xmlSolverConfigFile.toString());
    }

    @Test
    void createFromXmlResource_classLoader() {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlResource(
                "divertThroughClassLoader/org/optaplanner/core/api/solver/classloaderTestdataSolverConfig.xml", classLoader);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertThat(solver).isNotNull();
    }

    @Test
    void createFromXmlFile() throws IOException {
        File file = new File(solverTestDir, "testdataSolverConfig.xml");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlFile(file);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertThat(solver).isNotNull();
    }

    @Test
    void createFromXmlFile_classLoader() throws IOException {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        File file = new File(solverTestDir, "classloaderTestdataSolverConfig.xml");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/core/api/solver/classloaderTestdataSolverConfig.xml")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlFile(file, classLoader);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertThat(solver).isNotNull();
    }

    @Test
    void createFromInvalidXmlResource_failsShowingBothResourceAndReason() {
        final String invalidXmlSolverConfigResource = "org/optaplanner/core/api/solver/invalidSolverConfig.xml";
        assertThatIllegalArgumentException()
                .isThrownBy(() -> SolverFactory.createFromXmlResource(invalidXmlSolverConfigResource))
                .withMessageContaining(invalidXmlSolverConfigResource)
                .withStackTraceContaining("invalidElementThatShouldNotBeHere");
    }

    @Test
    void createFromInvalidXmlFile_failsShowingBothPathAndReason() throws IOException {
        final String invalidXmlSolverConfigResource = "org/optaplanner/core/api/solver/invalidSolverConfig.xml";
        File file = new File(solverTestDir, "invalidSolverConfig.xml");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(invalidXmlSolverConfigResource)) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        assertThatIllegalArgumentException()
                .isThrownBy(() -> SolverFactory.createFromXmlFile(file))
                .withMessageContaining(file.toString())
                .withStackTraceContaining("invalidElementThatShouldNotBeHere");
    }

    @Test
    void create() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertThat(solver).isNotNull();
    }

    @Test
    void getScoreDirectorFactory() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        DefaultSolverFactory<TestdataSolution> solverFactory =
                (DefaultSolverFactory<TestdataSolution>) SolverFactory.<TestdataSolution> create(solverConfig);
        InnerScoreDirectorFactory<TestdataSolution, SimpleScore> scoreDirectorFactory =
                solverFactory.getScoreDirectorFactory();
        assertThat(scoreDirectorFactory).isNotNull();

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        try (InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector =
                scoreDirectorFactory.buildScoreDirector()) {
            scoreDirector.setWorkingSolution(solution);
            SimpleScore score = scoreDirector.calculateScore();
            assertThat(score).isNotNull();
        }
    }

}
