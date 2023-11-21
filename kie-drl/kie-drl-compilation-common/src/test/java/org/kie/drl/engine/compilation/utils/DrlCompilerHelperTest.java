/**
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
package org.kie.drl.engine.compilation.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.io.FileSystemResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.drl.engine.compilation.model.DrlCompilationContext;
import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.drl.engine.compilation.model.DrlPackageDescrSetResource;
import org.kie.drl.engine.compilation.model.ExecutableModelClassesContainer;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.internal.builder.KnowledgeBuilderFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

class DrlCompilerHelperTest {

    private static DrlCompilationContext context;
    private static Set<File> drlFiles;
    private static Set<PackageDescr> packageDescrs;

    @BeforeAll
    static void setUp() throws IOException, DroolsParserException {
        context = DrlCompilationContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        drlFiles = Files.walk(Paths.get("src/test/resources"))
                .map(Path::toFile)
                .filter(File::isFile)
                .collect(Collectors.toSet());
        KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        DrlResourceHandler drlResourceHandler = new DrlResourceHandler(knowledgeBuilderConfiguration);
        packageDescrs = new HashSet<>();
        for (File drlFile : drlFiles) {
            FileSystemResource fileSystemResource = new FileSystemResource(drlFile);
            PackageDescr process = drlResourceHandler.process(fileSystemResource);
            packageDescrs.add(process);
        }
    }

    @Test
    void getDrlCallableClassesContainerFromPackageDescrResource() {
        String basePath = UUID.randomUUID().toString();
        DrlPackageDescrSetResource toProcess = new DrlPackageDescrSetResource(packageDescrs, basePath);
        EfestoCompilationOutput retrieved = DrlCompilerHelper.pkgDescrToExecModel(toProcess, context);
        commonVerifyEfestoCompilationOutput(retrieved, basePath);
    }

    @Test
    void getDrlCallableClassesContainerFromFileResource() {
        String basePath = UUID.randomUUID().toString();
        DrlFileSetResource toProcess = new DrlFileSetResource(drlFiles, basePath);
        EfestoCompilationOutput retrieved = DrlCompilerHelper.drlToExecutableModel(toProcess, context);
        commonVerifyEfestoCompilationOutput(retrieved, basePath);
    }

    private void commonVerifyEfestoCompilationOutput(EfestoCompilationOutput toVerify, String baseBath) {
        assertThat(toVerify).isNotNull().isInstanceOf(ExecutableModelClassesContainer.class);
        ExecutableModelClassesContainer retrieved = (ExecutableModelClassesContainer) toVerify;
        assertThat(retrieved.getModelLocalUriId().model()).isEqualTo("drl");
        assertThat(retrieved.getModelLocalUriId().basePath()).isEqualTo(SLASH + baseBath);
        assertThat(retrieved.getFullClassNames()).hasSize(3); // magic number due to compiled resources
        assertThat(retrieved.getCompiledClassesMap()).hasSize(49); // magic number due to compiled resources
    }

}