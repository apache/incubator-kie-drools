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
package org.drools.model.project.codegen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.drl.parser.DroolsError;
import org.drools.model.project.codegen.context.DroolsModelBuildContext;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.util.maven.support.ReleaseIdImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.drools.compiler.kie.builder.impl.AbstractKieModule.addDTableToCompiler;
import static org.drools.compiler.kie.builder.impl.AbstractKieModule.loadResourceConfiguration;

/**
 * Utility class to wrap ModelBuilderImpl + KnowledgeBuilder and extract the generated source code or metadata
 */
public class DroolsModelBuilder {
    public static final ReleaseIdImpl DUMMY_RELEASE_ID = new ReleaseIdImpl("dummy:dummy:0.0.0");
    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsModelBuilder.class);
    private final ModelBuilderImpl<KogitoPackageSources> modelBuilder;

    private final DroolsModelBuildContext context;
    private final Collection<Resource> resources;
    private final boolean decisionTableSupported;
    private final boolean hotReloadMode;

    public DroolsModelBuilder(DroolsModelBuildContext context, Collection<Resource> resources, boolean decisionTableSupported, boolean hotReloadMode) {
        this.context = context;
        this.resources = resources;
        this.decisionTableSupported = decisionTableSupported;
        this.hotReloadMode = hotReloadMode;
        this.modelBuilder = makeModelBuilder();
    }

    void build() {
        CompositeKnowledgeBuilder batch = modelBuilder.batch();
        resources.forEach(f -> addResource(batch, f));

        try {
            batch.build();
        } catch (RuntimeException e) {
            for (DroolsError error : modelBuilder.getErrors().getErrors()) {
                LOGGER.error(error.toString());
            }
            LOGGER.error(e.getMessage());
            throw new RuleCodegenError(e, modelBuilder.getErrors().getErrors());
        }

        if (modelBuilder.hasErrors()) {
            for (DroolsError error : modelBuilder.getErrors().getErrors()) {
                LOGGER.error(error.toString());
            }
            throw new RuleCodegenError(modelBuilder.getErrors().getErrors());
        }
    }

    public Collection<GeneratedFile> generateCanonicalModelSources() {
        List<GeneratedFile> modelFiles = new ArrayList<>();
        List<org.drools.modelcompiler.builder.GeneratedFile> legacyModelFiles = new ArrayList<>();

        for (KogitoPackageSources pkgSources : modelBuilder.getPackageSources()) {
            pkgSources.collectGeneratedFiles(legacyModelFiles);
            modelFiles.addAll(generateInternalResource(pkgSources));
        }

        modelFiles.addAll(convertGeneratedRuleFile(legacyModelFiles));
        return modelFiles;
    }

    Collection<KogitoPackageSources> packageSources() {
        return modelBuilder.getPackageSources();
    }

    private ModelBuilderImpl<KogitoPackageSources> makeModelBuilder() {
        if (!decisionTableSupported &&
                resources.stream().anyMatch(r -> r.getResourceType() == ResourceType.DTABLE)) {
            throw new MissingDecisionTableDependencyError();
        }

        return new ModelBuilderImpl<>(
                KogitoPackageSources::dumpSources,
                KogitoKnowledgeBuilderConfigurationImpl.fromContext(context),
                DUMMY_RELEASE_ID,
                hotReloadMode);
    }

    private List<GeneratedFile> generateInternalResource(KogitoPackageSources pkgSources) {
        List<GeneratedFile> modelFiles = new ArrayList<>();
        org.drools.modelcompiler.builder.GeneratedFile reflectConfigSource = pkgSources.getReflectConfigSource();
        if (reflectConfigSource != null) {
            modelFiles.add(new GeneratedFile(GeneratedFileType.INTERNAL_RESOURCE,
                    reflectConfigSource.getPath(),
                    reflectConfigSource.getData()));
        }
        return modelFiles;
    }

    private void addResource(CompositeKnowledgeBuilder batch, Resource resource) {
        if (resource.getResourceType() == ResourceType.PROPERTIES) {
            return;
        }
        if (resource.getResourceType() == ResourceType.DTABLE) {
            Resource resourceProps = findPropertiesResource(resource);
            if (resourceProps != null) {
                ResourceConfiguration conf = loadResourceConfiguration(resource.getSourcePath(), x -> true, x -> {
                    try {
                        return resourceProps.getInputStream();
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                if (conf instanceof DecisionTableConfiguration) {
                    addDTableToCompiler(batch, resource, ((DecisionTableConfiguration) conf));
                    return;
                }
            }
        }
        batch.add(resource, resource.getResourceType());
    }

    private Resource findPropertiesResource(Resource resource) {
        return resources.stream().filter(r -> r.getSourcePath().equals(resource.getSourcePath() + ".properties")).findFirst().orElse(null);
    }

    private Collection<GeneratedFile> convertGeneratedRuleFile(Collection<org.drools.modelcompiler.builder.GeneratedFile> legacyModelFiles) {
        return legacyModelFiles.stream().map(f -> new GeneratedFile(RuleCodegen.RULE_TYPE, f.getPath(), f.getData())).collect(toList());
    }
}
