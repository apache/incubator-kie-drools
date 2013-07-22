/*
 * Copyright 2011 JBoss Inc
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

package org.optaplanner.core.impl.domain.value;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.impl.domain.common.PropertyAccessor;
import org.optaplanner.core.impl.domain.common.ReflectionPropertyAccessor;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.solution.Solution;

public class FromEntityPropertyPlanningValueRangeDescriptor extends AbstractPlanningValueRangeDescriptor {

    private PropertyAccessor rangePropertyAccessor;

    public FromEntityPropertyPlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor,
            ValueRange valueRangeAnnotation) {
        super(variableDescriptor);
        validate(valueRangeAnnotation);
        processValueRangeAnnotation(valueRangeAnnotation);
    }

    private void validate(ValueRange valueRangeAnnotation) {
        if (!valueRangeAnnotation.solutionProperty().equals("")) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + variableDescriptor.getEntityDescriptor().getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") of type (" + valueRangeAnnotation.type() + ") with a non-empty solutionProperty ("
                    + valueRangeAnnotation.solutionProperty() + ").");
        }
        if (valueRangeAnnotation.planningEntityProperty().equals("")) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + variableDescriptor.getEntityDescriptor().getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") of type (" + valueRangeAnnotation.type() + ") with an empty planningEntityProperty ("
                    + valueRangeAnnotation.planningEntityProperty() + ").");
        }
    }

    private void processValueRangeAnnotation(ValueRange valueRangeAnnotation) {
        processPlanningEntityProperty(valueRangeAnnotation);
    }

    private void processPlanningEntityProperty(ValueRange valueRangeAnnotation) {
        String planningEntityProperty = valueRangeAnnotation.planningEntityProperty();
        PlanningEntityDescriptor entityDescriptor = variableDescriptor.getEntityDescriptor();
        rangePropertyAccessor = new ReflectionPropertyAccessor(
                entityDescriptor.getPropertyDescriptor(planningEntityProperty));
        if (rangePropertyAccessor == null) {
            String exceptionMessage = "The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") that refers to a planningEntityProperty (" + planningEntityProperty
                    + ") that does not exist.";
            if (planningEntityProperty.length() >= 2 && Character.isUpperCase(planningEntityProperty.charAt(1))) {
                String correctedPlanningEntityProperty = planningEntityProperty.substring(0, 1).toUpperCase()
                        + planningEntityProperty.substring(1);
                exceptionMessage += " But it probably needs to be correctedPlanningEntityProperty ("
                        + correctedPlanningEntityProperty + ") instead because the JavaBeans spec states" +
                        " the first letter should be a upper case if the second is upper case.";
            }
            throw new IllegalArgumentException(exceptionMessage);
        }
        if (!Collection.class.isAssignableFrom(rangePropertyAccessor.getPropertyType())) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") that refers to a planningEntityProperty (" + planningEntityProperty
                    + ") that does not return a Collection.");
        }
    }

    public boolean isEntityDependent() {
        return true;
    }

    public Collection<?> extractAllValues(Solution solution) {
        Set<Object> valueSet = new LinkedHashSet<Object>();
        for (Object entity : variableDescriptor.getEntityDescriptor().extractEntities(solution)) {
            valueSet.addAll(extractValues(solution, entity));
        }
        return valueSet;
    }

    public Collection<?> extractValues(Solution solution, Object entity) {
        return extractValuesFromEntity(entity);
    }

    public Collection<?> extractValuesFromEntity(Object entity) {
        return (Collection<?>) rangePropertyAccessor.executeGetter(entity);
    }

    public long getValueCount(Solution solution, Object entity) {
        return extractValues(solution, entity).size();
    }

}
