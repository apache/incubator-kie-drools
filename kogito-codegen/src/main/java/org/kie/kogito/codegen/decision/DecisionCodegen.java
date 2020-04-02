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
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.grafana.GrafanaConfigurationWriter;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;
import static org.kie.api.io.ResourceType.determineResourceType;
import static org.kie.kogito.codegen.ApplicationGenerator.log;

public class DecisionCodegen extends AbstractGenerator {

    public static DecisionCodegen ofJar(Path jarPath) throws IOException {
        List<Resource> resources = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile( jarPath.toFile() )) {
            Enumeration< ? extends ZipEntry> entries = zipFile.entries();
            while ( entries.hasMoreElements() ) {
                ZipEntry entry = entries.nextElement();
                ResourceType resourceType = determineResourceType(entry.getName());
                if (resourceType == ResourceType.DMN) {
                    InternalResource resource = new ByteArrayResource( readBytesFromInputStream( zipFile.getInputStream( entry ) ) );
                    resource.setResourceType( resourceType );
                    resource.setSourcePath( entry.getName() );
                    resources.add(resource);
                }
            }
        }

        return ofDecisions(jarPath, parseDecisions(resources));
    }

    public static DecisionCodegen ofPath(Path path) throws IOException {
        Path srcPath = Paths.get(path.toString());
        try (Stream<Path> filesStream = Files.walk(srcPath)) {
            List<File> files = filesStream.filter(p -> p.toString().endsWith(".dmn"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            return ofFiles(srcPath, files);
        }
    }

    public static DecisionCodegen ofFiles(Path basePath, Collection<File> files) throws IOException {
        List<DMNModel> result = parseDecisions(files.stream().map(FileSystemResource::new).collect(Collectors.toList()));
        return ofDecisions(basePath, result);
    }

    private static DecisionCodegen ofDecisions(Path basePath, List<DMNModel> result) {
        return new DecisionCodegen(basePath, result);
    }

    private static List<DMNModel> parseDecisions(Collection<Resource> resources) throws IOException {
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                                                 .setRootClassLoader(null)
                                                 .buildConfiguration()
                                                 .fromResources(resources)
                                                 .getOrElseThrow(e -> new RuntimeException("Error compiling DMN model(s)", e));
        return dmnRuntime.getModels();
    }

    private static final String grafanaTemplatePath = "/grafana-dashboard-template/dashboard-template.json";
    private String packageName;
    private String applicationCanonicalName;
    private DependencyInjectionAnnotator annotator;

    private DecisionContainerGenerator moduleGenerator;

    private Path basePath;
    private final Map<String, DMNModel> models;
    private final List<GeneratedFile> generatedFiles = new ArrayList<>();
    private boolean useMonitoring = false;

    public DecisionCodegen(Path basePath, Collection<DMNModel> models) {
        this.basePath = basePath;
        this.models = new HashMap<>();
        for (DMNModel model : models) {
            this.models.put(model.getDefinitions().getId(), model);
        }

        // set default package name
        setPackageName(ApplicationGenerator.DEFAULT_PACKAGE_NAME);
        this.moduleGenerator = new DecisionContainerGenerator(applicationCanonicalName, basePath, models);
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
        this.applicationCanonicalName = packageName + ".Application";
    }

    public Path getBasePath() {
        return this.basePath;
    }

    public void setDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
    }

    public DecisionContainerGenerator moduleGenerator() {
        return moduleGenerator;
    }

    public List<GeneratedFile> generate() {
        if (models.isEmpty()) {
            return Collections.emptyList();
        }

        List<DMNRestResourceGenerator> rgs = new ArrayList<>(); // REST resources

        for (DMNModel model : models.values()) {
            DMNRestResourceGenerator resourceGenerator = new DMNRestResourceGenerator(model, applicationCanonicalName).withDependencyInjection(annotator).withMonitoring(useMonitoring);
            rgs.add(resourceGenerator);
        }

        for (DMNRestResourceGenerator resourceGenerator : rgs) {
            if (useMonitoring) {
                generateAndStoreGrafanaDashboard(resourceGenerator);
            }

            storeFile(GeneratedFile.Type.REST, resourceGenerator.generatedFilePath(), resourceGenerator.generate());
        }

        return generatedFiles;
    }

    private void generateAndStoreGrafanaDashboard(DMNRestResourceGenerator resourceGenerator) {
        Definitions definitions = resourceGenerator.getDmnModel().getDefinitions();
        List<Decision> decisions = definitions.getDrgElement().stream().filter(x -> x.getParentDRDElement() instanceof Decision).map(x -> (Decision) x).collect(Collectors.toList());

        String dashboard = GrafanaConfigurationWriter.generateDashboardForDMNEndpoint(grafanaTemplatePath, resourceGenerator.getNameURL(), decisions);
        generatedFiles.add(
                new org.kie.kogito.codegen.GeneratedFile(
                        org.kie.kogito.codegen.GeneratedFile.Type.RESOURCE,
                        "dashboards/dashboard-endpoint-" + resourceGenerator.getNameURL() + ".json",
                        dashboard));
    }

    @Override
    public void updateConfig(ConfigGenerator cfg) {
        // nothing.
    }

    private void storeFile(GeneratedFile.Type type, String path, String source) {
        generatedFiles.add(new GeneratedFile(type, path, log( source ).getBytes( StandardCharsets.UTF_8 )));
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

}