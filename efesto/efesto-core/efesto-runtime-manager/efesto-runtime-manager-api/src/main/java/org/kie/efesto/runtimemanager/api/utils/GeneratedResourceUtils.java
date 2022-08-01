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
package org.kie.efesto.runtimemanager.api.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.io.MemoryFile;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.common.api.model.GeneratedClassResource;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedRedirectResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.utils.CollectionUtils.findAtMostOne;
import static org.kie.efesto.common.api.utils.FileUtils.getFileFromFileName;
import static org.kie.efesto.common.api.utils.JSONUtils.getGeneratedResourcesObject;

public class GeneratedResourceUtils {

    private static final Logger logger = LoggerFactory.getLogger(GeneratedResourceUtils.class.getName());

    private GeneratedResourceUtils() {
    }

    public static boolean isPresentExecutableOrRedirect(FRI fri, String modelType) {
        return Stream
                .of(getGeneratedExecutableResource(fri, modelType),
                        getGeneratedRedirectResource(fri, modelType))
                .anyMatch(Optional::isPresent);
    }

    public static Optional<GeneratedExecutableResource> getGeneratedExecutableResource(FRI fri, String modelType) {
        return getIndexFile(modelType).flatMap(indexFile -> getGeneratedExecutableResource(fri, indexFile));
    }

    public static Optional<GeneratedExecutableResource> getGeneratedExecutableResource(FRI fri, IndexFile indexFile) {
        Collection<GeneratedExecutableResource> allExecutableResources = getAllGeneratedExecutableResources(indexFile);
        return findAtMostOne(allExecutableResources,
                             generatedResource -> generatedResource.getFri().equals(fri),
                             (s1, s2) -> new KieRuntimeServiceException("Found more than one Executable Resource (" + s1 + " and " + s2 + ") for " + fri));
    }

    public static Optional<GeneratedRedirectResource> getGeneratedRedirectResource(FRI fri, String modelType) {
        return getIndexFile(modelType).flatMap(indexFile -> {
            try {
                GeneratedResources generatedResources = getGeneratedResourcesObject(indexFile);
                return generatedResources.stream()
                        .filter(generatedResource -> generatedResource instanceof GeneratedRedirectResource &&
                                ((GeneratedRedirectResource) generatedResource).getFri().equals(fri))
                        .findFirst()
                        .map(GeneratedRedirectResource.class::cast);
            } catch (Exception e) {
                logger.error("Failed to read GeneratedResources from {}.", indexFile.getName(), e);
                return Optional.empty();
            }
        });
    }

    public static Collection<GeneratedExecutableResource> getAllGeneratedExecutableResources(String modelType) {
        return getIndexFile(modelType).map(GeneratedResourceUtils::getAllGeneratedExecutableResources).orElse(Collections.emptySet());
    }

    public static Collection<GeneratedExecutableResource> getAllGeneratedExecutableResources(IndexFile indexFile) {
        logger.debug("getAllGeneratedExecutableResources {}", indexFile);
        Collection<GeneratedExecutableResource> toReturn = new HashSet<>();
        try {
            GeneratedResources generatedResources = getGeneratedResourcesObject(indexFile);
            logger.debug("generatedResources {}", generatedResources);
            toReturn.addAll(generatedResources.stream()
                                    .filter(GeneratedExecutableResource.class::isInstance)
                                    .map(GeneratedExecutableResource.class::cast)
                                    .collect(Collectors.toSet()));
        } catch (Exception e) {
            logger.error("Failed to read GeneratedClassResource from {}.", indexFile.getName(), e);
        }
        return toReturn;
    }

    public static Collection<GeneratedClassResource> getAllGeneratedClassResources(String modelType) {
        Collection<GeneratedClassResource> toReturn = new HashSet<>();
        getIndexFile(modelType).ifPresent(indexFile -> {
            try {
                GeneratedResources generatedResources = getGeneratedResourcesObject(indexFile);
                toReturn.addAll(generatedResources.stream()
                                        .filter(GeneratedClassResource.class::isInstance)
                                        .map(GeneratedClassResource.class::cast)
                                        .collect(Collectors.toSet()));
            } catch (Exception e) {
                logger.debug("Failed to read GeneratedClassResource from {}.", indexFile.getName(), e);
            }
        });
        return toReturn;
    }

    public static Optional<IndexFile> getIndexFile(String modelType) {
        logger.debug("getIndexFile {}", modelType);
        IndexFile toSearch = new IndexFile(modelType);
        Optional<File> retrieved = getFileFromFileName(toSearch.getName());
        if (retrieved.isPresent()) {
            File actualFile = retrieved.get();
            IndexFile toReturn = actualFile instanceof MemoryFile ?
                    new IndexFile((MemoryFile)actualFile) : new IndexFile(actualFile);
            logger.debug("returning {}", toReturn);
            return Optional.of(toReturn);
        }
        logger.debug("returning empty");
        return Optional.empty();
    }

}
