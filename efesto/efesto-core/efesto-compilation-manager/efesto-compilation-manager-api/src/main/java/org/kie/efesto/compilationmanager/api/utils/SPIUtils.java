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
package org.kie.efesto.compilationmanager.api.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.utils.CollectionUtils.findAtMostOne;

public class SPIUtils {

    private SPIUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(SPIUtils.class.getName());

    private static final ServiceLoader<CompilationManager> compilationManagerLoader = ServiceLoader.load(CompilationManager.class);
    private static final ServiceLoader<KieCompilerService> kieCompilerServiceLoader = ServiceLoader.load(KieCompilerService.class);

    public static Optional<KieCompilerService> getKieCompilerService(EfestoResource resource, boolean refresh) {
        logger.debug("getKieCompilerService {} {}", resource, refresh);
        return findAtMostOne(getServices(refresh), service -> service.canManageResource(resource),
                (s1, s2) -> new KieCompilerServiceException("Found more than one compiler services: " + s1 + " and " + s2));
    }

    public static Optional<KieCompilerService> getKieCompilerServiceFromEfestoCompilationContext(EfestoResource resource, EfestoCompilationContext context) {
        logger.debug("getKieCompilerServiceFromEfestoCompilationContext {} {}", resource, context);
        ServiceLoader<KieCompilerService> contextServiceLoader = context.getKieCompilerServiceLoader();
        return findAtMostOne(contextServiceLoader, service -> service.canManageResource(resource),
                             (s1, s2) -> new KieCompilerServiceException("Found more than one compiler services: " + s1 + " and " + s2));
    }

    public static List<KieCompilerService> getKieCompilerServices(boolean refresh) {
        logger.debug("getKieCompilerServices {}", refresh);
        List<KieCompilerService> toReturn = new ArrayList<>();
        Iterable<KieCompilerService> services = getServices(refresh);
        services.forEach(toReturn::add);
        logger.debug("toReturn {} {}", toReturn, toReturn.size());
        if (logger.isTraceEnabled()) {
            toReturn.forEach(provider -> logger.trace("{}", provider));
        }
        return toReturn;
    }

    public static Optional<CompilationManager> getCompilationManager(boolean refresh) {
        logger.debug("getCompilationManager {}", refresh);
        Iterable<CompilationManager> managers = getManagers(refresh);
        return managers.iterator().hasNext() ? Optional.of(managers.iterator().next()) : Optional.empty();
    }

    private static Iterable<KieCompilerService> getServices(boolean refresh) {
        if (refresh) {
            kieCompilerServiceLoader.reload();
        }
        return kieCompilerServiceLoader;
    }

    private static Iterable<CompilationManager> getManagers(boolean refresh) {
        if (refresh) {
            compilationManagerLoader.reload();
        }
        return compilationManagerLoader;
    }

    public static Set<String> collectModelTypes(EfestoCompilationContext context) {
        Iterable<KieCompilerService> kieCompilerServices = getServices(false);
        Set<String> modelTypes = new HashSet<>();
        kieCompilerServices.forEach(kieCompilerService -> modelTypes.add(kieCompilerService.getModelType()));
        ServiceLoader<KieCompilerService> serviceLoader = context.getKieCompilerServiceLoader();
        for (KieCompilerService kieCompilerService : serviceLoader) {
            modelTypes.add(kieCompilerService.getModelType());
        }
        return modelTypes;
    }
}
