/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.AbstractGenerator;
import org.kie.kogito.codegen.prediction.config.PredictionConfigGenerator;
import org.kie.kogito.pmml.openapi.api.PMMLOASResult;
import org.kie.kogito.pmml.openapi.factories.PMMLOASResultFactory;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLFactoryModel;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelFactory;
import org.kie.pmml.compiler.PMMLCompilationContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.drools.codegen.common.GeneratedFileType.COMPILED_CLASS;
import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;
import static org.kie.efesto.common.api.utils.CollectionUtils.findExactlyOne;
import static org.kie.efesto.common.api.utils.JSONUtils.getGeneratedResourcesObject;
import static org.kie.efesto.compilationmanager.core.utils.CompilationManagerUtils.getExistingIndexFile;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedExecutableResource;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedRedirectResource;
import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.PMMLLoaderUtils.loadAllKiePMMLModelFactories;

public class PredictionCodegen extends AbstractGenerator {

    public static final String DMN_JPMML_CLASS = "org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator";
    public static final String GENERATOR_NAME = "predictions";
    private static final Logger LOGGER = LoggerFactory.getLogger(PredictionCodegen.class);
    private static final GeneratedFileType INDEX_FILE = GeneratedFileType.of("IndexFile",
            GeneratedFileType.Category.INTERNAL_RESOURCE);
    private static final CompilationManager compilationManager =
            org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(true).get();
    private final Collection<PMMLResource> resources;
    private final Set<IndexFile> indexFiles;

    private static final String DEFAULT_INDEXFILE_DIRECTORY = "./target/classes";

    public PredictionCodegen(KogitoBuildContext context, Collection<PMMLResource> resources,
            Set<IndexFile> indexFiles) {
        super(context, GENERATOR_NAME, new PredictionConfigGenerator(context));
        this.resources = resources;
        this.indexFiles = indexFiles;
    }

    public static PredictionCodegen ofCollectedResources(KogitoBuildContext context,
            Collection<CollectedResource> resources) {
        LOGGER.debug("ofCollectedResources {}", resources);
        if (context.hasClassAvailable(DMN_JPMML_CLASS)) {
            LOGGER.debug("jpmml libraries available on classpath, skipping kogito-pmml parsing and compilation");
            return ofPredictions(context, Collections.emptyList(), Collections.emptySet());
        }
        deleteIndexFiles();
        Set<IndexFile> indexFiles = new HashSet<>();
        Collection<PMMLResource> pmmlResources = resources.stream()
                .filter(r -> r.resource().getResourceType() == ResourceType.PMML)
                .flatMap(r -> parsePredictions(context.getClassLoader(), r.basePath(),
                        Collections.singletonList(r.resource()),
                        indexFiles).stream())
                .collect(toList());
        return ofPredictions(context, pmmlResources, indexFiles);
    }

    private static PredictionCodegen ofPredictions(KogitoBuildContext context, Collection<PMMLResource> resources, Set<IndexFile> indexFiles) {
        LOGGER.debug("ofPredictions {} {}", resources, indexFiles);
        return new PredictionCodegen(context, resources, indexFiles);
    }

