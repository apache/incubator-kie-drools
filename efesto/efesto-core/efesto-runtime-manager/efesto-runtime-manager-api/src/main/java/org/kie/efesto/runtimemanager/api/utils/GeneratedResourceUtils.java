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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.efesto.common.api.model.EfestoContext;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedRedirectResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.utils.CollectionUtils.findAtMostOne;

public class GeneratedResourceUtils {

    private static final Logger logger = LoggerFactory.getLogger(GeneratedResourceUtils.class.getName());

    private GeneratedResourceUtils() {
    }

    public static boolean isPresentExecutableOrRedirect(FRI fri, EfestoContext context) {
        return Stream
                .of(getGeneratedExecutableResource(fri, context.getGeneratedResourcesMap()),
                    getGeneratedRedirectResource(fri, context.getGeneratedResourcesMap()))
                .anyMatch(Optional::isPresent);
    }

    public static Optional<GeneratedExecutableResource> getGeneratedExecutableResource(FRI fri, Map<String, GeneratedResources> generatedResourcesMap) {
        if (!generatedResourcesMap.containsKey(fri.getModel())) {
            return Optional.empty();
        } else {
            return getGeneratedExecutableResource(fri, generatedResourcesMap.get(fri.getModel()));
        }
    }

    /**
     * find GeneratedExecutableResource from GeneratedResources without IndexFile
     */
    public static Optional<GeneratedExecutableResource> getGeneratedExecutableResource(FRI fri, GeneratedResources generatedResources) {
        Collection<GeneratedExecutableResource> allExecutableResources = new HashSet<>();
        allExecutableResources.addAll(generatedResources.stream()
                                    .filter(GeneratedExecutableResource.class::isInstance)
                                    .map(GeneratedExecutableResource.class::cast)
                                    .collect(Collectors.toSet()));
        return findAtMostOne(allExecutableResources,
                             generatedResource -> generatedResource.getFri().equals(fri),
                             (s1, s2) -> new KieRuntimeServiceException("Found more than one Executable Resource (" + s1 + " and " + s2 + ") for " + fri));
    }

    public static Optional<GeneratedRedirectResource> getGeneratedRedirectResource(FRI fri, Map<String, GeneratedResources> generatedResourcesMap) {
        if (!generatedResourcesMap.containsKey(fri.getModel())) {
            return Optional.empty();
        } else {
            return getGeneratedRedirectResource(fri, generatedResourcesMap.get(fri.getModel()));
        }
    }

    public static Optional<GeneratedRedirectResource> getGeneratedRedirectResource(FRI fri, GeneratedResources generatedResources) {
        Collection<GeneratedRedirectResource> allExecutableResources = new HashSet<>();
        allExecutableResources.addAll(generatedResources.stream()
                                              .filter(GeneratedRedirectResource.class::isInstance)
                                              .map(GeneratedRedirectResource.class::cast)
                                              .collect(Collectors.toSet()));
        return findAtMostOne(allExecutableResources,
                             generatedResource -> generatedResource.getFri().equals(fri),
                             (s1, s2) -> new KieRuntimeServiceException("Found more than one Redirect Resource (" + s1 + " and " + s2 + ") for " + fri));
    }

    public static Collection<GeneratedExecutableResource> getAllGeneratedExecutableResources(GeneratedResources generatedResources) {
        Collection<GeneratedExecutableResource> toReturn = new HashSet<>();
        try {
            logger.debug("generatedResources {}", generatedResources);
            toReturn.addAll(generatedResources.stream()
                                              .filter(GeneratedExecutableResource.class::isInstance)
                                              .map(GeneratedExecutableResource.class::cast)
                                              .collect(Collectors.toSet()));
        } catch (Exception e) {
            logger.error("Failed to read GeneratedClassResource from context.", e);
        }
        return toReturn;
    }
}
