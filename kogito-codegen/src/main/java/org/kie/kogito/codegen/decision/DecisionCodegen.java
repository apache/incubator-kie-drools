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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.core.io.impl.FileSystemResource;
import org.kie.api.io.Resource;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.model.api.Definitions;
import org.kie.kogito.codegen.AbstractGenerator;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratedFile.Type;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import static org.kie.kogito.codegen.ApplicationGenerator.log;

public class DecisionCodegen extends AbstractGenerator {

    public static DecisionCodegen ofPath(Path path) throws IOException {
        Path srcPath = Paths.get(path.toString());
        try (Stream<Path> filesStream = Files.walk(srcPath)) {
            List<File> files = filesStream.filter(p -> p.toString().endsWith(".dmn"))
                                          .map(Path::toFile)
                                          .collect(Collectors.toList());
            return ofFiles(files);
        }
    }

    public static DecisionCodegen ofFiles(Collection<File> files) throws IOException {
        List<Definitions> result = parseDecisions(files);
        return ofDecisions(result);
    }

    private static DecisionCodegen ofDecisions(List<Definitions> ds) {
        return new DecisionCodegen(ds);
    }

    private static List<Definitions> parseDecisions(Collection<File> files) throws IOException {
        List<Definitions> result = new ArrayList<>();
        for (File bpmnFile : files) {
            FileSystemResource r = new FileSystemResource(bpmnFile);
            Definitions ps = parseDecisionFile(r);
            result.add(ps);
        }
        return result;
    }

    private static Definitions parseDecisionFile(Resource r) throws IOException {
        return DMNMarshallerFactory.newDefaultMarshaller().unmarshal(r.getReader());
    }

    private String packageName;
    private String applicationCanonicalName; 
    private DependencyInjectionAnnotator annotator;
    
    private DecisionContainerGenerator moduleGenerator;

    private final Map<String, Definitions> models;
    private final List<GeneratedFile> generatedFiles = new ArrayList<>();

    public DecisionCodegen(Collection<? extends Definitions> models) {
        this.models = new HashMap<>();
        for (Definitions model : models) {
            this.models.put(model.getId(), model);
        }

        // set default package name
        setPackageName(ApplicationGenerator.DEFAULT_PACKAGE_NAME);
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
        this.moduleGenerator = new DecisionContainerGenerator();
        this.applicationCanonicalName = packageName + ".Application";
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
        
        for (Definitions model : models.values()) {
            DMNRestResourceGenerator resourceGenerator = new DMNRestResourceGenerator(model, applicationCanonicalName).withDependencyInjection(annotator);
            rgs.add(resourceGenerator);
        }
        
        for (DMNRestResourceGenerator resourceGenerator : rgs) {
            storeFile(Type.REST, resourceGenerator.generatedFilePath(), resourceGenerator.generate());
        }

        return generatedFiles;
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

}