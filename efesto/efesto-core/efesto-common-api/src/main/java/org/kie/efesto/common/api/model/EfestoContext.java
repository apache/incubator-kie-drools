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

import java.util.Set;

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
}
