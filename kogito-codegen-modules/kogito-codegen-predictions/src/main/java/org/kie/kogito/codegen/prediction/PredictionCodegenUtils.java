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
package org.kie.kogito.codegen.prediction;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.pmml.openapi.api.PMMLOASResult;
import org.kie.kogito.pmml.openapi.factories.PMMLOASResultFactory;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLFactoryModel;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.drools.codegen.common.GeneratedFileType.COMPILED_CLASS;
import static org.drools.codegen.common.GeneratedFileType.REST;
import static org.kie.efesto.common.core.utils.JSONUtils.getGeneratedResourcesObject;
import static org.kie.efesto.common.core.utils.JSONUtils.getGeneratedResourcesString;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

public class PredictionCodegenUtils {

    private PredictionCodegenUtils() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PredictionCodegenUtils.class);
    private static final GeneratedFileType INDEX_FILE = GeneratedFileType.of("IndexFile",
            GeneratedFileType.Category.INTERNAL_RESOURCE);

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
        files.add(new GeneratedFile(REST, resourceGenerator.generatedFilePath(), resourceGenerator.generate()));

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
