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

package org.optaplanner.core.impl.domain.variable.nextprev;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.Demand;

abstract class AbstractNextPrevElementShadowVariableDescriptor<Solution_> extends ShadowVariableDescriptor<Solution_> {

    protected ListVariableDescriptor<Solution_> sourceVariableDescriptor;

    AbstractNextPrevElementShadowVariableDescriptor(
            EntityDescriptor<Solution_> entityDescriptor,
            MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
    }

    abstract String getSourceVariableName();

    abstract String getAnnotationName();

    @Override
    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        // Do nothing
    }

    @Override
    public void linkVariableDescriptors(DescriptorPolicy descriptorPolicy) {
        linkShadowSources(descriptorPolicy);
    }

    private void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        String sourceVariableName = getSourceVariableName();
        List<EntityDescriptor<Solution_>> entitiesWithSourceVariable =
                entityDescriptor.getSolutionDescriptor().getEntityDescriptors().stream()
                        .filter(entityDescriptor -> entityDescriptor.hasVariableDescriptor(sourceVariableName))
                        .collect(Collectors.toList());
        if (entitiesWithSourceVariable.isEmpty()) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a @" + getAnnotationName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not a valid planning variable on any of the entity classes ("
                    + entityDescriptor.getSolutionDescriptor().getEntityDescriptors() + ").");
        }
        if (entitiesWithSourceVariable.size() > 1) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a @" + getAnnotationName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not a unique planning variable."
                    + " A planning variable with the name (" + sourceVariableName + ") exists on multiple entity classes ("
                    + entitiesWithSourceVariable + ").");
        }
        VariableDescriptor<Solution_> variableDescriptor =
                entitiesWithSourceVariable.get(0).getVariableDescriptor(sourceVariableName);
        if (variableDescriptor == null) {
            throw new IllegalStateException(
                    "Impossible state: variableDescriptor (" + variableDescriptor + ") is null"
                            + " but previous checks indicate that the entityClass (" + entitiesWithSourceVariable.get(0)
                            + ") has a planning variable with sourceVariableName (" + sourceVariableName + ").");
        }
        if (!(variableDescriptor instanceof ListVariableDescriptor)) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a @" + getAnnotationName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not a @" + PlanningListVariable.class.getSimpleName() + ".");
        }
        sourceVariableDescriptor = (ListVariableDescriptor<Solution_>) variableDescriptor;
        if (!variableMemberAccessor.getType().equals(sourceVariableDescriptor.getElementType())) {
            throw new IllegalStateException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a @" + getAnnotationName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") of type (" + variableMemberAccessor.getType()
                    + ") which is not the type of elements (" + sourceVariableDescriptor.getElementType()
                    + ") of the source list variable (" + sourceVariableDescriptor + ").");
        }
        sourceVariableDescriptor.registerSinkVariableDescriptor(this);
    }

    @Override
    public List<VariableDescriptor<Solution_>> getSourceVariableDescriptorList() {
        return Collections.singletonList(sourceVariableDescriptor);
    }

    @Override
    public Demand<?> getProvidedDemand() {
        throw new UnsupportedOperationException(
                "Not implemented because no subsystems demand previous or next shadow variables.");
    }
}
