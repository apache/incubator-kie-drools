package org.optaplanner.examples.cloudbalancing.swingui.realtime;

import java.util.ArrayList;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class DeleteComputerProblemChange implements ProblemChange<CloudBalance> {

    private final CloudComputer computer;

    public DeleteComputerProblemChange(CloudComputer computer) {
        this.computer = computer;
    }

    @Override
    public void doChange(CloudBalance cloudBalance, ProblemChangeDirector problemChangeDirector) {
        problemChangeDirector.lookUpWorkingObject(computer)
                .ifPresentOrElse(workingComputer -> {
                    // First remove the problem fact from all planning entities that use it
                    for (CloudProcess process : cloudBalance.getProcessList()) {
                        if (process.getComputer() == workingComputer) {
                            problemChangeDirector.changeVariable(process, "computer",
                                    workingProcess -> workingProcess.setComputer(null));
                        }
                    }
                    // A SolutionCloner does not clone problem fact lists (such as computerList)
                    // Shallow clone the computerList so only workingSolution is affected, not bestSolution or guiSolution
                    ArrayList<CloudComputer> computerList = new ArrayList<>(cloudBalance.getComputerList());
                    cloudBalance.setComputerList(computerList);
                    // Remove the problem fact itself
                    problemChangeDirector.removeProblemFact(workingComputer, computerList::remove);
                }, () -> {
                    // The computer has already been deleted (the UI asked to changed the same computer twice).
                });
    }

}
