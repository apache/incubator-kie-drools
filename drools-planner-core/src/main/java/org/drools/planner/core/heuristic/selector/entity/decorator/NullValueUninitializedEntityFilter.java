package org.drools.planner.core.heuristic.selector.entity.decorator;

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionFilter;
import org.drools.planner.core.score.director.ScoreDirector;

public class NullValueUninitializedEntityFilter implements SelectionFilter<Object> {

    private final PlanningVariableDescriptor planningVariableDescriptor;

    public NullValueUninitializedEntityFilter(PlanningVariableDescriptor planningVariableDescriptor) {
        this.planningVariableDescriptor = planningVariableDescriptor;
    }

    public boolean accept(ScoreDirector scoreDirector, Object selection) {
        Object value = planningVariableDescriptor.getValue(selection);
        return value == null;
    }

}
