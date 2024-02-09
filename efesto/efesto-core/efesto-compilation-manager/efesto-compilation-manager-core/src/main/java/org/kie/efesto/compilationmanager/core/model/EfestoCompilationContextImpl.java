/**
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
package org.kie.efesto.compilationmanager.core.model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.listener.EfestoListener;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.core.utils.JSONUtils;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.memorycompiler.JavaConfiguration;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.memorycompiler.KieMemoryCompilerException;

import static org.kie.efesto.common.core.utils.JSONUtils.getGeneratedResourcesObject;
import static org.kie.efesto.common.core.utils.JSONUtils.writeGeneratedResourcesObject;

public class EfestoCompilationContextImpl<T extends EfestoListener> implements EfestoCompilationContext<T> {

    protected final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    protected final Map<String, GeneratedResources> generatedResourcesMap = new HashMap<>();

    protected EfestoCompilationContextImpl(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        this.memoryCompilerClassLoader = memoryCompilerClassLoader;
        prepareClassLoader();
        populateGeneratedResourcesMap();
    }

    private void prepareClassLoader() {
        for (ModelLocalUriId modelLocalUriId : localUriIdKeySet()) {
            Map<String, byte[]> generatedClasses = getGeneratedClasses(modelLocalUriId);
            generatedClasses.forEach(memoryCompilerClassLoader::addCodeIfAbsent);
        }
    }

    private void populateGeneratedResourcesMap() {
        Set<String> modelTypes = SPIUtils.collectModelTypes(this);
        Map<String, IndexFile> indexFileMap = IndexFile.findIndexFilesFromClassLoader(memoryCompilerClassLoader, modelTypes);
        indexFileMap.forEach((model, indexFile) -> {
            try {
                GeneratedResources generatedResources = JSONUtils.getGeneratedResourcesObject(indexFile);
                generatedResourcesMap.put(model, generatedResources);
            } catch (Exception e) {
                throw new EfestoCompilationManagerException("Failed to read IndexFile content : " + indexFile.getAbsolutePath(), e);
            }
        });
    }

    @Override
    public Map<String, GeneratedResources> getGeneratedResourcesMap() {
        return generatedResourcesMap;
    }

    @Override
    public void addGeneratedResources(String model, GeneratedResources generatedResources) {
        generatedResourcesMap.put(model, generatedResources);
    }

    @Override
    public Map<String, byte[]> compileClasses(Map<String, String> sourcesMap) {
        return KieMemoryCompiler.compileNoLoad(sourcesMap, memoryCompilerClassLoader, JavaConfiguration.CompilerType.NATIVE);
    }

    @Override
    public void loadClasses(Map<String, byte[]> compiledClassesMap) {
        for (Map.Entry<String, byte[]> entry : compiledClassesMap.entrySet()) {
            memoryCompilerClassLoader.addCode(entry.getKey(), entry.getValue());
            try {
                memoryCompilerClassLoader.loadClass(entry.getKey());
            } catch (ClassNotFoundException e) {
                throw new KieMemoryCompilerException(e.getMessage(), e);
            }
        }
    }

    @Override
    public ServiceLoader<KieCompilerService> getKieCompilerServiceLoader() {
        return ServiceLoader.load(KieCompilerService.class, memoryCompilerClassLoader);
    }

    @Override
    public byte[] getCode(String name) {
        return memoryCompilerClassLoader.getCode(name);
    }

    @Override
    public Map<String, IndexFile> createIndexFiles(Path targetDirectory) {
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
}
