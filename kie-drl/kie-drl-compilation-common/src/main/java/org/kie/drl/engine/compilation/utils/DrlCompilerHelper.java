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
package org.kie.drl.engine.compilation.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.model.codegen.execmodel.GeneratedFile;
import org.drools.model.codegen.project.KogitoPackageSources;
import org.drools.model.codegen.project.RuleCodegenError;
import org.drools.model.codegen.tool.ExplicitCanonicalModelCompiler;
import org.kie.api.io.Resource;
import org.kie.drl.engine.compilation.model.DecisionTableFileSetResource;
import org.kie.drl.engine.compilation.model.DrlCallableClassesContainer;
import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoSetResource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.memorycompiler.JavaConfiguration;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrlCompilerHelper {

    private static final Logger logger = LoggerFactory.getLogger(DrlCompilerHelper.class);

    private DrlCompilerHelper() {
    }

    public static DrlCallableClassesContainer getDrlCallableClassesContainer(DecisionTableFileSetResource resources, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        // TODO {mfusco}
        // There are two possible options
        // 1) translate the DecisionTableFileSetResource to DrlFileSetResource, and then invoke getDrlCallableClassesContainer(DrlFileSetResource, MemoryCompilerClassLoader)
        // 2) define an "intermediate" resource that contains PackageDescr, and returns it (as it will be done inside PMML)
        throw new KieCompilerServiceException("Not implemented, yet");
    }

    public static DrlCallableClassesContainer getDrlCallableClassesContainer(EfestoSetResource<PackageDescr> resources, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        Map<String, CompositePackageDescr> packages = new HashMap<>();
        for (PackageDescr packageDescr : resources.getContent()) {
            addPackageDescr(packageDescr, packageDescr.getResource(), packages);
        }
        return getDrlCallableClassesContainer(packages, resources.getBasePath(), new KnowledgeBuilderConfigurationImpl(), memoryCompilerClassLoader);
    }


    public static DrlCallableClassesContainer getDrlCallableClassesContainer(DrlFileSetResource resources, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration = new KnowledgeBuilderConfigurationImpl();

        DrlResourceHandler drlResourceHandler = new DrlResourceHandler(knowledgeBuilderConfiguration);

        Map<String, CompositePackageDescr> packages = new HashMap<>();

        for (Resource resource : resources.getFileSystemResource()) {
            parseAndAdd(drlResourceHandler, resource, packages);
        }
        return getDrlCallableClassesContainer(packages, resources.getBasePath(), knowledgeBuilderConfiguration, memoryCompilerClassLoader);
    }

    static DrlCallableClassesContainer getDrlCallableClassesContainer(Map<String, CompositePackageDescr> packages, String basePath, KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        ExplicitCanonicalModelCompiler<KogitoPackageSources> compiler =
                ExplicitCanonicalModelCompiler.of(
                        packages.values(),
                        knowledgeBuilderConfiguration,
                        KogitoPackageSources::dumpSources);

        compiler.process();
        BuildResultCollector buildResults = compiler.getBuildResults();

        if (buildResults.hasErrors()) {
            for (KnowledgeBuilderResult e : buildResults.getResults()) {
                logger.error(e.getMessage(), e);
            }
            throw new RuleCodegenError(buildResults.getAllResults());
        }

        Collection<KogitoPackageSources> packageSources = compiler.getPackageSources();

        List<GeneratedFile> legacyModelFiles = new ArrayList<>();
        List<String> generatedRulesModels = new ArrayList<>();

        for (KogitoPackageSources pkgSources : packageSources) {
            pkgSources.collectGeneratedFiles(legacyModelFiles);
            generatedRulesModels.add(pkgSources.getPackageName() + "." + pkgSources.getRulesFileName());
        }


        Map<String, String> sourceCode = legacyModelFiles.stream()
                .collect(Collectors.toMap(generatedFile -> generatedFile.getPath()
                                .replace(".java", "")
                                .replace(File.separatorChar, '.'),
                        generatedFile -> new String(generatedFile.getData(), StandardCharsets.UTF_8)));

        Map<String, byte[]> compiledClasses = compileClasses(sourceCode, memoryCompilerClassLoader);
        return new DrlCallableClassesContainer(new FRI(basePath, "drl"), generatedRulesModels, compiledClasses);
    }

    static void addPackageDescr(PackageDescr packageDescr, Resource resource, Map<String, CompositePackageDescr> packages) {
        CompositePackageDescr compositePackageDescr =
                packages.computeIfAbsent(packageDescr.getNamespace(), (CompositePackageDescr) -> new CompositePackageDescr(packageDescr.getNamespace()));
        compositePackageDescr.addPackageDescr(resource, packageDescr);
    }

    static void parseAndAdd(DrlResourceHandler drlResourceHandler, Resource resource, Map<String, CompositePackageDescr> packages) {
        try {
            addPackageDescr(drlResourceHandler.process(resource), resource, packages);
        } catch (DroolsParserException | IOException e) {
            throw new KieCompilerServiceException(e);
        }
    }

    static Map<String, byte[]> compileClasses(Map<String, String> sourcesMap, KieMemoryCompiler.MemoryCompilerClassLoader memoryClassLoader) {
        return KieMemoryCompiler.compileNoLoad(sourcesMap, memoryClassLoader, JavaConfiguration.CompilerType.NATIVE);
    }
}
