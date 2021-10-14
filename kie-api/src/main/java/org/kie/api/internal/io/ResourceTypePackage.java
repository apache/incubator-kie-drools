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

package org.kie.api.internal.io;

import java.io.Serializable;

import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

/**
 * A container for resources that have been processed by a {@link KieAssemblerService}.
 *
 * Resources are expected to be able to be looked up by a "name" or identifier.
 *
 * Each {@link ResourceTypePackage} is identified by a namespace.
 *
 * @param <T> the type of such a processed resource
 */
public interface ResourceTypePackage<T> extends Iterable<T>,
                                                Serializable {
    ResourceType getResourceType();

    /**
     * Remove artifacts inside this ResourceTypePackage which belong to the resource passed as parameter.
     * Concrete implementation of this interface shall extend this method in order to properly support incremental KieContainer updates.
     * 
     * @param resource
     * @return true if this ResourceTypePackage mutated as part of this method invocation.
     */
    default boolean removeResource(Resource resource) {
        return false;
    }

    void add(T element);

}
