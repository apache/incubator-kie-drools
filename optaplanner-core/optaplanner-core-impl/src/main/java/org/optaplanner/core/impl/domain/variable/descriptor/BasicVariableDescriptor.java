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

package org.optaplanner.core.impl.domain.variable.descriptor;

import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;

public class BasicVariableDescriptor<Solution_> extends GenuineVariableDescriptor<Solution_> {

    private boolean chained;
    private boolean nullable;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public BasicVariableDescriptor(EntityDescriptor<Solution_> entityDescriptor, MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    protected void processPropertyAnnotations(DescriptorPolicy descriptorPolicy) {
        PlanningVariable planningVariableAnnotation = variableMemberAccessor.getAnnotation(PlanningVariable.class);
        processNullable(planningVariableAnnotation);
        processChained(planningVariableAnnotation);
        processValueRangeRefs(descriptorPolicy, planningVariableAnnotation.valueRangeProviderRefs());
        processStrength(planningVariableAnnotation.strengthComparatorClass(),
                planningVariableAnnotation.strengthWeightFactoryClass());
    }

    private void processNullable(PlanningVariable planningVariableAnnotation) {
        nullable = planningVariableAnnotation.nullable();
        if (nullable && variableMemberAccessor.getType().isPrimitive()) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a @" + PlanningVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with nullable (" + nullable + "), which is not compatible with the primitive propertyType ("
                    + variableMemberAccessor.getType() + ").");
        }
    }

    private void processChained(PlanningVariable planningVariableAnnotation) {
        chained = planningVariableAnnotation.graphType() == PlanningVariableGraphType.CHAINED;
        if (!chained) {
            return;
        }
        if (!acceptsValueType(entityDescriptor.getEntityClass())) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a @" + PlanningVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with chained (" + chained + ") and propertyType (" + getVariablePropertyType()
                    + ") which is not a superclass/interface of or the same as the entityClass ("
                    + entityDescriptor.getEntityClass() + ").\n"
                    + "If an entity's chained planning variable cannot point to another entity of the same class,"
                    + " then it is impossible to make a chain longer than 1 entity and therefore chaining is useless.");
        }
        if (nullable) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a @" + PlanningVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with chained (" + chained + "), which is not compatible with nullable (" + nullable + ").");
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isListVariable() {
        return false;
    }

    @Override
    public boolean isChained() {
        return chained;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public boolean acceptsValueType(Class<?> valueType) {
        return getVariablePropertyType().isAssignableFrom(valueType);
    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    @Override
    public boolean isInitialized(Object entity) {
        if (isNullable()) {
            return true;
        }
        Object variable = getValue(entity);
        return variable != null;
    }
}
