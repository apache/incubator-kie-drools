/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.api.internal.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.kie.api.Service;

import static org.kie.api.internal.utils.ServiceUtil.instanceFromNames;

/**
 * Internal Interface
 *
 */
public interface ServiceRegistry extends Service {

    static <T> T getService(Class<T> cls) {
        return getInstance().get( cls );
    }

    static ServiceRegistry getInstance() {
        return ServiceRegistryHolder.serviceRegistry;
    }

    <T> T get(Class<T> cls);

    <T> List<T> getAll(Class<T> cls);

    class ServiceRegistryHolder {
        private static ServiceRegistry serviceRegistry = Impl.getServiceRegistry();
    }

    class Impl implements ServiceRegistry {

        private static final String DYNAMIC_IMPL = "org.drools.wiring.dynamic.DynamicServiceRegistrySupplier";
        private static final String KOGITO_IMPL = "org.kie.kogito.wiring.statics.KogitoStaticServiceRegistrySupplier";
        private static final String STATIC_IMPL = "org.drools.wiring.statics.StaticServiceRegistrySupplier";

        private static Supplier<ServiceRegistry> supplier;

        private Map<String, List<Object>> registry;

        public Impl() {
            registry = ServiceDiscoveryImpl.getInstance().getServices();
        }

        public synchronized void reset() {
            ServiceDiscoveryImpl.getInstance().reset();
        }

        public synchronized void reload() {
            registry = ServiceDiscoveryImpl.getInstance().getServices();
        }

        public <T> T get(Class<T> cls) {
            for ( Object service : getAll( cls ) ) {
                if ( cls.isInstance( service ) ) {
                    return (T) service;
                }
            }
            return null;
        }

        public <T> List<T> getAll(Class<T> cls) {
            return (List<T>) this.registry.getOrDefault( cls.getCanonicalName(), Collections.emptyList() );
        }

        public static ServiceRegistry getServiceRegistry() {
            if (supplier == null) {
                supplier = instanceFromNames(DYNAMIC_IMPL, KOGITO_IMPL, STATIC_IMPL);
            }
            return supplier.get();
        }

        public static void setSupplier( Supplier<ServiceRegistry> supplier ) {
            Impl.supplier = supplier;
        }
    }

    static boolean isSupported(Class<?> clazz) {
        return getInstance().get(clazz) != null;
    }

    static <R,A> Optional<R> ifSupported(Class<A> clazz, Function<A, R> executed) {
        return isSupported(clazz) ? Optional.of(executed.apply(getInstance().get(clazz))) : Optional.empty();
    }

}
