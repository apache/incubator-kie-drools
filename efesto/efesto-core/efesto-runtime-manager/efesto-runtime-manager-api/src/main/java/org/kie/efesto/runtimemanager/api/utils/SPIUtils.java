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
package org.kie.efesto.runtimemanager.api.utils;

import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoLocalRuntimeContext;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.kie.efesto.common.api.utils.CollectionUtils.findAtMostOne;

public class SPIUtils {

    private SPIUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(SPIUtils.class.getName());

    private static final ServiceLoader<RuntimeManager> runtimeManagerLoader = ServiceLoader.load(RuntimeManager.class);

    private static final ServiceLoader<LocalRuntimeManager> localRuntimeManagerLoader = ServiceLoader.load(LocalRuntimeManager.class);
    private static final ServiceLoader<DistributedRuntimeManager> distributedRuntimeManagerLoader = ServiceLoader.load(DistributedRuntimeManager.class);

    private static final ServiceLoader<RuntimeServiceProvider> runtimeServiceProvidersLoader = ServiceLoader.load(RuntimeServiceProvider.class);

    private static final ServiceLoader<KieRuntimeService> kieRuntimeServiceLoader = ServiceLoader.load(KieRuntimeService.class);

    private static List<RuntimeServiceProvider> runtimeServiceProviders = getRuntimeServiceProviders(runtimeServiceProvidersLoader);

    private static List<KieRuntimeService> kieRuntimeServices = getKieRuntimeServices(kieRuntimeServiceLoader);

    public static List<KieRuntimeService> getLocalDiscoveredKieRuntimeServices() {
        return kieRuntimeServices;
    }

    public static Optional<KieRuntimeService> getKieRuntimeService(List<KieRuntimeService> discoveredServices, EfestoInput<?> input, EfestoRuntimeContext context) {
        if (logger.isTraceEnabled()) {
            logger.trace("getKieRuntimeService {} {} {}", discoveredServices, input, context);
        }
        return findAtMostOne(discoveredServices, service -> service.canManageInput(input, context),
                             (s1, s2) -> new KieRuntimeServiceException("Found more than one compiler service: " + s1 + " and " + s2));
    }

    public static List<RuntimeServiceProvider> getRuntimeServiceProviders(boolean refresh) {
        if (logger.isTraceEnabled()) {
            logger.trace("getRuntimeServiceProviders {}", refresh);
        }
        if (!refresh) {
            return runtimeServiceProviders;
        }
        return runtimeServiceProviders = getRuntimeServiceProviders(getProviders(refresh));
    }

    public static List<KieRuntimeService> getKieRuntimeServices(boolean refresh) {
        if (logger.isTraceEnabled()) {
            logger.trace("getKieRuntimeServices {}", refresh);
        }
        if (!refresh) {
            return kieRuntimeServices;
        }
        return kieRuntimeServices = getKieRuntimeServices(getServices(refresh));
    }

    public static Optional<RuntimeManager> getRuntimeManager(boolean refresh) {
        logger.debug("getRuntimeManager {}", refresh);
        Iterable<RuntimeManager> managers = getManagers(refresh);
        return managers.iterator().hasNext() ? Optional.of(managers.iterator().next()) : Optional.empty();
    }

    public static Optional<LocalRuntimeManager> getLocalRuntimeManager(boolean refresh) {
        logger.debug("getLocalRuntimeManager {}", refresh);
        Iterable<LocalRuntimeManager> managers = getLocalManagers(refresh);
        return managers.iterator().hasNext() ? Optional.of(managers.iterator().next()) : Optional.empty();
    }

    public static List<DistributedRuntimeManager> getDistributedRuntimeManagers(boolean refresh) {
        logger.debug("getDistributedRuntimeManagers {}", refresh);
        Iterable<DistributedRuntimeManager> managers = getDistributedManagers(refresh);
        return StreamSupport
                .stream(managers.spliterator(), false)
                .collect(Collectors.toList());
    }

    private static List<RuntimeServiceProvider> getRuntimeServiceProviders(Iterable<RuntimeServiceProvider> serviceIterable) {
        List<RuntimeServiceProvider> toReturn = new ArrayList<>();
        serviceIterable.forEach(toReturn::add);
        if (logger.isTraceEnabled()) {
            logger.trace("toReturn {} {}", toReturn, toReturn.size());
            toReturn.forEach(provider -> logger.trace("{}", provider));
        }
        return toReturn;
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

    private static Iterable<LocalRuntimeManager> getLocalManagers(boolean refresh) {
        if (refresh) {
            localRuntimeManagerLoader.reload();
        }
        return localRuntimeManagerLoader;
    }

    private static Iterable<DistributedRuntimeManager> getDistributedManagers(boolean refresh) {
        if (refresh) {
            distributedRuntimeManagerLoader.reload();
        }
        return distributedRuntimeManagerLoader;
    }

    private static Iterable<RuntimeServiceProvider> getProviders(boolean refresh) {
        if (refresh) {
            runtimeServiceProvidersLoader.reload();
        }
        return runtimeServiceProvidersLoader;
    }

    public static Set<String> collectModelTypes(EfestoLocalRuntimeContext context) {
        List<KieRuntimeService> kieRuntimeServices = getKieRuntimeServices(false);
        Set<String> modelTypes = new HashSet<>();
        for (KieRuntimeService kieRuntimeService : kieRuntimeServices) {
            modelTypes.add(kieRuntimeService.getModelType());
        }
        ServiceLoader<KieRuntimeService> serviceLoader = context.getKieRuntimeService();
        for (KieRuntimeService kieRuntimeService : serviceLoader) {
            modelTypes.add(kieRuntimeService.getModelType());
        }
        return modelTypes;
    }
}
