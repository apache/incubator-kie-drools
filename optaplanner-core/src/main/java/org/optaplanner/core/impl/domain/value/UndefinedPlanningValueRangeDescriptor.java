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

import java.util.Collection;

import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.solution.Solution;

public class UndefinedPlanningValueRangeDescriptor extends AbstractPlanningValueRangeDescriptor {

    public UndefinedPlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor,
            ValueRange valueRangeAnnotation) {
        super(variableDescriptor);
        validate(valueRangeAnnotation);
    }

    private void validate(ValueRange valueRangeAnnotation) {
        if (!valueRangeAnnotation.solutionProperty().equals("")) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") of type (" + valueRangeAnnotation.type() + ") with a non-empty solutionProperty ("
                    + valueRangeAnnotation.solutionProperty() + ").");
        }
        if (!valueRangeAnnotation.planningEntityProperty().equals("")) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") of type (" + valueRangeAnnotation.type() + ") with a non-empty planningEntityProperty ("
                    + valueRangeAnnotation.planningEntityProperty() + ").");
        }
        if (valueRangeAnnotation.excludeUninitializedPlanningEntity()) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                    + ") of type (" + valueRangeAnnotation.type() + ") with excludeUninitializedPlanningEntity ("
                    + valueRangeAnnotation.excludeUninitializedPlanningEntity() + ").");
        }
    }

    public boolean isEntityDependent() {
        throw new IllegalStateException("The planningEntityClass ("
                + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                + ") which uses a @ValueRangeUndefined.");
    }

    public Collection<?> extractAllValuesWithFiltering(Solution solution) {
        throw new IllegalStateException("The planningEntityClass ("
                + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                + ") which uses a @ValueRangeUndefined.");
    }

    public Collection<?> extractValuesWithFiltering(Solution solution, Object planningEntity) {
        throw new IllegalStateException("The planningEntityClass ("
                + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariableName()
                + ") which uses a @ValueRangeUndefined.");
    }

    public long getProblemScale(Solution solution, Object planningEntity) {
        // Return 1, so the problem scale becomes the planning entity count. This is not perfect.
        return 1L;
    }

}
