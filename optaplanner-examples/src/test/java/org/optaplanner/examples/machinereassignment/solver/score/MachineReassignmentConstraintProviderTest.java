/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.machinereassignment.solver.score;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrBalancePenalty;
import org.optaplanner.examples.machinereassignment.domain.MrGlobalPenaltyInfo;
import org.optaplanner.examples.machinereassignment.domain.MrLocation;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrNeighborhood;
import org.optaplanner.examples.machinereassignment.domain.MrProcess;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.domain.MrProcessRequirement;
import org.optaplanner.examples.machinereassignment.domain.MrResource;
import org.optaplanner.examples.machinereassignment.domain.MrService;
import org.optaplanner.examples.machinereassignment.domain.solver.MrServiceDependency;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

public class MachineReassignmentConstraintProviderTest {

    private final ConstraintVerifier<MachineReassignmentConstraintProvider, MachineReassignment> constraintVerifier =
            ConstraintVerifier
                    .build(new MachineReassignmentConstraintProvider(), MachineReassignment.class, MrProcessAssignment.class);

    @Test
    public void maximumCapacity() {
        MrResource resource1 = new MrResource(0, false, 1);
        MrMachine machine = new MrMachine();
        MrProcess process = new MrProcess();
        MrProcessRequirement processRequirement = new MrProcessRequirement(process, resource1, 30L);
        process.setProcessRequirementList(Arrays.asList(processRequirement));

        MrMachineCapacity machineCapacity = new MrMachineCapacity(machine, resource1, 20L, 10L);
        MrProcessAssignment processAssignment = new MrProcessAssignment(0L, process, machine, machine);

        constraintVerifier.verifyThat(MachineReassignmentConstraintProvider::maximumCapacity)
                .given(machineCapacity, machine, process, resource1, processAssignment, processRequirement)
                .penalizesBy(10L);
    }

    @Test
    public void transientUsage() {
        MrResource normalResource = new MrResource(0, false, 5);
        MrResource transientlyConsumerResource = new MrResource(1, true, 10);

        MrMachine machine1 = new MrMachine();
        MrMachine machine2 = new MrMachine();

        MrMachineCapacity machineCapacityNormal = new MrMachineCapacity(machine2, normalResource, 20L, 10L);
        MrMachineCapacity machineCapacityTransientlyConsumed = new MrMachineCapacity(machine1,
                transientlyConsumerResource, 35L, 30L);

        MrProcess process = new MrProcess();
        MrProcessRequirement processRequirement1 = new MrProcessRequirement(process, normalResource, 30L);
        MrProcessRequirement processRequirement2 = new MrProcessRequirement(process, transientlyConsumerResource, 50L);
        process.setProcessRequirementList(Arrays.asList(processRequirement1, processRequirement2));

        MrProcessAssignment processAssignment = new MrProcessAssignment(0L, process, machine1, machine2);

        constraintVerifier.verifyThat(MachineReassignmentConstraintProvider::transientUsage)
                .given(normalResource, transientlyConsumerResource, machine1, machine2, machineCapacityNormal,
                        machineCapacityTransientlyConsumed, process, processRequirement1, processRequirement2,
                        processAssignment)
                .penalizesBy(15L);
    }

    @Test
    public void serviceConflict() {
        // 3 of 4 processes of the same service run on the same machine
        MrService service = new MrService();

        MrMachine machine1 = new MrMachine();
        MrMachine machine2 = new MrMachine();

        MrProcess process1 = new MrProcess(service);
        MrProcess process2 = new MrProcess(service);
        MrProcess process3 = new MrProcess(service);
        MrProcess process4 = new MrProcess(service);
        MrProcess process5 = new MrProcess(service);

        MrProcessAssignment process1AssignmentToMachine1 = new MrProcessAssignment(1L, process1, machine1);
        MrProcessAssignment process2AssignmentToMachine1 = new MrProcessAssignment(2L, process2, machine1);
        MrProcessAssignment process3AssignmentToMachine1 = new MrProcessAssignment(3L, process3, machine1);
        MrProcessAssignment process4AssignmentToMachine2 = new MrProcessAssignment(4L, process4, machine2);
        MrProcessAssignment process5AssignmentToNoMachine = new MrProcessAssignment(5L, process5);

        constraintVerifier.verifyThat(MachineReassignmentConstraintProvider::serviceConflict)
                .given(service, machine1, machine2, process1, process2, process3, process4, process1AssignmentToMachine1,
                        process2AssignmentToMachine1, process3AssignmentToMachine1, process4AssignmentToMachine2,
                        process5AssignmentToNoMachine)
                .penalizesBy(3L);
    }

