package org.acme.facilitylocation.solver;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.acme.facilitylocation.bootstrap.DemoDataBuilder;
import org.acme.facilitylocation.domain.FacilityLocationProblem;
import org.acme.facilitylocation.domain.Location;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverManager;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SolverManagerTest {

    @Inject
    SolverManager<FacilityLocationProblem, Long> solverManager;

    @Test
    void solve() throws ExecutionException, InterruptedException {
        FacilityLocationProblem problem = DemoDataBuilder.builder()
                .setCapacity(1200)
                .setDemand(900)
                .setAverageSetupCost(1000).setSetupCostStandardDeviation(200)
                .setFacilityCount(10)
                .setConsumerCount(150)
                .setSouthWestCorner(new Location(-10, -10))
                .setNorthEastCorner(new Location(10, 10))
                .build();
        solverManager.solve(0L, id -> problem, SolverManagerTest::printSolution).getFinalBestSolution();
    }

    static void printSolution(FacilityLocationProblem solution) {
        solution.getFacilities().forEach(facility -> System.out.printf("$%4d (%3d/%3d)%n",
                facility.getSetupCost(),
                facility.getUsedCapacity(),
                facility.getCapacity()));
    }
}
