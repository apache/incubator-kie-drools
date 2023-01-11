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

package org.kie.kogito.jobs.service.api;

import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDescriptorRegistry<T extends Descriptor> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDescriptorRegistry.class);
    protected final LinkedHashSet<T> descriptors = new LinkedHashSet<>();
    protected final Class<T> clazz;

    protected AbstractDescriptorRegistry(Class<T> clazz) {
        this.clazz = clazz;
        loadDescriptors();
    }

    public Set<T> getDescriptors() {
        return descriptors;
    }

    public java.util.Optional<T> getDescriptor(Recipient<?> recipient) {
        return getDescriptors().stream()
                .filter(descr -> descr.getType().isInstance(recipient))
                .findFirst();
    }

    protected void loadDescriptors() {
        LOGGER.debug("Loading recipient descriptor registry");
        final ServiceLoader<T> loader = ServiceLoader.load(clazz);
        loader.iterator().forEachRemaining(descriptor -> {
            LOGGER.debug("adding -> ({}) to registry", descriptor);
            descriptors.add(descriptor);
        });
        LOGGER.debug("total descriptors: {}", descriptors.size());
    }
}