    @Test
    public void serviceLocationSpread() {
        MrLocation location1 = new MrLocation(1L);
        MrLocation location2 = new MrLocation(2L);

        MrMachine machine1 = new MrMachine(0L, location1);
        MrMachine machine2 = new MrMachine(1L, location2);
        MrMachine machine3 = new MrMachine(2L, location2);

        // the service is expected to be spread across at least 5 locations
        MrService service = new MrService();
        service.setLocationSpread(5);

        MrProcess process1 = new MrProcess(service);
        MrProcess process2 = new MrProcess(service);
        MrProcess process3 = new MrProcess(service);

        // the service is spread across 3 machines in 2 different locations
        MrProcessAssignment process1AssignmentToMachine1 = new MrProcessAssignment(1L, process1, machine1);
        MrProcessAssignment process2AssignmentToMachine2 = new MrProcessAssignment(2L, process2, machine2);
        MrProcessAssignment process3AssignmentToMachine3 = new MrProcessAssignment(3L, process3, machine3);

        constraintVerifier.verifyThat(MachineReassignmentConstraintProvider::serviceLocationSpread)
                .given(service, location1, location2, machine1, machine2, machine3, process1, process2, process3,
                        process1AssignmentToMachine1, process2AssignmentToMachine2, process3AssignmentToMachine3)
                .penalizesBy(3L); // 5 - 3 (expected - real location spread)
    }

    @Test
    public void serviceDependency() {
        MrNeighborhood neighborhood1 = new MrNeighborhood(1L);
        MrNeighborhood neighborhood2 = new MrNeighborhood(2L);

        MrMachine machine1 = new MrMachine();
        machine1.setNeighborhood(neighborhood1);
        MrMachine machine2 = new MrMachine();
        machine2.setNeighborhood(neighborhood1);
        MrMachine machine3 = new MrMachine();
        machine3.setNeighborhood(neighborhood2);

        MrService service1 = new MrService();
        MrService service2 = new MrService();
        MrService service3 = new MrService();

        MrServiceDependency serviceDependency1 = new MrServiceDependency(service1, service2);
        MrServiceDependency serviceDependency2 = new MrServiceDependency(service1, service3);

        MrProcess process1 = new MrProcess(service1);
        MrProcess process2 = new MrProcess(service2);
        MrProcess process3 = new MrProcess(service3);

        MrProcessAssignment process1AssignmentToMachine1 = new MrProcessAssignment(1L, process1, machine1);
        MrProcessAssignment process2AssignmentToMachine2 = new MrProcessAssignment(2L, process2, machine2);
        MrProcessAssignment process3AssignmentToMachine3 = new MrProcessAssignment(3L, process3, machine3);

        constraintVerifier.verifyThat(MachineReassignmentConstraintProvider::serviceDependency)
                .given(neighborhood1, neighborhood2, machine1, machine2, machine3, service1, service2, service3,
                        serviceDependency1, serviceDependency2, process1, process2, process3, process1AssignmentToMachine1,
                        process2AssignmentToMachine2, process3AssignmentToMachine3)
                .penalizesBy(1L);
    }

    @Test
    public void loadCost() {
        MrResource resource1 = new MrResource(0, false, 5);
        MrResource resource2 = new MrResource(1, false, 10);
        MrMachine machine = new MrMachine();

        MrMachineCapacity machineCapacity1 = new MrMachineCapacity(machine, resource1, 20L, 10L);
        MrMachineCapacity machineCapacity2 = new MrMachineCapacity(machine, resource2, 20L, 10L);

        MrProcess process = new MrProcess();
        MrProcessRequirement processRequirement1 = new MrProcessRequirement(process, resource1, 15L);
        MrProcessRequirement processRequirement2 = new MrProcessRequirement(process, resource2, 15L);
        process.setProcessRequirementList(Arrays.asList(processRequirement1, processRequirement2));

        MrProcessAssignment processAssignment = new MrProcessAssignment(0L, process, machine, machine);

        // soft limits of both resources are exceeded by 5 (15 - 10) and the weights are 5 and 10.
        constraintVerifier.verifyThat(MachineReassignmentConstraintProvider::loadCost)
                .given(resource1, resource2, machine, machineCapacity1, machineCapacity2, process, processRequirement1,
                        processRequirement2, processAssignment)
                .penalizesBy(25L + 50L);
    }

