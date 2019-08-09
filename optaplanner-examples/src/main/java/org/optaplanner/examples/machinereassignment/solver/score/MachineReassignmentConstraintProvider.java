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
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.domain.solver.MrServiceDependency;

import static org.optaplanner.core.api.score.stream.common.ConstraintCollectors.*;
import static org.optaplanner.core.api.score.stream.common.Joiners.*;

public class MachineReassignmentConstraintProvider implements ConstraintProvider {

    @Override
    public void defineConstraints(ConstraintFactory constraintFactory) {
        maximumCapacity(constraintFactory); // TODO Doesn't work
        serviceConflict(constraintFactory);
        serviceLocationSpread(constraintFactory);
        serviceDependency(constraintFactory);
        transientUsage(constraintFactory); // TODO Doesn't work

        loadCost(constraintFactory); // TODO Doesn't work
        balanceCost(constraintFactory); // TODO Doesn't work
        processMoveCost(constraintFactory);
        serviceMoveCost(constraintFactory); // TODO Doesn't work
        machineMoveCost(constraintFactory);
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    /**
     * Maximum capacity: The maximum capacity for each resource for each machine must not be exceeded.
     */
    private void maximumCapacity(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraints.MAXIMUM_CAPACITY, HardSoftLongScore.ofHard(1L));
        constraint.from(MrMachineCapacity.class)
                .join(MrProcessAssignment.class,
                        equalTo(MrMachineCapacity::getMachine, MrProcessAssignment::getMachine)
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
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraints.SERVICE_CONFLICT,
                HardSoftLongScore.ofHard(1L));
        constraint.fromUniquePair(MrProcessAssignment.class,
                equalTo(MrProcessAssignment::getMachine),
                equalTo(MrProcessAssignment::getService)
        ).penalize();
    }

    /**
     * Spread: Processes of the same service must be serviceLocationSpread out across locations.
     */
    private void serviceLocationSpread(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraints.SERVICE_LOCATION_SPREAD,
                HardSoftLongScore.ofHard(1L));
        constraint.from(MrProcessAssignment.class)
                .groupBy(MrProcessAssignment::getService,
                        ConstraintCollectors.countDistinct(MrProcessAssignment::getLocation))
                .filter((service, distinctLocationCount) -> service.getLocationSpread() > distinctLocationCount)
                .penalize();
    }

    /**
     * Dependency: The processes of a service depending on another service must run in the neighborhood of a process
     * of the other service.
     */
    private void serviceDependency(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraints.SERVICE_DEPENDENCY,
                HardSoftLongScore.ofHard(1L));
        constraint.from(MrServiceDependency.class)
                .join(MrProcessAssignment.class,
                        equalTo(MrServiceDependency::getFromService, MrProcessAssignment::getService))
                // TODO this is a bug, the constraint is implemented incorrectly, it should probably use .notExist() instead
                .join(MrProcessAssignment.class,
                        equalTo((serviceDependency, processAssignment) -> serviceDependency.getToService(), MrProcessAssignment::getService))
                .filter((serviceDependency, processAssignmentFrom, processAssignmentTo) ->
                        !processAssignmentFrom.getNeighborhood().equals(processAssignmentTo.getNeighborhood()))
                .penalize();
    }

    /**
     * Transient usage: Some resources are transient and count towards the maximum capacity of both the original
     * machine as the newly assigned machine.
     */
    private void transientUsage(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraints.TRANSIENT_USAGE,
                HardSoftLongScore.ofHard(1L));
        constraint.from(MrMachineCapacity.class)
                .filter(MrMachineCapacity::isTransientlyConsumed)
                .join(constraint.from(MrProcessAssignment.class).filter(MrProcessAssignment::isMoved),
                        equalTo(MrMachineCapacity::getMachine, MrProcessAssignment::getOriginalMachine)
                )
                .groupBy((machineCapacity, processAssignment) -> machineCapacity,
                        sumLong((machineCapacity, processAssignment)
                                -> processAssignment.getUsage(machineCapacity.getResource())
                        )
                )
                .filter(((machineCapacity, usage) -> machineCapacity.getMaximumCapacity() < usage))
                .penalizeLong((machineCapacity, usage) -> machineCapacity.getMaximumCapacity() - usage);
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    /**
     * Load: The safety capacity for each resource for each machine should not be exceeded.
     */
    private void loadCost(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraints.LOAD_COST,
                HardSoftLongScore.ofSoft(1L));
        constraint.from(MrMachineCapacity.class)
                .join(MrProcessAssignment.class,
                        equalTo(MrMachineCapacity::getMachine, MrProcessAssignment::getMachine)
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

        /* TODO: requires quad streams support and groupBy taking two collectors. Alternatively, use a shadow variable.
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.BALANCE_COST,
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
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraints.PROCESS_MOVE_COST,
                HardSoftLongScore.ofSoft(1L));
        constraint.from(MrProcessAssignment.class)
                .filter(MrProcessAssignment::isMoved)
                .penalizeLong(MrProcessAssignment::getProcessMoveCost);
    }

    /**
     * Service move cost: A service has a move cost.
     */
    private void serviceMoveCost(ConstraintFactory constraintFactory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing aggregation function.");

        /* TODO: requires max aggregation function
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraintName.SERVICE_MOVE_COST,
                                                                          HardSoftLongScore.ofSoft(1L));
        constraint.from(MrProcessAssignment.class)
                .filter(MrProcessAssignment::isMoved)
                .groupBy(processAssignment -> processAssignment.getService(), ConstraintCollectors.count())
                .penalizeLong();

         */
    }

    /**
     * Machine move cost: Moving a process from machine A to machine B has another A-B specific move cost.
     */
    private void machineMoveCost(ConstraintFactory constraintFactory) {
        Constraint constraint = constraintFactory.newConstraintWithWeight(MrConstraints.MACHINE_MOVE_COST,
                HardSoftLongScore.ofSoft(1L));
        constraint.from(MrProcessAssignment.class)
                .filter(MrProcessAssignment::isMoved)
                .penalizeLong(MrProcessAssignment::getMachineMoveCost);
    }

}
