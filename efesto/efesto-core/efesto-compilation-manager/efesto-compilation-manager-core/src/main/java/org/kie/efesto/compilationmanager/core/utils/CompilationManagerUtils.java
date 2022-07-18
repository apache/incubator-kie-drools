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
package org.kie.efesto.compilationmanager.core.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.GeneratedClassResource;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCallableOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoCallableOutputClassesContainer;
import org.kie.efesto.compilationmanager.api.model.EfestoClassesContainer;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.memorycompiler.KieMemoryCompilerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;
import static org.kie.efesto.common.api.utils.FileUtils.getFileFromFileName;
import static org.kie.efesto.common.api.utils.JSONUtils.getGeneratedResourcesObject;
import static org.kie.efesto.common.api.utils.JSONUtils.writeGeneratedResourcesObject;
import static org.kie.efesto.compilationmanager.api.utils.SPIUtils.getKieCompilerService;

public class CompilationManagerUtils {

    private static final Logger logger = LoggerFactory.getLogger(CompilationManagerUtils.class.getName());
    private static final String DEFAULT_INDEXFILE_DIRECTORY = "./target/classes";

    private CompilationManagerUtils() {
    }

    public static Set<IndexFile> getIndexFilesWithProcessedResource(EfestoResource toProcess, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        Optional<KieCompilerService> retrieved = getKieCompilerService(toProcess, false);
        if (!retrieved.isPresent()) {
            logger.warn("Cannot find KieCompilerService for {}", toProcess.getClass());
            return Collections.emptySet();
        }
        Set<IndexFile> toPopulate = new HashSet<>();
        for (EfestoCompilationOutput compilationOutput : retrieved.get().processResource(toProcess, memoryCompilerClassLoader)) {
            if (compilationOutput instanceof EfestoCallableOutput) {
                IndexFile indexFile = CompilationManagerUtils.getIndexFile((EfestoCallableOutput) compilationOutput);
                toPopulate.add(indexFile);
                populateIndexFile(indexFile, compilationOutput);
                if (compilationOutput instanceof EfestoCallableOutputClassesContainer) {
                    loadClasses(((EfestoCallableOutputClassesContainer) compilationOutput).getCompiledClassesMap(), memoryCompilerClassLoader);
                }
            } else if (compilationOutput instanceof EfestoResource) {
                toPopulate.addAll(getIndexFilesWithProcessedResource((EfestoResource) compilationOutput, memoryCompilerClassLoader));
            }
        }
        return toPopulate;
    }

    static IndexFile getIndexFile(EfestoCallableOutput compilationOutput) {
        String parentPath = System.getProperty(INDEXFILE_DIRECTORY_PROPERTY, DEFAULT_INDEXFILE_DIRECTORY);
        IndexFile toReturn = new IndexFile(parentPath, compilationOutput.getFri().getModel());
        return getFileFromFileName(toReturn.getName()).map(IndexFile::new).orElseGet(() -> createIndexFile(toReturn));
    }

    private static IndexFile createIndexFile(IndexFile toCreate) {
        try {
            logger.debug("Writing file {}", toCreate.getPath());
            if (!toCreate.createNewFile()) {
                throw new KieCompilerServiceException("Failed to create " + toCreate.getName());
            }
        } catch (IOException e) {
            logger.error("Failed to create {} due to {}", toCreate.getName(), e);
            throw new KieCompilerServiceException("Failed to create " + toCreate.getName(), e);
        }
        return toCreate;
    }

    static void populateIndexFile(IndexFile toPopulate, EfestoCompilationOutput compilationOutput) {
        try {
            GeneratedResources generatedResources = getGeneratedResourcesObject(toPopulate);
            populateGeneratedResources(generatedResources, compilationOutput);
            writeGeneratedResourcesObject(generatedResources, toPopulate);
        } catch (IOException e) {
            throw new KieCompilerServiceException(e);
        }
    }

    static void populateGeneratedResources(GeneratedResources toPopulate, EfestoCompilationOutput compilationOutput) {
        toPopulate.add(getGeneratedResource(compilationOutput));
        if (compilationOutput instanceof EfestoClassesContainer) {
            toPopulate.addAll(getGeneratedResources((EfestoClassesContainer) compilationOutput));
        }
    }

    static GeneratedResource getGeneratedResource(EfestoCompilationOutput compilationOutput) {
        if (compilationOutput instanceof EfestoCallableOutput) {
            return new GeneratedExecutableResource(((EfestoCallableOutput) compilationOutput).getFri(), ((EfestoCallableOutput) compilationOutput).getFullClassNames());
        } else {
            throw new KieCompilerServiceException("Unmanaged type " + compilationOutput.getClass().getName());
        }
    }

    static List<GeneratedResource> getGeneratedResources(EfestoClassesContainer finalOutput) {
        return finalOutput.getCompiledClassesMap().keySet().stream()
                .map(CompilationManagerUtils::getGeneratedClassResource)
                .collect(Collectors.toList());
    }

    static GeneratedClassResource getGeneratedClassResource(String fullClassName) {
        return new GeneratedClassResource(fullClassName);
    }

    static void loadClasses(Map<String, byte[]> compiledClassesMap, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        for (Map.Entry<String, byte[]> entry : compiledClassesMap.entrySet()) {
            memoryCompilerClassLoader.addCode(entry.getKey(), entry.getValue());
            try {
                memoryCompilerClassLoader.loadClass(entry.getKey());
            } catch (ClassNotFoundException e) {
                throw new KieMemoryCompilerException(e.getMessage(), e);
            }
        }
    }
}


