/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.variable.listener.support.violation;

import java.util.Objects;
import java.util.function.Consumer;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;

final class ShadowVariableSnapshot {

    private final ShadowVariableDescriptor<?> shadowVariableDescriptor;
    private final Object entity;
    private final Object originalValue;

    private ShadowVariableSnapshot(ShadowVariableDescriptor<?> shadowVariableDescriptor, Object entity, Object originalValue) {
        this.shadowVariableDescriptor = shadowVariableDescriptor;
        this.entity = entity;
        this.originalValue = originalValue;
    }

    static ShadowVariableSnapshot of(ShadowVariableDescriptor<?> shadowVariableDescriptor, Object entity) {
        return new ShadowVariableSnapshot(shadowVariableDescriptor, entity, shadowVariableDescriptor.getValue(entity));
    }

    void validate(Consumer<String> violationMessageConsumer) {
        Object newValue = shadowVariableDescriptor.getValue(entity);
        if (!Objects.equals(originalValue, newValue)) {
            violationMessageConsumer.accept("    The entity (" + entity
                    + ")'s shadow variable (" + shadowVariableDescriptor.getSimpleEntityAndVariableName()
                    + ")'s corrupted value (" + originalValue + ") changed to uncorrupted value (" + newValue
                    + ") after all " + VariableListener.class.getSimpleName()
                    + "s were triggered without changes to the genuine variables.\n"
                    + "      Maybe the " + VariableListener.class.getSimpleName() + " class ("
                    + shadowVariableDescriptor.getVariableListenerClass().getSimpleName()
                    + ") for that shadow variable (" + shadowVariableDescriptor.getSimpleEntityAndVariableName()
                    + ") forgot to update it when one of its sources changed.\n");
        }
    }

    ShadowVariableDescriptor<?> getShadowVariableDescriptor() {
        return shadowVariableDescriptor;
    }
}
