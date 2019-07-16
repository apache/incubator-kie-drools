/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.process.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;

class MapProcessInstances<T> implements ProcessInstances<T> {

    private final ConcurrentHashMap<Long, ProcessInstance<T>> instances = new ConcurrentHashMap<>();

    @Override
    public Optional<? extends ProcessInstance<T>> findById(long id) {
        return Optional.ofNullable(instances.get(id));
    }

    @Override
    public Collection<? extends ProcessInstance<T>> values() {
        return instances.values();
    }

    void update(long id, ProcessInstance<T> instance) {
        if (instance.status() == org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE) {
            instances.put(id, instance);
        }
    }

    void remove(long id) {
        instances.remove(id);
    }
}
