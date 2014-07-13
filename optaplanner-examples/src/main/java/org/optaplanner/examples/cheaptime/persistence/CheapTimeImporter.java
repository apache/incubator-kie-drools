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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.MachineCapacity;
import org.optaplanner.examples.cheaptime.domain.PeriodPowerCost;
import org.optaplanner.examples.cheaptime.domain.Resource;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.domain.TaskRequirement;
import org.optaplanner.examples.cheaptime.solver.CostCalculator;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;

public class CheapTimeImporter extends AbstractTxtSolutionImporter {

    public static void main(String[] args) {
        CheapTimeImporter importer = new CheapTimeImporter();
        importer.convert("instance00/instance.txt", "instance00.xml");
        importer.convert("instance01/instance.txt", "instance01.xml");
        importer.convert("instance02/instance.txt", "instance02.xml");
        importer.convert("instance03/instance.txt", "instance03.xml");
        importer.convert("instance04/instance.txt", "instance04.xml");
        importer.convert("instance05/instance.txt", "instance05.xml");
        importer.convert("instance06/instance.txt", "instance06.xml");
        importer.convert("instance07/instance.txt", "instance07.xml");
        importer.convert("instance08/instance.txt", "instance08.xml");
        importer.convert("instance09/instance.txt", "instance09.xml");
        importer.convert("instance10/instance.txt", "instance10.xml");
        importer.convert("instance11/instance.txt", "instance11.xml");
        importer.convert("instance12/instance.txt", "instance12.xml");
        importer.convert("instance13/instance.txt", "instance13.xml");
        importer.convert("instance14/instance.txt", "instance14.xml");
        importer.convert("instance15/instance.txt", "instance15.xml");
        importer.convert("instance16/instance.txt", "instance16.xml");
        importer.convert("instance17/instance.txt", "instance17.xml");
        importer.convert("instance18/instance.txt", "instance18.xml");
        importer.convert("instance19/instance.txt", "instance19.xml");
        importer.convert("instance20/instance.txt", "instance20.xml");
        importer.convert("instance21/instance.txt", "instance21.xml");
        importer.convert("instance22/instance.txt", "instance22.xml");
        importer.convert("instance23/instance.txt", "instance23.xml");
        importer.convert("instance24/instance.txt", "instance24.xml");
        importer.convert("instance25/instance.txt", "instance25.xml");
        importer.convert("instance26/instance.txt", "instance26.xml");
        importer.convert("instance27/instance.txt", "instance27.xml");
        importer.convert("instance28/instance.txt", "instance28.xml");
        importer.convert("instance29/instance.txt", "instance29.xml");
        importer.convert("instance30/instance.txt", "instance30.xml");
        importer.convert("instance31/instance.txt", "instance31.xml");
        importer.convert("instance32/instance.txt", "instance32.xml");
        importer.convert("instance33/instance.txt", "instance33.xml");
        importer.convert("instance34/instance.txt", "instance34.xml");
        importer.convert("instance35/instance.txt", "instance35.xml");
        importer.convert("instance36/instance.txt", "instance36.xml");
        importer.convert("instance37/instance.txt", "instance37.xml");
        importer.convert("instance38/instance.txt", "instance38.xml");
        importer.convert("instance39/instance.txt", "instance39.xml");
        importer.convert("instance40/instance.txt", "instance40.xml");
        importer.convert("instance41/instance.txt", "instance41.xml");
        importer.convert("instance42/instance.txt", "instance42.xml");
        importer.convert("instance43/instance.txt", "instance43.xml");
        importer.convert("instance44/instance.txt", "instance44.xml");
        importer.convert("instance45/instance.txt", "instance45.xml");
        importer.convert("instance46/instance.txt", "instance46.xml");
        importer.convert("instance47/instance.txt", "instance47.xml");
        importer.convert("instance48/instance.txt", "instance48.xml");
        importer.convert("instance49/instance.txt", "instance49.xml");
    }

    public CheapTimeImporter() {
        super(new CheapTimeDao());
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new CheapTimeInputBuilder();
    }

    public static class CheapTimeInputBuilder extends TxtInputBuilder {

        private CheapTimeSolution solution;

        private int maximumEndPeriod;
        private int resourceListSize;

