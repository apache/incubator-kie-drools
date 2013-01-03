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

package org.drools.planner.core.domain.variable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.core.domain.common.PropertyAccessor;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.solution.Solution;

public class PlanningEntityPropertyPlanningValueRangeDescriptor extends AbstractPlanningValueRangeDescriptor {

    private PropertyAccessor rangePropertyAccessor;

    public PlanningEntityPropertyPlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor,
            ValueRange valueRangeAnnotation) {
        super(variableDescriptor);
        validate(valueRangeAnnotation);
        processValueRangeAnnotation(valueRangeAnnotation);
    }

    private void validate(ValueRange valueRangeAnnotation) {
        if (!valueRangeAnnotation.solutionProperty().equals("")) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") of type (" + valueRangeAnnotation.type() + ") with a non-empty solutionProperty ("
                    + valueRangeAnnotation.solutionProperty() + ").");
        }
        if (valueRangeAnnotation.planningEntityProperty().equals("")) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") of type (" + valueRangeAnnotation.type() + ") with an empty planningEntityProperty ("
                    + valueRangeAnnotation.planningEntityProperty() + ").");
        }
    }

    private void processValueRangeAnnotation(ValueRange valueRangeAnnotation) {
        processPlanningEntityProperty(valueRangeAnnotation);
        processExcludeUninitializedPlanningEntity(valueRangeAnnotation);
    }

    private void processPlanningEntityProperty(ValueRange valueRangeAnnotation) {
        String planningEntityProperty = valueRangeAnnotation.planningEntityProperty();
        PlanningEntityDescriptor planningEntityDescriptor = variableDescriptor.getPlanningEntityDescriptor();
        rangePropertyAccessor = planningEntityDescriptor.getPropertyAccessor(planningEntityProperty);
        if (rangePropertyAccessor == null) {
            String exceptionMessage = "The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
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
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") that refers to a planningEntityProperty (" + planningEntityProperty
                    + ") that does not return a Collection.");
        }
    }

    public Collection<?> extractAllValues(Solution solution) {
        Set<Object> valueSet = new LinkedHashSet<Object>();
        for (Object entity : variableDescriptor.getPlanningEntityDescriptor().extractEntities(solution)) {
            valueSet.addAll(extractValues(solution, entity));
        }
        return valueSet;
    }

    public Collection<?> extractValues(Solution solution, Object planningEntity) {
        Collection<?> values = extractValuesWithoutFiltering(planningEntity);
        return applyFiltering(values);
    }

    private Collection<?> extractValuesWithoutFiltering(Object planningEntity) {
        return (Collection<?>) rangePropertyAccessor.executeGetter(planningEntity);
    }

    public long getProblemScale(Solution solution, Object planningEntity) {
        return extractValuesWithoutFiltering(planningEntity).size();
    }

}
