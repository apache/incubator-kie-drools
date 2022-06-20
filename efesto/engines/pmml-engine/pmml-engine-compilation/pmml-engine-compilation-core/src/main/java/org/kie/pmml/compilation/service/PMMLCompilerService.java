/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.compilation.service;

import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.common.utils.StringUtils;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoSetResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.memorycompiler.JavaConfiguration;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.exceptions.ExternalException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.HasRedirectOutput;
import org.kie.pmml.commons.model.*;
import org.kie.pmml.compilation.executor.PMMLCompiler;
import org.kie.pmml.compilation.executor.PMMLCompilerImpl;
import org.kie.pmml.compilation.impl.HasClassloaderImpl;
import org.kie.pmml.compilation.model.EfestoCallableOutputPMMLClassesContainer;
import org.kie.pmml.compilation.model.EfestoRedirectOutputPMML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.kie.efesto.common.api.model.FRI.SLASH;
import static org.kie.efesto.common.api.utils.FileNameUtils.getFileName;
import static org.kie.efesto.common.api.utils.FileNameUtils.removeSuffix;
import static org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager;


/**
 * Class meant to <b>compile</b> resources
 */
public class PMMLCompilerService {

    private static final Logger logger = LoggerFactory.getLogger(PMMLCompilerService.class.getName());

    private static final PMMLCompiler PMML_COMPILER = new PMMLCompilerImpl();

    private PMMLCompilerService() {
        // Avoid instantiation
    }


    public static List<EfestoCompilationOutput> getEfestoCompilationOutputPMML(EfestoFileResource resource, KieMemoryCompiler.MemoryCompilerClassLoader memoryClassLoader) {
        return getEfestoFinalOutputPMML(resource, memoryClassLoader);
    }

    static List<EfestoCompilationOutput> getEfestoFinalOutputPMML(EfestoFileResource resource, KieMemoryCompiler.MemoryCompilerClassLoader memoryClassLoader) {
        List<EfestoCompilationOutput> toReturn = new ArrayList<>();
        List<KiePMMLModel> kiePmmlModels = getKiePMMLModelsFromResourcesWithConfigurationsWithSources(new HasClassloaderImpl(memoryClassLoader), Collections.singletonList(resource));
        List<KiePMMLModelWithSources> kiePmmlModelsWithSources = kiePmmlModels
                .stream()
                .filter(KiePMMLModelWithSources.class::isInstance)
                .map(KiePMMLModelWithSources.class::cast)
                .collect(Collectors.toList());
        String fileName = removeSuffix((resource.getContent()).getName());
        Map<String, String> allSourcesMap = new HashMap<>();
        iterateOverKiePmmlModelsWithSources(kiePmmlModelsWithSources, toReturn, allSourcesMap, memoryClassLoader);
        List<KiePMMLFactoryModel> kiePMMLFactoryModels = kiePmmlModels
                .stream()
                .filter(KiePMMLFactoryModel.class::isInstance)
                .map(KiePMMLFactoryModel.class::cast)
                .collect(Collectors.toList());
        kiePMMLFactoryModels.forEach(kiePMMLFactoryModel -> allSourcesMap.putAll(kiePMMLFactoryModel.getSourcesMap()));
        Map<String, byte[]> compiledClasses = compileClasses(allSourcesMap, memoryClassLoader);
        kiePMMLFactoryModels.forEach(kiePMMLFactoryModel -> {
            String modelName = kiePMMLFactoryModel.getName().substring(0, kiePMMLFactoryModel.getName().lastIndexOf("Factory"));
            String basePath = fileName + SLASH + modelName;
            FRI fri = new FRI(basePath, "pmml");
            String fullResourceClassName = kiePMMLFactoryModel.getSourcesMap().keySet().iterator().next();
            toReturn.add(new EfestoCallableOutputPMMLClassesContainer(fri, fullResourceClassName, compiledClasses));
        });
        return toReturn;
    }

