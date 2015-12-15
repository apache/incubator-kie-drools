/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.primlong.LongConstraintMatchTotal;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.ConstraintMatchAwareIncrementalScoreCalculator;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrBalancePenalty;
import org.optaplanner.examples.machinereassignment.domain.MrGlobalPenaltyInfo;
import org.optaplanner.examples.machinereassignment.domain.MrLocation;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrNeighborhood;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.domain.MrResource;
import org.optaplanner.examples.machinereassignment.domain.MrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MachineReassignmentIncrementalScoreCalculator
        extends AbstractIncrementalScoreCalculator<MachineReassignment>
        implements ConstraintMatchAwareIncrementalScoreCalculator<MachineReassignment> {

    protected static final String CONSTRAINT_PACKAGE = "org.optaplanner.examples.machinereassignment.solver";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private MachineReassignment machineReassignment;
    private MrGlobalPenaltyInfo globalPenaltyInfo;

    private Map<MrService, MrServiceScorePart> serviceScorePartMap;
    private Map<Integer, Integer> movedProcessCountToServiceCount;
    private int serviceMoveCost;
    private Map<MrMachine, MrMachineScorePart> machineScorePartMap;

    private long hardScore;
    private long softScore;

    public void resetWorkingSolution(MachineReassignment machineReassignment) {
        this.machineReassignment = machineReassignment;
        hardScore = 0L;
        softScore = 0L;
        globalPenaltyInfo = machineReassignment.getGlobalPenaltyInfo();
        List<MrService> serviceList = machineReassignment.getServiceList();
        serviceScorePartMap = new HashMap<MrService, MrServiceScorePart>(serviceList.size());
        for (MrService service : serviceList) {
            serviceScorePartMap.put(service, new MrServiceScorePart(service));
        }
        movedProcessCountToServiceCount = new HashMap<Integer, Integer>(serviceList.size());
        movedProcessCountToServiceCount.put(0, serviceList.size());
        serviceMoveCost = 0;
        List<MrMachine> machineList = machineReassignment.getMachineList();
        machineScorePartMap = new HashMap<MrMachine, MrMachineScorePart>(machineList.size());
        for (MrMachine machine : machineList) {
            machineScorePartMap.put(machine, new MrMachineScorePart(machine));
        }
        for (MrProcessAssignment processAssignment : machineReassignment.getProcessAssignmentList()) {
            MrMachine originalMachine = processAssignment.getOriginalMachine();
            if (originalMachine != null) {
                machineScorePartMap.get(originalMachine).initOriginalProcessAssignment(processAssignment);
            }
        }
        for (MrProcessAssignment processAssignment : machineReassignment.getProcessAssignmentList()) {
            insert(processAssignment);
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        // TODO the maps should probably be adjusted
        insert((MrProcessAssignment) entity);
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        retract((MrProcessAssignment) entity);
    }

    public void afterVariableChanged(Object entity, String variableName) {
        insert((MrProcessAssignment) entity);
    }

    public void beforeEntityRemoved(Object entity) {
        retract((MrProcessAssignment) entity);
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
        // TODO the maps should probably be adjusted
    }

    private void insert(MrProcessAssignment processAssignment) {
        MrMachine machine = processAssignment.getMachine();
        if (machine != null) {
            MrServiceScorePart serviceScorePart = serviceScorePartMap.get(processAssignment.getService());
            serviceScorePart.addProcessAssignment(processAssignment);
            MrMachineScorePart machineScorePart = machineScorePartMap.get(machine);
            machineScorePart.addProcessAssignment(processAssignment);
        }
    }

    private void retract(MrProcessAssignment processAssignment) {
        MrMachine machine = processAssignment.getMachine();
        if (machine != null) {
            MrServiceScorePart serviceScorePart = serviceScorePartMap.get(processAssignment.getService());
            serviceScorePart.removeProcessAssignment(processAssignment);
            MrMachineScorePart machineScorePart = machineScorePartMap.get(machine);
            machineScorePart.removeProcessAssignment(processAssignment);
        }
    }

    public HardSoftLongScore calculateScore() {
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }

    private class MrServiceScorePart {

        private final MrService service;

        private Map<MrLocation, Integer> locationBag;
        private Map<MrNeighborhood, Integer> neighborhoodBag;
        private int movedProcessCount;

        private MrServiceScorePart(MrService service) {
            this.service = service;
            locationBag = new HashMap<MrLocation, Integer>(machineReassignment.getLocationList().size());
            hardScore -= service.getLocationSpread();
            List<MrNeighborhood> neighborhoodList = machineReassignment.getNeighborhoodList();
            neighborhoodBag = new HashMap<MrNeighborhood, Integer>(neighborhoodList.size());
            for (MrNeighborhood neighborhood : neighborhoodList) {
                neighborhoodBag.put(neighborhood, 0);
            }
            movedProcessCount = 0;
        }

        private void addProcessAssignment(MrProcessAssignment processAssignment) {
            // Spread constraints
            MrLocation location = processAssignment.getLocation();
            Integer locationProcessCount = locationBag.get(location);
            if (locationProcessCount == null) {
                if (service.getLocationSpread() > locationBag.size()) {
                    hardScore += (service.getLocationSpread() - locationBag.size());
                }
                locationBag.put(location, 1);
                if (service.getLocationSpread() > locationBag.size()) {
                    hardScore -= (service.getLocationSpread() - locationBag.size());
                }
            } else {
                locationBag.put(location, locationProcessCount + 1);
            }
            // Dependency constraints
            MrNeighborhood neighborhood = processAssignment.getNeighborhood();
            int neighborhoodProcessCount = neighborhoodBag.get(neighborhood) + 1;
            neighborhoodBag.put(neighborhood, neighborhoodProcessCount);
            for (MrService toDependencyService : service.getToDependencyServiceList()) {
                int toDependencyNeighborhoodProcessCount = serviceScorePartMap.get(toDependencyService)
                        .neighborhoodBag.get(neighborhood);
                if (toDependencyNeighborhoodProcessCount == 0) {
                    hardScore--;
                }
            }
            if (neighborhoodProcessCount == 1) {
                for (MrService fromDependencyService : service.getFromDependencyServiceList()) {
                    int fromDependencyNeighborhoodProcessCount = serviceScorePartMap.get(fromDependencyService)
                            .neighborhoodBag.get(neighborhood);
                    hardScore += fromDependencyNeighborhoodProcessCount;
                }
            }
            // Service move cost
            if (processAssignment.isMoved()) {
                int oldServiceCount = movedProcessCountToServiceCount.get(movedProcessCount);
                movedProcessCountToServiceCount.put(movedProcessCount, oldServiceCount - 1);
                if (serviceMoveCost == movedProcessCount) {
                    serviceMoveCost++;
                    softScore -= globalPenaltyInfo.getServiceMoveCostWeight();
                }
                movedProcessCount++;
                Integer newServiceCount = movedProcessCountToServiceCount.get(movedProcessCount);
                if (newServiceCount == null) {
                    newServiceCount = 0;
                }
                movedProcessCountToServiceCount.put(movedProcessCount, newServiceCount + 1);
            }
        }

        private void removeProcessAssignment(MrProcessAssignment processAssignment) {
            // Spread constraints
            MrLocation location = processAssignment.getLocation();
            int locationProcessCount = locationBag.get(location);
            if (locationProcessCount == 1) {
                if (service.getLocationSpread() > locationBag.size()) {
                    hardScore += (service.getLocationSpread() - locationBag.size());
                }
                locationBag.remove(location);
                if (service.getLocationSpread() > locationBag.size()) {
                    hardScore -= (service.getLocationSpread() - locationBag.size());
                }
            } else {
                locationBag.put(location, locationProcessCount - 1);
            }
            // Dependency constraints
            MrNeighborhood neighborhood = processAssignment.getNeighborhood();
            int neighborhoodProcessCount = neighborhoodBag.get(neighborhood) - 1;
            neighborhoodBag.put(neighborhood, neighborhoodProcessCount);
            for (MrService toDependencyService : service.getToDependencyServiceList()) {
                int toDependencyNeighborhoodProcessCount = serviceScorePartMap.get(toDependencyService)
                        .neighborhoodBag.get(neighborhood);
                if (toDependencyNeighborhoodProcessCount == 0) {
                    hardScore++;
                }
            }
            if (neighborhoodProcessCount == 0) {
                for (MrService fromDependencyService : service.getFromDependencyServiceList()) {
                    int fromDependencyNeighborhoodProcessCount = serviceScorePartMap.get(fromDependencyService)
                            .neighborhoodBag.get(neighborhood);
                    hardScore -= fromDependencyNeighborhoodProcessCount;
                }
            }
            // Service move cost
            if (processAssignment.isMoved()) {
                int oldServiceCount = movedProcessCountToServiceCount.get(movedProcessCount);
                // Hack: This will linger a few entries with key 0 in movedProcessCountToServiceCount
                movedProcessCountToServiceCount.put(movedProcessCount, oldServiceCount - 1);
                if (oldServiceCount == 1 && serviceMoveCost == movedProcessCount) {
                    serviceMoveCost--;
                    softScore += globalPenaltyInfo.getServiceMoveCostWeight();
                }
                movedProcessCount--;
                int newServiceCount = movedProcessCountToServiceCount.get(movedProcessCount);
                movedProcessCountToServiceCount.put(movedProcessCount, newServiceCount + 1);
            }
        }

    }

    private class MrMachineScorePart {

        private final MrMachine machine;
        private final List<MrMachineCapacityScorePart> machineCapacityScorePartList;
        private Map<MrService, Integer> serviceBag;

        public MrMachineScorePart(MrMachine machine) {
            this.machine = machine;
            List<MrMachineCapacity> machineCapacityList = machine.getMachineCapacityList();
            machineCapacityScorePartList = new ArrayList<MrMachineCapacityScorePart>(machineCapacityList.size());
            for (MrMachineCapacity machineCapacity : machineCapacityList) {
                machineCapacityScorePartList.add(new MrMachineCapacityScorePart(machineCapacity));
            }
            serviceBag = new HashMap<MrService, Integer>(10);
            doBalancePenaltyCosts();
        }

        public void initOriginalProcessAssignment(MrProcessAssignment processAssignment) {
            for (MrMachineCapacityScorePart machineCapacityScorePart : machineCapacityScorePartList) {
                machineCapacityScorePart.initOriginalProcessAssignment(processAssignment);
            }
        }

        private void addProcessAssignment(MrProcessAssignment processAssignment) {
            // Balance cost
            undoBalancePenaltyCosts();
            for (MrMachineCapacityScorePart machineCapacityScorePart : machineCapacityScorePartList) {
                machineCapacityScorePart.addProcessAssignment(processAssignment);
            }
            // Service conflict
            MrService service = processAssignment.getService();
            Integer serviceProcessCountInteger = serviceBag.get(service);
            int serviceProcessCount = serviceProcessCountInteger == null ? 0 : serviceProcessCountInteger;
            if (serviceProcessCount > 1) {
                hardScore += (serviceProcessCount - 1);
            }
            serviceProcessCount++;
            if (serviceProcessCount > 1) {
                hardScore -= (serviceProcessCount - 1);
            }
            serviceProcessCountInteger = serviceProcessCount == 0 ? null : serviceProcessCount;
            serviceBag.put(service, serviceProcessCountInteger);
            // Balance cost
            doBalancePenaltyCosts();
            // Move costs
            if (processAssignment.isMoved()) {
                // Process move cost
                softScore -= processAssignment.getProcessMoveCost() * globalPenaltyInfo.getProcessMoveCostWeight();
                // Machine move cost
                softScore -= processAssignment.getMachineMoveCost() * globalPenaltyInfo.getMachineMoveCostWeight();
            }
        }

        private void removeProcessAssignment(MrProcessAssignment processAssignment) {
            undoBalancePenaltyCosts();
            for (MrMachineCapacityScorePart machineCapacityScorePart : machineCapacityScorePartList) {
                machineCapacityScorePart.removeProcessAssignment(processAssignment);
            }
            // Service conflict
            MrService service = processAssignment.getService();
            Integer serviceProcessCountInteger = serviceBag.get(service);
            int serviceProcessCount = serviceProcessCountInteger == null ? 0 : serviceProcessCountInteger;
            if (serviceProcessCount > 1) {
                hardScore += (serviceProcessCount - 1);
            }
            serviceProcessCount--;
            if (serviceProcessCount > 1) {
                hardScore -= (serviceProcessCount - 1);
            }
            serviceProcessCountInteger = serviceProcessCount == 0 ? null : serviceProcessCount;
            serviceBag.put(service, serviceProcessCountInteger);
            doBalancePenaltyCosts();
            // Move costs
            if (processAssignment.isMoved()) {
                // Process move cost
                softScore += processAssignment.getProcessMoveCost() * globalPenaltyInfo.getProcessMoveCostWeight();
                // Machine move cost
                softScore += processAssignment.getMachineMoveCost() * globalPenaltyInfo.getMachineMoveCostWeight();
            }
        }

        private void doBalancePenaltyCosts() {
            for (MrBalancePenalty balancePenalty : machineReassignment.getBalancePenaltyList()) {
                long originAvailable = machineCapacityScorePartList.get(balancePenalty.getOriginResource().getIndex())
                        .getBalanceAvailable();
                long targetAvailable = machineCapacityScorePartList.get(balancePenalty.getTargetResource().getIndex())
                        .getBalanceAvailable();
                if (originAvailable > 0L) {
                    long minimumTargetAvailable = originAvailable * balancePenalty.getMultiplicand();
                    // targetAvailable might be negative, but that's ok (and even avoids score traps)
                    if (targetAvailable < minimumTargetAvailable) {
                        softScore -= (minimumTargetAvailable - targetAvailable) * balancePenalty.getWeight();
                    }
                }
            }
        }

        private void undoBalancePenaltyCosts() {
            for (MrBalancePenalty balancePenalty : machineReassignment.getBalancePenaltyList()) {
                long originAvailable = machineCapacityScorePartList.get(balancePenalty.getOriginResource().getIndex())
                        .getBalanceAvailable();
                long targetAvailable = machineCapacityScorePartList.get(balancePenalty.getTargetResource().getIndex())
                        .getBalanceAvailable();
                if (originAvailable > 0L) {
                    long minimumTargetAvailable = originAvailable * balancePenalty.getMultiplicand();
                    // targetAvailable might be negative, but that's ok (and even avoids score traps)
                    if (targetAvailable < minimumTargetAvailable) {
                        softScore += (minimumTargetAvailable - targetAvailable) * balancePenalty.getWeight();
                    }
                }
            }
        }

    }

    private class MrMachineCapacityScorePart {

        private final MrMachineCapacity machineCapacity;
        private long maximumAvailable;
        private long safetyAvailable;
        private long balanceAvailable; // == maximumAvailable without transient

        private MrMachineCapacityScorePart(MrMachineCapacity machineCapacity) {
            this.machineCapacity = machineCapacity;
            maximumAvailable = machineCapacity.getMaximumCapacity();
            safetyAvailable = machineCapacity.getSafetyCapacity();
            balanceAvailable = machineCapacity.getMaximumCapacity();
        }

        private void initOriginalProcessAssignment(MrProcessAssignment processAssignment) {
            if (machineCapacity.isTransientlyConsumed()) {
                // Capacity constraints + Transient usage constraints
                long processUsage = processAssignment.getProcess().getProcessRequirement(machineCapacity.getResource())
                        .getUsage();
                hardScore -= Math.min(maximumAvailable, 0);
                maximumAvailable -= processUsage;
                hardScore += Math.min(maximumAvailable, 0);
            }
        }

        private void addProcessAssignment(MrProcessAssignment processAssignment) {
            MrResource resource = machineCapacity.getResource();
            long processUsage = processAssignment.getUsage(resource);
            if (!machineCapacity.isTransientlyConsumed() || processAssignment.isMoved()) {
                // Capacity constraints + Transient usage constraints
                hardScore -= Math.min(maximumAvailable, 0);
                maximumAvailable -= processUsage;
                hardScore += Math.min(maximumAvailable, 0);
            }
            // Load cost
            softScore -= Math.min(safetyAvailable, 0) * resource.getLoadCostWeight();
            safetyAvailable -= processUsage;
            softScore += Math.min(safetyAvailable, 0) * resource.getLoadCostWeight();
            balanceAvailable -= processUsage;
        }

        private void removeProcessAssignment(MrProcessAssignment processAssignment) {
            MrResource resource = machineCapacity.getResource();
            long processUsage = processAssignment.getUsage(resource);
            if (!machineCapacity.isTransientlyConsumed() || processAssignment.isMoved()) {
                // Capacity constraints + Transient usage constraints
                hardScore -= Math.min(maximumAvailable, 0);
                maximumAvailable += processUsage;
                hardScore += Math.min(maximumAvailable, 0);
            }
            // Load cost
            softScore -= Math.min(safetyAvailable, 0) * resource.getLoadCostWeight();
            safetyAvailable += processUsage;
            softScore += Math.min(safetyAvailable, 0) * resource.getLoadCostWeight();
            balanceAvailable += processUsage;
        }

        public long getBalanceAvailable() {
            return balanceAvailable;
        }

    }

    @Override
    public void resetWorkingSolution(MachineReassignment workingSolution, boolean constraintMatchEnabled) {
        resetWorkingSolution(workingSolution);
        // ignore constraintMatchEnabled, it is always presumed enabled
    }

    @Override
    public Collection<ConstraintMatchTotal> getConstraintMatchTotals() {
        LongConstraintMatchTotal maximumCapacityMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "maximumCapacity", 0);
        LongConstraintMatchTotal serviceConflictMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "serviceConflict", 0);
        LongConstraintMatchTotal serviceLocationSpreadMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "serviceLocationSpread", 0);
        LongConstraintMatchTotal serviceDependencyMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "serviceDependency", 0);
        LongConstraintMatchTotal loadCostMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "loadCost", 1);
        LongConstraintMatchTotal balanceCostMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "balanceCost", 1);
        LongConstraintMatchTotal processMoveCostMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "processMoveCost", 1);
        LongConstraintMatchTotal serviceMoveCostMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "serviceMoveCost", 1);
        LongConstraintMatchTotal machineMoveCostMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "machineMoveCost", 1);

        for (MrServiceScorePart serviceScorePart : serviceScorePartMap.values()) {
            MrService service = serviceScorePart.service;
            if (service.getLocationSpread() > serviceScorePart.locationBag.size()) {
                serviceLocationSpreadMatchTotal.addConstraintMatch(
                        Arrays.<Object>asList(service),
                        - (service.getLocationSpread() - serviceScorePart.locationBag.size()));
            }
        }
        for (MrMachineScorePart machineScorePart : machineScorePartMap.values()) {
            for (MrMachineCapacityScorePart machineCapacityScorePart : machineScorePart.machineCapacityScorePartList) {
                if (machineCapacityScorePart.maximumAvailable < 0L) {
                    maximumCapacityMatchTotal.addConstraintMatch(
                            Arrays.<Object>asList(machineCapacityScorePart.machineCapacity),
                            machineCapacityScorePart.maximumAvailable);
                }
                if (machineCapacityScorePart.safetyAvailable < 0L) {
                    loadCostMatchTotal.addConstraintMatch(
                            Arrays.<Object>asList(machineCapacityScorePart.machineCapacity),
                            machineCapacityScorePart.safetyAvailable
                                    * machineCapacityScorePart.machineCapacity.getResource().getLoadCostWeight());
                }
            }
            for (MrBalancePenalty balancePenalty : machineReassignment.getBalancePenaltyList()) {
                long originAvailable = machineScorePart.machineCapacityScorePartList
                        .get(balancePenalty.getOriginResource().getIndex()).getBalanceAvailable();
                long targetAvailable = machineScorePart.machineCapacityScorePartList
                        .get(balancePenalty.getTargetResource().getIndex()).getBalanceAvailable();
                if (originAvailable > 0L) {
                    long minimumTargetAvailable = originAvailable * balancePenalty.getMultiplicand();
                    // targetAvailable might be negative, but that's ok (and even avoids score traps)
                    if (targetAvailable < minimumTargetAvailable) {
                        balanceCostMatchTotal.addConstraintMatch(
                                Arrays.<Object>asList(machineScorePart.machine, balancePenalty),
                                - (minimumTargetAvailable - targetAvailable) * balancePenalty.getWeight());
                    }
                }
            }
            for (Map.Entry<MrService, Integer> entry : machineScorePart.serviceBag.entrySet()) {
                int serviceProcessCount = entry.getValue();
                serviceConflictMatchTotal.addConstraintMatch(
                        Arrays.<Object>asList(entry.getKey()),
                        - (serviceProcessCount - 1));

            }
        }
        for (MrProcessAssignment processAssignment : machineReassignment.getProcessAssignmentList()) {
            for (MrService toDependencyService : processAssignment.getService().getToDependencyServiceList()) {
                int toDependencyNeighborhoodProcessCount = serviceScorePartMap.get(toDependencyService)
                        .neighborhoodBag.get(processAssignment.getNeighborhood());
                if (toDependencyNeighborhoodProcessCount == 0) {
                    serviceDependencyMatchTotal.addConstraintMatch(
                            Arrays.<Object>asList(processAssignment, toDependencyService),
                            - 1);
                }
            }
            if (processAssignment.isMoved()) {
                processMoveCostMatchTotal.addConstraintMatch(
                        Arrays.<Object>asList(processAssignment),
                        - (processAssignment.getProcessMoveCost() * globalPenaltyInfo.getProcessMoveCostWeight()));
                machineMoveCostMatchTotal.addConstraintMatch(
                        Arrays.<Object>asList(processAssignment),
                        - (processAssignment.getMachineMoveCost() * globalPenaltyInfo.getMachineMoveCostWeight()));
            }
        }
        for (int i = 0; i < serviceMoveCost; i++) {
            serviceMoveCostMatchTotal.addConstraintMatch(
                    Arrays.<Object>asList(i),
                    - globalPenaltyInfo.getServiceMoveCostWeight());
        }

        List<ConstraintMatchTotal> constraintMatchTotalList = new ArrayList<ConstraintMatchTotal>(4);
        constraintMatchTotalList.add(maximumCapacityMatchTotal);
        constraintMatchTotalList.add(serviceConflictMatchTotal);
        constraintMatchTotalList.add(serviceLocationSpreadMatchTotal);
        constraintMatchTotalList.add(serviceDependencyMatchTotal);
        constraintMatchTotalList.add(loadCostMatchTotal);
        constraintMatchTotalList.add(balanceCostMatchTotal);
        constraintMatchTotalList.add(processMoveCostMatchTotal);
        constraintMatchTotalList.add(serviceMoveCostMatchTotal);
        constraintMatchTotalList.add(machineMoveCostMatchTotal);
        return constraintMatchTotalList;
    }

}
