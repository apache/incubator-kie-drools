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
import java.util.Set;
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
import org.kie.drl.api.identifiers.DrlIdFactory;
import org.kie.drl.api.identifiers.LocalComponentIdDrl;
import org.kie.drl.engine.compilation.model.DecisionTableFileSetResource;
import org.kie.drl.engine.compilation.model.DrlCompilationContext;
import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.drl.engine.compilation.model.DrlPackageDescrSetResource;
import org.kie.drl.engine.compilation.model.ExecutableModelClassesContainer;
import org.kie.efesto.common.api.identifiers.ReflectiveAppRoot;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoSetResource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrlCompilerHelper {

    private static final Logger logger = LoggerFactory.getLogger(DrlCompilerHelper.class);

    private DrlCompilerHelper() {
    }

    public static ExecutableModelClassesContainer dTableToDrl(DecisionTableFileSetResource resources, DrlCompilationContext context) {
        // TODO {mfusco}
        throw new KieCompilerServiceException("Not implemented, yet");
    }

    public static DrlPackageDescrSetResource drlToPackageDescrs(DrlFileSetResource resources, DrlCompilationContext context) {
        KnowledgeBuilderConfigurationImpl conf = (KnowledgeBuilderConfigurationImpl) context.newKnowledgeBuilderConfiguration();
        Set<PackageDescr> packageDescrSet = buildCompositePackageDescrs(resources, conf).stream().collect(Collectors.toSet());
        return new DrlPackageDescrSetResource(packageDescrSet, resources.getModelLocalUriId().basePath());
    }

    public static ExecutableModelClassesContainer pkgDescrToExecModel(EfestoSetResource<PackageDescr> resources, DrlCompilationContext context) {
        return pkgDescrToExecModel(toCompositePackageDescrs(resources.getContent()), resources.getModelLocalUriId().basePath(), new KnowledgeBuilderConfigurationImpl(), context);
    }

    public static ExecutableModelClassesContainer drlToExecutableModel(DrlFileSetResource resources, DrlCompilationContext context) {
        KnowledgeBuilderConfigurationImpl conf = (KnowledgeBuilderConfigurationImpl) context.newKnowledgeBuilderConfiguration();

        return pkgDescrToExecModel(buildCompositePackageDescrs(resources, conf), resources.getModelLocalUriId().basePath(), conf, context);
    }

    public static ExecutableModelClassesContainer pkgDescrToExecModel(Collection<CompositePackageDescr> packages, String basePath, KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration, DrlCompilationContext context) {
        ExplicitCanonicalModelCompiler<KogitoPackageSources> compiler =
                ExplicitCanonicalModelCompiler.of( packages, knowledgeBuilderConfiguration, KogitoPackageSources::dumpSources );

        compiler.process();
        BuildResultCollector buildResults = compiler.getBuildResults();

        if (buildResults.hasErrors()) {
            for (KnowledgeBuilderResult e : buildResults.getResults()) {
                logger.error(e.getMessage(), e);
            }
            throw new RuleCodegenError(buildResults.getAllResults());
        }

        Collection<KogitoPackageSources> packageSources = compiler.getPackageSources();

        List<GeneratedFile> modelFiles = new ArrayList<>();
        List<String> generatedRulesModels = new ArrayList<>();
        for (KogitoPackageSources pkgSources : packageSources) {
            pkgSources.collectGeneratedFiles(modelFiles);
            generatedRulesModels.addAll(pkgSources.getExecutableRulesClasses());
        }

        Map<String, String> sourceCode = modelFiles.stream()
                .collect(Collectors.toMap(generatedFile -> generatedFile.getPath()
                                                  .replace(".java", "")
                                                  .replace(File.separatorChar, '.'),
                                          generatedFile -> new String(generatedFile.getData(),
                                                                      StandardCharsets.UTF_8)));

        Map<String, byte[]> compiledClasses = context.compileClasses(sourceCode);
        LocalComponentIdDrl modelLocalUriId = new ReflectiveAppRoot("")
                .get(DrlIdFactory.class)
                .get(basePath);
        return new ExecutableModelClassesContainer(modelLocalUriId, generatedRulesModels, compiledClasses);
    }

    private static Collection<CompositePackageDescr> buildCompositePackageDescrs(DrlFileSetResource resources, KnowledgeBuilderConfigurationImpl conf) {
        DrlResourceHandler drlResourceHandler = new DrlResourceHandler(conf);
        return toCompositePackageDescrs(resources.getFileSystemResource().stream().map(r -> resourceToPackageDescr(drlResourceHandler, r)).collect(Collectors.toList()));
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
