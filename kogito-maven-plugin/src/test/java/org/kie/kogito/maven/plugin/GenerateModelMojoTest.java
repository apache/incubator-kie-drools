/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.maven.plugin;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.junit5.InjectMojo;
import org.apache.maven.plugin.testing.junit5.MojoTest;
import org.drools.codegen.common.GeneratedFile;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.manager.CompilerHelper;
import org.kie.kogito.codegen.manager.GenerateModelHelper;
import org.kie.kogito.codegen.manager.processes.PersistenceGenerationHelper;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Fail.fail;
import static org.kie.kogito.codegen.manager.CompilerHelper.RESOURCES;
import static org.kie.kogito.codegen.manager.CompilerHelper.SOURCES;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@MojoTest
class GenerateModelMojoTest {

    private static final KogitoBuildContext kogitoBuildContextMocked = mock(KogitoBuildContext.class);
    private static final ClassLoader classLoaderMocked = mock(ClassLoader.class);

    @Test
    @InjectMojo(goal = "generateModel", pom = "src/test/resources/unit/generate-model/pom.xml")
    void generateModel(GenerateModelMojo mojo) {
        commonSetup(mojo);
        try (MockedStatic<GenerateModelHelper> generateModelHelperMockedStatic = mockStatic(GenerateModelHelper.class)) {
            mojo.generateModel(kogitoBuildContextMocked);
            generateModelHelperMockedStatic.verify(() -> GenerateModelHelper.generateModelFiles(kogitoBuildContextMocked, false), times(1));
        }
    }

    @Test
    @InjectMojo(goal = "generateModel", pom = "src/test/resources/unit/generate-model/pom.xml")
    void generatePersistence(GenerateModelMojo mojo) {
        commonSetup(mojo);
        try (MockedStatic<PersistenceGenerationHelper> persistenceGenerationHelperMockedStatic = mockStatic(PersistenceGenerationHelper.class)) {
            mojo.generatePersistence(kogitoBuildContextMocked, classLoaderMocked);
            persistenceGenerationHelperMockedStatic.verify(() -> PersistenceGenerationHelper.generatePersistenceFiles(kogitoBuildContextMocked, classLoaderMocked, mojo.schemaVersion), times(1));
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    @Test
    @InjectMojo(goal = "generateModel", pom = "src/test/resources/unit/generate-model/pom.xml")
    void compileAndDump(GenerateModelMojo mojo) {
        commonSetup(mojo);
        try (MockedStatic<CompilerHelper> compilerHelperMockedStatic = mockStatic(CompilerHelper.class)) {
            Collection<GeneratedFile> generatedSources = new HashSet<>();
            Collection<GeneratedFile> generatedResources = new HashSet<>();
            Map<String, Collection<GeneratedFile>> generatedFiles = Map.of(SOURCES, generatedSources, RESOURCES, generatedResources);
            mojo.compileAndDump(generatedFiles, classLoaderMocked);
            compilerHelperMockedStatic.verify(
                    () -> CompilerHelper.compileAndDumpGeneratedSources(generatedSources, classLoaderMocked, mojo.project.getRuntimeClasspathElements(), mojo.baseDir, "UTF-8", "17", "17"),
                    times(1));
            compilerHelperMockedStatic.verify(() -> CompilerHelper.dumpResources(generatedResources, mojo.baseDir), times(1));
        } catch (MojoExecutionException e) {
            fail(e.getMessage(), e);
        }
    }

    private void commonSetup(GenerateModelMojo mojo) {
        mojo.outputDirectory = new File(mojo.project.getModel().getBuild().getOutputDirectory());
        mojo.baseDir = mojo.project.getBasedir();
        mojo.projectBaseDir = mojo.project.getBasedir();
        mojo.projectSourceEncoding = "UTF-8";
    }
}
