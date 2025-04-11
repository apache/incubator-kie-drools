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
package org.drools.scenariosimulation.backend.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import java.util.Map;
import java.util.Set;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.pmml.EfestoPMMLUtils;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.io.MemoryFile;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedModelResource;
import org.kie.efesto.common.api.model.GeneratedResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoFileSetResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoLocalRuntimeContext;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.kie.memorycompiler.KieMemoryCompiler;

public class DMNSimulationUtils {

    private static String delimiter = "/";
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader =
            new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    private static final CompilationManager compilationManager =
            SPIUtils.getCompilationManager(true).orElseThrow(() -> new RuntimeException("Compilation Manager not " +
                                                                                                "available"));
    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(true).orElseThrow(() -> new RuntimeException("Runtime Manager not available"));

    private DMNSimulationUtils() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map<String, GeneratedResources> compileModels(Collection<File> dmnFiles, Collection<File> pmmlFiles) {
        //pmmlFiles.forEach(pmmlFile -> EfestoPMMLUtils.compilePMML(pmmlFile, "PUPPA", memoryCompilerClassLoader));
        ModelLocalUriId dmnModelLocalUriId = new ModelLocalUriId(LocalUri.Root.append("dmn").append("scesim"));
        EfestoFileSetResource toProcessDmn = new EfestoFileSetResource(new HashSet<>(dmnFiles), dmnModelLocalUriId);
        EfestoCompilationContext dmnCompilationContext =
                EfestoCompilationContextUtils.buildWithParentClassLoader(memoryCompilerClassLoader);
        compilationManager.processResource(dmnCompilationContext, toProcessDmn);
        return dmnCompilationContext.getGeneratedResourcesMap();
    }

//    public static Map<String, GeneratedResources> extractDMNModel(String path) {
//        File dmnFile = getMemoryFile(path);
//        EfestoFileResource toProcessDmn = new EfestoFileResource(dmnFile);
//        EfestoCompilationContext compilationContext = EfestoCompilationContextUtils.buildWithParentClassLoader
//        (memoryCompilerClassLoader);
//        compilationManager.processResource(compilationContext, toProcessDmn);
//        return compilationContext.getGeneratedResourcesMap();
//    }

    @SuppressWarnings("rawtypes")
    public static EfestoOutput getEfestoOutput(Map<String, GeneratedResources> generatedResourcesMap,
                                               ModelLocalUriId modelLocalUriId, Map<String, Object> inputData) {
        EfestoLocalRuntimeContext runtimeContext =
                EfestoRuntimeContextUtils.buildWithParentClassLoader(memoryCompilerClassLoader, generatedResourcesMap);
        EfestoInput<Map<String, Object>> inputDMN = new BaseEfestoInput<>(modelLocalUriId, inputData);
        Collection<EfestoOutput> efestoOutputs = runtimeManager.evaluateInput(runtimeContext, inputDMN);
        return efestoOutputs.iterator().next();
    }

//    public static DMNModel extractDMNModel(DMNRuntime dmnRuntime, String path) {
//        List<String> pathSplit = Arrays.asList(new StringBuilder(path).reverse().toString().split(delimiter));
//        List<DMNModel> dmnModels = dmnRuntime.getModels();
//
//        return findDMNModel(dmnModels, pathSplit, 1);
//    }
//
//    public static DMNRuntime extractDMNRuntime(KieContainer kieContainer) {
//        return KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);
//    }

    public static DMNModel findDMNModel(List<DMNModel> dmnModels, List<String> pathToFind, int step) {
        List<DMNModel> result = new ArrayList<>();
        String pathToCompare = String.join(delimiter, pathToFind.subList(0, step));
        for (DMNModel dmnModel : dmnModels) {
            String modelPath = new StringBuilder(dmnModel.getResource().getSourcePath()).reverse().toString();
            if (modelPath.startsWith(pathToCompare)) {
                result.add(dmnModel);
            }
        }
        if (result.isEmpty()) {
            throw new ImpossibleToFindDMNException("Retrieving the DMNModel has failed. Make sure the used DMN asset " +
                                                           "does not " +
                                                           "produce any compilation errors and that the project does " +
                                                           "not " +
                                                           "contain multiple DMN assets with the same name and " +
                                                           "namespace. " +
                                                           "In addition, check if the reference to the DMN file is " +
                                                           "correct " +
                                                           "in the Settings panel. " +
                                                           "After addressing the issues, build the project again.");
        } else if (result.size() == 1) {
            return result.get(0);
        } else {
            return findDMNModel(dmnModels, pathToFind, step + 1);
        }
    }

    private static MemoryFile getMemoryFile(String fullFilePath) {
        return org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName(fullFilePath)
                .map(file -> {
                    try {
                        return new MemoryFile(file.toPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException(String.format("Failed to get %s file", fullFilePath)));
    }
}
