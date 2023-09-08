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
package org.kie.efesto.compilationmanager.core.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.GeneratedClassResource;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedRedirectResource;
import org.kie.efesto.common.api.model.GeneratedResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCallableOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoCallableOutputClassesContainer;
import org.kie.efesto.compilationmanager.api.model.EfestoClassesContainer;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoRedirectOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileNameOrFilePath;
import static org.kie.efesto.common.core.utils.JSONUtils.getGeneratedResourcesObject;
import static org.kie.efesto.common.core.utils.JSONUtils.writeGeneratedResourcesObject;
import static org.kie.efesto.compilationmanager.api.utils.SPIUtils.getKieCompilerService;
import static org.kie.efesto.compilationmanager.api.utils.SPIUtils.getKieCompilerServiceFromEfestoCompilationContext;

public class CompilationManagerUtils {

    private static final Logger logger = LoggerFactory.getLogger(CompilationManagerUtils.class.getName());
    private static final String DEFAULT_INDEXFILE_DIRECTORY = "./target/classes";

    private CompilationManagerUtils() {}

    /**
     * Process resources and populate generatedResources into context without writing to IndexFile
     * @param toProcess the resource to process
     * @param context the compilation context
     */
    public static void processResourceWithContext(EfestoResource toProcess, EfestoCompilationContext context) {
        Optional<KieCompilerService> retrieved = getKieCompilerService(toProcess, false);
        if (retrieved.isEmpty()) {
            logger.warn("Cannot find KieCompilerService for {}, trying in context classloader", toProcess.getClass());
            retrieved = getKieCompilerServiceFromEfestoCompilationContext(toProcess, context);
        }
        if (retrieved.isEmpty()) {
            logger.warn("Cannot find KieCompilerService for {}", toProcess.getClass());
            return;
        }
        processResources(retrieved.get(), toProcess, context);
    }

    public static Optional<IndexFile> getExistingIndexFile(String model) {
        String parentPath = System.getProperty(INDEXFILE_DIRECTORY_PROPERTY, DEFAULT_INDEXFILE_DIRECTORY);
        IndexFile toReturn = new IndexFile(parentPath, model);
        return getFileFromFileNameOrFilePath(toReturn.getName(), toReturn.getAbsolutePath()).map(IndexFile::new);
    }

    static void processResources(KieCompilerService kieCompilerService, EfestoResource toProcess, EfestoCompilationContext context) {
        List<EfestoCompilationOutput> efestoCompilationOutputList = kieCompilerService.processResource(toProcess, context);
        for (EfestoCompilationOutput compilationOutput : efestoCompilationOutputList) {
            if (compilationOutput instanceof EfestoCallableOutput) {
                populateContext(context, (EfestoCallableOutput) compilationOutput);
                if (compilationOutput instanceof EfestoCallableOutputClassesContainer) {
                    EfestoCallableOutputClassesContainer classesContainer =
                            (EfestoCallableOutputClassesContainer) compilationOutput;
                    context.loadClasses(classesContainer.getCompiledClassesMap());
                    context.addGeneratedClasses(classesContainer.getModelLocalUriId().asModelLocalUriId(),
                                                classesContainer.getCompiledClassesMap());
                }
            } else if (compilationOutput instanceof EfestoResource) {
                processResourceWithContext((EfestoResource) compilationOutput, context);
            }
        }
    }

    static IndexFile getIndexFile(EfestoCallableOutput compilationOutput) {
        String parentPath = System.getProperty(INDEXFILE_DIRECTORY_PROPERTY, DEFAULT_INDEXFILE_DIRECTORY);
        IndexFile toReturn = new IndexFile(parentPath, compilationOutput.getModelLocalUriId().model());
        return getExistingIndexFile(compilationOutput.getModelLocalUriId().model()).orElseGet(() -> createIndexFile(toReturn));
    }

    private static IndexFile createIndexFile(IndexFile toCreate) {
        try {
            logger.debug("Writing file {} {}", toCreate.getAbsolutePath() , toCreate.getName());
            if (!toCreate.createNewFile()) {
                throw new KieCompilerServiceException("Failed to create (" + toCreate.getAbsolutePath() + ") " + toCreate.getName());
            }
        } catch (IOException e) {
            String errorMessage = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : e.getClass().getName();
            logger.error("Failed to create {} {} due to {}", toCreate.getAbsolutePath(), toCreate.getName(), errorMessage);
            throw new KieCompilerServiceException("Failed to create (" + toCreate.getAbsolutePath() + ") " + toCreate.getName(), e);
        }
        return toCreate;
    }

    static void populateIndexFile(IndexFile toPopulate, EfestoCompilationOutput compilationOutput) {
        try {
            GeneratedResources generatedResources = getGeneratedResourcesObject(toPopulate);
            populateGeneratedResources(generatedResources, compilationOutput);
            writeGeneratedResourcesObject(generatedResources, toPopulate);
        } catch (Exception e) {
            throw new KieCompilerServiceException(e);
        }
    }

    static void populateContext(EfestoCompilationContext context, EfestoCallableOutput compilationOutput) {
        try {
            String model = compilationOutput.getModelLocalUriId().model();
            GeneratedResources generatedResources = (GeneratedResources) context.getGeneratedResourcesMap().computeIfAbsent(model, key -> new GeneratedResources());
            populateGeneratedResources(generatedResources, compilationOutput);
        } catch (Exception e) {
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
        if (compilationOutput instanceof EfestoRedirectOutput) {
            return new GeneratedRedirectResource(((EfestoRedirectOutput) compilationOutput).getModelLocalUriId(),
                                                 ((EfestoRedirectOutput) compilationOutput).getTargetEngine());
        } else if (compilationOutput instanceof EfestoCallableOutput) {
            return new GeneratedExecutableResource(((EfestoCallableOutput) compilationOutput).getModelLocalUriId(), ((EfestoCallableOutput) compilationOutput).getFullClassNames());
        } else {
            throw new KieCompilerServiceException("Unmanaged type " + compilationOutput.getClass().getName());
        }
    }

    static List<GeneratedResource> getGeneratedResources(EfestoClassesContainer finalOutput) {
        List<GeneratedResource> toReturn = new ArrayList<>();
        for (String key : finalOutput.getCompiledClassesMap().keySet()) {
            toReturn.add(getGeneratedClassResource(key));
        }
        return toReturn;
    }

    static GeneratedClassResource getGeneratedClassResource(String fullClassName) {
        return new GeneratedClassResource(fullClassName);
    }

}
