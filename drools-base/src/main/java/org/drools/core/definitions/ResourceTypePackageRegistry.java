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

package org.drools.core.definitions;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

public class ResourceTypePackageRegistry implements Serializable {

    private final Map<ResourceType, ResourceTypePackage<?>> packages = new HashMap<>();

    public boolean remove(Resource resource) {
        boolean somethingWasRemoved = false;
        for (ResourceTypePackage rtp : packages.values()) {
            somethingWasRemoved = rtp.removeResource(resource) || somethingWasRemoved;
        }
        return somethingWasRemoved;

    }

    public ResourceTypePackage<?> get(ResourceType type) {
        return packages.get(type);
    }

    public void put(ResourceType type, ResourceTypePackage<?> pkg) {
        this.packages.put(type, pkg);
    }

    public boolean isEmpty() {
        return packages.isEmpty();
    }

    public Collection<ResourceTypePackage<?>> values() {
        return packages.values();
    }

    public <T extends ResourceTypePackage<?>> T computeIfAbsent(ResourceType resourceType, Function<? super ResourceType, T> mappingFunction) {
        return (T) packages.computeIfAbsent(resourceType, mappingFunction);
    }
}
