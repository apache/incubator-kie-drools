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
package org.kie.efesto.runtimemanager.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.cache.EfestoIdentifierClassKey;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.runtimemanager.api.utils.SPIUtils.getDiscoveredKieRuntimeServices;
import static org.kie.efesto.runtimemanager.api.utils.SPIUtils.getKieRuntimeService;
import static org.kie.efesto.runtimemanager.api.utils.SPIUtils.getKieRuntimeServiceFromEfestoRuntimeContext;

public class RuntimeManagerUtils {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeManagerUtils.class.getName());

    protected static final Map<EfestoClassKey, List<KieRuntimeService>> firstLevelCache = new HashMap<>();
    protected static final Map<EfestoIdentifierClassKey, KieRuntimeService> secondLevelCache = new HashMap<>();

    private RuntimeManagerUtils() {
    }

    static {
        populateFirstLevelCache(firstLevelCache);
    }

    static void populateFirstLevelCache(final Map<EfestoClassKey, List<KieRuntimeService>> toPopulate) {
        List<KieRuntimeService> discoveredKieRuntimeServices = getDiscoveredKieRuntimeServices();
        populateFirstLevelCache(discoveredKieRuntimeServices, toPopulate);
    }

    static void populateFirstLevelCache(final List<KieRuntimeService> discoveredKieRuntimeServices,
                                        final Map<EfestoClassKey, List<KieRuntimeService>> toPopulate) {
        discoveredKieRuntimeServices.forEach(kieRuntimeService -> {
            EfestoClassKey efestoClassKey = kieRuntimeService.getEfestoClassKeyIdentifier();
            toPopulate.merge(efestoClassKey, new ArrayList<>(Collections.singletonList(kieRuntimeService)), (previous,
                                                                          toAdd) -> {
                List<KieRuntimeService> toReturn = new ArrayList<>();
                toReturn.addAll(previous);
                toReturn.addAll(toAdd);
                return toReturn;
            });
        });
    }

    static Optional<KieRuntimeService> getKieRuntimeServiceLocal(EfestoRuntimeContext context, EfestoInput input) {

        KieRuntimeService cachedKieRuntimeService = getKieRuntimeServiceFromSecondLevelCache(input);
        if (cachedKieRuntimeService != null) {
            return Optional.of(cachedKieRuntimeService);
        }
        Optional<KieRuntimeService> retrieved = getKieRuntimeServiceFromFirstLevelCache(context, input);
        if (retrieved.isEmpty()) {
            retrieved = getKieRuntimeServiceFromEfestoRuntimeContextLocal(context, input);
        }
        if (retrieved.isEmpty()) {
            logger.warn("Cannot find KieRuntimeService for {}", input.getModelLocalUriId());
        } else {
            secondLevelCache.put(input.getSecondLevelCacheKey(), retrieved.get());
        }
        return retrieved;
    }

    /**
     * Looks for <code>KieRuntimeService</code> inside the <b>secondLevelCache</b> by <code>EfestoIdentifierClassKey</code>
     * @param input
     * @return The found <code>KieRuntimeService</code>, or <code>null</code>
     */
    static KieRuntimeService getKieRuntimeServiceFromSecondLevelCache(EfestoInput input) {
        return secondLevelCache.get(input.getSecondLevelCacheKey());
    }

    /**
     * Looks for <code>KieRuntimeService</code> inside the <b>firstLevelCache</b> by <code>EfestoClassKey</code>
     * @param context
     * @param input
     * @return <code>Optional</code> of found <code>KieRuntimeService</code>, or <code>Optional.empty()</code>
     */
    static Optional<KieRuntimeService> getKieRuntimeServiceFromFirstLevelCache(EfestoRuntimeContext context,
                                                                               EfestoInput input) {
        List<KieRuntimeService> discoveredServices = firstLevelCache.get(input.getFirstLevelCacheKey());
        return (discoveredServices != null && !discoveredServices.isEmpty()) ?
                getKieRuntimeService(discoveredServices, input, context) :
                Optional.empty();
    }

    /**
     * Looks for <code>KieRuntimeService</code> inside the given <code>EfestoRuntimeContext</code> by <code>EfestoClassKey</code>,
     * eventually storing it inside the <b>firstLevelCache</b>.
     * @param context
     * @param input
     * @return <code>Optional</code> of found <code>KieRuntimeService</code>, or <code>Optional.empty()</code>
     */
    static Optional<KieRuntimeService> getKieRuntimeServiceFromEfestoRuntimeContextLocal(EfestoRuntimeContext context,
                                                                               EfestoInput input) {
        logger.warn("Cannot find KieRuntimeService for {}, looking inside context classloader",
                    input.getModelLocalUriId());
        Optional<KieRuntimeService> retrieved = getKieRuntimeServiceFromEfestoRuntimeContext(input, context);
        if (retrieved.isPresent()) {
            KieRuntimeService toAdd = retrieved.get();
            addKieRuntimeServiceToFirstLevelCache(toAdd, input.getFirstLevelCacheKey());
        }
        return retrieved;
    }

    static void addKieRuntimeServiceToFirstLevelCache(KieRuntimeService toAdd, EfestoClassKey firstLevelClassKey) {
        List<KieRuntimeService> stored = firstLevelCache.get(firstLevelClassKey);
        if (stored == null) {
            stored = new ArrayList<>();
            firstLevelCache.put(firstLevelClassKey, stored);
        }
        stored.add(toAdd);
    }

    static Optional<EfestoOutput> getOptionalOutput(EfestoRuntimeContext context, EfestoInput input) {
        Optional<KieRuntimeService> retrieved = getKieRuntimeServiceLocal(context, input);
        return retrieved.isPresent() ? retrieved.flatMap(kieRuntimeService -> kieRuntimeService.evaluateInput(input,
                                                                                                              context)) : Optional.empty();
    }
}
