/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;

public class VariableListenerNotification {

    private final Object entity;
    private final VariableListenerNotificationType type;

    public VariableListenerNotification(Object entity, VariableListenerNotificationType type) {
        this.entity = entity;
        this.type = type;
    }

    public Object getEntity() {
        return entity;
    }

    public VariableListenerNotificationType getType() {
        return type;
    }

    /**
     * Warning: do not test equality of {@link VariableListenerNotification}s for different {@link VariableListener}s
     * (so {@link ShadowVariableDescriptor}s) because equality does not take those into account (for performance)!
     *
     * @param o sometimes null
     * @return true if same entity instance and the same type
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof VariableListenerNotification) {
            VariableListenerNotification other = (VariableListenerNotification) o;
            return entity == other.entity && type == other.type;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(System.identityHashCode(entity), type);
    }

}
