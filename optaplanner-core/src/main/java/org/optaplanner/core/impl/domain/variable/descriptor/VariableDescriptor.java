/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;

public abstract class VariableDescriptor {

    protected final EntityDescriptor entityDescriptor;

    protected final MemberAccessor variableMemberAccessor;
    protected final String variableName;

    protected List<ShadowVariableDescriptor> sinkVariableDescriptorList = new ArrayList<ShadowVariableDescriptor>(4);

    public VariableDescriptor(EntityDescriptor entityDescriptor, MemberAccessor variableMemberAccessor) {
        this.entityDescriptor = entityDescriptor;
        this.variableMemberAccessor = variableMemberAccessor;
        variableName = variableMemberAccessor.getName();
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
        return variableMemberAccessor.getType();
    }

    // ************************************************************************
    // Shadows
    // ************************************************************************

    public void registerSinkVariableDescriptor(ShadowVariableDescriptor shadowVariableDescriptor) {
        sinkVariableDescriptorList.add(shadowVariableDescriptor);
    }

    /**
     * Inverse of {@link ShadowVariableDescriptor#getSourceVariableDescriptorList()}.
     * @return never null, only direct shadow variables that are affected by this variable
     */
    public List<ShadowVariableDescriptor> getSinkVariableDescriptorList() {
        return sinkVariableDescriptorList;
    }

    /**
     * @param value never null
     * @return true if it might be an anchor, false if it is definitely not an anchor
     */
    public boolean isValuePotentialAnchor(Object value) {
        return !entityDescriptor.getEntityClass().isAssignableFrom(value.getClass());
    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    public Object getValue(Object entity) {
        return variableMemberAccessor.executeGetter(entity);
    }

    public void setValue(Object entity, Object value) {
        variableMemberAccessor.executeSetter(entity, value);
    }

}
