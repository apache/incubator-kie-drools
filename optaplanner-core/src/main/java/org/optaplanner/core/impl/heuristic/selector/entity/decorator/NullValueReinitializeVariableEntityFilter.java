package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

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
