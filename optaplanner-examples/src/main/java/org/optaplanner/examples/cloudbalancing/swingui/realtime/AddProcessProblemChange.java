package org.optaplanner.examples.cloudbalancing.swingui.realtime;

import java.util.function.LongFunction;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class AddProcessProblemChange implements ProblemChange<CloudBalance> {

    private final LongFunction<CloudProcess> generator;

    public AddProcessProblemChange(LongFunction<CloudProcess> generator) {
        this.generator = generator;
    }

    @Override
    public void doChange(CloudBalance cloudBalance, ProblemChangeDirector problemChangeDirector) {
        // Set a unique id on the new process
        long nextProcessId = 0L;
        for (CloudProcess otherProcess : cloudBalance.getProcessList()) {
            if (nextProcessId <= otherProcess.getId()) {
                nextProcessId = otherProcess.getId() + 1L;
            }
        }
        CloudProcess process = generator.apply(nextProcessId);
        // A SolutionCloner clones planning entity lists (such as processList), so no need to clone the processList here
        // Add the planning entity itself
        problemChangeDirector.addEntity(process, cloudBalance.getProcessList()::add);
    }

}
