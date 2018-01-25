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
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;

import com.google.common.io.Resources;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.junit.Assert.*;

public class KieContainerSolverFactoryTest extends CommonTestMethodBase {

    private static KieServices kieServices;

    @BeforeClass
    public static void deployTestdataKjar() throws IOException {
        kieServices = KieServices.Factory.get();
    }

    protected static Resource buildResource(String resourceString, String targetPath) throws IOException {
        String content = readResourceToString(resourceString);
        Resource resource = kieServices.getResources().newReaderResource(new StringReader(content), "UTF-8");
        resource.setTargetPath(targetPath);
        if (resourceString.endsWith(".java")) {
            resource.setResourceType(ResourceType.JAVA);
        }
        return resource;
    }

    protected static String readResourceToString(String resourceString) throws IOException {
        URL url = Resources.getResource(resourceString);
        return Resources.toString(url, Charset.forName("UTF-8"));
    }

    protected ReleaseId deployTestdataKjar(String artifactId, String kmodulePath, String solverConfigPath)
            throws IOException {
        ReleaseId releaseId = kieServices.newReleaseId("org.optaplanner.core.test", artifactId, "1.0.0");

        String kmodule = readResourceToString(kmodulePath);
        Resource valueClass = buildResource("org/optaplanner/core/impl/testdata/domain/classloaded/ClassloadedTestdataValue.java",
                "testdata/kjar/ClassloadedTestdataValue.java");
        Resource entityClass = buildResource("org/optaplanner/core/impl/testdata/domain/classloaded/ClassloadedTestdataEntity.java",
                "testdata/kjar/ClassloadedTestdataEntity.java");
        Resource solutionClass = buildResource("org/optaplanner/core/impl/testdata/domain/classloaded/ClassloadedTestdataSolution.java",
                "testdata/kjar/ClassloadedTestdataSolution.java");
        Resource scoreRules = buildResource("org/optaplanner/core/api/solver/kieContainerTestdataScoreRules.drl",
                "testdata/kjar/scoreRules.drl");
        if (solverConfigPath  != null) {
            Resource solverConfig = buildResource(solverConfigPath,
                    "testdata/kjar/solverConfig.solver");

            createAndDeployJar(kieServices, kmodule, releaseId,
                    valueClass, entityClass, solutionClass,
                    scoreRules, solverConfig);
        } else {
            createAndDeployJar(kieServices, kmodule, releaseId,
                    valueClass, entityClass, solutionClass,
                    scoreRules);
        }
        return releaseId;
    }

    // ************************************************************************
    // Test methods
    // ************************************************************************

    @Test
    public void buildSolverWithReleaseId() throws IOException {
        ReleaseId releaseId = deployTestdataKjar(
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
        ReleaseId releaseId = deployTestdataKjar(
                "buildSolverWithKieContainer",
                "org/optaplanner/core/api/solver/kieContainerNamedKsessionKmodule.xml",
                "org/optaplanner/core/api/solver/kieContainerNamedKsessionTestdataSolverConfig.xml");
        KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromKieContainerXmlResource(
                kieContainer, "testdata/kjar/solverConfig.solver");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
        assertNewKieSessionSucceeds(solver);
    }

    @Test
    public void buildScanAnnotatedClassesSolver() throws IOException {
        ReleaseId releaseId = deployTestdataKjar(
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
        ReleaseId releaseId = deployTestdataKjar(
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
        ReleaseId releaseId = deployTestdataKjar(
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
        ReleaseId releaseId = deployTestdataKjar(
                "buildSolverWithReleaseId",
                "org/optaplanner/core/api/solver/kieContainerNamedKsessionKmodule.xml", null);
        KieContainer kieContainer = kieServices.newKieContainer(releaseId);
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
        DroolsScoreDirectorFactory scoreDirectorFactory = (DroolsScoreDirectorFactory<?>) solver.getScoreDirectorFactory();
        scoreDirectorFactory.newKieSession();
    }

}
