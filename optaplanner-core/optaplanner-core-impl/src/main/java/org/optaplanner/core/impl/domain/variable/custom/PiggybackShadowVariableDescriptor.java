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

package org.optaplanner.core.impl.domain.variable.custom;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.api.domain.variable.PiggybackShadowVariable;
import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListenerWithSources;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class PiggybackShadowVariableDescriptor<Solution_> extends ShadowVariableDescriptor<Solution_> {

    protected CustomShadowVariableDescriptor<Solution_> shadowVariableDescriptor;

    public PiggybackShadowVariableDescriptor(EntityDescriptor<Solution_> entityDescriptor,
            MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
    }

    @Override
    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        // Do nothing
    }

    @Override
    public void linkVariableDescriptors(DescriptorPolicy descriptorPolicy) {
        linkShadowSources(descriptorPolicy);
    }

    private void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        PiggybackShadowVariable piggybackShadowVariable = variableMemberAccessor.getAnnotation(PiggybackShadowVariable.class);
        EntityDescriptor<Solution_> shadowEntityDescriptor;
        Class<?> shadowEntityClass = piggybackShadowVariable.shadowEntityClass();
        if (shadowEntityClass.equals(PiggybackShadowVariable.NullEntityClass.class)) {
            shadowEntityDescriptor = entityDescriptor;
        } else {
            shadowEntityDescriptor = entityDescriptor.getSolutionDescriptor().findEntityDescriptor(shadowEntityClass);
            if (shadowEntityDescriptor == null) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a @" + PiggybackShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with a shadowEntityClass (" + shadowEntityClass
                        + ") which is not a valid planning entity."
                        + "\nMaybe check the annotations of the class (" + shadowEntityClass + ")."
                        + "\nMaybe add the class (" + shadowEntityClass
                        + ") among planning entities in the solver configuration.");
            }
        }
        String shadowVariableName = piggybackShadowVariable.shadowVariableName();
        VariableDescriptor<Solution_> uncastShadowVariableDescriptor =
                shadowEntityDescriptor.getVariableDescriptor(shadowVariableName);
        if (uncastShadowVariableDescriptor == null) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a @" + PiggybackShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with shadowVariableName (" + shadowVariableName
                    + ") which is not a valid planning variable on entityClass ("
                    + shadowEntityDescriptor.getEntityClass() + ").\n"
                    + shadowEntityDescriptor.buildInvalidVariableNameExceptionMessage(shadowVariableName));
        }
        if (!(uncastShadowVariableDescriptor instanceof CustomShadowVariableDescriptor)) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a @" + PiggybackShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with refVariable (" + uncastShadowVariableDescriptor.getSimpleEntityAndVariableName()
                    + ") that lacks a @" + ShadowVariable.class.getSimpleName() + " annotation.");
        }
        shadowVariableDescriptor = (CustomShadowVariableDescriptor<Solution_>) uncastShadowVariableDescriptor;
        shadowVariableDescriptor.registerSinkVariableDescriptor(this);
    }

    @Override
    public List<VariableDescriptor<Solution_>> getSourceVariableDescriptorList() {
        return Collections.singletonList(shadowVariableDescriptor);
    }

    @Override
    public Collection<Class<? extends AbstractVariableListener>> getVariableListenerClasses() {
        return shadowVariableDescriptor.getVariableListenerClasses();
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Demand<?> getProvidedDemand() {
        throw new UnsupportedOperationException("Custom shadow variable cannot be demanded.");
    }

    @Override
    public boolean hasVariableListener() {
        return false;
    }

    @Override
    public Iterable<VariableListenerWithSources<Solution_>> buildVariableListeners(SupplyManager supplyManager) {
        throw new UnsupportedOperationException("The piggybackShadowVariableDescriptor (" + this
                + ") cannot build a variable listener.");
    }
}
