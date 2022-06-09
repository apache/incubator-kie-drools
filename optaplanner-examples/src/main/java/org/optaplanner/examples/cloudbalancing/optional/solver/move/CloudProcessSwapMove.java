package org.optaplanner.examples.cloudbalancing.optional.solver.move;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class CloudProcessSwapMove extends AbstractMove<CloudBalance> {

    private CloudProcess leftCloudProcess;
    private CloudProcess rightCloudProcess;

    public CloudProcessSwapMove(CloudProcess leftCloudProcess, CloudProcess rightCloudProcess) {
        this.leftCloudProcess = leftCloudProcess;
        this.rightCloudProcess = rightCloudProcess;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<CloudBalance> scoreDirector) {
        return !Objects.equals(leftCloudProcess.getComputer(), rightCloudProcess.getComputer());
    }

    @Override
    public CloudProcessSwapMove createUndoMove(ScoreDirector<CloudBalance> scoreDirector) {
        return new CloudProcessSwapMove(rightCloudProcess, leftCloudProcess);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<CloudBalance> scoreDirector) {
        CloudComputer oldLeftCloudComputer = leftCloudProcess.getComputer();
        CloudComputer oldRightCloudComputer = rightCloudProcess.getComputer();
        scoreDirector.beforeVariableChanged(leftCloudProcess, "computer");
        leftCloudProcess.setComputer(oldRightCloudComputer);
        scoreDirector.afterVariableChanged(leftCloudProcess, "computer");
        scoreDirector.beforeVariableChanged(rightCloudProcess, "computer");
        rightCloudProcess.setComputer(oldLeftCloudComputer);
        scoreDirector.afterVariableChanged(rightCloudProcess, "computer");
    }

    @Override
    public CloudProcessSwapMove rebase(ScoreDirector<CloudBalance> destinationScoreDirector) {
        return new CloudProcessSwapMove(destinationScoreDirector.lookUpWorkingObject(leftCloudProcess),
                destinationScoreDirector.lookUpWorkingObject(rightCloudProcess));
    }

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + CloudProcess.class.getSimpleName() + ".computer)";
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftCloudProcess, rightCloudProcess);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(leftCloudProcess.getComputer(), rightCloudProcess.getComputer());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CloudProcessSwapMove other = (CloudProcessSwapMove) o;
        return Objects.equals(leftCloudProcess, other.leftCloudProcess) &&
                Objects.equals(rightCloudProcess, other.rightCloudProcess);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftCloudProcess, rightCloudProcess);
    }

    @Override
    public String toString() {
        return leftCloudProcess + " {" + leftCloudProcess.getComputer() + "} <-> "
                + rightCloudProcess + " {" + rightCloudProcess.getComputer() + "}";
    }

}
