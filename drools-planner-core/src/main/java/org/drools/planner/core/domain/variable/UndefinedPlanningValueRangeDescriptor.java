package org.drools.planner.core.domain.variable;

import java.util.Collection;
import java.util.Iterator;

import org.drools.planner.api.domain.variable.ValueRangeUndefined;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.solution.director.SolutionDirector;

public class UndefinedPlanningValueRangeDescriptor extends AbstractPlanningValueRangeDescriptor {

    public UndefinedPlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor,
            ValueRangeUndefined valueRangeUndefined) {
        super(variableDescriptor);
    }

    public Collection<?> extractValues(SolutionDirector solutionDirector, Object planningEntity) {
        throw new IllegalStateException("The planningEntityClass ("
                + variableDescriptor.getPlanningEntityDescriptor().getPlanningEntityClass()
                + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariablePropertyName()
                + ") which uses a @ValueRangeUndefined.");
    }

}