        public Solution readSolution() throws IOException {
            solution = new CheapTimeSolution();
            solution.setId(0L);
            int timeResolutionInMinutes = readIntegerValue();
            solution.setTimeResolutionInMinutes(timeResolutionInMinutes);
            maximumEndPeriod = ((24 * 60) / timeResolutionInMinutes) + 1;
            readResourceList();
            readMachineList();
            readTaskList();
            readForecastFile();
            createTaskAssignmentList();
            logger.info("CheapTime {} has {} resources, {} machines, {} periods and {} tasks.",
                    getInputId(),
                    solution.getResourceList().size(),
                    solution.getMachineList().size(),
                    maximumEndPeriod - 1,
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
            for (int i = 0; i < machineListSize; i++) {
                Machine machine = new Machine();
                String[] machineLineTokens = splitBySpacesOrTabs(readStringValue(), 4);
                machine.setId(Long.parseLong(machineLineTokens[0]));
                machine.setEnergyUsage(Integer.parseInt(machineLineTokens[1]));
                machine.setSpinUpDownCostMicros(CostCalculator.parseMicroCost(machineLineTokens[2])
                        + CostCalculator.parseMicroCost(machineLineTokens[3]));
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
                String[] taskLineTokens = splitBySpacesOrTabs(readStringValue(), 5);
                Task task = new Task();
                task.setId(Long.parseLong(taskLineTokens[0]));
                int duration = Integer.parseInt(taskLineTokens[1]);
                if (duration <= 0) {
                    throw new IllegalArgumentException("Task with id (" + task.getId()
                            + ") has a duration (" + duration + ") which is not 1 or higher.");
                }
                task.setDuration(duration);
                int earliestStart = Integer.parseInt(taskLineTokens[2]);
                if (earliestStart < 0 || earliestStart > maximumEndPeriod) {
                    throw new IllegalArgumentException("Task with id (" + task.getId()
                            + ") has a earliestStart (" + earliestStart
                            + ") which is not between 0 and maximumEndPeriod (" + maximumEndPeriod + "), both inclusive.");
                }
                task.setStartPeriodRangeFrom(earliestStart);
                int latestEnd = Integer.parseInt(taskLineTokens[3]);
                if (latestEnd < 0 || latestEnd > maximumEndPeriod) {
                    throw new IllegalArgumentException("Task with id (" + task.getId()
                            + ") has a latestEnd (" + latestEnd
                            + ") which is not between 0 and maximumEndPeriod (" + maximumEndPeriod + "), both inclusive.");
                }
                task.setPowerConsumptionMicros(CostCalculator.parseMicroCost(taskLineTokens[4]));
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

        private void readForecastFile() {
            File forecastInputFile = new File(inputFile.getParent(), "forecast.txt");
            if (!forecastInputFile.exists()) {
                throw new IllegalArgumentException("The forecastInputFile (" + forecastInputFile
                        + ") for instanceInputFile (" + inputFile + ") does not exist.");
            }
            BufferedReader forecastBufferedReader = null;
            try {
                forecastBufferedReader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(forecastInputFile), "UTF-8"));
                ForecastInputBuilder forecastInputBuilder = new ForecastInputBuilder();
                forecastInputBuilder.setInputFile(forecastInputFile);
                forecastInputBuilder.setBufferedReader(forecastBufferedReader);
                try {
                    forecastInputBuilder.readSolution();
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Exception in forecastInputFile ("
                            + forecastInputFile + ")", e);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("Exception in forecastInputFile ("
                            + forecastInputFile + ")", e);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not read the file (" + forecastInputFile.getName() + ").", e);
            } finally {
                IOUtils.closeQuietly(forecastBufferedReader);
            }
        }

        public class ForecastInputBuilder extends TxtInputBuilder {

            @Override
            public Solution readSolution() throws IOException {
                int periodListSize = readIntegerValue();
                List<PeriodPowerCost> periodPowerCostList = new ArrayList<PeriodPowerCost>(periodListSize);
                for (int i = 0; i < periodListSize; i++) {
                    String[] taskLineTokens = splitBySpacesOrTabs(readStringValue(), 2);
                    PeriodPowerCost periodPowerCost = new PeriodPowerCost();
                    int period = Integer.parseInt(taskLineTokens[0]);
                    periodPowerCost.setId((long) period);
                    periodPowerCost.setPeriod(period);
                    periodPowerCost.setPowerCostMicros(CostCalculator.parseMicroCost(taskLineTokens[1]));
                    periodPowerCostList.add(periodPowerCost);
                }
                solution.setPeriodPowerCostList(periodPowerCostList);
                return null; // Hack so the code can reuse read methods from TxtInputBuilder
            }

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
