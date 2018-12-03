/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.api.runtime;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.KieBase;
import org.kie.api.internal.runtime.KieRuntimeService;
import org.kie.api.internal.runtime.KieRuntimes;
import org.kie.api.internal.utils.ServiceRegistry;

/**
 * Maintains a collection of Knowledge Runtimes
 * that is bound to the given KieBase.
 */
public class KieRuntimeFactory {

    private final KieBase kieBase;
    private final ConcurrentHashMap<Class<?>, Object> runtimeServices = new ConcurrentHashMap<>();

    /**
     * Creates an instance of this factory for the given KieBase
     */
    public static KieRuntimeFactory of(KieBase kieBase) {
        return new KieRuntimeFactory(kieBase);
    }

    private KieRuntimeFactory(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    /**
     * Returns a singleton instance of the given class (if any)
     * @throws NoSuchElementException if it is not possible to find a service for the given class
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> cls) {
        T runtimeInstance = (T) runtimeServices.computeIfAbsent(cls, this::createRuntimeInstance);
        if (runtimeInstance == null) {
            throw new NoSuchElementException(cls.getName());
        } else {
            return runtimeInstance;
        }
    }

    /**
     * Create a runtime instance, and return nulls if it fails.
     * Respects the Map#computeIfAbsent contract
     * @param c
     * @return
     */
    private Object createRuntimeInstance(Class<?> c) {
        KieRuntimeService kieRuntimeService =
                ServiceRegistry.getInstance()
                        .get(KieRuntimes.class)
                        .getRuntimes()
                        .get(c.getName());
        if (kieRuntimeService == null) {
            return null;
        } else {
            return kieRuntimeService.newKieRuntime(kieBase);
        }
    }
}
