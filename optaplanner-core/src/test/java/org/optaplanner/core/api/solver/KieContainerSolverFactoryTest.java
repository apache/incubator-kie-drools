/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.util.Collections;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirectorFactory;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.util.KieContainerHelper;

import static org.junit.Assert.*;

public class KieContainerSolverFactoryTest extends CommonTestMethodBase {

    private final KieContainerHelper kieContainerHelper = new KieContainerHelper();

    @Test
    public void buildSolverWithReleaseId() throws IOException {
        ReleaseId releaseId = kieContainerHelper.deployTestdataSolverKjar(
                "buildSolverWithReleaseId",
                "org/optaplanner/core/api/solver/kieContainerNamedKsessionKmodule.xml",
                "org/optaplanner/core/api/solver/kieContainerNamedKsessionTestdataSolverConfig.xml");
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromKieContainerXmlResource(
                releaseId, "testdata/kjar/solverConfig.solver");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
        assertNewKieSessionSucceeds(solver);
    }

    @Test
    public void buildSolverWithKieContainer() throws IOException {
        ReleaseId releaseId = kieContainerHelper.deployTestdataSolverKjar(
                "buildSolverWithKieContainer",
                "org/optaplanner/core/api/solver/kieContainerNamedKsessionKmodule.xml",
                "org/optaplanner/core/api/solver/kieContainerNamedKsessionTestdataSolverConfig.xml");
        KieContainer kieContainer = KieServices.Factory.get().newKieContainer(releaseId);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromKieContainerXmlResource(
                kieContainer, "testdata/kjar/solverConfig.solver");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
        assertNewKieSessionSucceeds(solver);
    }

    @Test
    public void buildScanAnnotatedClassesSolver() throws IOException {
        ReleaseId releaseId = kieContainerHelper.deployTestdataSolverKjar(
                "buildScanAnnotatedClassesSolver",
                "org/optaplanner/core/api/solver/kieContainerNamedKsessionKmodule.xml",
                "org/optaplanner/core/api/solver/scanAnnotatedKieContainerTestdataSolverConfig.xml");
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromKieContainerXmlResource(
                releaseId, "testdata/kjar/solverConfig.solver");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
        assertNewKieSessionSucceeds(solver);
    }

    @Test
    public void buildSolverWithDefaultKsessionKmodule() throws IOException {
        ReleaseId releaseId = kieContainerHelper.deployTestdataSolverKjar(
                "buildSolverWithDefaultKsessionKmodule",
                "org/optaplanner/core/api/solver/kieContainerDefaultKsessionKmodule.xml",
                "org/optaplanner/core/api/solver/kieContainerDefaultKsessionTestdataSolverConfig.xml");
        SolverFactory<?> solverFactory = SolverFactory.createFromKieContainerXmlResource(
                releaseId, "testdata/kjar/solverConfig.solver");
        Solver<?> solver = solverFactory.buildSolver();
        assertNotNull(solver);
        assertNewKieSessionSucceeds(solver);
    }

    @Test
    public void buildSolverWithEmptyKmodule() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        ReleaseId releaseId = kieContainerHelper.deployTestdataSolverKjar(
                "buildSolverWithEmptyKmodule",
                "org/optaplanner/core/api/solver/kieContainerEmptyKmodule.xml",
                "org/optaplanner/core/api/solver/kieContainerDefaultKsessionTestdataSolverConfig.xml");
        SolverFactory<?> solverFactory = SolverFactory.createFromKieContainerXmlResource(
                releaseId, "testdata/kjar/solverConfig.solver");
        Solver<?> solver = solverFactory.buildSolver();
        assertNotNull(solver);
        assertNewKieSessionSucceeds(solver);
    }

    @Test
    public void buildSolverEmptyWithKieContainer() throws IOException, ReflectiveOperationException {
        ReleaseId releaseId = kieContainerHelper.deployTestdataSolverKjar(
                "buildSolverWithReleaseId",
                "org/optaplanner/core/api/solver/kieContainerNamedKsessionKmodule.xml", null);
        KieContainer kieContainer = KieServices.Factory.get().newKieContainer(releaseId);
        SolverFactory<?> solverFactory = SolverFactory.createEmptyFromKieContainer(kieContainer);
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        solverConfig.setSolutionClass(
                kieContainer.getClassLoader().loadClass("testdata.kjar.ClassloadedTestdataSolution"));
        solverConfig.setEntityClassList(Collections.singletonList(
                kieContainer.getClassLoader().loadClass("testdata.kjar.ClassloadedTestdataEntity")));
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setKsessionName("testdataKsession");
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        Solver<?> solver = solverFactory.buildSolver();
        assertNotNull(solver);
        assertNewKieSessionSucceeds(solver);
    }

    private void assertNewKieSessionSucceeds(Solver<?> solver) {
        DefaultSolver<?> defaultSolver = (DefaultSolver<?>) solver;
        DroolsScoreDirectorFactory scoreDirectorFactory = (DroolsScoreDirectorFactory<?>) defaultSolver.getScoreDirectorFactory();
        scoreDirectorFactory.newKieSession();
    }

}
