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

package org.kie.submarine.process.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import org.kie.submarine.process.ProcessInstance;
import org.kie.submarine.process.ProcessInstances;

class MapProcessInstances<T> implements ProcessInstances<T> {

    private final HashMap<Long, ProcessInstance<T>> instances = new HashMap<>();

    @Override
    public Optional<? extends ProcessInstance<T>> findById(long id) {
        return Optional.ofNullable(instances.get(id));
    }

    @Override
    public Collection<? extends ProcessInstance<T>> values() {
        return instances.values();
    }

    void update(long id, ProcessInstance<T> instance) {
        instances.put(id, instance);
    }

    void remove(long id) {
        instances.remove(id);
    }
}
