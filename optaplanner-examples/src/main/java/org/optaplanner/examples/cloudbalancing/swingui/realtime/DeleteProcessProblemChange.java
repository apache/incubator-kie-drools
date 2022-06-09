package org.optaplanner.examples.cloudbalancing.swingui.realtime;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class DeleteProcessProblemChange implements ProblemChange<CloudBalance> {

    private final CloudProcess process;

    public DeleteProcessProblemChange(CloudProcess process) {
        this.process = process;
    }

    @Override
    public void doChange(CloudBalance cloudBalance, ProblemChangeDirector problemChangeDirector) {
        // A SolutionCloner clones planning entity lists (such as processList), so no need to clone the processList here
        CloudProcess workingProcess = problemChangeDirector.lookUpWorkingObjectOrFail(process);
        if (workingProcess == null) {
            throw new IllegalStateException("A process " + process + " does not exist. Maybe it has been already deleted.");
        }
        // Remove the planning entity itself
        problemChangeDirector.removeEntity(workingProcess, cloudBalance.getProcessList()::remove);
    }

}
