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

package org.optaplanner.examples.machinereassignment.optional.score;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sumLong;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

import java.util.function.BiFunction;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.examples.machinereassignment.domain.MrBalancePenalty;
import org.optaplanner.examples.machinereassignment.domain.MrGlobalPenaltyInfo;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.domain.MrService;
import org.optaplanner.examples.machinereassignment.domain.solver.MrServiceDependency;
import org.optaplanner.examples.machinereassignment.score.MrConstraints;

public class MachineReassignmentConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                // hard constraints
                maximumCapacity(factory),
                serviceConflict(factory),
                serviceLocationSpread(factory),
                serviceDependency(factory),
                transientUsage(factory),

                // soft constraints
                loadCost(factory),
                balanceCost(factory),
                processMoveCost(factory),
                serviceMoveCost(factory),
                machineMoveCost(factory)
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    /**
     * Maximum capacity: The maximum capacity for each resource for each machine must not be exceeded.
     */
    protected Constraint maximumCapacity(ConstraintFactory factory) {
        return factory.from(MrMachineCapacity.class)
                .join(MrProcessAssignment.class,
                        equal(MrMachineCapacity::getMachine, MrProcessAssignment::getMachine))
                .groupBy((machineCapacity, processAssignment) -> machineCapacity.getMachine(),
                        (machineCapacity, processAssignment) -> machineCapacity,
                        sumLong((machineCapacity, processAssignment) -> processAssignment
                                .getUsage(machineCapacity.getResource())))
                .filter(((machine, machineCapacity, usage) -> machineCapacity.getMaximumCapacity() < usage))
                .penalizeLong(MrConstraints.MAXIMUM_CAPACITY, HardSoftLongScore.ONE_HARD,
                        (machine, machineCapacity, usage) -> usage - machineCapacity.getMaximumCapacity());
    }

    protected Constraint serviceConflict(ConstraintFactory factory) {
        return factory.fromUniquePair(MrProcessAssignment.class,
                equal(MrProcessAssignment::getMachine, MrProcessAssignment::getMachine),
                equal(MrProcessAssignment::getService, MrProcessAssignment::getService))
                .penalize(MrConstraints.SERVICE_CONFLICT, HardSoftLongScore.ONE_HARD);
    }

    /**
     * Spread: Processes of the same service must be serviceLocationSpread out across locations.
     */
    protected Constraint serviceLocationSpread(ConstraintFactory factory) {
        return factory.from(MrProcessAssignment.class)
                .groupBy(processAssignment -> processAssignment.getService(),
                        ConstraintCollectors.countDistinct(processAssignment -> processAssignment.getLocation()))
                .filter((service, distinctLocationCount) -> distinctLocationCount < service.getLocationSpread())
                .penalizeLong(MrConstraints.SERVICE_LOCATION_SPREAD, HardSoftLongScore.ONE_HARD,
                        (service, distinctLocationCount) -> service.getLocationSpread() - distinctLocationCount);
    }

    /**
     * Dependency: The processes of a service depending on another service must run in the neighborhood of a process
     * of the other service.
     */
    protected Constraint serviceDependency(ConstraintFactory factory) {
        return factory.from(MrServiceDependency.class)
                .join(MrProcessAssignment.class,
                        equal(MrServiceDependency::getFromService, MrProcessAssignment::getService))
                .ifExists(MrProcessAssignment.class,
                        equal((serviceDependency, processFrom) -> serviceDependency.getToService(),
                                MrProcessAssignment::getService),
                        filtering((serviceDependency, processFrom,
                                processTo) -> !processFrom.getNeighborhood().equals(processTo.getNeighborhood())))
                .penalize(MrConstraints.SERVICE_DEPENDENCY, HardSoftLongScore.ONE_HARD);
    }

    /**
     * Transient usage: Some resources are transient and count towards the maximum capacity of both the original
     * machine as the newly assigned machine.
     */
    protected Constraint transientUsage(ConstraintFactory factory) {
        return factory.from(MrMachineCapacity.class)
                .filter(MrMachineCapacity::isTransientlyConsumed)
                .join(factory.from(MrProcessAssignment.class).filter(MrProcessAssignment::isMoved),
                        equal(MrMachineCapacity::getMachine, MrProcessAssignment::getOriginalMachine))
                .groupBy((machineCapacity, processAssignment) -> machineCapacity,
                        sumLong((machineCapacity, processAssignment) -> processAssignment
                                .getUsage(machineCapacity.getResource())))
                .filter(((machineCapacity, usage) -> machineCapacity.getMaximumCapacity() < usage))
                .penalizeLong(MrConstraints.TRANSIENT_USAGE, HardSoftLongScore.ONE_HARD,
                        (machineCapacity, usage) -> usage - machineCapacity.getMaximumCapacity());
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    /**
     * Load: The safety capacity for each resource for each machine should not be exceeded.
     */
    protected Constraint loadCost(ConstraintFactory factory) {
        return factory.from(MrMachineCapacity.class)
                .join(MrProcessAssignment.class,
                        equal(MrMachineCapacity::getMachine, MrProcessAssignment::getMachine))
                .groupBy((machineCapacity, processAssignment) -> machineCapacity,
                        sumLong((machineCapacity, processAssignment) -> processAssignment
                                .getUsage(machineCapacity.getResource())))
                .filter(((machineCapacity, usage) -> machineCapacity.getSafetyCapacity() < usage))
                .penalizeLong(MrConstraints.LOAD_COST, HardSoftLongScore.ONE_SOFT,
                        (machineCapacity, usage) -> machineCapacity.getResource().getLoadCostWeight()
                                * (usage - machineCapacity.getSafetyCapacity()));
    }

    /**
     * availability(r) = capacity(m, r) - usage(m, r)
     * balanceCost = sum(max(0, multiplier * availability(m, r1) - availability(m, r2)))
     */
    protected Constraint balanceCost(ConstraintFactory factory) {
        return factory.from(MrBalancePenalty.class)
                .join(MrProcessAssignment.class)
                .groupBy((penalty, processAssignment) -> penalty,
                        (penalty, processAssignment) -> processAssignment.getMachine(),
                        sumLong((penalty, processAssignment) -> processAssignment.getUsage(penalty.getOriginResource())),
                        sumLong((penalty, processAssignment) -> processAssignment.getUsage(penalty.getTargetResource())))
                .penalizeLong(MrConstraints.BALANCE_COST, HardSoftLongScore.ONE_SOFT, this::balanceCost);
    }

    private long balanceCost(MrBalancePenalty penalty, MrMachine machine, long originalUsage, long targetUsage) {
        long originalAvailability = machine.getMachineCapacity(penalty.getOriginResource()).getMaximumCapacity()
                - originalUsage;
        long targetAvailability = machine.getMachineCapacity(penalty.getTargetResource()).getMaximumCapacity() - targetUsage;
        long lackingAvailability = (penalty.getMultiplicand() * originalAvailability) - targetAvailability;
        if (lackingAvailability <= 0L) {
            return 0L;
        }
        return lackingAvailability * penalty.getWeight();
    }

    /**
     * Process move cost: A process has a move cost.
     */
    protected Constraint processMoveCost(ConstraintFactory factory) {
        return factory.from(MrProcessAssignment.class)
                .filter(processAssignment -> processAssignment.isMoved() && processAssignment.getProcessMoveCost() > 0)
                .join(MrGlobalPenaltyInfo.class,
                        Joiners.filtering((processAssignment, penalty) -> penalty.getProcessMoveCostWeight() > 0))
                .penalize(MrConstraints.PROCESS_MOVE_COST, HardSoftLongScore.ONE_SOFT,
                        (processAssignment, penalty) -> processAssignment.getProcessMoveCost() * penalty
                                .getProcessMoveCostWeight());
    }

    /**
     * Service move cost: A service has a move cost.
     */
    protected Constraint serviceMoveCost(ConstraintFactory factory) {
        return factory.from(MrProcessAssignment.class)
                .filter(MrProcessAssignment::isMoved)
                .groupBy(processAssignment -> processAssignment.getService(), ConstraintCollectors.count())
                .groupBy(ConstraintCollectors.max((BiFunction<MrService, Integer, Integer>) (service, count) -> count))
                .join(MrGlobalPenaltyInfo.class)
                .penalize(MrConstraints.SERVICE_MOVE_COST, HardSoftLongScore.ONE_SOFT,
                        (count, penalty) -> count * penalty.getServiceMoveCostWeight());
    }

    /**
     * Machine move cost: Moving a process from machine A to machine B has another A-B specific move cost.
     */
    protected Constraint machineMoveCost(ConstraintFactory factory) {
        return factory.from(MrProcessAssignment.class)
                .filter(processAssignment -> processAssignment.isMoved() && processAssignment.getMachineMoveCost() > 0)
                .join(MrGlobalPenaltyInfo.class,
                        Joiners.filtering((processAssignment, penalty) -> penalty.getMachineMoveCostWeight() > 0))
                .penalize(MrConstraints.MACHINE_MOVE_COST, HardSoftLongScore.ONE_SOFT,
                        (processAssignment, penalty) -> processAssignment.getMachineMoveCost()
                                * penalty.getMachineMoveCostWeight());
    }
}
