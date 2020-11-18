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
import org.optaplanner.examples.cheaptime.app.CheapTimeApp;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.MachineCapacity;
import org.optaplanner.examples.cheaptime.domain.PeriodPowerPrice;
import org.optaplanner.examples.cheaptime.domain.Resource;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.domain.TaskRequirement;
import org.optaplanner.examples.cheaptime.score.CheapTimeCostCalculator;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;

public class CheapTimeImporter extends AbstractTxtSolutionImporter<CheapTimeSolution> {

    public static void main(String[] args) {
        SolutionConverter<CheapTimeSolution> converter = SolutionConverter.createImportConverter(
                CheapTimeApp.DATA_DIR_NAME, new CheapTimeImporter(), CheapTimeSolution.class);
        converter.convert("demo01", "demo01.xml");
        converter.convert("sample01", "sample01.xml");
        //        converter.convert("sample02", "sample02.xml");
        //        converter.convert("sample03", "sample03.xml");
        //        converter.convert("sample04", "sample04.xml");
        //        converter.convert("sample05", "sample05.xml");
        //        converter.convert("sample06", "sample06.xml");
        //        converter.convert("sample07", "sample07.xml");
        //        converter.convert("sample08", "sample08.xml");
        //        converter.convert("sample09", "sample09.xml");
        converter.convert("instance00", "instance00.xml");
        converter.convert("instance01", "instance01.xml");
        converter.convert("instance02", "instance02.xml");
        converter.convert("instance03", "instance03.xml");
        //        converter.convert("instance04", "instance04.xml");
        //        converter.convert("instance05", "instance05.xml");
        //        converter.convert("instance06", "instance06.xml");
        //        converter.convert("instance07", "instance07.xml");
        //        converter.convert("instance08", "instance08.xml");
        //        converter.convert("instance09", "instance09.xml");
        //        converter.convert("instance10", "instance10.xml");
        //        converter.convert("instance11", "instance11.xml");
        //        converter.convert("instance12", "instance12.xml");
        //        converter.convert("instance13", "instance13.xml");
        //        converter.convert("instance14", "instance14.xml");
        //        converter.convert("instance15", "instance15.xml");
        //        converter.convert("instance16", "instance16.xml");
        //        converter.convert("instance17", "instance17.xml");
        //        converter.convert("instance18", "instance18.xml");
        //        converter.convert("instance19", "instance19.xml");
        //        converter.convert("instance20", "instance20.xml");
        //        converter.convert("instance21", "instance21.xml");
        //        converter.convert("instance22", "instance22.xml");
        //        converter.convert("instance23", "instance23.xml");
        //        converter.convert("instance24", "instance24.xml");
        //        converter.convert("instance25", "instance25.xml");
        //        converter.convert("instance26", "instance26.xml");
        //        converter.convert("instance27", "instance27.xml");
        //        converter.convert("instance28", "instance28.xml");
        //        converter.convert("instance29", "instance29.xml");
        //        converter.convert("instance30", "instance30.xml");
        //        converter.convert("instance31", "instance31.xml");
        //        converter.convert("instance32", "instance32.xml");
        //        converter.convert("instance33", "instance33.xml");
        //        converter.convert("instance34", "instance34.xml");
        //        converter.convert("instance35", "instance35.xml");
        //        converter.convert("instance36", "instance36.xml");
        //        converter.convert("instance37", "instance37.xml");
        //        converter.convert("instance38", "instance38.xml");
        //        converter.convert("instance39", "instance39.xml");
        //        converter.convert("instance40", "instance40.xml");
        //        converter.convert("instance41", "instance41.xml");
        //        converter.convert("instance42", "instance42.xml");
        //        converter.convert("instance43", "instance43.xml");
        //        converter.convert("instance44", "instance44.xml");
        //        converter.convert("instance45", "instance45.xml");
        //        converter.convert("instance46", "instance46.xml");
        //        converter.convert("instance47", "instance47.xml");
        //        converter.convert("instance48", "instance48.xml");
        //        converter.convert("instance49", "instance49.xml");
    }

    @Override
    public boolean isInputFileDirectory() {
        return true;
    }

    @Override
    public String getInputFileSuffix() {
        throw new IllegalStateException("The inputFile is a directory, so there is no suffix.");
    }

    @Override
    public TxtInputBuilder<CheapTimeSolution> createTxtInputBuilder() {
        return new CheapTimeInputBuilder();
    }

    @Override
    public CheapTimeSolution readSolution(File inputFile) {
        // TODO Bridging hack because InputBuilder is designed for a single File.
        File instanceFile = new File(inputFile, "instance.txt");
        return super.readSolution(instanceFile);
    }

    public static class CheapTimeInputBuilder extends TxtInputBuilder<CheapTimeSolution> {

        private CheapTimeSolution solution;

        private int resourceListSize;

        @Override
        public CheapTimeSolution readSolution() throws IOException {
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
            List<Resource> resourceList = new ArrayList<>(resourceListSize);
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
            List<Machine> machineList = new ArrayList<>(machineListSize);
            List<MachineCapacity> machineCapacityList = new ArrayList<>(
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
                List<MachineCapacity> machineCapacityListOfMachine = new ArrayList<>(resourceListSize);
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
            List<Task> taskList = new ArrayList<>(taskListSize);
            List<TaskRequirement> taskRequirementList = new ArrayList<>(taskListSize * resourceListSize);
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
                List<TaskRequirement> taskRequirementListOfTask = new ArrayList<>(resourceListSize);
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
            try (BufferedReader forecastBufferedReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(forecastInputFile), "UTF-8"))) {
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
                throw new IllegalArgumentException(
                        "Could not read the forecastInputFile (" + forecastInputFile.getName() + ").", e);
            }
        }

        public class ForecastInputBuilder extends TxtInputBuilder<CheapTimeSolution> {

            @Override
            public CheapTimeSolution readSolution() throws IOException {
                int periodListSize = readIntegerValue();
                long periodDurationPerHour = CheapTimeCostCalculator.divideTwoMicros(
                        CheapTimeCostCalculator.toMicroCost(1440),
                        CheapTimeCostCalculator.toMicroCost(periodListSize * 60L));
                List<PeriodPowerPrice> periodPowerPriceList = new ArrayList<>(periodListSize);
                for (int i = 0; i < periodListSize; i++) {
                    String[] lineTokens = splitBySpacesOrTabs(readStringValue(), 2);
                    PeriodPowerPrice periodPowerPrice = new PeriodPowerPrice();
                    int period = Integer.parseInt(lineTokens[0]);
                    if (periodPowerPriceList.size() != period) {
                        throw new IllegalStateException("The forecast period (" + period
                                + ") does not increment normally and gets a different list index ("
                                + periodPowerPriceList.size() + ").");
                    }
                    periodPowerPrice.setId((long) period);
                    periodPowerPrice.setPeriod(period);
                    long hourlyPowerPriceMicros = CheapTimeCostCalculator.parseMicroCost(lineTokens[1]);
                    periodPowerPrice.setPowerPriceMicros(
                            CheapTimeCostCalculator.multiplyTwoMicros(hourlyPowerPriceMicros, periodDurationPerHour));
                    periodPowerPriceList.add(periodPowerPrice);
                }
                solution.setPeriodPowerPriceList(periodPowerPriceList);
                return null; // Hack so the code can reuse read methods from TxtInputBuilder
            }

        }

        private void createTaskAssignmentList() {
            List<Task> taskList = solution.getTaskList();
            List<TaskAssignment> taskAssignmentList = new ArrayList<>(taskList.size());
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
