/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.common.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.common.Joiners;
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.domain.solver.MrServiceDependency;

import static org.optaplanner.core.api.score.stream.common.ConstraintCollectors.sumLong;

public class MachineReassignmentConstraintProvider implements ConstraintProvider {

    @Override
    public void defineConstraints(ConstraintFactory constraintFactory) {
        maximumCapacity(constraintFactory);
        serviceConflict(constraintFactory);
        serviceLocationSpread(constraintFactory);
        serviceDependency(constraintFactory);
        transientUsage(constraintFactory);

        loadCost(constraintFactory);
        balanceCost(constraintFactory);
        processMoveCost(constraintFactory);
        serviceMoveCost(constraintFactory);
        machineMoveCost(constraintFactory);
    }

    /*************** HARD ***************/

    /**
     * Maximum capacity: The maximum capacity for each resource for each machine must not be exceeded.
     */
    private void maximumCapacity(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.MAXIMUM_CAPACITY.getName(), HardSoftLongScore.ofHard(1L));
        constraint.from(MrMachineCapacity.class)
                .join(MrProcessAssignment.class,
                      Joiners.equalTo(MrMachineCapacity::getMachine, MrProcessAssignment::getMachine)
                )
                .groupBy(
                        (machineCapacity, processAssignment) -> machineCapacity, sumLong(
                                (machineCapacity, processAssignment) -> processAssignment.getUsage(machineCapacity.getResource())
                        )
                )
                .filter(((machineCapacity, usage) -> machineCapacity.getMaximumCapacity() < usage))
                .penalizeLong((machineCapacity, usage) -> machineCapacity.getMaximumCapacity() - usage);
    }

    /**
     * Conflict: Processes of the same service must run on distinct machines.
     */
    private void serviceConflict(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.SERVICE_CONFLICT.getName(),
                                                                          HardSoftLongScore.ofHard(1L));
        constraint.from(MrProcessAssignment.class)
                .joinOther(
                        Joiners.equalTo(MrProcessAssignment::getMachine),
                        Joiners.lessThan(MrProcessAssignment::getId)
                )
                .filter((mrProcessAssignmentA, mrProcessAssignmentB) -> mrProcessAssignmentA.getService().equals(mrProcessAssignmentB.getService()))
                .penalize();
    }

    /**
     * Spread: Processes of the same service must be serviceLocationSpread out across locations.
     */
    private void serviceLocationSpread(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.SERVICE_LOCATION_SPREAD.getName(),
                                                                          HardSoftLongScore.ofHard(1L));
        constraint.from(MrProcessAssignment.class)
                .groupBy(processAssignment -> processAssignment.getService(),
                         ConstraintCollectors.countDistinct(processAssignment -> processAssignment.getLocation()))
                .filter((mrService, distinctLocationCount) -> mrService.getLocationSpread() > distinctLocationCount)
                .penalize();
    }

    /**
     * Dependency: The processes of a service depending on another service must run in the neighborhood of a process
     * of the other service.
     */
    private void serviceDependency(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.SERVICE_DEPENDENCY.getName(),
                                                                          HardSoftLongScore.ofHard(1L));
        constraint.from(MrServiceDependency.class)
                .join(MrProcessAssignment.class,
                      Joiners.equalTo(MrServiceDependency::getFromService, MrProcessAssignment::getService))
                .join(MrProcessAssignment.class,
                      Joiners.equalTo((serviceDependency, processAssignment) -> serviceDependency.getToService(), MrProcessAssignment::getService))
                .filter((mrServiceDependency, mrProcessAssignmentFrom, mrProcessAssignmentTo) ->
                                !mrProcessAssignmentFrom.getNeighborhood().equals(mrProcessAssignmentTo.getNeighborhood()))
                .penalize();
    }

    /**
     * Transient usage: Some resources are transient and count towards the maximum capacity of both the original
     * machine as the newly assigned machine.
     */
    private void transientUsage(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.TRANSIENT_USAGE.getName(),
                                                                          HardSoftLongScore.ofHard(1L));
        constraint.from(MrMachineCapacity.class)
                .join(MrProcessAssignment.class,
                      Joiners.equalTo(MrMachineCapacity::getMachine, MrProcessAssignment::getOriginalMachine)
                )
                .filter(
                        (mrMachineCapacity, mrProcessAssignment) -> mrProcessAssignment.isMoved() && mrMachineCapacity.isTransientlyConsumed()
                )
                .groupBy(
                        (machineCapacity, processAssignment) -> machineCapacity, sumLong(
                                (machineCapacity, processAssignment) -> processAssignment.getUsage(machineCapacity.getResource())
                        )
                )
                .filter(((machineCapacity, usage) -> machineCapacity.getMaximumCapacity() < usage))
                .penalizeLong((machineCapacity, usage) -> machineCapacity.getMaximumCapacity() - usage);
    }

    /*************** SOFT ***************/

    /**
     * Load: The safety capacity for each resource for each machine should not be exceeded.
     */
    private void loadCost(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.LOAD_COST.getName(),
                                                                          HardSoftLongScore.ofSoft(1L));
        constraint.from(MrMachineCapacity.class)
                .join(MrProcessAssignment.class,
                      Joiners.equalTo(MrMachineCapacity::getMachine, MrProcessAssignment::getMachine)
                )
                .groupBy(
                        (machineCapacity, processAssignment) -> machineCapacity, sumLong(
                                (machineCapacity, processAssignment) -> processAssignment.getUsage(machineCapacity.getResource())
                        )
                )
                .filter(((machineCapacity, usage) -> machineCapacity.getSafetyCapacity() < usage))
                .penalizeLong((machineCapacity, usage) -> machineCapacity.getSafetyCapacity() - usage);
    }

    /**
     * availability(r) = capacity(m, r) - usage(m, r)
     * balanceCost = sum(max(0, multiplier * availability(m, r1) - availability(m, r2)))
     */
    private void balanceCost(ConstraintFactory constraintFactory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for quad streams.");

        /*
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.BALANCE_COST.getName(),
                                                                          HardSoftLongScore.ofSoft(1L));
        constraint.from(MrBalancePenalty.class)
                .join(MrProcessAssignment.class)
                .groupBy((penalty) -> penalty, (penalty, assignment) -> assignment.getMachine(),
                         sumLong((penalty, assignment) -> processAssignment.getUsage(penalty.getOrigin())
                                 sumLong((penalty, assignment) ->
                                                 processAssignment.getUsage(penalty.getTarget()))))
                // QuadStream<MrBalancePenalty, MrMachine, Long, Long>
                .filter((penalty, machine, originUsage, targetUsage) ->
                                (machine.getCapacity(penalty.getOrigin()) - originUsage) <
                                        penalty.getMultiplier() * (machine.getCapacity(penalty.getTarget()) - targetUsage))

         */
    }

    /**
     * Process move cost: A process has a move cost.
     */
    private void processMoveCost(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.PROCESS_MOVE_COST.getName(),
                                                                          HardSoftLongScore.ofSoft(1L));
        constraint.from(MrProcessAssignment.class)
                .filter(processAssignment -> processAssignment.isMoved())
                .penalizeLong(MrProcessAssignment::getProcessMoveCost);
    }

    /**
     * Service move cost: A service has a move cost.
     */
    private void serviceMoveCost(ConstraintFactory constraintFactory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing aggregation function.");

        /*
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.SERVICE_MOVE_COST.getName(),
                                                                          HardSoftLongScore.ofSoft(1L));
        constraint.from(MrProcessAssignment.class)
                .filter(processAssignment -> processAssignment.isMoved())
                .groupBy(processAssignment -> processAssignment.getService(), ConstraintCollectors.count())
                .penalizeLong();

         */
    }

    /**
     * Machine move cost: Moving a process from machine A to machine B has another A-B specific move cost.
     */
    private void machineMoveCost(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.MACHINE_MOVE_COST.getName(),
                                                                          HardSoftLongScore.ofSoft(1L));
        constraint.from(MrProcessAssignment.class)
                .filter(processAssignment -> processAssignment.isMoved())
                .penalizeLong(MrProcessAssignment::getMachineMoveCost);
    }
}
