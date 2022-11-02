package org.optaplanner.examples.cloudbalancing.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.cloudbalancing.persistence.CloudBalancingGenerator;
import org.optaplanner.examples.cloudbalancing.swingui.realtime.AddComputerProblemChange;
import org.optaplanner.examples.cloudbalancing.swingui.realtime.AddProcessProblemChange;
import org.optaplanner.examples.cloudbalancing.swingui.realtime.DeleteComputerProblemChange;
import org.optaplanner.examples.cloudbalancing.swingui.realtime.DeleteProcessProblemChange;
import org.optaplanner.examples.common.app.RealTimePlanningTurtleTest;

class CloudBalancingRealTimePlanningTurtleTest extends RealTimePlanningTurtleTest<CloudBalance> {

    private CloudBalancingGenerator generator = new CloudBalancingGenerator(true);

    private List<CloudComputer> existingComputerList;
    private List<CloudProcess> existingProcessList;

    @Override
    protected String createSolverConfigResource() {
        return CloudBalancingApp.SOLVER_CONFIG;
    }

    @Override
    protected CloudBalance readProblem() {
        CloudBalance cloudBalance = generator.createCloudBalance(1200, 4800);
        existingComputerList = new ArrayList<>(cloudBalance.getComputerList());
        existingProcessList = new ArrayList<>(cloudBalance.getProcessList());
        return cloudBalance;
    }

    @Override
    protected ProblemChange<CloudBalance> nextProblemChange(Random random) {
        boolean capacityTooLow = existingComputerList.size() <= 20
                || existingComputerList.size() < existingProcessList.size() / 4;
        boolean capacityTooHigh = existingComputerList.size() > existingProcessList.size() / 2;
        if (random.nextBoolean()) {
            if (capacityTooLow || (!capacityTooHigh && random.nextBoolean())) {
                return new AddComputerProblemChange(expectedId -> {
                    CloudComputer computer = generator.generateComputer(expectedId);
                    existingComputerList.add(computer);
                    return computer;
                });
            } else {
                return new DeleteComputerProblemChange(
                        existingComputerList.remove(random.nextInt(existingComputerList.size())));
            }
        } else {
            if (capacityTooHigh || (!capacityTooLow && random.nextBoolean())) {
                return new AddProcessProblemChange(expectedId -> {
                    CloudProcess process = generator.generateProcess(expectedId);
                    existingProcessList.add(process);
                    return process;
                });
            } else {
                return new DeleteProcessProblemChange(
                        existingProcessList.remove(random.nextInt(existingProcessList.size())));
            }
        }
    }

}
