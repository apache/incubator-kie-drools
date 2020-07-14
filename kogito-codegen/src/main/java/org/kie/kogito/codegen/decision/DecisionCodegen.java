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
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.typesafe.DMNAllTypesIndex;
import org.kie.dmn.typesafe.DMNTypeSafePackageName;
import org.kie.dmn.typesafe.DMNTypeSafeTypeGenerator;
import org.kie.kogito.codegen.AbstractGenerator;
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
            try (ZipFile zipFile = new ZipFile( jarPath.toFile() )) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    ResourceType resourceType = determineResourceType( entry.getName() );
                    if ( resourceType == ResourceType.DMN ) {
                        InternalResource resource = new ByteArrayResource( readBytesFromInputStream( zipFile.getInputStream( entry ) ) );
                        resource.setResourceType( resourceType );
                        resource.setSourcePath( entry.getName() );
                        resources.add( resource );
                    }
                }
            }
            dmnResources.addAll( parseDecisions(jarPath, resources) );
        }

        return ofDecisions(dmnResources);
    }

    public static DecisionCodegen ofPath(Path... paths) throws IOException {
        List<DMNResource> resources = new ArrayList<>();
        for (Path path : paths) {
            Path srcPath = Paths.get( path.toString() );
            try (Stream<Path> filesStream = Files.walk( srcPath )) {
                List<File> files = filesStream.filter( p -> p.toString().endsWith( ".dmn" ) )
                        .map( Path::toFile )
                        .collect( Collectors.toList() );
                resources.addAll( parseFiles( srcPath, files ) );
            }
        }

        return ofDecisions(resources);
    }

    public static DecisionCodegen ofFiles(Path basePath, List<File> files) {
        return ofDecisions( parseFiles(basePath, files) );
    }

    private static DecisionCodegen ofDecisions(List<DMNResource> resources) {
        return new DecisionCodegen(resources);
    }

    private static List<DMNResource> parseFiles(Path path, List<File> files) {
        return parseDecisions(path, files.stream().map(FileSystemResource::new).collect( toList() ));
    }

    private static List<DMNResource> parseDecisions(Path path, List<Resource> resources) {
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                                                 .setRootClassLoader(null)
                                                 .buildConfiguration()
                                                 .fromResources(resources)
                                                 .getOrElseThrow(e -> new RuntimeException("Error compiling DMN model(s)", e));
        return dmnRuntime.getModels().stream().map( model -> new DMNResource( model, path )).collect( toList() );
    }

    private static final String operationalDashboardDmnTemplate = "/grafana-dashboard-template/operational-dashboard-template.json";
    private static final String domainDashboardDmnTemplate = "/grafana-dashboard-template/blank-dashboard.json";

    private String packageName;
    private String applicationCanonicalName;
    private DependencyInjectionAnnotator annotator;

    private DecisionContainerGenerator moduleGenerator;

    private final List<DMNResource> resources;
    private final List<GeneratedFile> generatedFiles = new ArrayList<>();
    private boolean useMonitoring = false;

    public DecisionCodegen(List<DMNResource> resources) {
        this.resources = resources;

        // set default package name
        setPackageName(ApplicationGenerator.DEFAULT_PACKAGE_NAME);
        this.moduleGenerator = new DecisionContainerGenerator(applicationCanonicalName, resources);
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
        this.applicationCanonicalName = packageName + ".Application";
    }

    public void setDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
    }

    public DecisionContainerGenerator moduleGenerator() {
        return moduleGenerator;
    }

    public List<GeneratedFile> generate() {
        if (resources.isEmpty()) {
            return Collections.emptyList();
        }

        List<DMNRestResourceGenerator> rgs = new ArrayList<>(); // REST resources

        for (DMNResource resource : resources) {
            DMNModel model = resource.getDmnModel();
            if (model.getName() == null || model.getName().isEmpty()) {
                throw new RuntimeException("Model name should not be empty");
            }

            boolean stronglyTypedEnabled = Optional.ofNullable(context())
                    .flatMap(c -> c.getApplicationProperty(STRONGLY_TYPED_CONFIGURATION_KEY))
                    .map(Boolean::parseBoolean)
                    .orElse(false);

            if(stronglyTypedEnabled) {
                generateStronglyTypedInput(model);
            }
            DMNRestResourceGenerator resourceGenerator = new DMNRestResourceGenerator(model, applicationCanonicalName)
                    .withDependencyInjection(annotator)
                    .withMonitoring(useMonitoring)
                    .withStronglyTyped(stronglyTypedEnabled);
            rgs.add(resourceGenerator);
        }

        for (DMNRestResourceGenerator resourceGenerator : rgs) {
            if (useMonitoring) {
                generateAndStoreGrafanaDashboards(resourceGenerator);
            }

            storeFile(GeneratedFile.Type.REST, resourceGenerator.generatedFilePath(), resourceGenerator.generate());
        }

        return generatedFiles;
    }

    private void generateStronglyTypedInput(DMNModel model) {
        try {
            DMNTypeSafePackageName.Factory factory = m -> new DMNTypeSafePackageName("", m.getNamespace(), "");
            DMNAllTypesIndex index = new DMNAllTypesIndex(factory, model);

            Map<String, String> allTypesSourceCode = new DMNTypeSafeTypeGenerator(
                    model,
                    index, factory )
                    .withJacksonAnnotation()
                    .processTypes()
                    .generateSourceCodeOfAllTypes();

            allTypesSourceCode.forEach((k,v) -> storeFile(GeneratedFile.Type.CLASS, k.replace(".", "/") + ".java", v));
            
        } catch(Exception e) {
            logger.error("Unable to generate Strongly Typed Input for: {} {}", model.getNamespace(), model.getName());
            throw e;
        }
    }

    private void generateAndStoreGrafanaDashboards(DMNRestResourceGenerator resourceGenerator) {
        Definitions definitions = resourceGenerator.getDmnModel().getDefinitions();
        List<Decision> decisions = definitions.getDrgElement().stream().filter(x -> x.getParentDRDElement() instanceof Decision).map(x -> (Decision) x).collect( toList());

        String operationalDashboard = GrafanaConfigurationWriter.generateOperationalDashboard(operationalDashboardDmnTemplate, resourceGenerator.getNameURL());
        String domainDashboard = GrafanaConfigurationWriter.generateDomainSpecificDMNDashboard(domainDashboardDmnTemplate, resourceGenerator.getNameURL(), decisions);

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
            cfg.withDecisionConfig(new DecisionConfigGenerator());
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
        return moduleGenerator;
    }

    public DecisionCodegen withMonitoring(boolean useMonitoring) {
        this.useMonitoring = useMonitoring;
        return this;
    }

    public DecisionCodegen withTracing(boolean useTracing) {
        this.moduleGenerator.withTracing(useTracing);
        return this;
    }
}