    @Test
    public void processMoveCost() {
        MrGlobalPenaltyInfo globalPenaltyInfo = new MrGlobalPenaltyInfo();
        globalPenaltyInfo.setProcessMoveCostWeight(10);

        MrMachine machine1 = new MrMachine();
        MrMachine machine2 = new MrMachine();

        MrProcess process = new MrProcess();
        process.setMoveCost(2);
        MrProcessAssignment processAssignment = new MrProcessAssignment(0L, process, machine1, machine2);

        MrProcessAssignment processAssignment2 = new MrProcessAssignment();
        processAssignment2.setProcess(process);
        processAssignment2.setOriginalMachine(machine1);

        MrProcessAssignment processAssignment3 = new MrProcessAssignment(0L, process, machine1, machine1);

        constraintVerifier.verifyThat(MachineReassignmentConstraintProvider::processMoveCost)
                .given(globalPenaltyInfo, machine1, machine2, process, processAssignment, processAssignment2,
                        processAssignment3)
                .penalizesBy(20L);
    }

    @Test
    public void serviceMoveCost() {
        MrGlobalPenaltyInfo globalPenaltyInfo = new MrGlobalPenaltyInfo();
        globalPenaltyInfo.setServiceMoveCostWeight(10);

        MrMachine machine1 = new MrMachine();
        MrMachine machine2 = new MrMachine();

        MrService service1 = new MrService(1L);
        MrService service2 = new MrService(2L);
        // service2 has only one process moving, while service1 has two processes moving => wins
        MrProcess process1 = new MrProcess(service1);
        MrProcess process2 = new MrProcess(service1);
        MrProcess process3 = new MrProcess(service2);

        MrProcessAssignment processAssignment1 = new MrProcessAssignment(0L, process1, machine1, machine2);
        MrProcessAssignment processAssignment2 = new MrProcessAssignment(1L, process2, machine1, machine2);
        MrProcessAssignment processAssignment3 = new MrProcessAssignment(1L, process3, machine1, machine2);

        constraintVerifier.verifyThat(MachineReassignmentConstraintProvider::serviceMoveCost)
                .given(globalPenaltyInfo, machine1, machine2, service1, service2, process1, process2, process3,
                        processAssignment1, processAssignment2, processAssignment3)
                .penalizesBy(2 * 10L);
    }

    @Test
    public void machineMoveCost() {
        MrGlobalPenaltyInfo globalPenaltyInfo = new MrGlobalPenaltyInfo();
        globalPenaltyInfo.setMachineMoveCostWeight(10);

        MrMachine machine1 = new MrMachine();
        MrMachine machine2 = new MrMachine();

        Map<MrMachine, Integer> costMapFromMachine1 = new HashedMap<>();
        costMapFromMachine1.put(machine2, 20);
        machine1.setMachineMoveCostMap(costMapFromMachine1);

        Map<MrMachine, Integer> costMapFromMachine2 = new HashedMap<>();
        costMapFromMachine2.put(machine1, 0);
        machine2.setMachineMoveCostMap(costMapFromMachine2);

        MrProcess process1 = new MrProcess();
        MrProcess process2 = new MrProcess();
        MrProcess process3 = new MrProcess();

        MrProcessAssignment processAssignment1 = new MrProcessAssignment(0L, process1, machine1, machine2);
        MrProcessAssignment processAssignment2 = new MrProcessAssignment(0L, process2, machine1, machine2);
        MrProcessAssignment processAssignment3 = new MrProcessAssignment(0L, process3, machine2, machine1);

        // 2 processes are moving from machine1 to machine2, which has a cost of 20 => 2 * 20 * 10 (global penalty) = 400.
        // The process3 moves from machine2 to machine1, which has a zero cost.
        constraintVerifier.verifyThat(MachineReassignmentConstraintProvider::machineMoveCost)
                .given(globalPenaltyInfo, machine1, machine2, process1, process2, process3, processAssignment1,
                        processAssignment2, processAssignment3)
                .penalizesBy(400L);

    }

