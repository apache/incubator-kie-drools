package org.drools.planner.core.heuristic.selector.entity.decorator;

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionFilter;
import org.drools.planner.core.score.director.ScoreDirector;

public class NullValueReinitializeVariableEntityFilter implements SelectionFilter<Object> {

    private final PlanningVariableDescriptor variableDescriptor;

    public NullValueReinitializeVariableEntityFilter(PlanningVariableDescriptor variableDescriptor) {
        this.variableDescriptor = variableDescriptor;
    }

    public boolean accept(ScoreDirector scoreDirector, Object selection) {
        Object value = variableDescriptor.getValue(selection);
        return value == null;
    }

}
