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

package org.optaplanner.examples.cheaptime.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.MachineCapacity;
import org.optaplanner.examples.cheaptime.domain.Resource;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.domain.TaskRequirement;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;

public class CheapTimeImporter extends AbstractTxtSolutionImporter {

    public static void main(String[] args) {
        new CheapTimeImporter().convertAll();
    }

    public CheapTimeImporter() {
        super(new CheapTimeDao());
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new CheapTimeInputBuilder();
    }

    public static class CheapTimeInputBuilder extends TxtInputBuilder {

        private CheapTimeSolution solution;

        private int maximumPeriod;
        private int resourceListSize;

        public Solution readSolution() throws IOException {
            solution = new CheapTimeSolution();
            solution.setId(0L);
            int timeResolutionInMinutes = readIntegerValue();
            solution.setTimeResolutionInMinutes(timeResolutionInMinutes);
            maximumPeriod = ((24 * 60) / timeResolutionInMinutes) + 1;
            readResourceList();
            readMachineList();
            readTaskList();
            readForecast();
            createTaskAssignmentList();
            logger.info("CheapTime {} has {} resources, {} machines, {} periods and {} tasks.",
                    getInputId(),
                    solution.getResourceList().size(),
                    solution.getMachineList().size(),
                    maximumPeriod,
                    solution.getTaskList().size()
            );
            return solution;
        }

        private void readResourceList() throws IOException {
            resourceListSize = readIntegerValue();
            List<Resource> resourceList = new ArrayList<Resource>(resourceListSize);
            for (int i = 0; i < resourceListSize; i++) {
                Resource resource = new Resource();
                resource.setId((long) i);
                resource.setIndex(i);
                resourceList.add(resource);
            }
            solution.setResourceList(resourceList);
        }

        private void readMachineList() throws IOException {
            int machineListSize = readIntegerValue();
            List<Machine> machineList = new ArrayList<Machine>(machineListSize);
            List<MachineCapacity> machineCapacityList = new ArrayList<MachineCapacity>(
                    machineListSize * resourceListSize);
            long machineCapacityId = 0L;
            for (int i = 0; i < machineListSize * 2; i++) {
                Machine machine = new Machine();
                String[] machineLineTokens = splitBySpacesOrTabs(readStringValue(), 4);
                machine.setId(Long.parseLong(machineLineTokens[0]));
                machine.setEnergyUsage(Integer.parseInt(machineLineTokens[1])); // TODO convert cost
                machine.setSpinUpDownCost(Integer.parseInt(machineLineTokens[2]) + Integer.parseInt(machineLineTokens[3])); // TODO convert cost
                String[] capacityLineTokens = splitBySpacesOrTabs(readStringValue(), resourceListSize);
                List<MachineCapacity> machineCapacityListOfMachine = new ArrayList<MachineCapacity>(resourceListSize);
                for (int j = 0; j < resourceListSize; j++) {
                    MachineCapacity machineCapacity = new MachineCapacity();
                    machineCapacity.setId(machineCapacityId);
                    machineCapacityId++;
                    machineCapacity.setMachine(machine);
                    machineCapacity.setResource(solution.getResourceList().get(j));
                    machineCapacity.setCapacity(Integer.parseInt(capacityLineTokens[j]));
                    machineCapacityList.add(machineCapacity);
                    machineCapacityListOfMachine.add(machineCapacity);
                }
                machine.setMachineCapacityList(machineCapacityListOfMachine);
                machineList.add(machine);
            }
            solution.setMachineList(machineList);
            solution.setMachineCapacityList(machineCapacityList);
        }

        private void readTaskList() throws IOException {
            int taskListSize = readIntegerValue();
            List<Task> taskList = new ArrayList<Task>(taskListSize);
            List<TaskRequirement> taskRequirementList = new ArrayList<TaskRequirement>(taskListSize * resourceListSize);
            long taskRequirementId = 0L;
            for (int i = 0; i < taskListSize; i++) {
                String[] taskLineTokens = splitBySpace(readStringValue(), 4);
                Task task = new Task();
                task.setId(Long.parseLong(taskLineTokens[0]));
                int duration = Integer.parseInt(taskLineTokens[1]);
                if (duration <= 0) {
                    throw new IllegalArgumentException("Task with id (" + task.getId()
                            + ") has a duration (" + duration + ") which is not 1 or higher.");
                }
                task.setDuration(duration);
                int earliestStart = Integer.parseInt(taskLineTokens[2]);
                if (earliestStart < 0 || earliestStart > maximumPeriod) {
                    throw new IllegalArgumentException("Task with id (" + task.getId()
                            + ") has a earliestStart (" + earliestStart
                            + ") which is not between 0 and maximumPeriod (" + maximumPeriod + "), both inclusive.");
                }
                task.setStartPeriodRangeFrom(earliestStart);
                int latestEnd = Integer.parseInt(taskLineTokens[3]);
                if (latestEnd < 0 || latestEnd > maximumPeriod) {
                    throw new IllegalArgumentException("Task with id (" + task.getId()
                            + ") has a latestEnd (" + latestEnd
                            + ") which is not between 0 and maximumPeriod (" + maximumPeriod + "), both inclusive.");
                }
                // + 1 because rangeTo is exclusive
                // (the fact that latestEnd is exclusive is irrelevant because start = end - duration)
                task.setStartPeriodRangeTo(latestEnd - duration + 1);
                String[] usageLineTokens = splitBySpacesOrTabs(readStringValue(), resourceListSize);
                List<TaskRequirement> taskRequirementListOfTask = new ArrayList<TaskRequirement>(resourceListSize);
                for (int j = 0; j < resourceListSize; j++) {
                    TaskRequirement taskRequirement = new TaskRequirement();
                    taskRequirement.setId(taskRequirementId);
                    taskRequirementId++;
                    taskRequirement.setTask(task);
                    taskRequirement.setResource(solution.getResourceList().get(j));
                    taskRequirement.setResourceUsage(Integer.parseInt(usageLineTokens[j]));
                    taskRequirementList.add(taskRequirement);
                    taskRequirementListOfTask.add(taskRequirement);
                }
                task.setTaskRequirementList(taskRequirementListOfTask);
                taskList.add(task);
            }
            solution.setTaskList(taskList);
            solution.setTaskRequirementList(taskRequirementList);
        }

        private void readForecast() {
            // TODO
        }


        private void createTaskAssignmentList() {
            List<Task> taskList = solution.getTaskList();
            List<TaskAssignment> taskAssignmentList = new ArrayList<TaskAssignment>(taskList.size());
            for (Task task : taskList) {
                TaskAssignment taskAssignment = new TaskAssignment();
                taskAssignment.setId(task.getId());
                taskAssignment.setTask(task);
                // Notice that we leave the PlanningVariable properties on null
                taskAssignmentList.add(taskAssignment);
            }
            solution.setTaskAssignmentList(taskAssignmentList);
        }

    }

}