    private static Collection<PMMLResource> parsePredictions(ClassLoader classLoader, Path path, List<Resource> resources, Set<IndexFile> indexFiles) {
        LOGGER.debug("parsePredictions {} {} {}", path, resources, indexFiles);
        Collection<PMMLResource> toReturn = new ArrayList<>();
        resources.forEach(resource -> {
            KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader =
                    new KieMemoryCompiler.MemoryCompilerClassLoader(classLoader);
            String fileName = resource.getSourcePath();
            if (fileName.contains(File.separator)) {
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
            EfestoResource<InputStream> efestoResource;
            PMMLCompilationContext compilationContext = getPMMLCompilationContext(fileName, memoryCompilerClassLoader);
            try {
                efestoResource = new EfestoInputStreamResource(resource.getInputStream(), fileName);
            } catch (IOException e) {
                throw new KiePMMLException("Failed to find " + resource.getSourcePath(), e);
            }
            Collection<IndexFile> createdIndexFiles = compileResource(compilationContext, efestoResource);
            IndexFile indexFile = findExactlyOne(createdIndexFiles, file -> file.getModel().equals(PMML_STRING),
                    (s1, s2) -> new KiePMMLException("Found more than one IndexFile" +
                            ".pmml_json: " + s1 + " and" +
                            " " + s2),
                    () -> new KiePMMLException("Failed to create IndexFile for PMML"));
            List<KiePMMLModel> kiePMMLModels = getKiePMMLModels(compilationContext, indexFile);
            String modelPath = resource.getSourcePath();
            PMMLResource toAdd = new PMMLResource(kiePMMLModels, path, modelPath,
                    getExecutableClassesForModel(compilationContext));
            toReturn.add(toAdd);
            indexFiles.addAll(createdIndexFiles);
        });
        return toReturn;
    }

    private static void deleteIndexFiles() {
        LOGGER.debug("deleteIndexFiles");
        List<String> toDelete = Arrays.asList("pmml", "drl");
        toDelete.forEach(model -> getExistingIndexFile(model).ifPresent(indexFile -> {
            if (indexFile.exists()) {
                try {
                    LOGGER.debug("Going to delete {}", indexFile.getAbsolutePath());
                    Files.delete(indexFile.toPath());
                } catch (IOException e) {
                    throw new KiePMMLException("Failed to delete " + indexFile.getAbsolutePath(), e);
                }
            }
        }));
    }

    private static Map<String, byte[]> getExecutableClassesForModel(PMMLCompilationContext compilationContext) {
        Map<String, byte[]> toReturn = new HashMap<>();
        Collection<GeneratedExecutableResource> executableResources =
                compilationContext.getModelLocalUriIdsForFile().stream().map(modelLocalUriId -> getGeneratedExecutableResource(modelLocalUriId, compilationContext.getGeneratedResourcesMap()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(toList());
        executableResources.forEach(executableResource -> {
            toReturn.putAll(compilationContext.getGeneratedClasses(executableResource.getModelLocalUriId()));
        });
        return toReturn;
    }

    private static PMMLCompilationContext getPMMLCompilationContext(String fileName,
            KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return new PMMLCompilationContextImpl(fileName, memoryCompilerClassLoader);
    }

    private static Collection<IndexFile> compileResource(PMMLCompilationContext compilationContext,
            EfestoResource<InputStream> efestoResource) {
        compilationManager.processResource(compilationContext,
                efestoResource);
        Path targetDirectory =
                new File(System.getProperty(INDEXFILE_DIRECTORY_PROPERTY, DEFAULT_INDEXFILE_DIRECTORY)).toPath();
        Collection<IndexFile> toReturn = compilationContext.createIndexFiles(targetDirectory).values();
        if (toReturn.stream().noneMatch(indexFile -> indexFile.getModel().equals(PMML_STRING))) {
            throw new KiePMMLException("Failed to create IndexFile for PMML");
        }
        return toReturn;
    }

    private static List<KiePMMLModel> getKiePMMLModels(PMMLCompilationContext compilationContext, IndexFile indexFile) {
        Set<ModelLocalUriId> modelLocalUriIds = getModelLocalUriIdInIndexFile(compilationContext.localUriIdKeySet(), indexFile);
        Collection<GeneratedExecutableResource> executableResources = modelLocalUriIds.stream()
                .map(modelLocalUriId -> getGeneratedExecutableResource(modelLocalUriId, compilationContext.getGeneratedResourcesMap()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toSet());
        Collection<KiePMMLModelFactory> kiePMMLModelFactories = loadAllKiePMMLModelFactories(executableResources,
                compilationContext);
        return kiePMMLModelFactories.stream()
                .flatMap(factory -> factory.getKiePMMLModels().stream())
                .collect(toList());
    }

    private static Set<ModelLocalUriId> getModelLocalUriIdInIndexFile(Set<ModelLocalUriId> modelLocalUriIds, IndexFile indexFile) {
        try {
            GeneratedResources generatedResources = getGeneratedResourcesObject(indexFile);
            Set<ModelLocalUriId> toReturn = new HashSet<>();
            modelLocalUriIds.forEach(modelLocalUriId -> {
                getGeneratedExecutableResource(modelLocalUriId, generatedResources).ifPresent(opt -> toReturn.add(modelLocalUriId));
                getGeneratedRedirectResource(modelLocalUriId, generatedResources).ifPresent(opt -> toReturn.add(modelLocalUriId));
            });
            return toReturn;
        } catch (Exception e) {
            throw new KiePMMLException("Failed to retrieve GeneratedResources from " + indexFile);
        }
    }

    @Override
    public Optional<ApplicationSection> section() {
        LOGGER.debug("section");
        return Optional.of(new PredictionModelsGenerator(context(), applicationCanonicalName(), resources));
    }

    @Override
    protected Collection<GeneratedFile> internalGenerate() {
        LOGGER.debug("internalGenerate");
        Collection<GeneratedFile> files = new ArrayList<>();
        for (PMMLResource resource : resources) {
            generateModelsFromResource(files, resource);
        }
        for (IndexFile indexFile : indexFiles) {
            generateModelFromIndexFile(files, indexFile);
        }
        return files;
    }

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }

    @Override
    public int priority() {
        return 40;
    }

    private void generateModelsFromResource(Collection<GeneratedFile> files, PMMLResource resource) {
        for (KiePMMLModel model : resource.getKiePmmlModels()) {
            checkModel(model);
            generateModel(files, model, resource);
        }
    }

    private void generateModel(Collection<GeneratedFile> files, KiePMMLModel model, PMMLResource resource) {
        generateModelBaseFiles(files, model, resource);
        generateModelRESTFiles(files, model);
    }

    private void checkModel(KiePMMLModel toCheck) {
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

    private void generateModelFromIndexFile(Collection<GeneratedFile> files, IndexFile indexFile) {
        if (indexFile.getPath().contains("test-classes")) {
            return;
        }
        files.add(new GeneratedFile(INDEX_FILE, indexFile.getName(), getFileContent(indexFile)));
    }

    private String getFileContent(IndexFile indexFile) {
        LOGGER.debug("getFileContent {}", indexFile);
        try {
            return Files.readString(indexFile.toPath());
        } catch (IOException e) {
            throw new KiePMMLException("Failed to read content of " + indexFile.getPath(), e);
        }
    }

    private void generateModelBaseFiles(Collection<GeneratedFile> files, KiePMMLModel model, PMMLResource resource) {
        Map<String, byte[]> byteMap = resource.getCompiledClasses();
        for (Map.Entry<String, byte[]> byteMapEntry : byteMap.entrySet()) {
            files.add(new GeneratedFile(COMPILED_CLASS, byteMapEntry.getKey(), byteMapEntry.getValue()));
        }

        if (model instanceof HasNestedModels) {
            for (KiePMMLModel nestedModel : ((HasNestedModels) model).getNestedModels()) {
                generateModelBaseFiles(files, nestedModel, resource);
            }
        }
    }

    private void generateModelRESTFiles(Collection<GeneratedFile> files, KiePMMLModel model) {
        if (!context().hasRESTForGenerator(this) || (model instanceof KiePMMLFactoryModel)) {
            return;
        }

        PMMLRestResourceGenerator resourceGenerator = new PMMLRestResourceGenerator(context(), model,
                applicationCanonicalName());
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
