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

import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SPIUtils {

    private SPIUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(SPIUtils.class.getName());

    private static final ServiceLoader<RuntimeManager> runtimeManagerLoader = ServiceLoader.load(RuntimeManager.class);

    private static final ServiceLoader<KieRuntimeService> kieRuntimeServiceLoader = ServiceLoader.load(KieRuntimeService.class);

    public static Optional<KieRuntimeService> getKieRuntimeService(EfestoInput<?> darInput, boolean refresh, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        logger.debug("getKieRuntimeService {} {}", darInput, refresh);
        List<KieRuntimeService> retrieved = getKieRuntimeServices(refresh);
        return retrieved.stream().filter(service -> service.canManageInput(darInput, memoryCompilerClassLoader)).findFirst();
    }

    public static List<KieRuntimeService> getKieRuntimeServices(boolean refresh) {
        logger.debug("getKieRuntimeServices {}", refresh);
        List<KieRuntimeService> toReturn = new ArrayList<>();
        Iterator<KieRuntimeService> services = getServices(refresh);
        services.forEachRemaining(toReturn::add);
        logger.debug("toReturn {} {}", toReturn, toReturn.size());
        if (logger.isTraceEnabled()) {
            toReturn.forEach(provider -> logger.trace("{}", provider));
        }
        return toReturn;
    }

    public static Optional<RuntimeManager> getRuntimeManager(boolean refresh) {
        logger.debug("getRuntimeManager {}", refresh);
        List<RuntimeManager> toReturn = new ArrayList<>();
        Iterator<RuntimeManager> managers = getManagers(refresh);
        managers.forEachRemaining(toReturn::add);
        return toReturn.stream().findFirst();
    }


    private static Iterator<KieRuntimeService> getServices(boolean refresh) {
        if (refresh) {
            kieRuntimeServiceLoader.reload();
        }
        return kieRuntimeServiceLoader.iterator();
    }

    private static Iterator<RuntimeManager> getManagers(boolean refresh) {
        if (refresh) {
            runtimeManagerLoader.reload();
        }
        return runtimeManagerLoader.iterator();
    }
}
