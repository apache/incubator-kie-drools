/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;

public abstract class AbstractPlanningValueRangeDescriptor implements PlanningValueRangeDescriptor {

    protected PlanningVariableDescriptor variableDescriptor;
    // TODO rename excludeUninitializedPlanningEntity: only applies to the uninitializedVariable
    protected boolean excludeUninitializedPlanningEntity; // TODO make this compatible with PlanningVariable.reinitializeVariableEntityFilter and use a SelectionFilter

    public AbstractPlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor) {
        this.variableDescriptor = variableDescriptor;
    }

    public PlanningVariableDescriptor getVariableDescriptor() {
        return variableDescriptor;
    }

    protected void processExcludeUninitializedPlanningEntity(ValueRange valueRangeAnnotation) {
        excludeUninitializedPlanningEntity = valueRangeAnnotation.excludeUninitializedPlanningEntity();
        if (excludeUninitializedPlanningEntity) {
            Class<?> variablePropertyType = variableDescriptor.getVariablePropertyType();
            Set<Class<?>> entityClassSet = variableDescriptor.getPlanningEntityDescriptor().getSolutionDescriptor()
                    .getPlanningEntityClassSet();
            boolean assignableFrom = false;
            for (Class<?> entityClass : entityClassSet) {
                if (variablePropertyType.isAssignableFrom(entityClass)) {
                    assignableFrom = true;
                    break;
                }
            }
            if (!assignableFrom) {
                throw new IllegalArgumentException("The planningEntityClass ("
                        + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                        + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                        + ") with excludeUninitializedPlanningEntity (true), but there is no planning entity class"
                        + " that extends the variablePropertyType (" + variablePropertyType + ").");
            }
        }
    }

    protected Collection<?> applyFiltering(Collection<?> values) {
        if (!excludeUninitializedPlanningEntity) {
            return values;
        }
        // TODO HACK remove me and replace by SelectionFilter
        Collection<Object> filteredValues = new ArrayList<Object>(values.size());
        for (Object value : values) {
            if (value.getClass().isAnnotationPresent(PlanningEntity.class)) {
                PlanningEntityDescriptor entityDescriptor = variableDescriptor.getPlanningEntityDescriptor()
                        .getSolutionDescriptor().getPlanningEntityDescriptor(value.getClass());
                if (entityDescriptor == null) {
                    throw new IllegalArgumentException("The planningEntityClass ("
                            + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                            + ") has a PlanningVariable annotated property ("
                            + variableDescriptor.getVariableName()
                            + ") with excludeUninitializedPlanningEntity (true),"
                            + " but a planning value class (" + value.getClass()
                            + ") annotated with PlanningEntity is a non configured as a planning entity.");
                }
                if (variableDescriptor.isInitialized(value)) {
                    filteredValues.add(value);
                }
            }
        }
        return filteredValues;
    }

    public boolean isValuesCacheable() {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getVariableName() + ")";
    }

}
