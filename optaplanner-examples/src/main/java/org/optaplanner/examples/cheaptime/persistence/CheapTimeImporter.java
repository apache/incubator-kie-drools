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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
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
import org.optaplanner.examples.cheaptime.solver.CheapTimeCostCalculator;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;

public class CheapTimeImporter extends AbstractTxtSolutionImporter {

    public static void main(String[] args) {
        CheapTimeImporter importer = new CheapTimeImporter();
        importer.convert("sample01", "sample01.xml");
        importer.convert("instance00", "instance00.xml");
        importer.convert("instance01", "instance01.xml");
        importer.convert("instance02", "instance02.xml");
        importer.convert("instance03", "instance03.xml");
    }

    public CheapTimeImporter() {
        super(new CheapTimeDao());
    }

    public CheapTimeImporter(boolean withoutDao) {
        super(withoutDao);
    }

    @Override
    public boolean isInputFileDirectory() {
        return true;
    }

    @Override
    public String getInputFileSuffix() {
        throw new IllegalStateException("The inputFile is a directory, so there is no suffix.");
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new CheapTimeInputBuilder();
    }

    @Override
    public Solution readSolution(File inputFile) {
        // TODO Bridging hack because InputBuilder is designed for a single File.
        File instanceFile = new File(inputFile, "instance.txt");
        return super.readSolution(instanceFile);
    }

    public static class CheapTimeInputBuilder extends TxtInputBuilder {

        private CheapTimeSolution solution;

        private int resourceListSize;

        public Solution readSolution() throws IOException {
            solution = new CheapTimeSolution();
            solution.setId(0L);
            int timeResolutionInMinutes = readIntegerValue();
            solution.setTimeResolutionInMinutes(timeResolutionInMinutes);
            solution.setGlobalPeriodRangeFrom(0);
            solution.setGlobalPeriodRangeTo(((24 * 60) / timeResolutionInMinutes));
            readResourceList();
            readMachineList();
            readTaskList();
            readForecastFile();
            createTaskAssignmentList();

            BigInteger possibleSolutionSize = BigInteger.ONE;
            for (Task task : solution.getTaskList()) {
                possibleSolutionSize = possibleSolutionSize.multiply(
                        BigInteger.valueOf(task.getStartPeriodRangeTo() - task.getStartPeriodRangeFrom()));
            }
            possibleSolutionSize = possibleSolutionSize.multiply(
                    BigInteger.valueOf(solution.getMachineList().size()).pow(solution.getTaskList().size()));
            logger.info("CheapTime {} has {} resources, {} machines, {} periods and {} tasks"
                         + " with a search space of {}.",
                    getInputId(),
                    solution.getResourceList().size(),
                    solution.getMachineList().size(),
                    solution.getGlobalPeriodRangeTo(),
                    solution.getTaskList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return solution;
        }

        @Override
        public String getInputId() {
            return FilenameUtils.getBaseName(inputFile.getParentFile().getPath());
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
                machine.setIndex(i);
                machine.setPowerConsumptionMicros(CheapTimeCostCalculator.parseMicroCost(machineLineTokens[1]));
                machine.setSpinUpDownCostMicros(CheapTimeCostCalculator.parseMicroCost(machineLineTokens[2])
                        + CheapTimeCostCalculator.parseMicroCost(machineLineTokens[3]));
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
                if (earliestStart < solution.getGlobalPeriodRangeFrom()
                        || earliestStart >= solution.getGlobalPeriodRangeTo()) {
                    throw new IllegalArgumentException("Task with id (" + task.getId()
                            + ") has a earliestStart (" + earliestStart
                            + ") which is not between globalPeriodRangeFrom (" + solution.getGlobalPeriodRangeFrom()
                            + ") and globalPeriodRangeTo (" + solution.getGlobalPeriodRangeTo() + ").");
                }
                task.setStartPeriodRangeFrom(earliestStart);
                int latestEnd = Integer.parseInt(taskLineTokens[3]);
                if (latestEnd < solution.getGlobalPeriodRangeFrom()
                        || latestEnd > solution.getGlobalPeriodRangeTo()) {
                    throw new IllegalArgumentException("Task with id (" + task.getId()
                            + ") has a latestEnd (" + latestEnd
                            + ") which is not between globalPeriodRangeFrom (" + solution.getGlobalPeriodRangeFrom()
                            + ") and globalPeriodRangeTo (" + solution.getGlobalPeriodRangeTo() + ").");
                }
                task.setPowerConsumptionMicros(CheapTimeCostCalculator.parseMicroCost(taskLineTokens[4]));
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
                long periodDurationPerHour = CheapTimeCostCalculator.divideTwoMicros(
                        CheapTimeCostCalculator.toMicroCost(1440),
                        CheapTimeCostCalculator.toMicroCost(periodListSize * 60L));
                List<PeriodPowerCost> periodPowerCostList = new ArrayList<PeriodPowerCost>(periodListSize);
                for (int i = 0; i < periodListSize; i++) {
                    String[] taskLineTokens = splitBySpacesOrTabs(readStringValue(), 2);
                    PeriodPowerCost periodPowerCost = new PeriodPowerCost();
                    int period = Integer.parseInt(taskLineTokens[0]);
                    if (periodPowerCostList.size() != period) {
                        throw new IllegalStateException("The forecast period (" + period
                                + ") does not increment normally and gets a different list index ("
                                + periodPowerCostList.size() + ").");
                    }
                    periodPowerCost.setId((long) period);
                    periodPowerCost.setPeriod(period);
                    long hourlyPowerCostMicros = CheapTimeCostCalculator.parseMicroCost(taskLineTokens[1]);
                    periodPowerCost.setPowerCostMicros(
                            CheapTimeCostCalculator.multiplyTwoMicros(hourlyPowerCostMicros, periodDurationPerHour));
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
