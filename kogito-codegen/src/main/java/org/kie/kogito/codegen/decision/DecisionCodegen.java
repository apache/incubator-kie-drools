/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.decision;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.DashboardGeneratedFileUtils;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.decision.config.DecisionConfigGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.io.CollectedResource;
import org.kie.kogito.grafana.GrafanaConfigurationWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.codegen.ApplicationGenerator.log;
import static org.kie.kogito.codegen.ApplicationGenerator.logger;

public class DecisionCodegen extends AbstractGenerator {

    public static final Logger LOG = LoggerFactory.getLogger(DecisionCodegen.class);

    public static String STRONGLY_TYPED_CONFIGURATION_KEY = "kogito.decisions.stronglytyped";
    public static String VALIDATION_CONFIGURATION_KEY = "kogito.decisions.validation";

    public static DecisionCodegen ofCollectedResources(Collection<CollectedResource> resources) {
        OASFactoryResolver.instance(); // manually invoke SPI, o/w Kogito CodeGen Kogito Quarkus extension failure at NewFileHotReloadTest due to java.util.ServiceConfigurationError: org.eclipse.microprofile.openapi.spi.OASFactoryResolver: io.smallrye.openapi.spi.OASFactoryResolverImpl not a subtype
        List<CollectedResource> dmnResources = resources.stream()
                .filter(r -> r.resource().getResourceType() == ResourceType.DMN)
                .collect(toList());
        return new DecisionCodegen(dmnResources);
    }

    public static DecisionCodegen ofPath(Path... paths) throws IOException {
        return ofCollectedResources(CollectedResource.fromPaths(paths));
    }

    private static final String operationalDashboardDmnTemplate = "/grafana-dashboard-template/operational-dashboard-template.json";
    private static final String domainDashboardDmnTemplate = "/grafana-dashboard-template/blank-dashboard.json";

    private String packageName;
    private String applicationCanonicalName;
    private DependencyInjectionAnnotator annotator;

    private DecisionContainerGenerator decisionContainerGenerator;

    private final List<CollectedResource> cResources;
    private final List<DMNResource> resources = new ArrayList<>();
    private final List<GeneratedFile> generatedFiles = new ArrayList<>();
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;
    private ClassLoader notPCLClassloader; // Kogito CodeGen design as of 2020-10-09
    private PCLResolverFn pclResolverFn = this::trueIFFClassIsPresent;

    public DecisionCodegen(List<CollectedResource> cResources) {
        this.cResources = cResources;

        // set default package name
        setPackageName(ApplicationGenerator.DEFAULT_PACKAGE_NAME);
        this.decisionContainerGenerator = new DecisionContainerGenerator(applicationCanonicalName, this.cResources);
    }

    private void loadModelsAndValidate() {
        Map<Resource, CollectedResource> r2cr = cResources.stream().collect(Collectors.toMap(CollectedResource::resource, Function.identity()));
        // First, we perform static validation on directly the XML
        DecisionValidation.dmnValidateResources(context(), r2cr.keySet());
        // DMN model processing; any semantic error during compilation will also be thrown accordingly
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                                                 .buildConfiguration()
                                                 .fromResources(r2cr.keySet())
                                                 .getOrElseThrow(e -> new RuntimeException("Error compiling DMN model(s)", e));
        // Any post-compilation of the DMN model validations: DT (static) analysis
        DecisionValidation.dmnValidateDecisionTablesInModels(context(), dmnRuntime.getModels());
        List<DMNResource> dmnResources = dmnRuntime.getModels().stream().map(model -> new DMNResource(model, r2cr.get(model.getResource()))).collect(toList());
        resources.addAll(dmnResources);
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
        this.applicationCanonicalName = packageName + ".Application";
    }