    static void iterateOverKiePmmlModelsWithSources(
            List<KiePMMLModelWithSources> toIterate,
            List<EfestoCompilationOutput> darCompilationOutputs,
            Map<String, String> allSourcesMap,
            KieMemoryCompiler.MemoryCompilerClassLoader memoryClassLoader) {
        toIterate.forEach(kiePMMLModelWithSources -> {
            Map<String, String> sourcesMap = kiePMMLModelWithSources.getSourcesMap();
            allSourcesMap.putAll(sourcesMap);
            if (kiePMMLModelWithSources instanceof HasRedirectOutput) {
                EfestoSetResource redirectResource = ((HasRedirectOutput) kiePMMLModelWithSources).getRedirectOutput();
                getRedirectCompilation(redirectResource, memoryClassLoader);
                FRI fri = new FRI(redirectResource.getBasePath(), "pmml");
                darCompilationOutputs.add(new EfestoRedirectOutputPMML(fri, kiePMMLModelWithSources.getName()));
            }
            if (kiePMMLModelWithSources instanceof HasNestedModels) {
                List<KiePMMLModelWithSources> nestedKiePmmlModelsWithSources = ((HasNestedModels) kiePMMLModelWithSources)
                        .getNestedModels()
                        .stream()
                        .filter(KiePMMLModelWithSources.class::isInstance)
                        .map(KiePMMLModelWithSources.class::cast)
                        .collect(Collectors.toList());
                iterateOverKiePmmlModelsWithSources(nestedKiePmmlModelsWithSources, darCompilationOutputs, allSourcesMap, memoryClassLoader);
            }
        });

    }

    static List<IndexFile> getRedirectCompilation(EfestoSetResource redirectOutput, KieMemoryCompiler.MemoryCompilerClassLoader memoryClassLoader) {
        Optional<CompilationManager> compilationManager = getCompilationManager(true);
        if (!compilationManager.isPresent()) {
            throw new KieCompilerServiceException("Cannot find CompilationManager");
        }
        return compilationManager.get().processResource(redirectOutput, memoryClassLoader);
    }

    /**
     * @param hasClassLoader
     * @param resources
     * @return
     * @throws KiePMMLException  if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    static List<KiePMMLModel> getKiePMMLModelsFromResourcesWithConfigurationsWithSources(HasClassLoader hasClassLoader, Collection<EfestoFileResource> resources) {
        return resources.stream()
                .flatMap(resource -> getKiePMMLModelsFromResourceWithSources(hasClassLoader, resource).stream())
                .collect(Collectors.toList());
    }

    /**
     * @param hasClassLoader
     * @param resource
     * @return
     */
    static List<KiePMMLModel> getKiePMMLModelsFromResourceWithSources(HasClassLoader hasClassLoader,
                                                                      EfestoFileResource resource) {
        String[] classNamePackageName = getFactoryClassNamePackageName(resource);
        String packageName = classNamePackageName[1];
        try {
            final List<KiePMMLModel> toReturn = PMML_COMPILER.getKiePMMLModelsWithSources(packageName,
                    resource.getInputStream(),
                    getFileName(resource.getSourcePath()),
                    hasClassLoader);
            return toReturn;
        } catch (IOException e) {
            throw new ExternalException("ExternalException", e);
        }
    }

    /**
     * Returns an array where the first item is the <b>factory class</b> name and the second item is the <b>package</b> name,
     * built starting from the given <code>Resource</code>
     *
     * @param resource
     * @return
     */
    static String[] getFactoryClassNamePackageName(EfestoFileResource resource) {
        String sourcePath = resource.getSourcePath();
        if (sourcePath == null || sourcePath.isEmpty()) {
            throw new IllegalArgumentException("Missing required sourcePath in resource " + resource + " -> " + resource.getClass().getName());
        }
        return StringUtils.getFactoryClassNamePackageName(sourcePath);
    }

    /**
     * Compile the given sources and add them to given <code>Classloader</code> of the current instance.
     * Returns the <code>compiled bytecode</code>
     *
     * @param sourcesMap
     * @return
     */
    static Map<String, byte[]> compileClasses(Map<String, String> sourcesMap, KieMemoryCompiler.MemoryCompilerClassLoader memoryClassLoader) {
        return KieMemoryCompiler.compileNoLoad(sourcesMap, memoryClassLoader, JavaConfiguration.CompilerType.NATIVE);
    }


}
