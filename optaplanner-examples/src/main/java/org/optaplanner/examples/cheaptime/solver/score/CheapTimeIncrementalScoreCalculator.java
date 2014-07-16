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
import org.optaplanner.examples.cheaptime.solver.CostCalculator;
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
            for (int period = startPeriod; period < endPeriod; period++) {
                MachinePeriodPart machinePeriodPart = machinePeriodList.get(period);
                machinePeriodPart.removeTaskAssignment(taskAssignment);
            }
        }
        if (newMachine != null) {
            List<MachinePeriodPart> machinePeriodList = machinePeriodListMap.get(newMachine);
            for (int period = startPeriod; period < endPeriod; period++) {
                MachinePeriodPart machinePeriodPart = machinePeriodList.get(period);
                machinePeriodPart.addTaskAssignment(taskAssignment);
            }
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
        for (int period = retractStart; period < retractEnd; period++) {
            MachinePeriodPart machinePeriodPart = machinePeriodList.get(period);
            machinePeriodPart.removeTaskAssignment(taskAssignment);
            softScore += CostCalculator.multiplyTwoMicros(task.getPowerConsumptionMicros(),
                    machinePeriodPart.periodPowerCostMicros);
        }
        for (int period = insertStart; period < insertEnd; period++) {
            MachinePeriodPart machinePeriodPart = machinePeriodList.get(period);
            machinePeriodPart.addTaskAssignment(taskAssignment);
            softScore -= CostCalculator.multiplyTwoMicros(task.getPowerConsumptionMicros(),
                    machinePeriodPart.periodPowerCostMicros);
        }
    }

    public HardSoftLongScore calculateScore() {
        return HardSoftLongScore.valueOf(hardScore, softScore);
    }

    private class MachinePeriodPart {

        private final Machine machine;
        private final int period;
        private final long periodPowerCostMicros;

        private int taskCount;
        private List<Integer> resourceAvailableList;

        private MachinePeriodPart(Machine machine, PeriodPowerCost periodPowerCost) {
            this.machine = machine;
            this.period = periodPowerCost.getPeriod();
            this.periodPowerCostMicros = periodPowerCost.getPowerCostMicros();
            taskCount = 0;
            if (machine != null) {
                resourceAvailableList = new ArrayList<Integer>(resourceListSize);
                for (int i = 0; i < resourceListSize; i++) {
                    resourceAvailableList.add(machine.getMachineCapacityList().get(i).getCapacity());
                }
            }
        }

        public void addTaskAssignment(TaskAssignment taskAssignment) {
            if (machine == null) {
                return;
            }
            Task task = taskAssignment.getTask();
            if (taskCount == 0) {
                softScore -= CostCalculator.multiplyTwoMicros(machine.getPowerConsumptionMicros(),
                        periodPowerCostMicros);
            }
            taskCount++;
            for (int i = 0; i < resourceAvailableList.size(); i++) {
                int resourceAvailable = resourceAvailableList.get(i);
                TaskRequirement taskRequirement = task.getTaskRequirementList().get(i);
                hardScore -= Math.min(resourceAvailable, 0);
                resourceAvailable -= taskRequirement.getResourceUsage();
                resourceAvailableList.set(i, resourceAvailable);
                hardScore += Math.min(resourceAvailable, 0);
            }
        }

        public void removeTaskAssignment(TaskAssignment taskAssignment) {
            if (machine == null) {
                return;
            }
            Task task = taskAssignment.getTask();
            if (taskCount == 1) {
                softScore += CostCalculator.multiplyTwoMicros(machine.getPowerConsumptionMicros(),
                        periodPowerCostMicros);
            }
            taskCount--;
            for (int i = 0; i < resourceAvailableList.size(); i++) {
                int resourceAvailable = resourceAvailableList.get(i);
                TaskRequirement taskRequirement = task.getTaskRequirementList().get(i);
                hardScore -= Math.min(resourceAvailable, 0);
                resourceAvailable += taskRequirement.getResourceUsage();
                resourceAvailableList.set(i, resourceAvailable);
                hardScore += Math.min(resourceAvailable, 0);
            }
        }

    }

}
