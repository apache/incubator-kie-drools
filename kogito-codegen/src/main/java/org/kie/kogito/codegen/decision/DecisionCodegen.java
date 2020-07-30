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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.core.io.internal.InternalResource;
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
import org.kie.dmn.typesafe.DMNAllTypesIndex;
import org.kie.dmn.typesafe.DMNTypeSafePackageName;
import org.kie.dmn.typesafe.DMNTypeSafeTypeGenerator;
import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.decision.config.DecisionConfigGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.grafana.GrafanaConfigurationWriter;

import static java.util.stream.Collectors.toList;
import static org.drools.core.util.IoUtils.readBytesFromInputStream;
import static org.kie.api.io.ResourceType.determineResourceType;
import static org.kie.kogito.codegen.ApplicationGenerator.log;
import static org.kie.kogito.codegen.ApplicationGenerator.logger;

public class DecisionCodegen extends AbstractGenerator {

    public static String STRONGLY_TYPED_CONFIGURATION_KEY = "kogito.decisions.stronglytyped";

    public static DecisionCodegen ofJar(Path... jarPaths) throws IOException {
        List<DMNResource> dmnResources = new ArrayList<>();

        for (Path jarPath : jarPaths) {
            List<Resource> resources = new ArrayList<>();
            try (ZipFile zipFile = new ZipFile(jarPath.toFile())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    ResourceType resourceType = determineResourceType(entry.getName());
                    if (resourceType == ResourceType.DMN) {
                        InternalResource resource = new ByteArrayResource(readBytesFromInputStream(zipFile.getInputStream(entry)));
                        resource.setResourceType(resourceType);
                        resource.setSourcePath(entry.getName());
                        resources.add(resource);
                    }
                }
            }
            dmnResources.addAll(parseDecisions(jarPath, resources));
        }

        return ofDecisions(dmnResources);
    }

    public static DecisionCodegen ofPath(Path... paths) throws IOException {
        List<DMNResource> resources = new ArrayList<>();
        for (Path path : paths) {
            Path srcPath = Paths.get(path.toString());
            try (Stream<Path> filesStream = Files.walk(srcPath)) {
                List<File> files = filesStream.filter(p -> p.toString().endsWith(".dmn"))
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                resources.addAll(parseFiles(srcPath, files));
            }
        }

        return ofDecisions(resources);
    }

    public static DecisionCodegen ofFiles(Path basePath, List<File> files) {
        return ofDecisions(parseFiles(basePath, files));
    }

    private static DecisionCodegen ofDecisions(List<DMNResource> resources) {
        return new DecisionCodegen(resources);
    }

    private static List<DMNResource> parseFiles(Path path, List<File> files) {
        return parseDecisions(path, files.stream().map(FileSystemResource::new).collect(toList()));
    }

    private static List<DMNResource> parseDecisions(Path path, List<Resource> resources) {
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(resources)
                .getOrElseThrow(e -> new RuntimeException("Error compiling DMN model(s)", e));
        return dmnRuntime.getModels().stream().map(model -> new DMNResource(model, path)).collect(toList());
    }

    private static final String operationalDashboardDmnTemplate = "/grafana-dashboard-template/operational-dashboard-template.json";
    private static final String domainDashboardDmnTemplate = "/grafana-dashboard-template/blank-dashboard.json";

    private String packageName;
    private String applicationCanonicalName;
    private DependencyInjectionAnnotator annotator;

    private DecisionContainerGenerator decisionContainerGenerator;

    private final List<DMNResource> resources;
    private final List<GeneratedFile> generatedFiles = new ArrayList<>();
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;

    public DecisionCodegen(List<DMNResource> resources) {
        this.resources = resources;

        // set default package name
        setPackageName(ApplicationGenerator.DEFAULT_PACKAGE_NAME);
        this.decisionContainerGenerator = new DecisionContainerGenerator(applicationCanonicalName, resources);
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
        if (resources.isEmpty()) {
            return Collections.emptyList();
        }
        generateAndStoreRestResources();
        generateAndStoreDecisionModelResourcesProvider();

        return generatedFiles;
    }

    private void generateAndStoreRestResources() {
        List<DecisionRestResourceGenerator> rgs = new ArrayList<>(); // REST resources

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
            DecisionRestResourceGenerator resourceGenerator = new DecisionRestResourceGenerator(model, applicationCanonicalName)
                    .withDependencyInjection(annotator)
                    .withAddons(addonsConfig)
                    .withStronglyTyped(stronglyTypedEnabled);
            rgs.add(resourceGenerator);
        }

        for (DecisionRestResourceGenerator resourceGenerator : rgs) {
            if (addonsConfig.useMonitoring()) {
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
        final DecisionModelResourcesProviderGenerator generator = new DecisionModelResourcesProviderGenerator(packageName, applicationCanonicalName, resources)
                                                                                                                                                               .withDependencyInjection(annotator)
                                                                                                                                                               .withAddons(addonsConfig);
        storeFile(GeneratedFile.Type.CLASS, generator.generatedFilePath(), generator.generate());
    }

    private void generateStronglyTypedInput(DMNModel model) {
        try {
            DMNTypeSafePackageName.Factory factory = m -> new DMNTypeSafePackageName("", m.getNamespace(), "");
            DMNAllTypesIndex index = new DMNAllTypesIndex(factory, model);

            Map<String, String> allTypesSourceCode = new DMNTypeSafeTypeGenerator(
                    model,
                    index, factory)
                    .withJacksonAnnotation()
                    .processTypes()
                    .generateSourceCodeOfAllTypes();

            allTypesSourceCode.forEach((k, v) -> storeFile(GeneratedFile.Type.CLASS, k.replace(".", "/") + ".java", v));
        } catch (Exception e) {
            logger.error("Unable to generate Strongly Typed Input for: {} {}", model.getNamespace(), model.getName());
            throw e;
        }
    }

    private void generateAndStoreGrafanaDashboards(DecisionRestResourceGenerator resourceGenerator) {
        Definitions definitions = resourceGenerator.getDmnModel().getDefinitions();
        List<Decision> decisions = definitions.getDrgElement().stream().filter(x -> x.getParentDRDElement() instanceof Decision).map(x -> (Decision) x).collect(toList());

        String operationalDashboard = GrafanaConfigurationWriter.generateOperationalDashboard(operationalDashboardDmnTemplate, resourceGenerator.getNameURL(), addonsConfig.useTracing());
        String domainDashboard = GrafanaConfigurationWriter.generateDomainSpecificDMNDashboard(domainDashboardDmnTemplate, resourceGenerator.getNameURL(), decisions, addonsConfig.useTracing());

        generatedFiles.add(new org.kie.kogito.codegen.GeneratedFile(org.kie.kogito.codegen.GeneratedFile.Type.RESOURCE,
                                                                    "dashboards/operational-dashboard-" + resourceGenerator.getNameURL() + ".json",
                                                                    operationalDashboard));
        generatedFiles.add(new org.kie.kogito.codegen.GeneratedFile(org.kie.kogito.codegen.GeneratedFile.Type.RESOURCE,
                                                                    "dashboards/domain-dashboard-" + resourceGenerator.getNameURL() + ".json",
                                                                    domainDashboard));
    }

    @Override
    public void updateConfig(ConfigGenerator cfg) {
        if (!resources.isEmpty()) {
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
}