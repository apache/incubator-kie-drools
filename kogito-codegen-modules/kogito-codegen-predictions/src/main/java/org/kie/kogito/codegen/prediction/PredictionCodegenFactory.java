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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.GeneratorFactory;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.compilation.PMMLCompilationContext;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelFactory;
import org.kie.pmml.compiler.PMMLCompilationContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedExecutableResource;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedRedirectResource;
import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.kie.pmml.commons.utils.PMMLLoaderUtils.loadAllKiePMMLModelFactories;

public class PredictionCodegenFactory implements GeneratorFactory {

    public static final String DMN_JPMML_CLASS = "org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator";
    private static final Logger LOGGER = LoggerFactory.getLogger(PredictionCodegenFactory.class);

    private static final CompilationManager compilationManager =
            org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(true).get();

    @Override
    public Generator create(KogitoBuildContext context, Collection<CollectedResource> collectedResources) {
        return ofCollectedResources(context, collectedResources);
    }

    public static PredictionCodegen ofCollectedResources(KogitoBuildContext context,
            Collection<CollectedResource> resources) {
        LOGGER.debug("ofCollectedResources {}", resources);
        if (context.hasClassAvailable(DMN_JPMML_CLASS)) {
            LOGGER.debug("jpmml libraries available on classpath, skipping kogito-pmml parsing and compilation");
            return ofPredictions(context, Collections.emptyList());
        }
        Collection<PMMLResource> pmmlResources = resources.stream()
                .filter(r -> r.resource().getResourceType() == ResourceType.PMML)
                .flatMap(r -> parsePredictions(context.getClassLoader(), r.basePath(),
                        Collections.singletonList(r.resource())).stream())
                .collect(toList());
        return ofPredictions(context, pmmlResources);
    }

    private static PredictionCodegen ofPredictions(KogitoBuildContext context, Collection<PMMLResource> resources) {
        LOGGER.debug("ofPredictions {} {}", context, resources);
        return new PredictionCodegen(context, resources);
    }

    static Collection<PMMLResource> parsePredictions(ClassLoader classLoader, Path path,
            List<Resource> resources) {
        LOGGER.debug("parsePredictions {} {}", path, resources);
        Collection<PMMLResource> toReturn = new ArrayList<>();
        resources.forEach(resource -> {
            KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader =
                    new KieMemoryCompiler.MemoryCompilerClassLoader(classLoader);
            String fileName = resource.getSourcePath(); // this is in fact PortablePath instance
            if (fileName.contains("/")) {
                fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
            }
            EfestoResource<InputStream> efestoResource;
            PMMLCompilationContext compilationContext = getPMMLCompilationContext(fileName, memoryCompilerClassLoader);
            try {
                efestoResource = new EfestoInputStreamResource(resource.getInputStream(), fileName);
            } catch (IOException e) {
                throw new KiePMMLException("Failed to find " + resource.getSourcePath(), e);
            }
            compileResource(compilationContext, efestoResource);
            List<KiePMMLModel> kiePMMLModels = getKiePMMLModels(compilationContext, fileName);
            String modelPath = resource.getSourcePath();
            PMMLResource toAdd = new PMMLResource(kiePMMLModels, path, modelPath,
                    getExecutableClassesForModel(compilationContext),
                    compilationContext.getGeneratedResourcesMap());
            toReturn.add(toAdd);
        });
        return toReturn;
    }

    private static Map<String, byte[]> getExecutableClassesForModel(PMMLCompilationContext compilationContext) {
        Map<String, byte[]> toReturn = new HashMap<>();
        Collection<GeneratedExecutableResource> executableResources =
                compilationContext.getModelLocalUriIdsForFile()
                        .stream()
                        .map(modelLocalUriId -> getGeneratedExecutableResource(modelLocalUriId,
                                compilationContext.getGeneratedResourcesMap()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(toList());
        executableResources.forEach(executableResource -> toReturn.putAll(compilationContext.getGeneratedClasses(executableResource.getModelLocalUriId())));
        return toReturn;
    }

    private static PMMLCompilationContext getPMMLCompilationContext(String fileName,
            KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return new PMMLCompilationContextImpl(fileName, memoryCompilerClassLoader);
    }

    private static void compileResource(PMMLCompilationContext compilationContext,
            EfestoResource<InputStream> efestoResource) {
        compilationManager.processResource(compilationContext,
                efestoResource);
    }

    private static List<KiePMMLModel> getKiePMMLModels(PMMLCompilationContext compilationContext, String fileName) {
        Set<ModelLocalUriId> modelLocalUriIds = getPmmlModelLocalUriIdInCompilationContext(compilationContext,
                fileName);
        Collection<GeneratedExecutableResource> executableResources = modelLocalUriIds.stream()
                .map(modelLocalUriId -> getGeneratedExecutableResource(modelLocalUriId,
                        compilationContext.getGeneratedResourcesMap()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toSet());
        Collection<KiePMMLModelFactory> kiePMMLModelFactories = loadAllKiePMMLModelFactories(executableResources,
                compilationContext);
        return kiePMMLModelFactories.stream()
                .flatMap(factory -> factory.getKiePMMLModels().stream())
                .collect(toList());
    }

    private static Set<ModelLocalUriId> getPmmlModelLocalUriIdInCompilationContext(PMMLCompilationContext compilationContext, String fileName) {
        try {
            String fileNameNoSuffix = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
            final LocalUri localUri = LocalUri.Root.append(PMML_STRING).append(fileNameNoSuffix);
            Set<ModelLocalUriId> toReturn = new HashSet<>();
            compilationContext.localUriIdKeySet()
                    .stream()
                    .filter(modelLocalUriId -> modelLocalUriId.model().equals(PMML_STRING) && modelLocalUriId.asLocalUri().parent().equals(localUri))
                    .forEach(modelLocalUriId -> {
                        GeneratedResources generatedResources =
                                compilationContext.getGeneratedResourcesMap().get(modelLocalUriId.model());
                        getGeneratedExecutableResource(modelLocalUriId, generatedResources).ifPresent(opt -> toReturn.add(modelLocalUriId));
                        getGeneratedRedirectResource(modelLocalUriId, generatedResources).ifPresent(opt -> toReturn.add(modelLocalUriId));
                    });
            return toReturn;
        } catch (Exception e) {
            throw new KiePMMLException("Failed to retrieve GeneratedResources from " + compilationContext);
        }
    }
}
