package org.optaplanner.examples.cloudbalancing.score;

import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class CloudBalancingConstraintProviderTest
        extends AbstractConstraintProviderTest<CloudBalancingConstraintProvider, CloudBalance> {

    @ConstraintProviderTest
    void requiredCpuPowerTotal(ConstraintVerifier<CloudBalancingConstraintProvider, CloudBalance> constraintVerifier) {
        CloudComputer computer1 = new CloudComputer(1, 1, 1, 1, 2);
        CloudComputer computer2 = new CloudComputer(2, 2, 2, 2, 4);
        CloudProcess unassignedProcess = new CloudProcess(0, 1, 1, 1);
        // Total = 2, available = 1.
        CloudProcess process1 = new CloudProcess(1, 1, 1, 1);
        process1.setComputer(computer1);
        CloudProcess process2 = new CloudProcess(2, 1, 1, 1);
        process2.setComputer(computer1);
        // Total = 1, available = 2.
        CloudProcess process3 = new CloudProcess(3, 1, 1, 1);
        process3.setComputer(computer2);

        constraintVerifier.verifyThat(CloudBalancingConstraintProvider::requiredCpuPowerTotal)
                .given(unassignedProcess, process1, process2, process3)
                .penalizesBy(1); // Only the first computer.
    }

    @ConstraintProviderTest
    void requiredMemoryTotal(ConstraintVerifier<CloudBalancingConstraintProvider, CloudBalance> constraintVerifier) {
        CloudComputer computer1 = new CloudComputer(1, 1, 1, 1, 2);
        CloudComputer computer2 = new CloudComputer(2, 2, 2, 2, 4);
        CloudProcess unassignedProcess = new CloudProcess(0, 1, 1, 1);
        // Total = 2, available = 1.
        CloudProcess process1 = new CloudProcess(1, 1, 1, 1);
        process1.setComputer(computer1);
        CloudProcess process2 = new CloudProcess(2, 1, 1, 1);
        process2.setComputer(computer1);
        // Total = 1, available = 2.
        CloudProcess process3 = new CloudProcess(3, 1, 1, 1);
        process3.setComputer(computer2);

        constraintVerifier.verifyThat(CloudBalancingConstraintProvider::requiredMemoryTotal)
                .given(unassignedProcess, process1, process2, process3)
                .penalizesBy(1); // Only the first computer.
    }

    @ConstraintProviderTest
    void requiredNetworkBandwidthTotal(ConstraintVerifier<CloudBalancingConstraintProvider, CloudBalance> constraintVerifier) {
        CloudComputer computer1 = new CloudComputer(1, 1, 1, 1, 2);
        CloudComputer computer2 = new CloudComputer(2, 2, 2, 2, 4);
        CloudProcess unassignedProcess = new CloudProcess(0, 1, 1, 1);
        // Total = 2, available = 1.
        CloudProcess process1 = new CloudProcess(1, 1, 1, 1);
        process1.setComputer(computer1);
        CloudProcess process2 = new CloudProcess(2, 1, 1, 1);
        process2.setComputer(computer1);
        // Total = 1, available = 2.
        CloudProcess process3 = new CloudProcess(3, 1, 1, 1);
        process3.setComputer(computer2);

        constraintVerifier.verifyThat(CloudBalancingConstraintProvider::requiredNetworkBandwidthTotal)
                .given(unassignedProcess, process1, process2, process3)
                .penalizesBy(1); // Only the first computer.
    }

    @ConstraintProviderTest
    void computerCost(ConstraintVerifier<CloudBalancingConstraintProvider, CloudBalance> constraintVerifier) {
        CloudComputer computer1 = new CloudComputer(1, 1, 1, 1, 2);
        CloudComputer computer2 = new CloudComputer(2, 2, 2, 2, 4);
        CloudProcess unassignedProcess = new CloudProcess(0, 1, 1, 1);
        CloudProcess process = new CloudProcess(1, 1, 1, 1);
        process.setComputer(computer1);

        constraintVerifier.verifyThat(CloudBalancingConstraintProvider::computerCost)
                .given(computer1, computer2, unassignedProcess, process)
                .penalizesBy(2);
    }

    @Override
    protected ConstraintVerifier<CloudBalancingConstraintProvider, CloudBalance> createConstraintVerifier() {
        return ConstraintVerifier.build(new CloudBalancingConstraintProvider(), CloudBalance.class, CloudProcess.class);

    }
}
