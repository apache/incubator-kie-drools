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
package org.kie.kogito.maven.plugin.util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.kie.kogito.Model;
import org.kie.kogito.ProcessInput;
import org.kie.kogito.UserTask;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.json.JsonSchemaGenerator;
import org.kie.kogito.codegen.process.persistence.PersistenceGenerator;
import org.kie.kogito.codegen.process.persistence.marshaller.ReflectionMarshallerGenerator;
import org.kie.kogito.codegen.process.persistence.proto.ReflectionProtoGenerator;
import org.reflections.Reflections;

import static java.util.Arrays.asList;
import static org.kie.kogito.codegen.core.utils.GeneratedFileValidation.validateGeneratedFileTypes;
import static org.kie.kogito.maven.plugin.util.CompilerHelper.RESOURCES;
import static org.kie.kogito.maven.plugin.util.CompilerHelper.SOURCES;

public class PersistenceGenerationHelper {

    private PersistenceGenerationHelper() {
    }

    public static Map<String, Collection<GeneratedFile>> generatePersistenceFiles(KogitoBuildContext context,
            Reflections reflections,
            String schemaVersion) throws MojoExecutionException {
        try {

            @SuppressWarnings({ "rawtype", "unchecked" })
            Set<Class<?>> modelClasses = (Set) reflections.getSubTypesOf(Model.class);

            ReflectionProtoGenerator protoGenerator = ReflectionProtoGenerator.builder()
                    .build(modelClasses);

            // Generate persistence files
            PersistenceGenerator persistenceGenerator = new PersistenceGenerator(context, protoGenerator, new ReflectionMarshallerGenerator(context, protoGenerator.getDataClasses()));
            Map<String, Collection<GeneratedFile>> toReturn = getMappedGeneratedFiles(persistenceGenerator);

            // Json schema generation
            Stream<Class<?>> processClassStream = reflections.getTypesAnnotatedWith(ProcessInput.class).stream();
            toReturn.get(RESOURCES).addAll(generateJsonSchemaFromClasses(processClassStream, schemaVersion));

            Stream<Class<?>> userTaskClassStream = reflections.getTypesAnnotatedWith(UserTask.class).stream();
            toReturn.get(RESOURCES).addAll(generateJsonSchemaFromClasses(userTaskClassStream, schemaVersion));
            return toReturn;
        } catch (Exception e) {
            throw new MojoExecutionException("Error during processing model classes", e);
        }
    }

    static Map<String, Collection<GeneratedFile>> getMappedGeneratedFiles(PersistenceGenerator persistenceGenerator) {
        Collection<GeneratedFile> persistenceFiles = persistenceGenerator.generate();

        validateGeneratedFileTypes(persistenceFiles, asList(GeneratedFileType.Category.SOURCE, GeneratedFileType.Category.INTERNAL_RESOURCE, GeneratedFileType.Category.STATIC_HTTP_RESOURCE));

        Collection<GeneratedFile> generatedClasses = new HashSet<>(); // avoid duplicated
        Collection<GeneratedFile> generatedResources = new HashSet<>(); // avoid duplicated
        persistenceFiles.forEach(generatedFile -> {
            switch (generatedFile.category()) {
                case SOURCE -> generatedClasses.add(generatedFile);
                case INTERNAL_RESOURCE, STATIC_HTTP_RESOURCE -> generatedResources.add(generatedFile);
                default -> throw new IllegalStateException("Unexpected file with category: " + generatedFile.category());
            }
        });
        return Map.of(SOURCES, generatedClasses, RESOURCES, generatedResources);
    }

    private static Collection<GeneratedFile> generateJsonSchemaFromClasses(Stream<Class<?>> classes, String schemaVersion) throws IOException {
        return new JsonSchemaGenerator.ClassBuilder(classes)
                .withSchemaVersion(schemaVersion).build()
                .generate();
    }

}
