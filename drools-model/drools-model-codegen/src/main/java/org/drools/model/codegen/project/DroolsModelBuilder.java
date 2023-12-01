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
package org.drools.model.codegen.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.compiler.builder.conf.DecisionTableConfigurationImpl;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.resources.DecisionTableResourceHandler;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.builder.impl.resources.ResourceHandler;
import org.drools.compiler.builder.impl.resources.YamlResourceHandler;
import org.drools.compiler.kie.builder.impl.DecisionTableConfigurationDelegate;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.PackageModelWriter;
import org.drools.model.codegen.tool.ExplicitCanonicalModelCompiler;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.util.maven.support.ReleaseIdImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.builder.impl.AbstractKieModule.loadResourceConfiguration;
import static org.kie.internal.builder.KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration;

/**
 * Utility class to wrap ModelBuilderImpl + KnowledgeBuilder and extract the generated source code or metadata
 */
public class DroolsModelBuilder {
    public static final ReleaseIdImpl DUMMY_RELEASE_ID = new ReleaseIdImpl("dummy:dummy:0.0.0");
    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsModelBuilder.class);
    private Collection<CodegenPackageSources> codegenPackageSources;
    private Collection<PackageModel> packageModels;

    private final Collection<Resource> resources;
    private final boolean decisionTableSupported;
    private final KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration;
    private final DroolsModelBuildContext context;
    private final Function<PackageModel, PackageModelWriter> packageModelWriterProvider;

    private final Map<ResourceType, ResourceHandler> handlersMap = new HashMap<>();

    public DroolsModelBuilder(
            DroolsModelBuildContext context,
            Collection<Resource> resources,
            boolean decisionTableSupported,
            Function<PackageModel, PackageModelWriter> packageModelWriterProvider) {
        this.context = context;
        this.resources = resources;
        this.decisionTableSupported = decisionTableSupported;
        this.knowledgeBuilderConfiguration = configFromContext(context);
        this.packageModelWriterProvider = packageModelWriterProvider;

        checkDependencyTableSupport();
    }

    private static KnowledgeBuilderConfigurationImpl configFromContext(DroolsModelBuildContext buildContext) {
        KnowledgeBuilderConfigurationImpl conf = newKnowledgeBuilderConfiguration(buildContext.getClassLoader()).as(KnowledgeBuilderConfigurationImpl.KEY);
        for (String prop : buildContext.getApplicationProperties()) {
            if (prop.startsWith("drools")) {
                conf.setProperty(prop, buildContext.getApplicationProperty(prop).orElseThrow());
            }
        }
        return conf;
    }

    public void build() {
        Map<String, CompositePackageDescr> packages = new HashMap<>();
        for (Resource resource : resources) {
            try {
                ResourceType resourceType = resource.getResourceType();
                if (resourceType == ResourceType.DRL || resourceType == ResourceType.YAML) {
                    handleResource(getHandlerForType(resourceType), packages, resource, null);
                } else if (resource.getResourceType() == ResourceType.DTABLE) {
                    handleDtable(getHandlerForType(resourceType), packages, resource);
                }
            } catch (DroolsParserException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        ExplicitCanonicalModelCompiler<CodegenPackageSources> compiler =
                ExplicitCanonicalModelCompiler.of(
                                packages.values(),
                                knowledgeBuilderConfiguration,
                                packageModelWriterProvider.andThen(CodegenPackageSources::dumpSources))
                        .setContext(context);

        compiler.process();
        BuildResultCollector buildResults = compiler.getBuildResults();

        if (buildResults.hasErrors()) {
            for (KnowledgeBuilderResult error : buildResults.getResults()) {
                LOGGER.error(error.toString());
            }
            throw new RuleCodegenError(buildResults.getAllResults());
        }

        this.codegenPackageSources = compiler.getPackageSources();
        this.packageModels = compiler.getPackageModels();
    }

    private ResourceHandler getHandlerForType(ResourceType type) {
        return handlersMap.computeIfAbsent(type, this::createHandlerForType);
    }

    private ResourceHandler createHandlerForType(ResourceType type) {
        if (type == ResourceType.DRL) {
            return new DrlResourceHandler(knowledgeBuilderConfiguration);
        } else if (type == ResourceType.YAML) {
            return new YamlResourceHandler(knowledgeBuilderConfiguration);
        } else if (type == ResourceType.DTABLE) {
            return new DecisionTableResourceHandler(knowledgeBuilderConfiguration, DUMMY_RELEASE_ID);
        }
        throw new IllegalArgumentException("No registered ResourceHandler for type " + type);
    }

    private void handleDtable(ResourceHandler decisionTableHandler, Map<String, CompositePackageDescr> packages, Resource resource) throws DroolsParserException, IOException {
        Collection<ResourceConfiguration> resourceConfigurations = loadDtableResourceConfiguration(resource);
        for (ResourceConfiguration cfg : resourceConfigurations) {
            handleResource(decisionTableHandler, packages, resource, cfg);
        }
    }

    private void handleResource(ResourceHandler resourceHandler, Map<String, CompositePackageDescr> packages, Resource resource, ResourceConfiguration cfg) throws DroolsParserException, IOException {
        PackageDescr packageDescr = resourceHandler.process(resource, cfg);
        Collection<KnowledgeBuilderResult> results = resourceHandler.getResults();
        if (!results.isEmpty()) {
            throw new DroolsParserException(
                    results.stream()
                            .map(KnowledgeBuilderResult::toString)
                            .collect(Collectors.joining("\n")));
        }
        CompositePackageDescr compositePackageDescr =
                packages.computeIfAbsent(packageDescr.getNamespace(), CompositePackageDescr::new);
        compositePackageDescr.addPackageDescr(resource, packageDescr);
    }

    public boolean hasRuleUnits() {
        return codegenPackageSources.stream().anyMatch(pkg -> !pkg.getRuleUnits().isEmpty());
    }

    public Collection<GeneratedFile> generateCanonicalModelSources() {
        List<GeneratedFile> modelFiles = new ArrayList<>();
        List<GeneratedFile> legacyModelFiles = new ArrayList<>();

        for (CodegenPackageSources pkgSources : this.codegenPackageSources) {
            pkgSources.collectGeneratedFiles(legacyModelFiles);
            modelFiles.addAll(generateInternalResource(pkgSources));
        }

        modelFiles.addAll(legacyModelFiles);
        return modelFiles;
    }

    public Collection<CodegenPackageSources> packageSources() {
        return this.codegenPackageSources;
    }
    
    public Collection<PackageModel> getPackageModels() {
        return this.packageModels;
    }

    private void checkDependencyTableSupport() {
        if (!decisionTableSupported &&
                resources.stream().anyMatch(r -> r.getResourceType() == ResourceType.DTABLE)) {
            throw new MissingDecisionTableDependencyError();
        }
    }

    private List<GeneratedFile> generateInternalResource(CodegenPackageSources pkgSources) {
        List<GeneratedFile> modelFiles = new ArrayList<>();
        GeneratedFile reflectConfigSource = pkgSources.getReflectConfigSource();
        if (reflectConfigSource != null) {
            modelFiles.add(new GeneratedFile(GeneratedFileType.INTERNAL_RESOURCE,
                    reflectConfigSource.relativePath(),
                    reflectConfigSource.contents()));
        }
        return modelFiles;
    }

    private Collection<ResourceConfiguration> loadDtableResourceConfiguration(Resource resource) {
        Resource resourceProps = findPropertiesResource(resource);
        if (resourceProps == null) return Collections.singletonList(new DecisionTableConfigurationImpl());
        DecisionTableConfiguration cfg =
                (DecisionTableConfiguration) loadResourceConfiguration(resource.getSourcePath(), x -> true, x -> {
                    try {
                        return resourceProps.getInputStream();
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
        return parseWorksheetConfig(cfg);
    }

    // originally org.drools.compiler.kie.builder.impl.AbstractKieModule.addDTableToCompiler(org.kie.internal.builder.CompositeKnowledgeBuilder, org.kie.api.io.Resource, org.kie.internal.builder.DecisionTableConfiguration, org.kie.internal.builder.ResourceChangeSet)
    private static Collection<ResourceConfiguration> parseWorksheetConfig(DecisionTableConfiguration dtableConf) {
        String sheetNames = dtableConf.getWorksheetName();
        if (sheetNames == null || sheetNames.indexOf(',') < 0) {
            return Collections.singletonList(dtableConf);
        } else {
            ArrayList<ResourceConfiguration> dtConfigs = new ArrayList<>();
            for (String sheetName : sheetNames.split("\\,")) {
                dtConfigs.add(new DecisionTableConfigurationDelegate(dtableConf, sheetName));
            }
            return dtConfigs;
        }
    }


    private Resource findPropertiesResource(Resource resource) {
        return resources.stream().filter(r -> r.getSourcePath().equals(resource.getSourcePath() + ".properties")).findFirst().orElse(null);
    }
}
