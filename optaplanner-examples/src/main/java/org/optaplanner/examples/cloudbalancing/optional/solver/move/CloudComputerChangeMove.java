package org.optaplanner.examples.cloudbalancing.optional.solver.move;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class CloudComputerChangeMove extends AbstractMove<CloudBalance> {

    private CloudProcess cloudProcess;
    private CloudComputer toCloudComputer;

    public CloudComputerChangeMove(CloudProcess cloudProcess, CloudComputer toCloudComputer) {
        this.cloudProcess = cloudProcess;
        this.toCloudComputer = toCloudComputer;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<CloudBalance> scoreDirector) {
        return !Objects.equals(cloudProcess.getComputer(), toCloudComputer);
    }

    @Override
    public CloudComputerChangeMove createUndoMove(ScoreDirector<CloudBalance> scoreDirector) {
        return new CloudComputerChangeMove(cloudProcess, cloudProcess.getComputer());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<CloudBalance> scoreDirector) {
        scoreDirector.beforeVariableChanged(cloudProcess, "computer");
        cloudProcess.setComputer(toCloudComputer);
        scoreDirector.afterVariableChanged(cloudProcess, "computer");
    }

    @Override
    public CloudComputerChangeMove rebase(ScoreDirector<CloudBalance> destinationScoreDirector) {
        return new CloudComputerChangeMove(destinationScoreDirector.lookUpWorkingObject(cloudProcess),
                destinationScoreDirector.lookUpWorkingObject(toCloudComputer));
    }

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + CloudProcess.class.getSimpleName() + ".computer)";
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(cloudProcess);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toCloudComputer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CloudComputerChangeMove other = (CloudComputerChangeMove) o;
        return Objects.equals(cloudProcess, other.cloudProcess) &&
                Objects.equals(toCloudComputer, other.toCloudComputer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cloudProcess, toCloudComputer);
    }

    @Override
    public String toString() {
        return cloudProcess + " {" + cloudProcess.getComputer() + " -> " + toCloudComputer + "}";
    }

}
