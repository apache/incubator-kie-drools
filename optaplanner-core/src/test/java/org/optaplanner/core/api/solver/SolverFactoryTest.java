/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.solver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class SolverFactoryTest {

    private static File solverTestDir;

    @BeforeClass
    public static void setup() {
        solverTestDir = new File("target/test/solverTest/");
        solverTestDir.mkdirs();
    }

    @Test
    public void createFromXmlResource() {
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void createFromXmlResource_noGenericsForBackwardsCompatibility() {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml");
        Solver solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFromXmlResource_nonExisting() {
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/nonExistingSolverConfig.xml");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void createFromXmlResource_classLoader() {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlResource(
                "divertThroughClassLoader/org/optaplanner/core/api/solver/classloaderTestdataSolverConfig.xml", classLoader);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void createFromXmlFile() throws IOException {
        File file = new File(solverTestDir, "testdataSolverConfig.xml");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlFile(file);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void createFromXmlFile_classLoader() throws IOException {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        File file = new File(solverTestDir, "classloaderTestdataSolverConfig.xml");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/optaplanner/core/api/solver/classloaderTestdataSolverConfig.xml")) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromXmlFile(file, classLoader);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void create() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void create_classLoader() {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        solverConfig.setClassLoader(classLoader);
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig()
                .withScoreDrls("divertThroughClassLoader/org/optaplanner/core/api/solver/classloaderTestdataScoreRules.drl"));
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void getScoreDirectorFactory() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        ScoreDirectorFactory<TestdataSolution> scoreDirectorFactory = solverFactory.getScoreDirectorFactory();
        assertNotNull(scoreDirectorFactory);

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        try (ScoreDirector<TestdataSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector()) {
            scoreDirector.setWorkingSolution(solution);
            Score score = scoreDirector.calculateScore();
            assertNotNull(score);
        }
    }

    @Test
    @Deprecated
    public void cloneSolverFactory() {
        SolverFactory<TestdataSolution> solverFactoryTemplate = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml");
        solverFactoryTemplate.getSolverConfig().setTerminationConfig(new TerminationConfig());
        SolverFactory<TestdataSolution> solverFactory1 = solverFactoryTemplate.cloneSolverFactory();
        SolverFactory<TestdataSolution> solverFactory2 = solverFactoryTemplate.cloneSolverFactory();
        assertNotSame(solverFactory1, solverFactory2);
        solverFactory1.getSolverConfig().getTerminationConfig().setMinutesSpentLimit(1L);
        solverFactory2.getSolverConfig().getTerminationConfig().setMinutesSpentLimit(2L);
        assertEquals((Long) 1L, solverFactory1.getSolverConfig().getTerminationConfig().getMinutesSpentLimit());
        assertEquals((Long) 2L, solverFactory2.getSolverConfig().getTerminationConfig().getMinutesSpentLimit());
        Solver<TestdataSolution> solver1 = solverFactory1.buildSolver();
        Solver<TestdataSolution> solver2 = solverFactory2.buildSolver();
        assertNotSame(solver1, solver2);
    }

}
