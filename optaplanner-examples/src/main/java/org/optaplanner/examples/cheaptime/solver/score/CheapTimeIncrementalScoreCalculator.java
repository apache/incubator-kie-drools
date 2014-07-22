/*
 * Copyright 2014 JBoss Inc
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.PeriodPowerCost;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.domain.TaskRequirement;
import org.optaplanner.examples.cheaptime.solver.CheapTimeCostCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheapTimeIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<CheapTimeSolution> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private CheapTimeSolution cheapTimeSolution;

    private int resourceListSize;
    private int globalPeriodRangeTo;
    private Map<Machine, List<MachinePeriodPart>> machinePeriodListMap;

    private long hardScore;
    private long softScore;

    private Machine oldMachine = null;
    private Integer oldStartPeriod = null;

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void resetWorkingSolution(CheapTimeSolution solution) {
        this.cheapTimeSolution = solution;
        hardScore = 0L;
        softScore = 0L;
        if (solution.getGlobalPeriodRangeFrom() != 0) {
            throw new IllegalStateException("The globalPeriodRangeFrom (" + solution.getGlobalPeriodRangeFrom()
                    + ") should be 0.");
        }
        resourceListSize = solution.getResourceList().size();
        globalPeriodRangeTo = solution.getGlobalPeriodRangeTo();
        List<Machine> machineList = solution.getMachineList();
        List<PeriodPowerCost> periodPowerCostList = solution.getPeriodPowerCostList();
        machinePeriodListMap = new LinkedHashMap<Machine, List<MachinePeriodPart>>(machineList.size());
        for (Machine machine : machineList) {
            List<MachinePeriodPart> machinePeriodList = new ArrayList<MachinePeriodPart>(globalPeriodRangeTo);
            for (int period = 0; period < globalPeriodRangeTo; period++) {
                machinePeriodList.add(new MachinePeriodPart(machine, periodPowerCostList.get(period)));
            }
            machinePeriodListMap.put(machine, machinePeriodList);
        }
        List<MachinePeriodPart> unassignedMachinePeriodList = new ArrayList<MachinePeriodPart>(globalPeriodRangeTo);
        for (int period = 0; period < globalPeriodRangeTo; period++) {
            unassignedMachinePeriodList.add(new MachinePeriodPart(null, periodPowerCostList.get(period)));
        }
        machinePeriodListMap.put(null, unassignedMachinePeriodList);
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
        modifyMachine(taskAssignment, null, taskAssignment.getMachine());
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
        modifyMachine(taskAssignment, oldMachine, null);
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
            List<MachinePeriodPart> machinePeriodList = machinePeriodListMap.get(oldMachine);
            retractRange(taskAssignment, machinePeriodList, startPeriod, endPeriod, false);
        }
        if (newMachine != null) {
            List<MachinePeriodPart> machinePeriodList = machinePeriodListMap.get(newMachine);
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
        Machine machine = taskAssignment.getMachine();
        List<MachinePeriodPart> machinePeriodList = machinePeriodListMap.get(machine);
        if (retractStart != retractEnd) {
            retractRange(taskAssignment, machinePeriodList, retractStart, retractEnd, true);
        }
        if (insertStart != insertEnd) {
            insertRange(taskAssignment, machinePeriodList, insertStart, insertEnd, true);
        }
    }

    private void retractRange(TaskAssignment taskAssignment, List<MachinePeriodPart> machinePeriodList,
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
            previousStatus = machinePeriodList.get(startPeriod - 1).status;
            if (previousStatus == MachinePeriodStatus.IDLE) {
                idleAvailable = spinUpDownCostMicros;
                for (int i = startPeriod - 1; i >= 0; i--) {
                    MachinePeriodPart machinePeriod = machinePeriodList.get(i);
                    if (machinePeriod.status.isActive()) {
                        idlePeriodStart = i + 1;
                        break;
                    }
                    machinePeriod.status = MachinePeriodStatus.OFF;
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
            MachinePeriodPart machinePeriod = machinePeriodList.get(i);
            machinePeriod.retractTaskAssignment(taskAssignment);
            if (retractTaskCost) {
                softScore += CheapTimeCostCalculator.multiplyTwoMicros(powerConsumptionMicros,
                        machinePeriod.periodPowerCostMicros);
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
                        machinePeriodList.get(j).makeIdle();
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
        if (endPeriod < globalPeriodRangeTo && machinePeriodList.get(endPeriod).status != MachinePeriodStatus.OFF
                && !previousStatus.isActive()) {
            for (int i = endPeriod; i < globalPeriodRangeTo; i++) {
                MachinePeriodPart machinePeriod = machinePeriodList.get(i);
                if (machinePeriod.status.isActive()) {
                    if (previousStatus == MachinePeriodStatus.OFF) {
                        machinePeriod.spinUp();
                    } else if (previousStatus == MachinePeriodStatus.IDLE) {
                        // Create idle period
                        for (int j = idlePeriodStart; j < i; j++) {
                            machinePeriodList.get(j).makeIdle();
                        }
                    }
                    break;
                } else if (machinePeriod.status == MachinePeriodStatus.IDLE) {
                    machinePeriod.status = MachinePeriodStatus.OFF;
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

    private void insertRange(TaskAssignment taskAssignment, List<MachinePeriodPart> machinePeriodList,
            int startPeriod, int endPeriod, boolean insertTaskCost) {
        long powerConsumptionMicros = taskAssignment.getTask().getPowerConsumptionMicros();
        MachinePeriodPart startMachinePeriod = machinePeriodList.get(startPeriod);
        boolean startIsOff = startMachinePeriod.status == MachinePeriodStatus.OFF;
        boolean lastIsOff = machinePeriodList.get(endPeriod - 1).status == MachinePeriodStatus.OFF;
        for (int i = startPeriod; i < endPeriod; i++) {
            MachinePeriodPart machinePeriod = machinePeriodList.get(i);
            machinePeriod.insertTaskAssignment(taskAssignment);
            if (insertTaskCost) {
                softScore -= CheapTimeCostCalculator.multiplyTwoMicros(powerConsumptionMicros,
                        machinePeriod.periodPowerCostMicros);
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
                MachinePeriodPart machinePeriod = machinePeriodList.get(i);
                if (machinePeriod.status.isActive()) {
                    idlePeriodStart = i + 1;
                    break;
                }
                idleAvailable -= machinePeriod.machineCostMicros;
            }
            if (idlePeriodStart >= 0) {
                // Create idle period
                for (int i = idlePeriodStart; i < startPeriod; i++) {
                    machinePeriodList.get(i).makeIdle();
                }
            } else {
                startMachinePeriod.spinUp();
            }
        }
        if (lastIsOff) {
            long idleAvailable = taskAssignment.getMachine().getSpinUpDownCostMicros();
            int idlePeriodEnd = Integer.MIN_VALUE;
            for (int i = endPeriod; i < globalPeriodRangeTo && idleAvailable >= 0L; i++) {
                MachinePeriodPart machinePeriod = machinePeriodList.get(i);
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
                    machinePeriodList.get(i).makeIdle();
                }
            }
        }
    }

    public HardSoftLongScore calculateScore() {
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }

    private class MachinePeriodPart {

        private final Machine machine;
        private final int period;
        private final long periodPowerCostMicros;
        private final long machineCostMicros;

        private int taskCount;
        private MachinePeriodStatus status;
        private int[] resourceAvailableList;

        private MachinePeriodPart(Machine machine, PeriodPowerCost periodPowerCost) {
            this.machine = machine;
            this.period = periodPowerCost.getPeriod();
            this.periodPowerCostMicros = periodPowerCost.getPowerCostMicros();
            taskCount = 0;
            status = MachinePeriodStatus.OFF;
            if (machine != null) {
                resourceAvailableList = new int[resourceListSize];
                for (int i = 0; i < resourceListSize; i++) {
                    resourceAvailableList[i] = machine.getMachineCapacityList().get(i).getCapacity();
                }
                machineCostMicros = CheapTimeCostCalculator.multiplyTwoMicros(machine.getPowerConsumptionMicros(),
                        periodPowerCostMicros);
            } else {
                machineCostMicros = Long.MIN_VALUE;
            }
        }

        public void spinUp() {
            if (status != MachinePeriodStatus.STILL_ACTIVE) {
                throw new IllegalStateException("Impossible status (" + status + ").");
            }
            softScore -= machine.getSpinUpDownCostMicros();
            status = MachinePeriodStatus.SPIN_UP_AND_ACTIVE;
        }

        public void undoSpinUp() {
            if (status != MachinePeriodStatus.SPIN_UP_AND_ACTIVE) {
                throw new IllegalStateException("Impossible status (" + status + ").");
            }
            softScore += machine.getSpinUpDownCostMicros();
            status = MachinePeriodStatus.STILL_ACTIVE;
        }

        public void makeIdle() {
            if (status != MachinePeriodStatus.OFF) {
                throw new IllegalStateException("Impossible status (" + status + ").");
            }
            softScore -= machineCostMicros;
            status = MachinePeriodStatus.IDLE;
        }

        public void insertTaskAssignment(TaskAssignment taskAssignment) {
            if (machine == null) {
                return;
            }
            Task task = taskAssignment.getTask();
            if (status == MachinePeriodStatus.OFF) {
                softScore -= machineCostMicros;
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
                softScore += machineCostMicros;
                if (status == MachinePeriodStatus.SPIN_UP_AND_ACTIVE) {
                    softScore += machine.getSpinUpDownCostMicros();
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
