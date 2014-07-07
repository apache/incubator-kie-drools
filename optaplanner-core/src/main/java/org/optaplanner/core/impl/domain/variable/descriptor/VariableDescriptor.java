/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.impl.domain.variable.descriptor;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.domain.common.PropertyAccessor;
import org.optaplanner.core.impl.domain.common.ReflectionPropertyAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;

public abstract class VariableDescriptor {

    protected final EntityDescriptor entityDescriptor;

    protected final PropertyAccessor variablePropertyAccessor;
    protected final String variableName;

    private List<ShadowVariableDescriptor> shadowVariableDescriptorList = new ArrayList<ShadowVariableDescriptor>(4);

    public VariableDescriptor(EntityDescriptor entityDescriptor,
            PropertyDescriptor propertyDescriptor) {
        this.entityDescriptor = entityDescriptor;
        variablePropertyAccessor = new ReflectionPropertyAccessor(propertyDescriptor);
        variableName = variablePropertyAccessor.getName();
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public EntityDescriptor getEntityDescriptor() {
        return entityDescriptor;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getSimpleEntityAndVariableName() {
        return entityDescriptor.getEntityClass().getSimpleName() + "." + variableName;
    }

    public Class<?> getVariablePropertyType() {
        return variablePropertyAccessor.getPropertyType();
    }

    // ************************************************************************
    // Shadows
    // ************************************************************************

    public void registerShadowVariableDescriptor(ShadowVariableDescriptor shadowVariableDescriptor) {
        shadowVariableDescriptorList.add(shadowVariableDescriptor);
    }

    public boolean hasAnyShadow() {
        return !shadowVariableDescriptorList.isEmpty();
    }

    public List<VariableListener> buildVariableListenerList() {
        List<VariableListener> variableListenerList = new ArrayList<VariableListener>(shadowVariableDescriptorList.size());
        // Always trigger the build-in shadow variables first
        for (ShadowVariableDescriptor shadowVariableDescriptor : shadowVariableDescriptorList) {
            variableListenerList.add(shadowVariableDescriptor.buildVariableListener());
        }
        return variableListenerList;
    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    public Object getValue(Object entity) {
        return variablePropertyAccessor.executeGetter(entity);
    }

    public void setValue(Object entity, Object value) {
        variablePropertyAccessor.executeSetter(entity, value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variableName
                + " of " + entityDescriptor.getEntityClass().getName() + ")";
    }

}
