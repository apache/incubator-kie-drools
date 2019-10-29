/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.domain.solver.MrServiceDependency;

import static org.optaplanner.core.api.score.stream.Joiners.equal;

public class MachineReassignmentConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                maximumCapacity(factory), // TODO Doesn't work
                serviceConflict(factory),
                serviceLocationSpread(factory),
                serviceDependency(factory), // TODO has bug
                transientUsage(factory), // TODO Doesn't work
        
                loadCost(factory), // TODO Doesn't work
                balanceCost(factory), // TODO Implement it
                processMoveCost(factory),
                serviceMoveCost(factory), // TODO Implement it
                machineMoveCost(factory)
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    /**
     * Maximum capacity: The maximum capacity for each resource for each machine must not be exceeded.
     */
    private Constraint maximumCapacity(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for bi-grouping.");
//        return factory.from(MrMachineCapacity.class)
//                .join(MrProcessAssignment.class,
//                        equal(MrMachineCapacity::getMachine, MrProcessAssignment::getMachine)
//                )
//                .groupBy(
//                        (machineCapacity, processAssignment) -> machineCapacity, sumLong(
//                                (machineCapacity, processAssignment) -> processAssignment.getUsage(machineCapacity.getResource())
//                        )
//                )
//                .filter(((machineCapacity, usage) -> machineCapacity.getMaximumCapacity() < usage))
//                .penalizeLong(MrConstraints.MAXIMUM_CAPACITY,
//                        HardSoftLongScore.ofHard(1L),
//                        (machineCapacity, usage) -> machineCapacity.getMaximumCapacity() - usage);
    }

    /**
     * Conflict: Processes of the same service must run on distinct machines.
     */
    private Constraint serviceConflict(ConstraintFactory factory) {
        return factory.fromUniquePair(MrProcessAssignment.class,
                equal(MrProcessAssignment::getMachine),
                equal(MrProcessAssignment::getService)
        ).penalize(MrConstraints.SERVICE_CONFLICT,
                HardSoftLongScore.ofHard(1L));
    }

    /**
     * Spread: Processes of the same service must be serviceLocationSpread out across locations.
     */
    private Constraint serviceLocationSpread(ConstraintFactory factory) {
        return factory.from(MrProcessAssignment.class)
                .groupBy(MrProcessAssignment::getService,
                        ConstraintCollectors.countDistinct(MrProcessAssignment::getLocation))
                .filter((service, distinctLocationCount) -> service.getLocationSpread() > distinctLocationCount)
                .penalize(MrConstraints.SERVICE_LOCATION_SPREAD,
                        HardSoftLongScore.ofHard(1L));
    }

    /**
     * Dependency: The processes of a service depending on another service must run in the neighborhood of a process
     * of the other service.
     */
    private Constraint serviceDependency(ConstraintFactory factory) {
        return factory.from(MrServiceDependency.class)
                .join(MrProcessAssignment.class,
                        equal(MrServiceDependency::getFromService, MrProcessAssignment::getService))
                // TODO this is a bug, the constraint is implemented incorrectly, it should probably use .notExist() instead
                .join(MrProcessAssignment.class,
                        equal((serviceDependency, processAssignment) -> serviceDependency.getToService(), MrProcessAssignment::getService))
                .filter((serviceDependency, processAssignmentFrom, processAssignmentTo) ->
                        !processAssignmentFrom.getNeighborhood().equals(processAssignmentTo.getNeighborhood()))
                .penalize(MrConstraints.SERVICE_DEPENDENCY,
                        HardSoftLongScore.ofHard(1L));
    }

    /**
     * Transient usage: Some resources are transient and count towards the maximum capacity of both the original
     * machine as the newly assigned machine.
     */
    private Constraint transientUsage(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for bi-grouping.");
//        return factory.from(MrMachineCapacity.class)
//                .filter(MrMachineCapacity::isTransientlyConsumed)
//                .join(factory.from(MrProcessAssignment.class).filter(MrProcessAssignment::isMoved),
//                        equal(MrMachineCapacity::getMachine, MrProcessAssignment::getOriginalMachine)
//                )
//                .groupBy((machineCapacity, processAssignment) -> machineCapacity,
//                        sumLong((machineCapacity, processAssignment)
//                                -> processAssignment.getUsage(machineCapacity.getResource())
//                        )
//                )
//                .filter(((machineCapacity, usage) -> machineCapacity.getMaximumCapacity() < usage))
//                .penalizeLong(MrConstraints.TRANSIENT_USAGE,
//                        HardSoftLongScore.ofHard(1L),
//                        (machineCapacity, usage) -> machineCapacity.getMaximumCapacity() - usage);
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    /**
     * Load: The safety capacity for each resource for each machine should not be exceeded.
     */
    private Constraint loadCost(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for bi-grouping.");
//        return factory.from(MrMachineCapacity.class)
//                .join(MrProcessAssignment.class,
//                        equal(MrMachineCapacity::getMachine, MrProcessAssignment::getMachine)
//                )
//                .groupBy(
//                        (machineCapacity, processAssignment) -> machineCapacity, sumLong(
//                                (machineCapacity, processAssignment) -> processAssignment.getUsage(machineCapacity.getResource())
//                        )
//                )
//                .filter(((machineCapacity, usage) -> machineCapacity.getSafetyCapacity() < usage))
//                .penalizeLong(MrConstraints.LOAD_COST,
//                        HardSoftLongScore.ofSoft(1L),
//                        (machineCapacity, usage) -> machineCapacity.getSafetyCapacity() - usage);
    }

    /**
     * availability(r) = capacity(m, r) - usage(m, r)
     * balanceCost = sum(max(0, multiplier * availability(m, r1) - availability(m, r2)))
     */
    private Constraint balanceCost(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for quad streams.");

        /* TODO: requires quad streams support and groupBy taking two collectors. Alternatively, use a shadow variable.
        return factory.from(MrBalancePenalty.class)
                .join(MrProcessAssignment.class)
                .groupBy((penalty) -> penalty, (penalty, assignment) -> assignment.getMachine(),
                         sumLong((penalty, assignment) -> processAssignment.getUsage(penalty.getOrigin())
                                 sumLong((penalty, assignment) ->
                                                 processAssignment.getUsage(penalty.getTarget()))))
                // QuadStream<MrBalancePenalty, MrMachine, Long, Long>
                .filter((penalty, machine, originUsage, targetUsage) ->
                                (machine.getCapacity(penalty.getOrigin()) - originUsage) <
                                        penalty.getMultiplier() * (machine.getCapacity(penalty.getTarget()) - targetUsage))
                .penalizeLong(MrConstraintName.BALANCE_COST, HardSoftLongScore.ofSoft(1L), ...);
         */
    }

    /**
     * Process move cost: A process has a move cost.
     */
    private Constraint processMoveCost(ConstraintFactory factory) {
        return factory.from(MrProcessAssignment.class)
                .filter(MrProcessAssignment::isMoved)
                .penalizeLong(MrConstraints.PROCESS_MOVE_COST,
                        HardSoftLongScore.ofSoft(1L),
                        MrProcessAssignment::getProcessMoveCost);
    }

    /**
     * Service move cost: A service has a move cost.
     */
    private Constraint serviceMoveCost(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing aggregation function.");

        /* TODO: requires max aggregation function
        return factory.from(MrProcessAssignment.class)
                .filter(MrProcessAssignment::isMoved)
                .groupBy(processAssignment -> processAssignment.getService(), ConstraintCollectors.count())
                .penalizeLong(MrConstraintName.SERVICE_MOVE_COST, HardSoftLongScore.ofSoft(1L));

         */
    }

    /**
     * Machine move cost: Moving a process from machine A to machine B has another A-B specific move cost.
     */
    private Constraint machineMoveCost(ConstraintFactory factory) {
        return factory.from(MrProcessAssignment.class)
                .filter(MrProcessAssignment::isMoved)
                .penalizeLong(MrConstraints.MACHINE_MOVE_COST,
                        HardSoftLongScore.ofSoft(1L),
                        MrProcessAssignment::getMachineMoveCost);
    }

}
