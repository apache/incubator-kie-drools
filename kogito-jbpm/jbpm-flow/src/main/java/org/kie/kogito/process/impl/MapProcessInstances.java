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

import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;

class MapProcessInstances<T> implements MutableProcessInstances<T> {

    private final ConcurrentHashMap<String, ProcessInstance<T>> instances = new ConcurrentHashMap<>();

    @Override
    public Optional<? extends ProcessInstance<T>> findById(String id) {
        return Optional.ofNullable(instances.get(resolveId(id)));      
    }

    @Override
    public Collection<? extends ProcessInstance<T>> values() {
        return instances.values();
    }
    
    @Override
    public void create(String id, ProcessInstance<T> instance) {
        if (isActive(instance)) {
            ProcessInstance<T> existing = instances.putIfAbsent(resolveId(id), instance);
            if (existing != null) {
                throw new ProcessInstanceDuplicatedException(id);
            }
        }
    }

    @Override
    public void update(String id, ProcessInstance<T> instance) {
        if (isActive(instance)) {
            instances.put(resolveId(id), instance);            
        }
    }

    @Override
    public void remove(String id) {
        instances.remove(resolveId(id));
    }

    @Override
    public boolean exists(String id) {
        return instances.containsKey(id);
    }

}
