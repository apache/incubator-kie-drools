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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.PeriodPowerCost;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.domain.TaskRequirement;
import org.optaplanner.examples.cheaptime.solver.CostCalculator;
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

public class CheapTimeIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<CheapTimeSolution> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private CheapTimeSolution cheapTimeSolution;

    private int resourceListSize;
    private int globalPeriodRangeTo;
    private Map<Machine, List<MachinePeriodPart>> machinePeriodListMap;

    private long hardScore;
    private long softScore;

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
        for (TaskAssignment taskAssignment : solution.getTaskAssignmentList()) {
            insert(taskAssignment);
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        insert((TaskAssignment) entity);
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        retract((TaskAssignment) entity);
    }

    public void afterVariableChanged(Object entity, String variableName) {
        insert((TaskAssignment) entity);
    }

    public void beforeEntityRemoved(Object entity) {
        retract((TaskAssignment) entity);
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    private void insert(TaskAssignment taskAssignment) {
        Machine machine = taskAssignment.getMachine();
        Integer startPeriod = taskAssignment.getStartPeriod();
        if (machine != null && startPeriod != null) {
            List<MachinePeriodPart> machinePeriodList = machinePeriodListMap.get(machine);
            int endPeriod = taskAssignment.getEndPeriod();
            for (int period = startPeriod; period < endPeriod; period++) { // TODO Use sublist iterator?
                MachinePeriodPart machinePeriodPart = machinePeriodList.get(period);
                machinePeriodPart.addTaskAssignment(taskAssignment);
            }
        }
    }

    private void retract(TaskAssignment taskAssignment) {
        Machine machine = taskAssignment.getMachine();
        Integer startPeriod = taskAssignment.getStartPeriod();
        if (machine != null && startPeriod != null) {
            List<MachinePeriodPart> machinePeriodList = machinePeriodListMap.get(machine);
            int endPeriod = taskAssignment.getEndPeriod();
            for (int period = startPeriod; period < endPeriod; period++) { // TODO Use sublist iterator?
                MachinePeriodPart machinePeriodPart = machinePeriodList.get(period);
                machinePeriodPart.removeTaskAssignment(taskAssignment);
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

        private int taskCount;
        private List<Integer> resourceAvailableList;

        private MachinePeriodPart(Machine machine, PeriodPowerCost periodPowerCost) {
            this.machine = machine;
            this.period = periodPowerCost.getPeriod();
            this.periodPowerCostMicros = periodPowerCost.getPowerCostMicros();
            taskCount = 0;
            resourceAvailableList = new ArrayList<Integer>(resourceListSize);
            for (int i = 0; i < resourceListSize; i++) {
                resourceAvailableList.add(machine.getMachineCapacityList().get(i).getCapacity());
            }
        }

        public void addTaskAssignment(TaskAssignment taskAssignment) {
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
            softScore -= CostCalculator.multiplyTwoMicros(task.getPowerConsumptionMicros(),
                    periodPowerCostMicros);
        }

        public void removeTaskAssignment(TaskAssignment taskAssignment) {
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
            softScore += CostCalculator.multiplyTwoMicros(task.getPowerConsumptionMicros(),
                    periodPowerCostMicros);
        }

    }

}
