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
package org.kie.kogito.codegen.decision;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.eclipse.microprofile.openapi.spi.OASFactoryResolver;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.model.api.BusinessKnowledgeModel;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.openapi.DMNOASGeneratorFactory;
import org.kie.dmn.openapi.model.DMNOASResult;
import org.kie.dmn.typesafe.DMNAllTypesIndex;
import org.kie.dmn.typesafe.DMNTypeSafePackageName;
import org.kie.dmn.typesafe.DMNTypeSafeTypeGenerator;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.AbstractGenerator;
import org.kie.kogito.codegen.core.DashboardGeneratedFileUtils;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;
import org.kie.kogito.codegen.decision.config.DecisionConfigGenerator;
import org.kie.kogito.codegen.decision.events.DecisionCloudEventMetaFactoryGenerator;
import org.kie.kogito.grafana.GrafanaConfigurationWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.codegen.decision.CodegenUtils.getDefinitionsFileFromModel;

public class DecisionCodegen extends AbstractGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(DecisionCodegen.class);
    public static final String GENERATOR_NAME = "decisions";

    public static String STRONGLY_TYPED_CONFIGURATION_KEY = "kogito.decisions.stronglytyped";
    public static String VALIDATION_CONFIGURATION_KEY = "kogito.decisions.validation";

    public static DecisionCodegen ofCollectedResources(KogitoBuildContext context, Collection<CollectedResource> resources) {
        OASFactoryResolver.instance(); // manually invoke SPI, o/w Kogito CodeGen Kogito Quarkus extension failure at NewFileHotReloadTest due to java.util.ServiceConfigurationError: org.eclipse.microprofile.openapi.spi.OASFactoryResolver: io.smallrye.openapi.spi.OASFactoryResolverImpl not a subtype
        List<CollectedResource> dmnResources = resources.stream()
                .filter(r -> r.resource().getResourceType() == ResourceType.DMN)
                .collect(toList());
        return new DecisionCodegen(context, dmnResources);
    }

    public static DecisionCodegen ofPath(KogitoBuildContext context, Path... paths) {
        return ofCollectedResources(context, CollectedResourceProducer.fromPaths(context.ignoreHiddenFiles(), paths));
    }

    private static final String operationalDashboardDmnTemplate = "/grafana-dashboard-template/operational-dashboard-template.json";
    private static final String domainDashboardDmnTemplate = "/grafana-dashboard-template/blank-dashboard.json";

    private final List<CollectedResource> cResources;
    private final List<DMNResource> resources = new ArrayList<>();
    private final List<GeneratedFile> generatedFiles = new ArrayList<>();
    private final List<String> classesForManualReflection = new ArrayList<>();

    public DecisionCodegen(KogitoBuildContext context, List<CollectedResource> cResources) {
        super(context, GENERATOR_NAME, new DecisionConfigGenerator(context));
        this.cResources = cResources;
    }

    private void loadModelsAndValidate() {
        Map<Resource, CollectedResource> r2cr = cResources.stream().collect(Collectors.toMap(CollectedResource::resource, Function.identity()));
        // First, we perform static validation on directly the XML
        DecisionValidation.dmnValidateResources(context(), r2cr.keySet());
        // DMN model processing; any semantic error during compilation will also be thrown accordingly
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .setRootClassLoader(context().getClassLoader()) // KOGITO-4788
                .buildConfiguration()
                .fromResources(r2cr.keySet())
                .getOrElseThrow(e -> new RuntimeException("Error compiling DMN model(s)", e));
        // Any post-compilation of the DMN model validations: DT (static) analysis
        DecisionValidation.dmnValidateDecisionTablesInModels(context(), dmnRuntime.getModels());
        List<DMNResource> dmnResources = dmnRuntime.getModels().stream().map(model -> new DMNResource(model, r2cr.get(model.getResource()))).collect(toList());
        resources.addAll(dmnResources);
    }

    @Override
    protected Collection<GeneratedFile> internalGenerate() {
        loadModelsAndValidate();
        generateAndStoreRestResources();
        generateAndStoreDecisionModelResourcesProvider();

        return generatedFiles;
    }

    @Override
    public boolean isEmpty() {
        return cResources.isEmpty();
    }

    private void generateAndStoreRestResources() {
        List<DecisionRestResourceGenerator> rgs = new ArrayList<>(); // REST resources
        List<DMNModel> models = resources.stream().map(DMNResource::getDmnModel).collect(Collectors.toList());

        for (DMNModel model : models) {
            if (model.getName() == null || model.getName().isEmpty()) {
                throw new RuntimeException("Model name should not be empty");
            }
            DMNOASResult oasResult = generateAndStoreDefinitionsJson(model);

            boolean stronglyTypedEnabled = Optional.ofNullable(context())
                    .flatMap(c -> c.getApplicationProperty(STRONGLY_TYPED_CONFIGURATION_KEY))
                    .map(Boolean::parseBoolean)
                    .orElse(false);

            if (stronglyTypedEnabled) {
                generateStronglyTypedInput(model);
            }
            DecisionRestResourceGenerator resourceGenerator = new DecisionRestResourceGenerator(context(), model, applicationCanonicalName())
                    .withStronglyTyped(stronglyTypedEnabled)
                    .withOASResult(oasResult, isMPAnnotationsPresent(), isIOSwaggerOASv3AnnotationsPresent());
            rgs.add(resourceGenerator);
        }

        if (context().hasRESTForGenerator(this)) {
            for (DecisionRestResourceGenerator resourceGenerator : rgs) {
                if (context().getAddonsConfig().usePrometheusMonitoring()) {
                    generateAndStoreGrafanaDashboards(resourceGenerator);
                }

                storeFile(REST_TYPE, resourceGenerator.generatedFilePath(), resourceGenerator.generate());
            }
        }

        DMNMarshaller marshaller = DMNMarshallerFactory.newDefaultMarshaller();
        for (DMNResource resource : resources) {
            DMNModel model = resource.getDmnModel();
            Definitions definitions = model.getDefinitions();
            for (DRGElement drg : definitions.getDrgElement()) {
                if (drg instanceof Decision) {
                    Decision decision = (Decision) drg;
                    decision.setExpression(null);
                } else if (drg instanceof BusinessKnowledgeModel) {
                    BusinessKnowledgeModel bkm = (BusinessKnowledgeModel) drg;
                    bkm.setEncapsulatedLogic(null);
                }
            }
            String relativePath = CodegenStringUtil.escapeIdentifier(model.getNamespace()).replace(".", "/") + "/" + CodegenStringUtil.escapeIdentifier(model.getName()) + ".dmn_nologic";
            storeFile(GeneratedFileType.INTERNAL_RESOURCE, relativePath, marshaller.marshal(definitions));
        }

        if (context().getAddonsConfig().useCloudEvents()) {
            final DecisionCloudEventMetaFactoryGenerator ceMetaFactoryGenerator = new DecisionCloudEventMetaFactoryGenerator(context(), models);
            storeFile(REST_TYPE, ceMetaFactoryGenerator.generatedFilePath(), ceMetaFactoryGenerator.generate());
        }
    }

    private DMNOASResult generateAndStoreDefinitionsJson(DMNModel dmnModel) {
        DMNOASResult toReturn = null;
        try {
            toReturn = DMNOASGeneratorFactory.generator(Collections.singleton(dmnModel)).build();
            String jsonContent = new ObjectMapper().writeValueAsString(toReturn.getJsonSchemaNode());
            final String DMN_DEFINITIONS_JSON = getDefinitionsFileFromModel(dmnModel);
            storeFile(GeneratedFileType.STATIC_HTTP_RESOURCE, DMN_DEFINITIONS_JSON, jsonContent);
        } catch (Exception e) {
            LOGGER.error("Error while trying to generate OpenAPI specification for the DMN models", e);
        }
        return toReturn;
    }

    private void generateAndStoreDecisionModelResourcesProvider() {
        final DecisionModelResourcesProviderGenerator generator = new DecisionModelResourcesProviderGenerator(context(),
                applicationCanonicalName(),
                resources);
        storeFile(GeneratedFileType.SOURCE, generator.generatedFilePath(), generator.generate());
    }

    private void generateStronglyTypedInput(DMNModel model) {
        try {
            DMNTypeSafePackageName.Factory factory = m -> new DMNTypeSafePackageName("", m.getNamespace(), "");
            DMNAllTypesIndex index = new DMNAllTypesIndex(factory, model);

            DMNTypeSafeTypeGenerator generator = new DMNTypeSafeTypeGenerator(model, index, factory).withJacksonAnnotation();
            boolean useMPAnnotations = isMPAnnotationsPresent();
            if (useMPAnnotations) {
                LOGGER.debug("useMPAnnotations");
                generator.withMPAnnotation();
            } else {
                LOGGER.debug("NO useMPAnnotations");
            }
            boolean useIOSwaggerOASv3Annotations = isIOSwaggerOASv3AnnotationsPresent();
            if (useIOSwaggerOASv3Annotations) {
                LOGGER.debug("useIOSwaggerOASv3Annotations");
                generator.withIOSwaggerOASv3();
            } else {
                LOGGER.debug("NO useIOSwaggerOASv3Annotations");
            }
            Map<String, String> allTypesSourceCode = generator
                    .processTypes()
                    .generateSourceCodeOfAllTypes();

            for (Entry<String, String> kv : allTypesSourceCode.entrySet()) {
                String fqcn = kv.getKey();
                String sourceCode = kv.getValue();

                storeFile(GeneratedFileType.SOURCE, fqcn.replace(".", "/") + ".java", sourceCode);
                classesForManualReflection.add(fqcn);
            }
            LOGGER.debug("classesForManualReflection: {}", classesForManualReflection);
        } catch (Exception e) {
            LOGGER.error("Unable to generate Strongly Typed Input for: {} {}", model.getNamespace(), model.getName());
            throw e;
        }
    }

    private boolean isMPAnnotationsPresent() {
        return context().hasClassAvailable("org.eclipse.microprofile.openapi.models.OpenAPI");
    }

    private boolean isIOSwaggerOASv3AnnotationsPresent() {
        return context().hasClassAvailable("io.swagger.v3.oas.annotations.media.Schema");
    }

    private void generateAndStoreGrafanaDashboards(DecisionRestResourceGenerator resourceGenerator) {
        Definitions definitions = resourceGenerator.getDmnModel().getDefinitions();
        List<Decision> decisions = definitions.getDrgElement().stream().filter(x -> x.getParentDRDElement() instanceof Decision).map(x -> (Decision) x).collect(toList());
        Optional<String> operationalDashboard = GrafanaConfigurationWriter.generateOperationalDashboard(
                operationalDashboardDmnTemplate,
                resourceGenerator.getNameURL(),
                context().getPropertiesMap(),
                resourceGenerator.getNameURL(),
                context().getGAV().orElse(KogitoGAV.EMPTY_GAV),
                context().getAddonsConfig().useTracing());
        String dashboardName = GrafanaConfigurationWriter.buildDashboardName(context().getGAV(), resourceGenerator.getNameURL());
        operationalDashboard.ifPresent(dashboard -> generatedFiles.addAll(DashboardGeneratedFileUtils.operational(dashboard, dashboardName + ".json")));
        Optional<String> domainDashboard = GrafanaConfigurationWriter.generateDomainSpecificDMNDashboard(
                domainDashboardDmnTemplate,
                resourceGenerator.getNameURL(),
                context().getPropertiesMap(),
                resourceGenerator.getNameURL(),
                context().getGAV().orElse(KogitoGAV.EMPTY_GAV),
                decisions,
                context().getAddonsConfig().useTracing());
        domainDashboard.ifPresent(dashboard -> generatedFiles.addAll(DashboardGeneratedFileUtils.domain(dashboard, dashboardName + ".json")));
    }

    private void storeFile(GeneratedFileType type, String path, String source) {
        generatedFiles.add(new GeneratedFile(type, path, source));
    }

    @Override
    public Optional<ApplicationSection> section() {
        return Optional.of(new DecisionContainerGenerator(
                context(),
                applicationCanonicalName(),
                this.cResources,
                this.classesForManualReflection));
    }

    @Override
    public int priority() {
        return 30;
    }
}
