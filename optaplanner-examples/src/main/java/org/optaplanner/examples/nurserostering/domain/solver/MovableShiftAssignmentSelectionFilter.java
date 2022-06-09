package org.optaplanner.examples.nurserostering.domain.solver;

import org.optaplanner.core.api.domain.entity.PinningFilter;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;

public class MovableShiftAssignmentSelectionFilter implements SelectionFilter<NurseRoster, ShiftAssignment> {

    private final PinningFilter<NurseRoster, ShiftAssignment> pinningFilter =
            new ShiftAssignmentPinningFilter();

    @Override
    public boolean accept(ScoreDirector<NurseRoster> scoreDirector, ShiftAssignment selection) {
        return !pinningFilter.accept(scoreDirector.getWorkingSolution(), selection);
    }

}
