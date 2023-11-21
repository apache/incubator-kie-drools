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

import org.drools.codegen.common.GeneratedFile;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.io.FileSystemResource;
import org.drools.model.codegen.execmodel.PackageModelWriter;
import org.drools.model.codegen.project.CodegenPackageSources;
import org.drools.model.codegen.project.RuleCodegenError;
import org.drools.model.codegen.tool.ExplicitCanonicalModelCompiler;
import org.kie.api.io.Resource;
import org.kie.drl.api.identifiers.DrlIdFactory;
import org.kie.drl.api.identifiers.KieDrlComponentRoot;
import org.kie.drl.api.identifiers.LocalComponentIdDrl;
import org.kie.drl.engine.compilation.model.DecisionTableFileSetResource;
import org.kie.drl.engine.compilation.model.DrlCompilationContext;
import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.drl.engine.compilation.model.DrlPackageDescrSetResource;
import org.kie.drl.engine.compilation.model.ExecutableModelClassesContainer;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoSetResource;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DrlCompilerHelper {

    private static final Logger logger = LoggerFactory.getLogger(DrlCompilerHelper.class);

    private DrlCompilerHelper() {
    }

    public static ExecutableModelClassesContainer dTableToDrl(DecisionTableFileSetResource resources, DrlCompilationContext context) {
        // TODO {mfusco}
        throw new KieCompilerServiceException("Not implemented, yet");
    }

    public static DrlPackageDescrSetResource drlToPackageDescrs(DrlFileSetResource resources, DrlCompilationContext context) {
        KnowledgeBuilderConfigurationImpl conf = context.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        Set<PackageDescr> packageDescrSet = new HashSet<>();
        packageDescrSet.addAll(buildCompositePackageDescrs(resources, conf));
        return new DrlPackageDescrSetResource(packageDescrSet, resources.getModelLocalUriId().basePath());
    }

    public static ExecutableModelClassesContainer pkgDescrToExecModel(EfestoSetResource<PackageDescr> resources, DrlCompilationContext context) {
        return pkgDescrToExecModel(toCompositePackageDescrs(resources.getContent()), resources.getModelLocalUriId().basePath(), KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY), context);
    }

    public static ExecutableModelClassesContainer drlToExecutableModel(DrlFileSetResource resources, DrlCompilationContext context) {
        KnowledgeBuilderConfigurationImpl conf = context.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
        return pkgDescrToExecModel(buildCompositePackageDescrs(resources, conf), resources.getModelLocalUriId().basePath(), conf, context);
    }

    public static ExecutableModelClassesContainer pkgDescrToExecModel(Collection<CompositePackageDescr> packages, String basePath, KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration, DrlCompilationContext context) {
        ExplicitCanonicalModelCompiler<CodegenPackageSources> compiler =
                ExplicitCanonicalModelCompiler.of(packages, knowledgeBuilderConfiguration,
                        pkgModel -> CodegenPackageSources.dumpSources(new PackageModelWriter(pkgModel)));

        compiler.process();
        BuildResultCollector buildResults = compiler.getBuildResults();

        if (buildResults.hasErrors()) {
            for (KnowledgeBuilderResult e : buildResults.getResults()) {
                logger.error(e.getMessage(), e);
            }
            throw new RuleCodegenError(buildResults.getAllResults());
        }

        Collection<CodegenPackageSources> packageSources = compiler.getPackageSources();

        List<GeneratedFile> modelFiles = new ArrayList<>();
        List<String> generatedRulesModels = new ArrayList<>();
        for (CodegenPackageSources pkgSources : packageSources) {
            pkgSources.collectGeneratedFiles(modelFiles);
            generatedRulesModels.addAll(pkgSources.getExecutableRulesClasses());
        }
        Map<String, String> sourceCode = new HashMap<>();
        for (GeneratedFile generatedFile : modelFiles) {
            String key = generatedFile.relativePath()
                    .replace(".java", "")
                    .replace(File.separatorChar, '.');
            String value = new String(generatedFile.contents(),
                                      StandardCharsets.UTF_8);
            sourceCode.put(key, value);
        }
        Map<String, byte[]> compiledClasses = context.compileClasses(sourceCode);
        LocalComponentIdDrl modelLocalUriId = new EfestoAppRoot()
                .get(KieDrlComponentRoot.class)
                .get(DrlIdFactory.class)
                .get(basePath);
        return new ExecutableModelClassesContainer(modelLocalUriId, generatedRulesModels, compiledClasses);
    }

    private static Collection<CompositePackageDescr> buildCompositePackageDescrs(DrlFileSetResource resources, KnowledgeBuilderConfigurationImpl conf) {
        DrlResourceHandler drlResourceHandler = new DrlResourceHandler(conf);
        List<PackageDescr> packageDescrs = new ArrayList<>();
        for(FileSystemResource resource : resources.getFileSystemResource()) {
            packageDescrs.add(resourceToPackageDescr(drlResourceHandler, resource));
        }
        return toCompositePackageDescrs(packageDescrs);
    }

    private static Collection<CompositePackageDescr> toCompositePackageDescrs(Iterable<PackageDescr> packageDescrs) {
        Map<String, CompositePackageDescr> packages = new HashMap<>();
        for (PackageDescr packageDescr : packageDescrs) {
            addPackageDescr(packageDescr, packageDescr.getResource(), packages);
        }
        return packages.values();
    }

    private static void addPackageDescr(PackageDescr packageDescr, Resource resource, Map<String, CompositePackageDescr> packages) {
        packages.computeIfAbsent(packageDescr.getNamespace(), CompositePackageDescr::new).addPackageDescr(resource, packageDescr);
    }

    private static PackageDescr resourceToPackageDescr(DrlResourceHandler drlResourceHandler, Resource resource) {
        try {
            return drlResourceHandler.process(resource);
        } catch (DroolsParserException | IOException e) {
            throw new KieCompilerServiceException(e);
        }
    }
}
