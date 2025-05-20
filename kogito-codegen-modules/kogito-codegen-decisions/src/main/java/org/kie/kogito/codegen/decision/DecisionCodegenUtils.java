/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.kie.kogito.codegen.decision;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.efesto.compiler.model.DMNResourceSetResource;
import org.kie.dmn.efesto.compiler.model.DmnCompilationContext;
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
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.api.model.GeneratedModelResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.DashboardGeneratedFileUtils;
import org.kie.kogito.codegen.decision.events.DecisionCloudEventMetaFactoryGenerator;
import org.kie.kogito.grafana.GrafanaConfigurationWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.stream.Collectors.toList;
import static org.kie.efesto.common.core.utils.JSONUtils.getGeneratedResourcesObject;
import static org.kie.efesto.common.core.utils.JSONUtils.getGeneratedResourcesString;
import static org.kie.kogito.codegen.api.Generator.REST_TYPE;
import static org.kie.kogito.codegen.decision.CodegenUtils.getDefinitionsFileFromModel;
import static org.kie.kogito.codegen.decision.DecisionCodegen.STRONGLY_TYPED_CONFIGURATION_KEY;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DecisionCodegenUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecisionCodegenUtils.class);
    private static final GeneratedFileType INDEX_FILE = GeneratedFileType.of("IndexFile",
            GeneratedFileType.Category.INTERNAL_RESOURCE);
    private static final CompilationManager compilationManager =
            SPIUtils.getCompilationManager(true).orElseThrow(() -> new RuntimeException("Compilation Manager not " +
                    "available"));

    private static final String operationalDashboardDmnTemplate = "/grafana-dashboard-template/operational-dashboard-template.json";
    private static final String domainDashboardDmnTemplate = "/grafana-dashboard-template/blank-dashboard.json";

    static Map.Entry<String, GeneratedResources> generateModelsFromResources(Collection<GeneratedFile> generatedFiles,
            List<String> classesForManualReflection,
            List<CollectedResource> cResources,
            Set<DMNProfile> customDMNProfiles,
            RuntimeTypeCheckOption runtimeTypeCheckOption,
            DecisionCodegen generator) {
        Map<Resource, CollectedResource> r2cr = cResources.stream().collect(Collectors.toMap(CollectedResource::resource, Function.identity()));
        Map.Entry<String, GeneratedResources> generatedResourcesEntry = loadModelsAndValidate(generator.context(), r2cr, customDMNProfiles, runtimeTypeCheckOption);
        List<DMNResource> resources = getDMNResources(generatedResourcesEntry, r2cr);
        boolean stronglyTypedEnabled = Optional.ofNullable(generator.context())
                .flatMap(c -> c.getApplicationProperty(STRONGLY_TYPED_CONFIGURATION_KEY))
                .map(Boolean::parseBoolean)
                .orElse(false);
        DMNMarshaller marshaller = DMNMarshallerFactory.newDefaultMarshaller();
        List<DMNModel> dmnModels = resources.stream().map(DMNResource::getDmnModel).toList();
        for (DMNModel model : dmnModels) {
            if (model.getName() == null || model.getName().isEmpty()) {
                throw new RuntimeException("Model name should not be empty");
            }
            generateModel(generatedFiles, classesForManualReflection, model, generator, stronglyTypedEnabled, marshaller);
        }
        generateCloudEventsResources(generatedFiles, generator.context(), dmnModels);
        generateAndStoreDecisionModelResourcesProvider(generatedFiles, resources, generator.context(), generator.applicationCanonicalName());
        return generatedResourcesEntry;
    }

    static List<DMNResource> getDMNResources(Map.Entry<String, GeneratedResources> generatedResourcesEntry, Map<Resource, CollectedResource> r2cr) {
        return generatedResourcesEntry.getValue().stream()
                .filter(GeneratedModelResource.class::isInstance)
                .map(GeneratedModelResource.class::cast)
                .map(generatedModelResource -> getDMNResourceFromGeneratedModelResource(generatedModelResource, r2cr))
                .toList();
    }

    static Map.Entry<String, GeneratedResources> loadModelsAndValidate(KogitoBuildContext context, Map<Resource, CollectedResource> r2cr,
            Set<DMNProfile> customDMNProfiles,
            RuntimeTypeCheckOption runtimeTypeCheckOption) {
        DecisionValidation.dmnValidateResources(context, r2cr.keySet());
        Set<Resource> dmnResources = r2cr.keySet();
        ModelLocalUriId dmnModelLocalUriId = new ModelLocalUriId(LocalUri.Root.append("dmn").append("scesim"));
        DMNResourceSetResource toProcessDmn = new DMNResourceSetResource(dmnResources, dmnModelLocalUriId);
        EfestoCompilationContext dmnCompilationContext = DmnCompilationContext.buildWithParentClassLoader(context.getClassLoader(), customDMNProfiles, runtimeTypeCheckOption);
        compilationManager.processResource(dmnCompilationContext, toProcessDmn);
        Map<String, GeneratedResources> generatedResourcesMap = dmnCompilationContext.getGeneratedResourcesMap();
        Map.Entry<String, GeneratedResources> toReturn = generatedResourcesMap.entrySet().stream().filter(entry -> entry.getKey().equals("dmn")).findFirst().orElseThrow(() -> new RuntimeException());
        toReturn.getValue().stream().filter(GeneratedModelResource.class::isInstance)
                .map(GeneratedModelResource.class::cast)
                .forEach(generatedResource -> {
                    GeneratedResources generatedResources = (GeneratedResources) dmnCompilationContext.getGeneratedResourcesMap().get("dmn");
                    if (generatedResources == null) {
                        ContextStorage.putEfestoCompilationContext(generatedResource.getModelLocalUriId(), dmnCompilationContext);
                    } else {
                        Optional<GeneratedModelResource> first = generatedResources.stream()
                                .filter(GeneratedModelResource.class::isInstance)
                                .map(GeneratedModelResource.class::cast)
                                .filter(storedGeneratedResources -> storedGeneratedResources.getModelLocalUriId().equals(generatedResource.getModelLocalUriId()))
                                .findFirst();
                        // let's avoid overwrite an already compiled resources
                        if (first.isEmpty() || first.get().getCompiledModel() == null) {
                            ContextStorage.putEfestoCompilationContext(generatedResource.getModelLocalUriId(), dmnCompilationContext);
                        }
                    }
                });
        return generatedResourcesMap.entrySet().stream().filter(entry -> entry.getKey().equals("dmn")).findFirst().orElseThrow(() -> new RuntimeException());
    }

    static DMNResource getDMNResourceFromGeneratedModelResource(GeneratedModelResource generatedModelResource, Map<Resource, CollectedResource> r2cr) {
        DMNModel dmnModel = getDMNModelFromGeneratedModelResource(generatedModelResource);
        CollectedResource collectedResource = r2cr.get(dmnModel.getResource());
        return new DMNResource(dmnModel, collectedResource);
    }

    static DMNModel getDMNModelFromGeneratedModelResource(GeneratedModelResource generatedModelResource) {
        DMNModel toReturn = (DMNModel) generatedModelResource.getCompiledModel();
        if (toReturn == null) {
            EfestoCompilationContext dmnCompilationContext = ContextStorage.getEfestoCompilationContext(generatedModelResource.getModelLocalUriId());
            GeneratedResources generatedResources = (GeneratedResources) dmnCompilationContext.getGeneratedResourcesMap().get("dmn");
            Optional<GeneratedModelResource> first = generatedResources.stream()
                    .filter(GeneratedModelResource.class::isInstance)
                    .map(GeneratedModelResource.class::cast)
                    .filter(storedGeneratedResources -> storedGeneratedResources.getModelLocalUriId().equals(generatedModelResource.getModelLocalUriId()))
                    .findFirst();
            if (first.isPresent()) {
                toReturn = (DMNModel) first.get().getCompiledModel();
            }
        }
        return toReturn;
    }

    static void generateModel(Collection<GeneratedFile> files,
            List<String> classesForManualReflection,
            DMNModel dmnModel, DecisionCodegen generator,
            boolean stronglyTypedEnabled,
            DMNMarshaller marshaller) {
        if (stronglyTypedEnabled) {
            generateStronglyTypedInput(files, classesForManualReflection, dmnModel, generator.isMPAnnotationsPresent(), generator.isIOSwaggerOASv3AnnotationsPresent());
        }
        generateModelRESTFiles(files, dmnModel, generator, stronglyTypedEnabled);
        generateDecisionsFiles(files, dmnModel, marshaller);
    }

    static void generateModelRESTFiles(Collection<GeneratedFile> generatedFiles, DMNModel dmnModel, DecisionCodegen generator, boolean stronglyTypedEnabled) {
        KogitoBuildContext context = generator.context();
        if (context.hasRESTForGenerator(generator)) {
            generateModelRESTFiles(generatedFiles, dmnModel,
                    context,
                    generator.applicationCanonicalName(),
                    stronglyTypedEnabled,
                    generator.isMPAnnotationsPresent(),
                    generator.isIOSwaggerOASv3AnnotationsPresent());
        }
    }

    static void generateModelRESTFiles(Collection<GeneratedFile> generatedFiles,
            DMNModel dmnModel,
            KogitoBuildContext context,
            String appCanonicalName,
            boolean stronglyTypedEnabled,
            boolean isMPAnnotationsPresent,
            boolean isIOSwaggerOASv3AnnotationsPresent) {
        DMNOASResult oasResult = DMNOASGeneratorFactory.generator(Collections.singleton(dmnModel)).build();
        DecisionRestResourceGenerator resourceGenerator = new DecisionRestResourceGenerator(context, dmnModel, appCanonicalName)
                .withStronglyTyped(stronglyTypedEnabled)
                .withOASResult(oasResult, isMPAnnotationsPresent, isIOSwaggerOASv3AnnotationsPresent);
        storeFile(generatedFiles, REST_TYPE, resourceGenerator.generatedFilePath(), resourceGenerator.generate());
        if (context.getAddonsConfig().usePrometheusMonitoring()) {
            generateAndStoreGrafanaDashboards(context, generatedFiles, resourceGenerator);
        }
        try {
            String jsonContent = new ObjectMapper().writeValueAsString(oasResult.getJsonSchemaNode());
            final String DMN_DEFINITIONS_JSON = getDefinitionsFileFromModel(dmnModel);
            storeFile(generatedFiles, GeneratedFileType.STATIC_HTTP_RESOURCE, DMN_DEFINITIONS_JSON, jsonContent);
        } catch (Exception e) {
            LOGGER.warn("Failed to write OAS schema");
        }
    }

    static void generateDecisionsFiles(Collection<GeneratedFile> generatedFiles, DMNModel model, DMNMarshaller marshaller) {
        Definitions definitions = model.getDefinitions();
        for (DRGElement drg : definitions.getDrgElement()) {
            if (drg instanceof Decision decision) {
                decision.setExpression(null);
            } else if (drg instanceof BusinessKnowledgeModel bkm) {
                bkm.setEncapsulatedLogic(null);
            }
        }
        String relativePath = CodegenStringUtil.escapeIdentifier(model.getNamespace()).replace(".", "/") + "/" + CodegenStringUtil.escapeIdentifier(model.getName()) + ".dmn_nologic";
        storeFile(generatedFiles, GeneratedFileType.INTERNAL_RESOURCE, relativePath, marshaller.marshal(definitions));
    }

    static void generateCloudEventsResources(Collection<GeneratedFile> generatedFiles, KogitoBuildContext context, List<DMNModel> dmnModels) {
        if (context.getAddonsConfig().useCloudEvents()) {
            final DecisionCloudEventMetaFactoryGenerator ceMetaFactoryGenerator = new DecisionCloudEventMetaFactoryGenerator(context, dmnModels);
            storeFile(generatedFiles, REST_TYPE, ceMetaFactoryGenerator.generatedFilePath(), ceMetaFactoryGenerator.generate());
        }
    }

    static void generateStronglyTypedInput(Collection<GeneratedFile> generatedFiles,
            List<String> classesForManualReflection,
            DMNModel model,
            boolean useMPAnnotations,
            boolean useIOSwaggerOASv3Annotations) {
        try {
            DMNTypeSafePackageName.Factory factory = m -> new DMNTypeSafePackageName("", m.getNamespace(), "");
            DMNAllTypesIndex index = new DMNAllTypesIndex(factory, model);

            DMNTypeSafeTypeGenerator generator = new DMNTypeSafeTypeGenerator(model, index, factory).withJacksonAnnotation();
            if (useMPAnnotations) {
                LOGGER.debug("useMPAnnotations");
                generator.withMPAnnotation();
            } else {
                LOGGER.debug("NO useMPAnnotations");
            }
            if (useIOSwaggerOASv3Annotations) {
                LOGGER.debug("useIOSwaggerOASv3Annotations");
                generator.withIOSwaggerOASv3();
            } else {
                LOGGER.debug("NO useIOSwaggerOASv3Annotations");
            }
            Map<String, String> allTypesSourceCode = generator
                    .processTypes()
                    .generateSourceCodeOfAllTypes();

            for (Map.Entry<String, String> kv : allTypesSourceCode.entrySet()) {
                String fqcn = kv.getKey();
                String sourceCode = kv.getValue();
                storeFile(generatedFiles, GeneratedFileType.SOURCE, fqcn.replace(".", "/") + ".java", sourceCode);
                classesForManualReflection.add(fqcn);
            }
            LOGGER.debug("classesForManualReflection: {}", classesForManualReflection);
        } catch (Exception e) {
            LOGGER.error("Unable to generate Strongly Typed Input for: {} {}", model.getNamespace(), model.getName());
            throw e;
        }
    }

    static void generateAndStoreGrafanaDashboards(KogitoBuildContext context,
            Collection<GeneratedFile> generatedFiles,
            DecisionRestResourceGenerator resourceGenerator) {
        Definitions definitions = resourceGenerator.getDmnModel().getDefinitions();
        List<Decision> decisions = definitions.getDrgElement().stream().filter(x -> x.getParentDRDElement() instanceof Decision).map(x -> (Decision) x).collect(toList());
        Optional<String> operationalDashboard = GrafanaConfigurationWriter.generateOperationalDashboard(
                operationalDashboardDmnTemplate,
                resourceGenerator.getNameURL(),
                context.getPropertiesMap(),
                resourceGenerator.getNameURL(),
                context.getGAV().orElse(KogitoGAV.EMPTY_GAV),
                context.getAddonsConfig().useTracing());
        String dashboardName = GrafanaConfigurationWriter.buildDashboardName(context.getGAV(), resourceGenerator.getNameURL());
        operationalDashboard.ifPresent(dashboard -> generatedFiles.addAll(DashboardGeneratedFileUtils.operational(dashboard, dashboardName + ".json")));
        Optional<String> domainDashboard = GrafanaConfigurationWriter.generateDomainSpecificDMNDashboard(
                domainDashboardDmnTemplate,
                resourceGenerator.getNameURL(),
                context.getPropertiesMap(),
                resourceGenerator.getNameURL(),
                context.getGAV().orElse(KogitoGAV.EMPTY_GAV),
                decisions,
                context.getAddonsConfig().useTracing());
        domainDashboard.ifPresent(dashboard -> generatedFiles.addAll(DashboardGeneratedFileUtils.domain(dashboard, dashboardName + ".json")));
    }

    static void generateAndStoreDecisionModelResourcesProvider(Collection<GeneratedFile> generatedFiles, List<DMNResource> resources, KogitoBuildContext context, String applicationCanonicalName) {
        final DecisionModelResourcesProviderGenerator generator = new DecisionModelResourcesProviderGenerator(context,
                applicationCanonicalName,
                resources);
        storeFile(generatedFiles, GeneratedFileType.SOURCE, generator.generatedFilePath(), generator.generate());
    }

    private static void storeFile(Collection<GeneratedFile> generatedFiles, GeneratedFileType type, String path, String source) {
        generatedFiles.add(new GeneratedFile(type, path, source));
    }

    static void generateModelFromGeneratedResources(Collection<GeneratedFile> files, Map.Entry<String, GeneratedResources> generatedResourcesEntry) {
        String indexFileName = String.format("%s.%s%s", "IndexFile", generatedResourcesEntry.getKey(), "_json");
        addUpdateGeneratedFile(files, indexFileName, generatedResourcesEntry.getValue());
    }

    static void addUpdateGeneratedFile(Collection<GeneratedFile> files, String indexFileName,
            GeneratedResources newGeneratedResources) {
        try {
            Optional<GeneratedFile> existing = files.stream().filter(generatedFile -> generatedFile.type().equals(INDEX_FILE) && generatedFile.relativePath().equals(indexFileName))
                    .findFirst();
            String content;
            if (existing.isPresent()) {
                GeneratedFile generatedFile = existing.get();
                content = getUpdatedGeneratedResourcesContent(generatedFile, newGeneratedResources);
                files.remove(generatedFile);
            } else {
                content = getGeneratedResourcesString(newGeneratedResources);
            }
            files.add(new GeneratedFile(INDEX_FILE, indexFileName, content));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize " + newGeneratedResources, e);
        }
    }

    static String getUpdatedGeneratedResourcesContent(GeneratedFile generatedFile,
            GeneratedResources newGeneratedResources) throws JsonProcessingException {
        String oldContent = new String(generatedFile.contents());
        GeneratedResources oldGeneratedResources = getGeneratedResourcesObject(oldContent);
        oldGeneratedResources.addAll(newGeneratedResources);
        return getGeneratedResourcesString(oldGeneratedResources);
    }

}
