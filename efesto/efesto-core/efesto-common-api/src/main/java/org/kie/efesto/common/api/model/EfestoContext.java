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

package org.kie.efesto.common.api.model;

import java.util.Map;
import java.util.Set;

import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.listener.EfestoListener;

/**
 * The context of an execution
 */
public interface EfestoContext<T extends EfestoListener> {

    /**
     * Add the given <code>EfestoListener</code> to the current <code>Context</code>
     * @param toAdd
     */
    default void addEfestoListener(final T toAdd) {
        throw new UnsupportedOperationException();
    }

    /**
     * Remove the given <code>EfestoListener</code> from the current <code>Context</code>.
     * @param toRemove
     */
    default void removeEfestoListener(final T toRemove) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an <b>unmodifiable set</b> of the <code>EfestoListener</code>s registered with the
     * current instance
     */
    default Set<T> getEfestoListeners() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get previously generated classes with the key {@code fri}
     * @param localUri
     * @return generatedClasses
     */
    default Map<String, byte[]> getGeneratedClasses(LocalUri localUri) {
        return GeneratedClassesRepository.INSTANCE.getGeneratedClasses(localUri);
    }

    /**
     * Add generated classes with the key {@code fri}
     * @param localUri
     * @param generatedClasses
     */
    default void addGeneratedClasses(LocalUri localUri, Map<String, byte[]> generatedClasses) {
        GeneratedClassesRepository.INSTANCE.addGeneratedClasses(localUri, generatedClasses);
    }

    /**
     * Returns {@code true} if this map contains a mapping for the {@code fri}
     * @param localUri
     * @return {@code true} if this map contains a mapping for the {@code fri}
     */
    default boolean containsKey(LocalUri localUri) {
        return GeneratedClassesRepository.INSTANCE.containsKey(localUri);
    }

    /**
     * @return {@code Set} of {@code LocalUri} key in this map
     */
    default Set<LocalUri> localUriKeySet() {
        return GeneratedClassesRepository.INSTANCE.localUriKeySet();
    }
}
