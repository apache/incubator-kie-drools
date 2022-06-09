package org.optaplanner.examples.cloudbalancing.optional.score;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.cloudbalancing.app.CloudBalancingApp;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.test.impl.score.buildin.hardsoft.HardSoftScoreVerifier;

class CloudBalancingScoreConstraintTest {

    private HardSoftScoreVerifier<CloudBalance> scoreVerifier = new HardSoftScoreVerifier<>(
            SolverFactory.createFromXmlResource(CloudBalancingApp.SOLVER_CONFIG));

    @Test
    void requiredCpuPowerTotal() {
        CloudComputer c1 = new CloudComputer(1L, 1000, 1, 1, 1);
        CloudComputer c2 = new CloudComputer(2L, 200, 1, 1, 1);
        CloudComputer c3 = new CloudComputer(3L, 30, 1, 1, 1);
        CloudProcess p1 = new CloudProcess(1L, 700, 5, 5);
        CloudProcess p2 = new CloudProcess(2L, 70, 5, 5);
        CloudProcess p3 = new CloudProcess(3L, 7, 5, 5);
        CloudBalance solution = new CloudBalance(0L,
                Arrays.asList(c1, c2, c3),
                Arrays.asList(p1, p2, p3));
        scoreVerifier.assertHardWeight("requiredCpuPowerTotal", 0, solution);
        p1.setComputer(c1);
        p2.setComputer(c1);
        scoreVerifier.assertHardWeight("requiredCpuPowerTotal", 0, solution);
        p1.setComputer(c2);
        p2.setComputer(c2);
        scoreVerifier.assertHardWeight("requiredCpuPowerTotal", -570, solution);
        p3.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredCpuPowerTotal", -570, solution);
        p2.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredCpuPowerTotal", -547, solution);
    }

    @Test
    void requiredMemoryTotal() {
        CloudComputer c1 = new CloudComputer(1L, 1, 1000, 1, 1);
        CloudComputer c2 = new CloudComputer(2L, 1, 200, 1, 1);
        CloudComputer c3 = new CloudComputer(3L, 1, 30, 1, 1);
        CloudProcess p1 = new CloudProcess(1L, 5, 700, 5);
        CloudProcess p2 = new CloudProcess(2L, 5, 70, 5);
        CloudProcess p3 = new CloudProcess(3L, 5, 7, 5);
        CloudBalance solution = new CloudBalance(0L,
                Arrays.asList(c1, c2, c3),
                Arrays.asList(p1, p2, p3));
        scoreVerifier.assertHardWeight("requiredMemoryTotal", 0, solution);
        p1.setComputer(c1);
        p2.setComputer(c1);
        scoreVerifier.assertHardWeight("requiredMemoryTotal", 0, solution);
        p1.setComputer(c2);
        p2.setComputer(c2);
        scoreVerifier.assertHardWeight("requiredMemoryTotal", -570, solution);
        p3.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredMemoryTotal", -570, solution);
        p2.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredMemoryTotal", -547, solution);
    }

    @Test
    void requiredNetworkBandwidthTotal() {
        CloudComputer c1 = new CloudComputer(1L, 1, 1, 1000, 1);
        CloudComputer c2 = new CloudComputer(2L, 1, 1, 200, 1);
        CloudComputer c3 = new CloudComputer(3L, 1, 1, 30, 1);
        CloudProcess p1 = new CloudProcess(1L, 5, 5, 700);
        CloudProcess p2 = new CloudProcess(2L, 5, 5, 70);
        CloudProcess p3 = new CloudProcess(3L, 5, 5, 7);
        CloudBalance solution = new CloudBalance(0L,
                Arrays.asList(c1, c2, c3),
                Arrays.asList(p1, p2, p3));
        scoreVerifier.assertHardWeight("requiredNetworkBandwidthTotal", 0, solution);
        p1.setComputer(c1);
        p2.setComputer(c1);
        scoreVerifier.assertHardWeight("requiredNetworkBandwidthTotal", 0, solution);
        p1.setComputer(c2);
        p2.setComputer(c2);
        scoreVerifier.assertHardWeight("requiredNetworkBandwidthTotal", -570, solution);
        p3.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredNetworkBandwidthTotal", -570, solution);
        p2.setComputer(c3);
        scoreVerifier.assertHardWeight("requiredNetworkBandwidthTotal", -547, solution);
    }

    @Test
    void computerCost() {
        CloudComputer c1 = new CloudComputer(1L, 1, 1, 1, 200);
        CloudComputer c2 = new CloudComputer(2L, 1, 1, 1, 30);
        CloudComputer c3 = new CloudComputer(3L, 1, 1, 1, 4);
        CloudProcess p1 = new CloudProcess(1L, 5, 5, 5);
        CloudProcess p2 = new CloudProcess(2L, 5, 5, 5);
        CloudProcess p3 = new CloudProcess(3L, 5, 5, 5);
        CloudBalance solution = new CloudBalance(0L,
                Arrays.asList(c1, c2, c3),
                Arrays.asList(p1, p2, p3));
        scoreVerifier.assertSoftWeight("computerCost", 0, solution);
        p1.setComputer(c1);
        p2.setComputer(c1);
        scoreVerifier.assertSoftWeight("computerCost", -200, solution);
        p3.setComputer(c3);
        scoreVerifier.assertSoftWeight("computerCost", -204, solution);
    }

}
