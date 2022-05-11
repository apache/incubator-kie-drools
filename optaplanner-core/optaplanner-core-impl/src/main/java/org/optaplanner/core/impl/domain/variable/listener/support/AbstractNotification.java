/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.variable.listener.support;

import java.util.Objects;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;

abstract class AbstractNotification {

    protected final Object entity;
    protected final int index;

    protected AbstractNotification(Object entity, int index) {
        this.entity = entity;
        this.index = index;
    }

    protected AbstractNotification(Object entity) {
        this(entity, -1);
    }

    /**
     * Warning: do not test equality of {@link AbstractNotification}s for different {@link VariableListener}s
     * (so {@link ShadowVariableDescriptor}s) because equality does not take those into account (for performance)!
     *
     * @param o sometimes null
     * @return true if same entity instance and the same type
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractNotification that = (AbstractNotification) o;
        return entity.equals(that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(System.identityHashCode(entity), getClass());
    }
}
