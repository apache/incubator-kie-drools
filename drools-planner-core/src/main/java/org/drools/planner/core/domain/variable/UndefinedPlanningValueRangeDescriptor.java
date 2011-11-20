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

import java.util.Collection;
import java.util.Iterator;

import org.drools.planner.api.domain.variable.ValueRangeUndefined;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.director.SolutionDirector;

public class UndefinedPlanningValueRangeDescriptor extends AbstractPlanningValueRangeDescriptor {

    public UndefinedPlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor,
            ValueRangeUndefined valueRangeUndefined) {
        super(variableDescriptor);
    }

    public Collection<?> extractValues(Solution solution, Object planningEntity) {
        throw new IllegalStateException("The planningEntityClass ("
                + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariablePropertyName()
                + ") which uses a @ValueRangeUndefined.");
    }

    public long getProblemScale(Solution solution, Object planningEntity) {
        // Return 1, so the problem scale becomes the planning entity count. This is not perfect.
        return 1L;
    }

}
