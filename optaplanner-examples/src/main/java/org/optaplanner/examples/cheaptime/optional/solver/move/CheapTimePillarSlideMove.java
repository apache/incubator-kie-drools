package org.optaplanner.examples.cheaptime.optional.solver.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;

public class CheapTimePillarSlideMove extends AbstractMove<CheapTimeSolution> {

    private final List<TaskAssignment> pillar;
    private final int startPeriodDiff;

    public CheapTimePillarSlideMove(List<TaskAssignment> pillar, int startPeriodDiff) {
        this.pillar = pillar;
        this.startPeriodDiff = startPeriodDiff;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<CheapTimeSolution> scoreDirector) {
        return true;
    }

    @Override
    public CheapTimePillarSlideMove createUndoMove(ScoreDirector<CheapTimeSolution> scoreDirector) {
        return new CheapTimePillarSlideMove(pillar, -startPeriodDiff);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<CheapTimeSolution> scoreDirector) {
        for (TaskAssignment taskAssignment : pillar) {
            scoreDirector.beforeVariableChanged(taskAssignment, "startPeriod");
            taskAssignment.setStartPeriod(taskAssignment.getStartPeriod() + startPeriodDiff);
            scoreDirector.afterVariableChanged(taskAssignment, "startPeriod");
        }
    }

    @Override
    public CheapTimePillarSlideMove rebase(ScoreDirector<CheapTimeSolution> destinationScoreDirector) {
        return new CheapTimePillarSlideMove(rebaseList(pillar, destinationScoreDirector), startPeriodDiff);
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(pillar);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        // Presumes this method is always called after the move is done.
        List<Integer> startPeriodList = new ArrayList<>(pillar.size());
        for (TaskAssignment taskAssignment : pillar) {
            startPeriodList.add(taskAssignment.getStartPeriod());
        }
        return Collections.singletonList(startPeriodList);
    }

    @Override
    public String toString() {
        return pillar + " {" + (startPeriodDiff < 0 ? startPeriodDiff : "+" + startPeriodDiff) + "}";
    }

}
