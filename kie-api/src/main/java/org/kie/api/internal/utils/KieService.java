/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.util.ServiceLoader;

public interface KieService extends Comparable<KieService> {
    default int servicePriority() {
        return 0;
    }

    @Override
    default int compareTo(KieService other) {
        if (servicePriority() == other.servicePriority()) {
            throw new IllegalStateException("Found 2 services with same priority (" + servicePriority() + "): " + this.getClass().getCanonicalName() + " and " + other.getClass().getCanonicalName());
        }
        return servicePriority() - other.servicePriority();
    }

    static <T extends KieService> T load(Class<T> serviceClass) {
        ServiceLoader<T> loader = ServiceLoader.load(serviceClass, serviceClass.getClassLoader());
        T service = null;
        for (T impl : loader) {
            if (service == null || impl.compareTo(service) > 0) {
                service = impl;
            }
        }
        return service;
    }
}
