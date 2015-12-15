/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.cheaptime.solver.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.primlong.LongConstraintMatchTotal;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.ConstraintMatchAwareIncrementalScoreCalculator;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.PeriodPowerPrice;
import org.optaplanner.examples.cheaptime.domain.Resource;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.domain.TaskRequirement;
import org.optaplanner.examples.cheaptime.solver.CheapTimeCostCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheapTimeIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<CheapTimeSolution>
        implements ConstraintMatchAwareIncrementalScoreCalculator<CheapTimeSolution> {

    protected static final String CONSTRAINT_PACKAGE = "org.optaplanner.examples.cheaptime.solver";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private CheapTimeSolution cheapTimeSolution;

    private int resourceListSize;
    private int globalPeriodRangeTo;
    private MachinePeriodPart[][] machineToMachinePeriodListMap; // Map<List<>> replaced by array[][] for performance
    private MachinePeriodPart[] unassignedMachinePeriodList; // List<> replaced by array[] for performance

    private long hardScore;
    private long mediumScore;
    private long softScore;

    private Machine oldMachine = null;
    private Integer oldStartPeriod = null;

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void resetWorkingSolution(CheapTimeSolution solution) {
        this.cheapTimeSolution = solution;
        hardScore = 0L;
        mediumScore = 0L;
        softScore = 0L;
        if (solution.getGlobalPeriodRangeFrom() != 0) {
            throw new IllegalStateException("The globalPeriodRangeFrom (" + solution.getGlobalPeriodRangeFrom()
                    + ") should be 0.");
        }
        resourceListSize = solution.getResourceList().size();
        globalPeriodRangeTo = solution.getGlobalPeriodRangeTo();
        List<Machine> machineList = solution.getMachineList();
        List<PeriodPowerPrice> periodPowerPriceList = solution.getPeriodPowerPriceList();
        machineToMachinePeriodListMap = new MachinePeriodPart[machineList.size()][];
        for (Machine machine : machineList) {
            MachinePeriodPart[] machinePeriodList = new MachinePeriodPart[globalPeriodRangeTo];
            for (int period = 0; period < globalPeriodRangeTo; period++) {
                machinePeriodList[period] = new MachinePeriodPart(machine, periodPowerPriceList.get(period));
            }
            machineToMachinePeriodListMap[machine.getIndex()] = machinePeriodList;
        }
        unassignedMachinePeriodList = new MachinePeriodPart[globalPeriodRangeTo];
        for (int period = 0; period < globalPeriodRangeTo; period++) {
            unassignedMachinePeriodList[period] = new MachinePeriodPart(null, periodPowerPriceList.get(period));
        }
        for (TaskAssignment taskAssignment : solution.getTaskAssignmentList()) {
            // Do not do modifyMachine(taskAssignment, null, taskAssignment.getMachine());
            // because modifyStartPeriod does all it's effects too
            modifyStartPeriod(taskAssignment, null, taskAssignment.getStartPeriod());
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        TaskAssignment taskAssignment = (TaskAssignment) entity;
        // Do not do modifyMachine(taskAssignment, null, taskAssignment.getMachine());
        // because modifyStartPeriod does all it's effects too
        modifyStartPeriod(taskAssignment, null, taskAssignment.getStartPeriod());
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        TaskAssignment taskAssignment = (TaskAssignment) entity;
        if (variableName.equals("machine")) {
            oldMachine = taskAssignment.getMachine();
        } else if (variableName.equals("startPeriod")) {
            oldStartPeriod = taskAssignment.getStartPeriod();
        } else {
            throw new IllegalArgumentException("The variableName (" + variableName + ") is not supported.");
        }
    }

    public void afterVariableChanged(Object entity, String variableName) {
        TaskAssignment taskAssignment = (TaskAssignment) entity;
        if (variableName.equals("machine")) {
            modifyMachine(taskAssignment, oldMachine, taskAssignment.getMachine());
        } else if (variableName.equals("startPeriod")) {
            modifyStartPeriod(taskAssignment, oldStartPeriod, taskAssignment.getStartPeriod());
        } else {
            throw new IllegalArgumentException("The variableName (" + variableName + ") is not supported.");
        }
    }

    public void beforeEntityRemoved(Object entity) {
        TaskAssignment taskAssignment = (TaskAssignment) entity;
        oldMachine = taskAssignment.getMachine();
        oldStartPeriod = taskAssignment.getStartPeriod();
    }

    public void afterEntityRemoved(Object entity) {
        TaskAssignment taskAssignment = (TaskAssignment) entity;
        // Do not do modifyMachine(taskAssignment, oldMachine, null);
        // because modifyStartPeriod does all it's effects too
        modifyStartPeriod(taskAssignment, oldStartPeriod, null);
    }

    // ************************************************************************
    // Modify methods
    // ************************************************************************

    private void modifyMachine(TaskAssignment taskAssignment, Machine oldMachine, Machine newMachine) {
        if (ObjectUtils.equals(oldMachine, newMachine)) {
            return;
        }
        Integer startPeriod = taskAssignment.getStartPeriod();
        if (startPeriod == null) {
            return;
        }
        Integer endPeriod = taskAssignment.getEndPeriod();
        if (oldMachine != null) {
            MachinePeriodPart[] machinePeriodList = machineToMachinePeriodListMap[oldMachine.getIndex()];
            retractRange(taskAssignment, machinePeriodList, startPeriod, endPeriod, false);
        }
        if (newMachine != null) {
            MachinePeriodPart[] machinePeriodList = machineToMachinePeriodListMap[newMachine.getIndex()];
            insertRange(taskAssignment, machinePeriodList, startPeriod, endPeriod, false);
        }
    }

    private void modifyStartPeriod(TaskAssignment taskAssignment, Integer oldStartPeriod, Integer newStartPeriod) {
        if (ObjectUtils.equals(oldStartPeriod, newStartPeriod)) {
            return;
        }
        Task task = taskAssignment.getTask();
        int duration = task.getDuration();
        int retractStart;
        int retractEnd;
        int insertStart;
        int insertEnd;
        if (oldStartPeriod == null) {
            retractStart = -1;
            retractEnd = -1;
            insertStart = newStartPeriod;
            insertEnd = insertStart + duration;
        } else if (newStartPeriod == null) {
            retractStart = oldStartPeriod;
            retractEnd = retractStart + duration;
            insertStart = -1;
            insertEnd = -1;
        } else {
            retractStart = oldStartPeriod;
            retractEnd = retractStart + duration;
            insertStart = newStartPeriod;
            insertEnd = insertStart + duration;
            if (oldStartPeriod < newStartPeriod) {
                if (insertStart < retractEnd) {
                    int overlap = retractEnd - insertStart;
                    retractEnd -= overlap;
                    insertStart += overlap;
                }
            } else {
                if (retractStart < insertEnd) {
                    int overlap = insertEnd - retractStart;
                    insertEnd -= overlap;
                    retractStart += overlap;
                }
            }
        }
        if (oldStartPeriod != null) {
            softScore += oldStartPeriod;
        }
        if (newStartPeriod != null) {
            softScore -= newStartPeriod;
        }
        Machine machine = taskAssignment.getMachine();
        MachinePeriodPart[] machinePeriodList;
        if (machine != null) {
            machinePeriodList = machineToMachinePeriodListMap[machine.getIndex()];
        } else {
            machinePeriodList = unassignedMachinePeriodList;
        }
        if (retractStart != retractEnd) {
            retractRange(taskAssignment, machinePeriodList, retractStart, retractEnd, true);
        }
        if (insertStart != insertEnd) {
            insertRange(taskAssignment, machinePeriodList, insertStart, insertEnd, true);
        }
    }

    private void retractRange(TaskAssignment taskAssignment, MachinePeriodPart[] machinePeriodList,
            int startPeriod, int endPeriod, boolean retractTaskCost) {
        long powerConsumptionMicros = taskAssignment.getTask().getPowerConsumptionMicros();
        long spinUpDownCostMicros = taskAssignment.getMachine().getSpinUpDownCostMicros();

        MachinePeriodStatus previousStatus;
        int idlePeriodStart = Integer.MIN_VALUE;
        long idleAvailable;
        if (startPeriod == 0) {
            previousStatus = MachinePeriodStatus.OFF;
            idleAvailable = Long.MIN_VALUE;
        } else {
            previousStatus = machinePeriodList[startPeriod - 1].status;
            if (previousStatus == MachinePeriodStatus.IDLE) {
                idleAvailable = spinUpDownCostMicros;
                for (int i = startPeriod - 1; i >= 0; i--) {
                    MachinePeriodPart machinePeriod = machinePeriodList[i];
                    if (machinePeriod.status.isActive()) {
                        idlePeriodStart = i + 1;
                        break;
                    }
                    machinePeriod.undoMakeIdle();
                    idleAvailable -= machinePeriod.machineCostMicros;
                }
                if (idleAvailable < 0L) {
                    throw new IllegalStateException("The range of idlePeriodStart (" + idlePeriodStart
                            + ") to startPeriod (" + startPeriod
                            + ") should have been IDLE because the idleAvailable (" + idleAvailable
                            + ") is negative.");
                }
            } else {
                idleAvailable = Long.MIN_VALUE;
            }
        }
        for (int i = startPeriod; i < endPeriod; i++) {
            MachinePeriodPart machinePeriod = machinePeriodList[i];
            machinePeriod.retractTaskAssignment(taskAssignment);
            if (retractTaskCost) {
                mediumScore += CheapTimeCostCalculator.multiplyTwoMicros(powerConsumptionMicros,
                        machinePeriod.periodPowerPriceMicros);
            }
            // SpinUp vs idle
            if (machinePeriod.status.isActive()) {
                if (previousStatus == MachinePeriodStatus.OFF) {
                    // Only if (startPeriod == i), it could be SPIN_UP_AND_ACTIVE
                    if (machinePeriod.status != MachinePeriodStatus.SPIN_UP_AND_ACTIVE) {
                        machinePeriod.spinUp();
                    }
                } else if (previousStatus == MachinePeriodStatus.IDLE) {
                    // Create idle period
                    for (int j = idlePeriodStart; j < i; j++) {
                        machinePeriodList[j].makeIdle();
                    }
                    idlePeriodStart = Integer.MIN_VALUE;
                    idleAvailable = Long.MIN_VALUE;
                }
                previousStatus = MachinePeriodStatus.STILL_ACTIVE;
            } else if (machinePeriod.status == MachinePeriodStatus.OFF) {
                if (previousStatus != MachinePeriodStatus.OFF) {
                    if (previousStatus.isActive()) {
                        idlePeriodStart = i;
                        idleAvailable = spinUpDownCostMicros;
                    }
                    idleAvailable -= machinePeriod.machineCostMicros;
                    if (idleAvailable < 0) {
                        previousStatus = MachinePeriodStatus.OFF;
                        idlePeriodStart = Integer.MIN_VALUE;
                        idleAvailable = Long.MIN_VALUE;
                    } else {
                        previousStatus = MachinePeriodStatus.IDLE;
                    }
                }
            } else {
                throw new IllegalStateException("Impossible status (" + machinePeriod.status + ").");
            }
        }
        if (endPeriod < globalPeriodRangeTo && machinePeriodList[endPeriod].status != MachinePeriodStatus.OFF
                && !previousStatus.isActive()) {
            for (int i = endPeriod; i < globalPeriodRangeTo; i++) {
                MachinePeriodPart machinePeriod = machinePeriodList[i];
                if (machinePeriod.status.isActive()) {
                    if (previousStatus == MachinePeriodStatus.OFF) {
                        machinePeriod.spinUp();
                    } else if (previousStatus == MachinePeriodStatus.IDLE) {
                        // Create idle period
                        for (int j = idlePeriodStart; j < i; j++) {
                            machinePeriodList[j].makeIdle();
                        }
                    }
                    break;
                } else if (machinePeriod.status == MachinePeriodStatus.IDLE) {
                    machinePeriod.undoMakeIdle();
                    if (previousStatus == MachinePeriodStatus.IDLE) {
                        idleAvailable -= machinePeriod.machineCostMicros;
                        if (idleAvailable < 0) {
                            previousStatus = MachinePeriodStatus.OFF;
                            idlePeriodStart = Integer.MIN_VALUE;
                            idleAvailable = Long.MIN_VALUE;
                        }
                    }
                } else {
                    throw new IllegalStateException("Impossible status (" + machinePeriod.status + ").");
                }
            }
        }
    }

    private void insertRange(TaskAssignment taskAssignment, MachinePeriodPart[] machinePeriodList,
            int startPeriod, int endPeriod, boolean insertTaskCost) {
        long powerConsumptionMicros = taskAssignment.getTask().getPowerConsumptionMicros();
        MachinePeriodPart startMachinePeriod = machinePeriodList[startPeriod];
        boolean startIsOff = startMachinePeriod.status == MachinePeriodStatus.OFF;
        boolean lastIsOff = machinePeriodList[endPeriod - 1].status == MachinePeriodStatus.OFF;
        for (int i = startPeriod; i < endPeriod; i++) {
            MachinePeriodPart machinePeriod = machinePeriodList[i];
            machinePeriod.insertTaskAssignment(taskAssignment);
            if (insertTaskCost) {
                mediumScore -= CheapTimeCostCalculator.multiplyTwoMicros(powerConsumptionMicros,
                        machinePeriod.periodPowerPriceMicros);
            }
            // SpinUp vs idle
            if (machinePeriod.status == MachinePeriodStatus.SPIN_UP_AND_ACTIVE && i != startPeriod) {
                machinePeriod.undoSpinUp();
            }
        }
        // SpinUp vs idle
        if (startIsOff) {
            long idleAvailable = taskAssignment.getMachine().getSpinUpDownCostMicros();
            int idlePeriodStart = Integer.MIN_VALUE;
            for (int i = startPeriod - 1; i >= 0 && idleAvailable >= 0L; i--) {
                MachinePeriodPart machinePeriod = machinePeriodList[i];
                if (machinePeriod.status.isActive()) {
                    idlePeriodStart = i + 1;
                    break;
                }
                idleAvailable -= machinePeriod.machineCostMicros;
            }
            if (idlePeriodStart >= 0) {
                // Create idle period
                for (int i = idlePeriodStart; i < startPeriod; i++) {
                    machinePeriodList[i].makeIdle();
                }
            } else {
                startMachinePeriod.spinUp();
            }
        }
        if (lastIsOff) {
            long idleAvailable = taskAssignment.getMachine().getSpinUpDownCostMicros();
            int idlePeriodEnd = Integer.MIN_VALUE;
            for (int i = endPeriod; i < globalPeriodRangeTo && idleAvailable >= 0L; i++) {
                MachinePeriodPart machinePeriod = machinePeriodList[i];
                if (machinePeriod.status.isActive()) {
                    idlePeriodEnd = i;
                    machinePeriod.undoSpinUp();
                    break;
                }
                idleAvailable -= machinePeriod.machineCostMicros;
            }
            if (idlePeriodEnd >= 0) {
                // Create idle period
                for (int i = endPeriod; i < idlePeriodEnd; i++) {
                    machinePeriodList[i].makeIdle();
                }
            }
        }
    }

    public HardMediumSoftLongScore calculateScore() {
        return HardMediumSoftLongScore.valueOf(hardScore, mediumScore, softScore);
    }

    private class MachinePeriodPart {

        private final Machine machine;
        private final int period;
        private final long periodPowerPriceMicros;
        private final long machineCostMicros;

        private int taskCount;
        private MachinePeriodStatus status;
        private int[] resourceAvailableList; // List<> replaced by array[] for performance

        private MachinePeriodPart(Machine machine, PeriodPowerPrice periodPowerPrice) {
            this.machine = machine;
            this.period = periodPowerPrice.getPeriod();
            this.periodPowerPriceMicros = periodPowerPrice.getPowerPriceMicros();
            taskCount = 0;
            status = MachinePeriodStatus.OFF;
            if (machine != null) {
                resourceAvailableList = new int[resourceListSize];
                for (int i = 0; i < resourceListSize; i++) {
                    resourceAvailableList[i] = machine.getMachineCapacityList().get(i).getCapacity();
                }
                machineCostMicros = CheapTimeCostCalculator.multiplyTwoMicros(machine.getPowerConsumptionMicros(),
                        periodPowerPriceMicros);
            } else {
                machineCostMicros = Long.MIN_VALUE;
            }
        }

        public void spinUp() {
            if (status != MachinePeriodStatus.STILL_ACTIVE) {
                throw new IllegalStateException("Impossible status (" + status + ").");
            }
            mediumScore -= machine.getSpinUpDownCostMicros();
            status = MachinePeriodStatus.SPIN_UP_AND_ACTIVE;
        }

        public void undoSpinUp() {
            if (status != MachinePeriodStatus.SPIN_UP_AND_ACTIVE) {
                throw new IllegalStateException("Impossible status (" + status + ").");
            }
            mediumScore += machine.getSpinUpDownCostMicros();
            status = MachinePeriodStatus.STILL_ACTIVE;
        }

        public void makeIdle() {
            if (status != MachinePeriodStatus.OFF) {
                throw new IllegalStateException("Impossible status (" + status + ").");
            }
            mediumScore -= machineCostMicros;
            status = MachinePeriodStatus.IDLE;
        }

        public void undoMakeIdle() {
            if (status != MachinePeriodStatus.IDLE) {
                throw new IllegalStateException("Impossible status (" + status + ").");
            }
            mediumScore += machineCostMicros;
            status = MachinePeriodStatus.OFF;
        }

        public void insertTaskAssignment(TaskAssignment taskAssignment) {
            if (machine == null) {
                return;
            }
            Task task = taskAssignment.getTask();
            if (status == MachinePeriodStatus.OFF) {
                mediumScore -= machineCostMicros;
                status = MachinePeriodStatus.STILL_ACTIVE;
            } else if (status == MachinePeriodStatus.IDLE) {
                status = MachinePeriodStatus.STILL_ACTIVE;
            }
            taskCount++;
            for (int i = 0; i < resourceAvailableList.length; i++) {
                int resourceAvailable = resourceAvailableList[i];
                TaskRequirement taskRequirement = task.getTaskRequirementList().get(i);
                if (resourceAvailable < 0) {
                    hardScore -= resourceAvailable;
                }
                resourceAvailable -= taskRequirement.getResourceUsage();
                if (resourceAvailable < 0) {
                    hardScore += resourceAvailable;
                }
                resourceAvailableList[i] = resourceAvailable;
            }
        }

        public void retractTaskAssignment(TaskAssignment taskAssignment) {
            if (machine == null) {
                return;
            }
            Task task = taskAssignment.getTask();
            if (status == MachinePeriodStatus.OFF || status == MachinePeriodStatus.IDLE) {
                throw new IllegalStateException("Impossible status (" + status + ").");
            }
            taskCount--;
            if (taskCount == 0) {
                mediumScore += machineCostMicros;
                if (status == MachinePeriodStatus.SPIN_UP_AND_ACTIVE) {
                    mediumScore += machine.getSpinUpDownCostMicros();
                }
                status = MachinePeriodStatus.OFF;
            }
            for (int i = 0; i < resourceAvailableList.length; i++) {
                int resourceAvailable = resourceAvailableList[i];
                TaskRequirement taskRequirement = task.getTaskRequirementList().get(i);
                if (resourceAvailable < 0) {
                    hardScore -= resourceAvailable;
                }
                resourceAvailable += taskRequirement.getResourceUsage();
                if (resourceAvailable < 0) {
                    hardScore += resourceAvailable;
                }
                resourceAvailableList[i] = resourceAvailable;
            }
        }

        @Override
        public String toString() {
            return status.name() + " (" + taskCount + " tasks)";
        }

    }

    // ************************************************************************
    // ConstraintMatchAwareIncrementalScoreCalculator methods
    // ************************************************************************

    @Override
    public void resetWorkingSolution(CheapTimeSolution workingSolution, boolean constraintMatchEnabled) {
        resetWorkingSolution(workingSolution);
        // ignore constraintMatchEnabled, it is always presumed enabled
    }

    @Override
    public Collection<ConstraintMatchTotal> getConstraintMatchTotals() {
        List<Resource> resourceList = cheapTimeSolution.getResourceList();
        LongConstraintMatchTotal resourceCapacityMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "resourceCapacity", 0);
        LongConstraintMatchTotal spinUpDownMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "spinUpDown", 1);
        LongConstraintMatchTotal machineConsumptionMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "machineConsumption", 1);
        LongConstraintMatchTotal taskConsumptionMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "taskConsumption", 1);
        LongConstraintMatchTotal minimizeTaskStartPeriodMatchTotal = new LongConstraintMatchTotal(
                CONSTRAINT_PACKAGE, "minimizeTaskStartPeriod", 2);
        for (Machine machine : cheapTimeSolution.getMachineList()) {
            for (int period = 0; period < globalPeriodRangeTo; period++) {
                MachinePeriodPart machinePeriod = machineToMachinePeriodListMap[machine.getIndex()][period];
                for (int i = 0; i < machinePeriod.resourceAvailableList.length; i++) {
                    int resourceAvailable = machinePeriod.resourceAvailableList[i];
                    if (resourceAvailable < 0) {
                        resourceCapacityMatchTotal.addConstraintMatch(
                                Arrays.<Object>asList(machine, period, resourceList.get(i)),
                                resourceAvailable);
                    }
                }
                if (machinePeriod.status == MachinePeriodStatus.SPIN_UP_AND_ACTIVE) {
                    spinUpDownMatchTotal.addConstraintMatch(
                            Arrays.<Object>asList(machine, period),
                            - machine.getSpinUpDownCostMicros());
                }
                if (machinePeriod.status != MachinePeriodStatus.OFF) {
                    machineConsumptionMatchTotal.addConstraintMatch(
                            Arrays.<Object>asList(machine, period),
                            - machinePeriod.machineCostMicros);
                }
            }
        }
        // Individual taskConsumption isn't tracked for performance
        taskConsumptionMatchTotal.addConstraintMatch(Arrays.<Object>asList(),
                mediumScore - spinUpDownMatchTotal.getWeightTotal() - machineConsumptionMatchTotal.getWeightTotal());
        // Individual taskStartPeriod isn't tracked for performance
        // but we mimic it
        for (TaskAssignment taskAssignment : cheapTimeSolution.getTaskAssignmentList()) {
            Integer startPeriod = taskAssignment.getStartPeriod();
            if (startPeriod != null) {
                minimizeTaskStartPeriodMatchTotal.addConstraintMatch(Arrays.<Object>asList(taskAssignment),
                        - startPeriod);
            }

        }

        List<ConstraintMatchTotal> constraintMatchTotalList = new ArrayList<ConstraintMatchTotal>(4);
        constraintMatchTotalList.add(resourceCapacityMatchTotal);
        constraintMatchTotalList.add(spinUpDownMatchTotal);
        constraintMatchTotalList.add(machineConsumptionMatchTotal);
        constraintMatchTotalList.add(taskConsumptionMatchTotal);
        constraintMatchTotalList.add(minimizeTaskStartPeriodMatchTotal);
        return constraintMatchTotalList;
    }

    private enum MachinePeriodStatus {
        OFF,
        IDLE,
        SPIN_UP_AND_ACTIVE,
        STILL_ACTIVE;

        public boolean isActive() {
            return this == MachinePeriodStatus.STILL_ACTIVE || this == MachinePeriodStatus.SPIN_UP_AND_ACTIVE;
        }

    }

}
