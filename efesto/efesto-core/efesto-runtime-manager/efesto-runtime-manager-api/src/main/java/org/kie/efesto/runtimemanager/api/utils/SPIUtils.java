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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.utils.CollectionUtils.findAtMostOne;

public class SPIUtils {

    private SPIUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(SPIUtils.class.getName());

    private static final ServiceLoader<RuntimeManager> runtimeManagerLoader = ServiceLoader.load(RuntimeManager.class);

    private static final ServiceLoader<KieRuntimeService> kieRuntimeServiceLoader = ServiceLoader.load(KieRuntimeService.class);

    private static List<KieRuntimeService> kieRuntimeServices = getKieRuntimeServices(kieRuntimeServiceLoader);

    public static Optional<KieRuntimeService> getKieRuntimeService(EfestoInput<?> input, boolean refresh, EfestoRuntimeContext context) {
        if ( logger.isTraceEnabled() ) {
            logger.trace("getKieRuntimeService {} {}", input, refresh);
        }
        return findAtMostOne(getKieRuntimeServices(refresh), service -> service.canManageInput(input, context),
                (s1, s2) -> new KieRuntimeServiceException("Found more than one compiler services: " + s1 + " and " + s2));
    }

    public static Optional<KieRuntimeService> getKieRuntimeServiceFromEfestoRuntimeContext(EfestoInput<?> input, EfestoRuntimeContext context) {
        if ( logger.isTraceEnabled() ) {
            logger.trace("getKieCompilerServiceFromEfestoCompilationContext {} {}", input, context);
        }
        ServiceLoader<KieRuntimeService> contextServiceLoader = context.getKieRuntimeService();
        return findAtMostOne(contextServiceLoader, service -> service.canManageInput(input, context),
                             (s1, s2) -> new KieRuntimeServiceException("Found more than one compiler services: " + s1 + " and " + s2));
    }

    public static List<KieRuntimeService> getKieRuntimeServices(boolean refresh) {
        if ( logger.isTraceEnabled() ) {
            logger.trace("getKieRuntimeServices {}", refresh);
        }
        if (!refresh) {
            return kieRuntimeServices;
        }
        return kieRuntimeServices = getKieRuntimeServices(getServices(refresh));
    }

    private static List<KieRuntimeService> getKieRuntimeServices(Iterable<KieRuntimeService> serviceIterable) {
        List<KieRuntimeService> toReturn = new ArrayList<>();
        serviceIterable.forEach(toReturn::add);
        if (logger.isTraceEnabled()) {
            logger.trace("toReturn {} {}", toReturn, toReturn.size());
            toReturn.forEach(provider -> logger.trace("{}", provider));
        }
        return toReturn;
    }

    public static Optional<RuntimeManager> getRuntimeManager(boolean refresh) {
        logger.debug("getRuntimeManager {}", refresh);
        List<RuntimeManager> toReturn = new ArrayList<>();
        Iterable<RuntimeManager> managers = getManagers(refresh);
        managers.forEach(toReturn::add);
        return toReturn.stream().findFirst();
    }


    private static Iterable<KieRuntimeService> getServices(boolean refresh) {
        if (refresh) {
            kieRuntimeServiceLoader.reload();
        }
        return kieRuntimeServiceLoader;
    }

    private static Iterable<RuntimeManager> getManagers(boolean refresh) {
        if (refresh) {
            runtimeManagerLoader.reload();
        }
        return runtimeManagerLoader;
    }
}
