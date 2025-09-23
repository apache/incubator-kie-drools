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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.junit5.InjectMojo;
import org.apache.maven.plugin.testing.junit5.MojoTest;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.manager.BuilderManager;
import org.kie.kogito.codegen.manager.CompilerHelper;
import org.kie.kogito.codegen.manager.GenerateModelHelper;
import org.kie.kogito.codegen.manager.processes.PersistenceGenerationHelper;
import org.kie.kogito.codegen.manager.util.CodeGenManagerUtil;
import org.mockito.MockedStatic;
import org.mockito.verification.VerificationMode;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@MojoTest
class GenerateModelMojoTest {

    @Test
    @InjectMojo(goal = "generateModel", pom = "src/test/resources/unit/generate-model/pom.xml")
    void generateModelWithOnDemand(GenerateModelMojo mojo) {
        commonSetup(mojo, true);
        commonGenerateModel(mojo);
    }

    @Test
    @InjectMojo(goal = "generateModel", pom = "src/test/resources/unit/generate-model/pom.xml")
    void generateModelWithoutOnDemand(GenerateModelMojo mojo) {
        commonSetup(mojo, false);
        commonGenerateModel(mojo);
    }

    private void commonGenerateModel(GenerateModelMojo mojo) {
        try (MockedStatic<BuilderManager> builderManagerMockedStatic = mockStatic(BuilderManager.class);
                MockedStatic<CodeGenManagerUtil> codeGenManagerUtilMockedStatic = mockStatic(CodeGenManagerUtil.class);
                MockedStatic<GenerateModelHelper> generateModelHelperMockedStatic = mockStatic(GenerateModelHelper.class);
                MockedStatic<CompilerHelper> compilerHelperMockedStatic = mockStatic(CompilerHelper.class);
                MockedStatic<PersistenceGenerationHelper> persistenceGenerationHelperMockedStatic = mockStatic(PersistenceGenerationHelper.class)) {
            builderManagerMockedStatic.when(() -> BuilderManager.build(any(BuilderManager.BuildInfo.class))).thenCallRealMethod();
            generateModelHelperMockedStatic.when(() -> GenerateModelHelper.generateModel(any(GenerateModelHelper.GenerateModelInfo.class))).thenCallRealMethod();
            mojo.execute();
            builderManagerMockedStatic.verify(() -> BuilderManager.build(any(BuilderManager.BuildInfo.class)), times(1));
            codeGenManagerUtilMockedStatic.verify(() -> CodeGenManagerUtil.setSystemProperties(any()), times(1));
            generateModelHelperMockedStatic.verify(() -> GenerateModelHelper.generateModel(any(GenerateModelHelper.GenerateModelInfo.class)), times(1));
            VerificationMode expectedGeneratedModelFiles = mojo.onDemand ? never() : times(1);
            generateModelHelperMockedStatic.verify(() -> GenerateModelHelper.generateModelFiles(any(GenerateModelHelper.GenerateModelFilesInfo.class)), expectedGeneratedModelFiles);
            compilerHelperMockedStatic.verify(() -> CompilerHelper.compileAndDump(any(CompilerHelper.CompileInfo.class)), times(2));
            persistenceGenerationHelperMockedStatic.verify(() -> PersistenceGenerationHelper.generatePersistenceFiles(any(),
                    any(),
                    any()), times(1));
        } catch (MojoExecutionException e) {
            fail(e.getMessage(), e);
        }
    }

    private void commonSetup(GenerateModelMojo mojo, boolean onDemand) {
        mojo.projectBuildOutputDirectory = new File(mojo.project.getModel().getBuild().getOutputDirectory());
        mojo.projectBaseDir = mojo.project.getBasedir();
        mojo.projectSourceEncoding = "UTF-8";
        mojo.onDemand = onDemand;
    }
}