    @Override
    public void setDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
    }

    @Override
    public List<GeneratedFile> generate() {
        if (cResources.isEmpty()) {
            return Collections.emptyList();
        }
        loadModelsAndValidate();
        generateAndStoreRestResources();
        generateAndStoreDecisionModelResourcesProvider();

        return generatedFiles;
    }

    private void generateAndStoreRestResources() {
        List<DecisionRestResourceGenerator> rgs = new ArrayList<>(); // REST resources
        
        DMNOASResult oasResult = null;
        try {
            List<DMNModel> models = resources.stream().map(DMNResource::getDmnModel).collect(Collectors.toList());
            oasResult = DMNOASGeneratorFactory.generator(models).build();
            String jsonContent = new ObjectMapper().writeValueAsString(oasResult.getJsonSchemaNode());
            storeFile(GeneratedFile.Type.GENERATED_CP_RESOURCE, "META-INF/resources/dmnDefinitions.json", jsonContent);
        } catch (Exception e) {
            LOG.error("Error while trying to generate OpenAPI specification for the DMN models", e);
        }

        for (DMNResource resource : resources) {
            DMNModel model = resource.getDmnModel();
            if (model.getName() == null || model.getName().isEmpty()) {
                throw new RuntimeException("Model name should not be empty");
            }

            boolean stronglyTypedEnabled = Optional.ofNullable(context())
                    .flatMap(c -> c.getApplicationProperty(STRONGLY_TYPED_CONFIGURATION_KEY))
                    .map(Boolean::parseBoolean)
                    .orElse(false);

            if (stronglyTypedEnabled) {
                generateStronglyTypedInput(model);
            }
            DecisionRestResourceGenerator resourceGenerator = new DecisionRestResourceGenerator(model, applicationCanonicalName).withDependencyInjection(annotator)
                                                                                                                                .withAddons(addonsConfig)
                                                                                                                                .withStronglyTyped(stronglyTypedEnabled)
                                                                                                                                .withOASResult(oasResult, isMPAnnotationsPresent(), isIOSwaggerOASv3AnnotationsPresent());
            rgs.add(resourceGenerator);
        }

        for (DecisionRestResourceGenerator resourceGenerator : rgs) {
            if (addonsConfig.usePrometheusMonitoring()) {
                generateAndStoreGrafanaDashboards(resourceGenerator);
            }

            storeFile(GeneratedFile.Type.REST, resourceGenerator.generatedFilePath(), resourceGenerator.generate());
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
            storeFile(GeneratedFile.Type.GENERATED_CP_RESOURCE, relativePath, marshaller.marshal(definitions));
        }
    }

    private void generateAndStoreDecisionModelResourcesProvider() {
        final DecisionModelResourcesProviderGenerator generator = new DecisionModelResourcesProviderGenerator(packageName,
                                                                                                              applicationCanonicalName,
                                                                                                              resources)
                .withDependencyInjection(annotator)
                .withAddons(addonsConfig);
        storeFile(GeneratedFile.Type.CLASS, generator.generatedFilePath(), generator.generate());
    }

    private void generateStronglyTypedInput(DMNModel model) {
        try {
            DMNTypeSafePackageName.Factory factory = m -> new DMNTypeSafePackageName("", m.getNamespace(), "");
            DMNAllTypesIndex index = new DMNAllTypesIndex(factory, model);

            DMNTypeSafeTypeGenerator generator = new DMNTypeSafeTypeGenerator(model, index, factory).withJacksonAnnotation();
            boolean useMPAnnotations = isMPAnnotationsPresent();
            if (useMPAnnotations) {
                logger.debug("useMPAnnotations");
                generator.withMPAnnotation();
            } else {
                logger.debug("NO useMPAnnotations");
            }
            boolean useIOSwaggerOASv3Annotations = isIOSwaggerOASv3AnnotationsPresent();
            if (useIOSwaggerOASv3Annotations) {
                logger.debug("useIOSwaggerOASv3Annotations");
                generator.withIOSwaggerOASv3();
            } else {
                logger.debug("NO useIOSwaggerOASv3Annotations");
            }
            Map<String, String> allTypesSourceCode = generator
                    .processTypes()
                    .generateSourceCodeOfAllTypes();

            allTypesSourceCode.forEach((k, v) -> storeFile(GeneratedFile.Type.CLASS, k.replace(".", "/") + ".java", v));
        } catch (Exception e) {
            logger.error("Unable to generate Strongly Typed Input for: {} {}", model.getNamespace(), model.getName());
            throw e;
        }
    }

    private boolean isMPAnnotationsPresent() {
        return this.pclResolverFn.apply("org.eclipse.microprofile.openapi.models.OpenAPI");
    }

    private boolean isIOSwaggerOASv3AnnotationsPresent() {
        return this.pclResolverFn.apply("io.swagger.v3.oas.annotations.media.Schema");
    }

    private boolean trueIFFClassIsPresent(String fqn) {
        if (notPCLClassloader != null) {
            try {
                Class<?> c = notPCLClassloader.loadClass(fqn);
                if (c != null) {
                    return true;
                }
            } catch (Exception e) {
                // do nothing.
            }
        }
        return false;
    }

    private void generateAndStoreGrafanaDashboards(DecisionRestResourceGenerator resourceGenerator) {
        Definitions definitions = resourceGenerator.getDmnModel().getDefinitions();
        List<Decision> decisions = definitions.getDrgElement().stream().filter(x -> x.getParentDRDElement() instanceof Decision).map(x -> (Decision) x).collect(toList());

        String operationalDashboard = GrafanaConfigurationWriter.generateOperationalDashboard(operationalDashboardDmnTemplate, resourceGenerator.getNameURL(), addonsConfig.useTracing());
        String domainDashboard = GrafanaConfigurationWriter.generateDomainSpecificDMNDashboard(domainDashboardDmnTemplate, resourceGenerator.getNameURL(), decisions, addonsConfig.useTracing());
        generatedFiles.addAll(DashboardGeneratedFileUtils.operational(operationalDashboard, resourceGenerator.getNameURL() + ".json"));
        generatedFiles.addAll(DashboardGeneratedFileUtils.domain(domainDashboard, resourceGenerator.getNameURL() + ".json"));
    }

    @Override
    public void updateConfig(ConfigGenerator cfg) {
        if (!cResources.isEmpty()) {
            cfg.withDecisionConfig(new DecisionConfigGenerator(packageName));
        }
    }

    private void storeFile(GeneratedFile.Type type, String path, String source) {
        generatedFiles.add(new GeneratedFile(type, path, log(source).getBytes(StandardCharsets.UTF_8)));
    }

    public List<GeneratedFile> getGeneratedFiles() {
        return generatedFiles;
    }

    @Override
    public ApplicationSection section() {
        return decisionContainerGenerator;
    }

    public DecisionCodegen withAddons(AddonsConfig addonsConfig) {
        this.decisionContainerGenerator.withAddons(addonsConfig);
        this.addonsConfig = addonsConfig;
        return this;
    }

    public DecisionCodegen withClassLoader(ClassLoader classLoader) {
        this.notPCLClassloader = classLoader;
        return this;
    }

    public DecisionCodegen withPCLResolverFn(PCLResolverFn fn) {
        this.pclResolverFn = fn;
        return this;
    }
}