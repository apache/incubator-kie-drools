/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.domain.variable.listener.support.violation;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;

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
                    + ") after all variable listeners were triggered without changes to the genuine variables.\n"
                    + "      Maybe one of the listeners ("
                    + shadowVariableDescriptor.getVariableListenerClasses().stream()
                            .map(Class::getSimpleName)
                            .collect(Collectors.toList())
                    + ") for that shadow variable (" + shadowVariableDescriptor.getSimpleEntityAndVariableName()
                    + ") forgot to update it when one of its sourceVariables ("
                    + shadowVariableDescriptor.getSourceVariableDescriptorList().stream()
                            .map(VariableDescriptor::getSimpleEntityAndVariableName)
                            .collect(Collectors.toList())
                    + ") changed.\n"
                    + "      Or vice versa, maybe one of the listeners computes this shadow variable using a planning variable"
                    + " that is not declared as its source."
                    + " Use the repeatable @" + ShadowVariable.class.getSimpleName()
                    + " annotation for each source variable that is used to compute this shadow variable."
                    + "\n");
        }
    }

    ShadowVariableDescriptor<?> getShadowVariableDescriptor() {
        return shadowVariableDescriptor;
    }

    @Override
    public String toString() {
        return entity + "." + shadowVariableDescriptor.getVariableName() + " = " + originalValue;
    }
}
