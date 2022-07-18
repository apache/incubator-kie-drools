/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.drl.engine.testingmodule.compilation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.io.FileSystemResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.drl.engine.compilation.model.DrlPackageDescrSetResource;
import org.kie.drl.engine.testingmodule.utils.DrlTestUtils;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.core.service.CompilationManagerImpl;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.assertj.core.api.Assertions.assertThat;

class CompileDrlTest {

    private static CompilationManager compilationManager;
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private static Set<File> drlFiles;

    private static Set<PackageDescr> packageDescrs;

    @BeforeAll
    static void setUp() throws IOException, DroolsParserException {
        DrlTestUtils.refreshDrlIndexFile();
        compilationManager = new CompilationManagerImpl();
        memoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(CompilationManager.class.getClassLoader());
        drlFiles = DrlTestUtils.collectDrlFiles("src/test/resources/org/drools/model/project/codegen");
        KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration =
                new KnowledgeBuilderConfigurationImpl();
        DrlResourceHandler drlResourceHandler =
                new DrlResourceHandler(knowledgeBuilderConfiguration);
        packageDescrs = new HashSet<>();
        for (File drlFile : drlFiles) {
            FileSystemResource fileSystemResource = new FileSystemResource(drlFile);
            PackageDescr process = drlResourceHandler.process(fileSystemResource);
            packageDescrs.add(process);
        }
    }

    @Test
    void compileDrlFromFile() {
        String basePath = UUID.randomUUID().toString();
        DrlFileSetResource toProcess = new DrlFileSetResource(drlFiles, basePath);
        Collection<IndexFile> retrieved = compilationManager.processResource(memoryCompilerClassLoader, toProcess);
        assertThat(retrieved).isNotNull().hasSize(1);
    }

    @Test
    void compileDrlFromPackageDescr() {
        String basePath = UUID.randomUUID().toString();
        DrlPackageDescrSetResource toProcess = new DrlPackageDescrSetResource(packageDescrs, basePath);
        Collection<IndexFile> retrieved = compilationManager.processResource(memoryCompilerClassLoader, toProcess);
        assertThat(retrieved).isNotNull().hasSize(1);
    }

}
