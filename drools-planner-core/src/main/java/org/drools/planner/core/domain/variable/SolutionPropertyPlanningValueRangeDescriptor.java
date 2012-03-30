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

package org.drools.planner.core.domain.variable;

import java.beans.PropertyDescriptor;
import java.util.Collection;

import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.core.domain.common.DescriptorUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.solution.Solution;

public class SolutionPropertyPlanningValueRangeDescriptor extends AbstractPlanningValueRangeDescriptor {

    private PropertyDescriptor rangePropertyDescriptor;

    public SolutionPropertyPlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor,
            ValueRange valueRangeAnnotation) {
        super(variableDescriptor);
        validate(valueRangeAnnotation);
        processValueRangeAnnotation(valueRangeAnnotation);
    }

    private void validate(ValueRange valueRangeAnnotation) {
        if (valueRangeAnnotation.solutionProperty().equals("")) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") of type (" + valueRangeAnnotation.type() + ") with an empty solutionProperty ("
                    + valueRangeAnnotation.solutionProperty() + ").");
        }
        if (!valueRangeAnnotation.planningEntityProperty().equals("")) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") of type (" + valueRangeAnnotation.type() + ") with a non-empty planningEntityProperty ("
                    + valueRangeAnnotation.planningEntityProperty() + ").");
        }
    }

    private void processValueRangeAnnotation(ValueRange valueRangeAnnotation) {
        processSolutionProperty(valueRangeAnnotation);
        processExcludeUninitializedPlanningEntity(valueRangeAnnotation);
    }

    private void processSolutionProperty(ValueRange valueRangeAnnotation) {
        String solutionProperty = valueRangeAnnotation.solutionProperty();
        PlanningEntityDescriptor planningEntityDescriptor = variableDescriptor.getPlanningEntityDescriptor();
        rangePropertyDescriptor = planningEntityDescriptor.getSolutionDescriptor()
                .getPropertyDescriptor(solutionProperty);
        if (rangePropertyDescriptor == null) {
            String exceptionMessage = "The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") that refers to a solutionClass ("
                    + planningEntityDescriptor.getSolutionDescriptor().getSolutionClass()
                    + ") solutionProperty (" + solutionProperty
                    + ") that does not exist.";
            if (solutionProperty.length() >= 2 && Character.isUpperCase(solutionProperty.charAt(1))) {
                String correctedSolutionProperty = solutionProperty.substring(0, 1).toUpperCase()
                        + solutionProperty.substring(1);
                exceptionMessage += " But it probably needs to be correctedSolutionProperty ("
                        + correctedSolutionProperty + ") instead because the JavaBeans spec states" +
                        " the first letter should be a upper case if the second is upper case.";
            }
            throw new IllegalArgumentException(exceptionMessage);
        }
        if (!Collection.class.isAssignableFrom(rangePropertyDescriptor.getPropertyType())) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") that refers to a solutionClass ("
                    + planningEntityDescriptor.getSolutionDescriptor().getSolutionClass()
                    + ") solutionProperty (" + solutionProperty
                    + ") that does not return a Collection.");
        }
    }

    public Collection<?> extractAllValues(Solution solution) {
        Collection<?> values = extractValuesWithoutFiltering(solution);
        return applyFiltering(values);
    }

    public Collection<?> extractValues(Solution solution, Object planningEntity) {
        return extractAllValues(solution);
    }

    private Collection<?> extractValuesWithoutFiltering(Solution solution) {
        return (Collection<?>) DescriptorUtils.executeGetter(rangePropertyDescriptor, solution);
    }

    public long getProblemScale(Solution solution, Object planningEntity) {
        return extractValuesWithoutFiltering(solution).size();
    }

    @Override
    public boolean isValuesCacheable() {
        return !excludeUninitializedPlanningEntity;
    }

}
