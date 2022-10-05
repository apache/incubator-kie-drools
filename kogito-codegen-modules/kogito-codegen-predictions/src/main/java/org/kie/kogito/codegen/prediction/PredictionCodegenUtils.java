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
package org.kie.kogito.codegen.prediction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.pmml.openapi.api.PMMLOASResult;
import org.kie.kogito.pmml.openapi.factories.PMMLOASResultFactory;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLFactoryModel;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.drools.codegen.common.GeneratedFileType.COMPILED_CLASS;
import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;
import static org.kie.efesto.common.core.utils.JSONUtils.getGeneratedResourcesObject;
import static org.kie.efesto.common.core.utils.JSONUtils.writeGeneratedResourcesObject;
import static org.kie.kogito.codegen.api.Generator.REST_TYPE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

public class PredictionCodegenUtils {

    private PredictionCodegenUtils() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PredictionCodegenUtils.class);
    private static final GeneratedFileType INDEX_FILE = GeneratedFileType.of("IndexFile",
            GeneratedFileType.Category.INTERNAL_RESOURCE);

    private static final String DEFAULT_INDEXFILE_DIRECTORY = "./target/classes";

    static void generateModelsFromResource(Collection<GeneratedFile> files, PMMLResource resource, PredictionCodegen generator) {
        for (KiePMMLModel model : resource.getKiePmmlModels()) {
            checkModel(model);
            generateModel(files, model, resource, generator);
        }
    }

    static void generateModel(Collection<GeneratedFile> files, KiePMMLModel model, PMMLResource resource, PredictionCodegen generator) {
        generateModelBaseFiles(files, resource);
        generateModelRESTFiles(files, model, generator);
    }

    static void checkModel(KiePMMLModel toCheck) {
        if ((toCheck instanceof HasSourcesMap)) {
            String errorMessage = String.format("Unexpected HasSourcesMap instance, retrieved %s inside %s",
                    toCheck.getClass().getName(), toCheck);
            throw new IllegalStateException(errorMessage);
        }
        if (toCheck.getName() == null || toCheck.getName().isEmpty()) {
            String errorMessage = String.format("Model name should not be empty inside %s", toCheck);
            throw new IllegalStateException(errorMessage);
        }
        if (toCheck.getFileName() == null || toCheck.getFileName().isEmpty()) {
            String errorMessage = String.format("Model fileName should not be empty inside %s", toCheck);
            throw new IllegalStateException(errorMessage);
        }
    }

    static void generateModelFromIndexFile(Collection<GeneratedFile> files, IndexFile indexFile) {
        if (indexFile.getPath().contains("test-classes")) {
            return;
        }
        files.add(new GeneratedFile(INDEX_FILE, indexFile.getName(), getFileContent(indexFile)));
    }

    static Map<String, IndexFile> createIndexFiles(Map<String, GeneratedResources> generatedResourcesMap) {
        Path targetDirectory =
                new File(System.getProperty(INDEXFILE_DIRECTORY_PROPERTY, DEFAULT_INDEXFILE_DIRECTORY)).toPath();
        Map<String, IndexFile> indexFiles = new HashMap<>();
        for (Map.Entry<String, GeneratedResources> entry : generatedResourcesMap.entrySet()) {
            String model = entry.getKey();
            GeneratedResources generatedResources = entry.getValue();
            IndexFile indexFile = new IndexFile(targetDirectory.toString(), model);
            try {
                if (indexFile.exists()) {
                    GeneratedResources existingGeneratedResources = getGeneratedResourcesObject(indexFile);
                    generatedResources.addAll(existingGeneratedResources);
                }
                writeGeneratedResourcesObject(generatedResources, indexFile);
            } catch (Exception e) {
                throw new EfestoCompilationManagerException("Failed to write to IndexFile : " + indexFile.getAbsolutePath(), e);
            }
            indexFiles.put(model, indexFile);
        }
        return indexFiles;
    }

    static String getFileContent(IndexFile indexFile) {
        LOGGER.debug("getFileContent {}", indexFile);
        try {
            return Files.readString(indexFile.toPath());
        } catch (IOException e) {
            throw new KiePMMLException("Failed to read content of " + indexFile.getPath(), e);
        }
    }

    static void generateModelBaseFiles(Collection<GeneratedFile> files, PMMLResource resource) {
        Map<String, byte[]> byteMap = resource.getCompiledClasses();
        for (Map.Entry<String, byte[]> byteMapEntry : byteMap.entrySet()) {
            files.add(new GeneratedFile(COMPILED_CLASS, byteMapEntry.getKey(), byteMapEntry.getValue()));
        }
    }

    static void generateModelRESTFiles(Collection<GeneratedFile> files, KiePMMLModel model,
            PredictionCodegen generator) {
        KogitoBuildContext context = generator.context();
        if (context.hasRESTForGenerator(generator) && !(model instanceof KiePMMLFactoryModel)) {
            generateModelRESTFiles(files, model, context, generator.applicationCanonicalName());
        }
    }

    static void generateModelRESTFiles(Collection<GeneratedFile> files, KiePMMLModel model,
            KogitoBuildContext context, String applicationCanonicalName) {
        PMMLRestResourceGenerator resourceGenerator = new PMMLRestResourceGenerator(context, model,
                applicationCanonicalName);
        files.add(new GeneratedFile(REST_TYPE, resourceGenerator.generatedFilePath(), resourceGenerator.generate()));

        PMMLOASResult oasResult = PMMLOASResultFactory.getPMMLOASResult(model);
        try {
            String jsonContent = new ObjectMapper().writeValueAsString(oasResult.jsonSchemaNode());
            String jsonFile = String.format("%s.json", getSanitizedClassName(model.getName()));
            files.add(new GeneratedFile(GeneratedFileType.STATIC_HTTP_RESOURCE, jsonFile, jsonContent));
        } catch (Exception e) {
            LOGGER.warn("Failed to write OAS schema");
        }
    }
}
