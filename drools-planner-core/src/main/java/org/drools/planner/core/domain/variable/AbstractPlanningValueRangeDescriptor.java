package org.drools.planner.core.domain.variable;

public abstract class AbstractPlanningValueRangeDescriptor implements PlanningValueRangeDescriptor {

    protected PlanningVariableDescriptor variableDescriptor;

    public AbstractPlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor) {
        this.variableDescriptor = variableDescriptor;
    }

    public boolean isValuesCacheable() {
        return false;
    }

}
