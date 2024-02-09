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
package org.kie.efesto.runtimemanager.api.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoContext;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedRedirectResource;
import org.kie.efesto.common.api.model.GeneratedResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.utils.CollectionUtils.findAtMostOne;

public class GeneratedResourceUtils {

    private static final Logger logger = LoggerFactory.getLogger(GeneratedResourceUtils.class.getName());

    private GeneratedResourceUtils() {
    }

    public static boolean isPresentExecutableOrRedirect(ModelLocalUriId modelLocalUriId, EfestoContext context) {
        return  getGeneratedExecutableResource(modelLocalUriId, context.getGeneratedResourcesMap()).isPresent() ||
                getGeneratedRedirectResource(modelLocalUriId, context.getGeneratedResourcesMap()).isPresent();
    }

    public static Optional<GeneratedExecutableResource> getGeneratedExecutableResource(ModelLocalUriId modelLocalUriId, Map<String, GeneratedResources> generatedResourcesMap) {
        if (!generatedResourcesMap.containsKey(modelLocalUriId.model())) {
            return Optional.empty();
        } else {
            return getGeneratedExecutableResource(modelLocalUriId, generatedResourcesMap.get(modelLocalUriId.model()));
        }
    }

    /**
     * find GeneratedExecutableResource from GeneratedResources without IndexFile
     */
    public static Optional<GeneratedExecutableResource> getGeneratedExecutableResource(ModelLocalUriId modelLocalUriId, GeneratedResources generatedResources) {
        Collection<GeneratedExecutableResource> allExecutableResources = getAllGeneratedExecutableResources(generatedResources);
        return findAtMostOne(allExecutableResources,
                             generatedResource -> generatedResource.getModelLocalUriId().equals(modelLocalUriId),
                             (s1, s2) -> new KieRuntimeServiceException("Found more than one Executable Resource (" + s1 + " and " + s2 + ") for " + modelLocalUriId));
    }

    public static Optional<GeneratedRedirectResource> getGeneratedRedirectResource(ModelLocalUriId modelLocalUriId, Map<String, GeneratedResources> generatedResourcesMap) {
        if (!generatedResourcesMap.containsKey(modelLocalUriId.model())) {
            return Optional.empty();
        } else {
            return getGeneratedRedirectResource(modelLocalUriId, generatedResourcesMap.get(modelLocalUriId.model()));
        }
    }

    public static Optional<GeneratedRedirectResource> getGeneratedRedirectResource(ModelLocalUriId modelLocalUriId, GeneratedResources generatedResources) {
        Collection<GeneratedRedirectResource> allExecutableResources = new HashSet<>();
        for (GeneratedResource generatedResource : generatedResources) {
            if (generatedResource instanceof GeneratedRedirectResource) {
                allExecutableResources.add((GeneratedRedirectResource) generatedResource);
            }
        }
        return findAtMostOne(allExecutableResources,
                             generatedResource -> generatedResource.getModelLocalUriId().equals(modelLocalUriId),
                             (s1, s2) -> new KieRuntimeServiceException("Found more than one Redirect Resource (" + s1 + " and " + s2 + ") for " + modelLocalUriId));
    }

    public static Collection<GeneratedExecutableResource> getAllGeneratedExecutableResources(GeneratedResources generatedResources) {
        Collection<GeneratedExecutableResource> toReturn = new HashSet<>();
        try {
            logger.debug("getAllGeneratedExecutableResources {}", generatedResources);
            for (GeneratedResource generatedResource : generatedResources) {
                if (generatedResource instanceof GeneratedExecutableResource) {
                    toReturn.add((GeneratedExecutableResource) generatedResource);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to read GeneratedClassResource from context.", e);
        }
        return toReturn;
    }
}
