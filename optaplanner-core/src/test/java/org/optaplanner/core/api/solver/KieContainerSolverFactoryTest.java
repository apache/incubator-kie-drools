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
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;

import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public class KieContainerSolverFactoryTest extends CommonTestMethodBase {

    private static KieServices kieServices;
    private static ReleaseId releaseId;

    @BeforeClass
    public static void deployTestdataKjar() throws IOException {
        kieServices = KieServices.Factory.get();
        releaseId = kieServices.newReleaseId("org.optaplanner", "optaplanner-testdata-kjar", "1.0.0");

        Resource valueClass = buildResource("org/optaplanner/core/impl/testdata/domain/classloaded/ClassloadedTestdataValue.java",
                "testdata/kjar/ClassloadedTestdataValue.java");
        Resource entityClass = buildResource("org/optaplanner/core/impl/testdata/domain/classloaded/ClassloadedTestdataEntity.java",
                "testdata/kjar/ClassloadedTestdataEntity.java");
        Resource solutionClass = buildResource("org/optaplanner/core/impl/testdata/domain/classloaded/ClassloadedTestdataSolution.java",
                "testdata/kjar/ClassloadedTestdataSolution.java");
        Resource scoreRules = buildResource("org/optaplanner/core/api/solver/kieContainerTestdataScoreRules.drl",
                "testdata/kjar/scoreRules.drl");
        Resource solverConfig = buildResource("org/optaplanner/core/api/solver/kieContainerTestdataSolverConfig.xml",
                "testdata/kjar/kieContainerTestdataSolverConfig.solver");
        Resource scanAnnotatedSolverConfig = buildResource("org/optaplanner/core/api/solver/scanAnnotatedKieContainerTestdataSolverConfig.xml",
                "testdata/kjar/scanAnnotatedKieContainerTestdataSolverConfig.solver");
        String kmodule = readResourceToString("org/optaplanner/core/api/solver/kieContainerKmodule.xml");
        createAndDeployJar(kieServices, kmodule, releaseId,
                valueClass, entityClass, solutionClass,
                scoreRules,
                solverConfig, scanAnnotatedSolverConfig);
    }

    private static Resource buildResource(String resourceString, String targetPath) throws IOException {
        String content = readResourceToString(resourceString);
        Resource resource = kieServices.getResources().newReaderResource(new StringReader(content), "UTF-8");
        resource.setTargetPath(targetPath);
        if (resourceString.endsWith(".java")) {
            resource.setResourceType(ResourceType.JAVA);
        }
        return resource;
    }

    private static String readResourceToString(String resourceString) throws IOException {
        URL url = Resources.getResource(resourceString);
        return Resources.toString(url, Charset.forName("UTF-8"));
    }

    // ************************************************************************
    // Test methods
    // ************************************************************************

    @Test
    public void buildSolverWithReleaseId() {
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromKieContainerXmlResource(
                releaseId, "testdata/kjar/kieContainerTestdataSolverConfig.solver");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void buildSolverWithKieContainer() {
        KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromKieContainerXmlResource(
                kieContainer, "testdata/kjar/kieContainerTestdataSolverConfig.solver");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void buildScanAnnotatedSolverWithReleaseId() {
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromKieContainerXmlResource(
                releaseId, "testdata/kjar/scanAnnotatedKieContainerTestdataSolverConfig.solver");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void buildScanAnnotatedSolverWithKieContainer() {
        KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.createFromKieContainerXmlResource(
                kieContainer, "testdata/kjar/scanAnnotatedKieContainerTestdataSolverConfig.solver");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

}