    @Test
    public void balanceCost() {
        MrResource cpu = new MrResource(0, false, 0);
        MrResource mem = new MrResource(1, false, 0);
        MrResource disk = new MrResource(2, false, 0);

        MrBalancePenalty cpuMemBalance = new MrBalancePenalty(cpu, mem, 3, 1);
        MrBalancePenalty cpuDiskBalance = new MrBalancePenalty(cpu, disk, 5, 1_000);

        MrMachine machine1 = new MrMachine(0L);
        MrMachineCapacity machine1Capacity1 = new MrMachineCapacity(machine1, cpu, 20L, 1L);
        MrMachineCapacity machine1Capacity2 = new MrMachineCapacity(machine1, mem, 300L, 1L);
        MrMachineCapacity machine1Capacity3 = new MrMachineCapacity(machine1, disk, 4000L, 1L);
        machine1.setMachineCapacityList(Arrays.asList(machine1Capacity1, machine1Capacity2, machine1Capacity3));

        MrMachine machine2 = new MrMachine(1L);
        MrMachineCapacity machine2Capacity1 = new MrMachineCapacity(machine2, cpu, 90L, 1L);
        MrMachineCapacity machine2Capacity2 = new MrMachineCapacity(machine2, mem, 800L, 1L);
        MrMachineCapacity machine2Capacity3 = new MrMachineCapacity(machine2, disk, 7000L, 1L);
        machine2.setMachineCapacityList(Arrays.asList(machine2Capacity1, machine2Capacity2, machine2Capacity3));

        MrProcess process1 = new MrProcess(0L);
        MrProcessRequirement process1Requirement1 = new MrProcessRequirement(process1, cpu, 10L);
        MrProcessRequirement process1Requirement2 = new MrProcessRequirement(process1, mem, 200L);
        MrProcessRequirement process1Requirement3 = new MrProcessRequirement(process1, disk, 3000L);
        process1.setProcessRequirementList(Arrays.asList(process1Requirement1, process1Requirement2, process1Requirement3));

        MrProcessAssignment processAssignment1 = new MrProcessAssignment(1L, process1, machine1);

        MrProcess process2 = new MrProcess(1L);
        MrProcessRequirement process2Requirement1 = new MrProcessRequirement(process2, cpu, 8L); // 20 - 18 = 2
        MrProcessRequirement process2Requirement2 = new MrProcessRequirement(process2, mem, 95L); // 300 - 295 = 5. Needs 6, lacks 1
        MrProcessRequirement process2Requirement3 = new MrProcessRequirement(process2, disk, 997L); // 4000 - 3997 = 3. Needs 10, lacks 7
        process2.setProcessRequirementList(Arrays.asList(process2Requirement1, process2Requirement2, process2Requirement3));

        MrProcessAssignment processAssignment2 = new MrProcessAssignment(2L, process2, machine1);

        constraintVerifier.verifyThat(MachineReassignmentConstraintProvider::balanceCost)
                .given(cpu, mem, disk, cpuMemBalance, cpuDiskBalance, machine1, machine2,
                        machine1Capacity1, machine1Capacity2, machine1Capacity3, machine2Capacity1, machine2Capacity2,
                        machine2Capacity3, process1, process2, processAssignment1, processAssignment2)
                .penalizesBy(7_001L);
    }

    @Test
    public void balanceCostSingleMachine() {
        MrResource cpu = new MrResource(0L, 0, false, 1);
        MrResource mem = new MrResource(1L, 1, false, 1);
        MrResource disk = new MrResource(2L, 2, false, 1);

        MrBalancePenalty balancePenaltyCpuMem = new MrBalancePenalty(0L, cpu, mem, 2, 1);

        MrMachine machine = new MrMachine(0L);

        MrMachineCapacity machineCapacityCpu = new MrMachineCapacity(0L, machine, cpu, 2L, 1L);
        MrMachineCapacity machineCapacityMem = new MrMachineCapacity(1L, machine, mem, 4L, 2L);
        MrMachineCapacity machineCapacityDisk = new MrMachineCapacity(2L, machine, disk, 200L, 100L);
        machine.setMachineCapacityList(Arrays.asList(machineCapacityCpu, machineCapacityMem, machineCapacityDisk));

        MrProcess process = new MrProcess(0L);
        MrProcessRequirement processRequirementCpu = new MrProcessRequirement(0L, process, cpu, 1L);
        MrProcessRequirement processRequirementMem = new MrProcessRequirement(1L, process, mem, 4L);
        MrProcessRequirement processRequirementDisk = new MrProcessRequirement(2L, process, disk, 100L);
        process.setProcessRequirementList(Arrays.asList(processRequirementCpu, processRequirementMem, processRequirementDisk));

        MrProcessAssignment processAssignment = new MrProcessAssignment(1L, process, machine);

        constraintVerifier.verifyThat(MachineReassignmentConstraintProvider::balanceCost)
                .given(cpu, mem, disk, balancePenaltyCpuMem, machine, machineCapacityCpu, machineCapacityMem,
                        machineCapacityDisk, process, processAssignment)
                .penalizesBy(2L);
    }

    @Test
    public void solutionWithMultipleConstraints() throws IOException {
        constraintVerifier.verifyThat()
                .givenSolution(readSolution("model-a1-1-0hard-44306501soft.xml"))
                .scores(HardSoftLongScore.of(-0, -44306501));
    }

    private static MachineReassignment readSolution(String resource) throws IOException {
        XStreamSolutionFileIO<MachineReassignment> solutionFileIO = new XStreamSolutionFileIO<>(MachineReassignment.class);
        try (InputStream inputStream = MachineReassignmentConstraintProviderTest.class.getResourceAsStream(resource)) {
            return solutionFileIO.read(inputStream);
        }
    }
}
