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
package org.drools.model.codegen.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.resources.DecisionTableResourceHandler;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.model.codegen.tool.ExplicitCanonicalModelCompiler;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.util.maven.support.ReleaseIdImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.drools.compiler.kie.builder.impl.AbstractKieModule.loadResourceConfiguration;

/**
 * Utility class to wrap ModelBuilderImpl + KnowledgeBuilder and extract the generated source code or metadata
 */
public class DroolsModelBuilder {
    public static final ReleaseIdImpl DUMMY_RELEASE_ID = new ReleaseIdImpl("dummy:dummy:0.0.0");
    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsModelBuilder.class);
    private Collection<KogitoPackageSources> kogitoPackageSources;

    private final Collection<Resource> resources;
    private final boolean decisionTableSupported;
    private final KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration;
    private final DroolsModelBuildContext context;

    public DroolsModelBuilder(DroolsModelBuildContext context, Collection<Resource> resources, boolean decisionTableSupported) {
        this.context = context;
        this.resources = resources;
        this.decisionTableSupported = decisionTableSupported;
        this.knowledgeBuilderConfiguration = new KnowledgeBuilderConfigurationImpl();
        checkDependencyTableSupport();
    }

    void build() {

        DrlResourceHandler drlResourceHandler =
                new DrlResourceHandler(knowledgeBuilderConfiguration);
        DecisionTableResourceHandler decisionTableHandler =
                new DecisionTableResourceHandler(knowledgeBuilderConfiguration, DUMMY_RELEASE_ID);

        Map<String, CompositePackageDescr> packages = new HashMap<>();

        for (Resource resource : resources) {
            try {
                if (resource.getResourceType() == ResourceType.DRL) {
                    handleDrl(drlResourceHandler, packages, resource);
                } else if (resource.getResourceType() == ResourceType.DTABLE) {
                    handleDtable(decisionTableHandler, packages, resource);
                }
            } catch (DroolsParserException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        ExplicitCanonicalModelCompiler<KogitoPackageSources> compiler =
                ExplicitCanonicalModelCompiler.of(
                packages.values(),
                knowledgeBuilderConfiguration,
                KogitoPackageSources::dumpSources).setContext(context);

        compiler.process();
        BuildResultCollector buildResults = compiler.getBuildResults();

        if (buildResults.hasErrors()) {
            for (KnowledgeBuilderResult error : buildResults.getResults()) {
                LOGGER.error(error.toString());
            }
            throw new RuleCodegenError(buildResults.getAllResults());
        }

        this.kogitoPackageSources = compiler.getPackageSources();
    }

    public boolean hasRuleUnits() {
        return kogitoPackageSources.stream().anyMatch(pkg -> !pkg.getRuleUnits().isEmpty());
    }

    private void handleDtable(DecisionTableResourceHandler decisionTableHandler, Map<String, CompositePackageDescr> packages, Resource resource) throws DroolsParserException {
        PackageDescr packageDescr = decisionTableHandler.process(resource, loadDtableResourceConfiguration(resource));
        CompositePackageDescr compositePackageDescr =
                packages.computeIfAbsent(packageDescr.getNamespace(), CompositePackageDescr::new);
        compositePackageDescr.addPackageDescr(resource, packageDescr);
    }

    private void handleDrl(DrlResourceHandler drlResourceHandler, Map<String, CompositePackageDescr> packages, Resource resource) throws DroolsParserException, IOException {
        PackageDescr packageDescr = drlResourceHandler.process(resource);
        CompositePackageDescr compositePackageDescr =
                packages.computeIfAbsent(packageDescr.getNamespace(), CompositePackageDescr::new);
        compositePackageDescr.addPackageDescr(resource, packageDescr);
    }

    public Collection<GeneratedFile> generateCanonicalModelSources() {
        List<GeneratedFile> modelFiles = new ArrayList<>();
        List<org.drools.model.codegen.execmodel.GeneratedFile> legacyModelFiles = new ArrayList<>();

        for (KogitoPackageSources pkgSources : this.kogitoPackageSources) {
            pkgSources.collectGeneratedFiles(legacyModelFiles);
            modelFiles.addAll(generateInternalResource(pkgSources));
        }

        modelFiles.addAll(convertGeneratedRuleFile(legacyModelFiles));
        return modelFiles;
    }

    Collection<KogitoPackageSources> packageSources() {
        return this.kogitoPackageSources;
    }

    private void checkDependencyTableSupport() {
        if (!decisionTableSupported &&
                resources.stream().anyMatch(r -> r.getResourceType() == ResourceType.DTABLE)) {
            throw new MissingDecisionTableDependencyError();
        }
    }

    private List<GeneratedFile> generateInternalResource(KogitoPackageSources pkgSources) {
        List<GeneratedFile> modelFiles = new ArrayList<>();
        org.drools.model.codegen.execmodel.GeneratedFile reflectConfigSource = pkgSources.getReflectConfigSource();
        if (reflectConfigSource != null) {
            modelFiles.add(new GeneratedFile(GeneratedFileType.INTERNAL_RESOURCE,
                    reflectConfigSource.getPath(),
                    reflectConfigSource.getData()));
        }
        return modelFiles;
    }

    private ResourceConfiguration loadDtableResourceConfiguration(Resource resource) {
        Resource resourceProps = findPropertiesResource(resource);
        if (resourceProps == null) return null;
        return loadResourceConfiguration(resource.getSourcePath(), x -> true, x -> {
            try {
                return resourceProps.getInputStream();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        });
    }

    private Resource findPropertiesResource(Resource resource) {
        return resources.stream().filter(r -> r.getSourcePath().equals(resource.getSourcePath() + ".properties")).findFirst().orElse(null);
    }

    private Collection<GeneratedFile> convertGeneratedRuleFile(Collection<org.drools.model.codegen.execmodel.GeneratedFile> legacyModelFiles) {
        return legacyModelFiles.stream().map(f -> new GeneratedFile(RuleCodegen.RULE_TYPE, f.getPath(), f.getData())).collect(toList());
    }
}
