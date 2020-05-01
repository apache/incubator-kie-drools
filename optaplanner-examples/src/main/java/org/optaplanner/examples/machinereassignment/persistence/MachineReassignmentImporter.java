/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.machinereassignment.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.machinereassignment.app.MachineReassignmentApp;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrBalancePenalty;
import org.optaplanner.examples.machinereassignment.domain.MrGlobalPenaltyInfo;
import org.optaplanner.examples.machinereassignment.domain.MrLocation;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrNeighborhood;
import org.optaplanner.examples.machinereassignment.domain.MrProcess;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.domain.MrProcessRequirement;
import org.optaplanner.examples.machinereassignment.domain.MrResource;
import org.optaplanner.examples.machinereassignment.domain.MrService;

public class MachineReassignmentImporter extends AbstractTxtSolutionImporter<MachineReassignment> {

    public static void main(String[] args) {
        SolutionConverter<MachineReassignment> converter = SolutionConverter.createImportConverter(
                MachineReassignmentApp.DATA_DIR_NAME, new MachineReassignmentImporter(), MachineReassignment.class);
        converter.convert("model_a1_1.txt");
        converter.convert("model_a1_2.txt");
        converter.convert("model_a1_3.txt");
        converter.convert("model_a1_4.txt");
        converter.convert("model_a1_5.txt");
        converter.convert("model_a2_1.txt");
        converter.convert("model_a2_2.txt");
        converter.convert("model_a2_3.txt");
        converter.convert("model_a2_4.txt");
        converter.convert("model_a2_5.txt");
        // model_b's are too big to write as XML
    }

    @Override
    public String getInputFileSuffix() {
        return "txt";
    }

    @Override
    public TxtInputBuilder<MachineReassignment> createTxtInputBuilder() {
        return new MachineReassignmentInputBuilder();
    }

    public static class MachineReassignmentInputBuilder extends TxtInputBuilder<MachineReassignment> {

        private MachineReassignment machineReassignment;

        private int resourceListSize;
        private List<MrResource> resourceList;
        private List<MrService> serviceList;
        private List<MrMachine> machineList;
        private int processListSize;
        private List<MrProcess> processList;

        @Override
        public MachineReassignment readSolution() throws IOException {
            machineReassignment = new MachineReassignment();
            machineReassignment.setId(0L);
            readResourceList();
            readMachineList();
            readServiceList();
            readProcessList();
            readBalancePenaltyList();
            readGlobalPenaltyInfo();
            readProcessAssignmentList();
            BigInteger possibleSolutionSize = BigInteger.valueOf(machineReassignment.getMachineList().size()).pow(
                    machineReassignment.getProcessList().size());
            logger.info("MachineReassignment {} has {} resources, {} neighborhoods, {} locations, {} machines," +
                    " {} services, {} processes and {} balancePenalties with a search space of {}.",
                    getInputId(),
                    machineReassignment.getResourceList().size(),
                    machineReassignment.getNeighborhoodList().size(),
                    machineReassignment.getLocationList().size(),
                    machineReassignment.getMachineList().size(),
                    machineReassignment.getServiceList().size(),
                    machineReassignment.getProcessList().size(),
                    machineReassignment.getBalancePenaltyList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return machineReassignment;
        }

        private void readResourceList() throws IOException {
            resourceListSize = readIntegerValue();
            resourceList = new ArrayList<>(resourceListSize);
            long resourceId = 0L;
            for (int i = 0; i < resourceListSize; i++) {
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line, 2);
                MrResource resource = new MrResource();
                resource.setId(resourceId);
                resource.setIndex(i);
                resource.setTransientlyConsumed(parseBooleanFromNumber(lineTokens[0]));
                resource.setLoadCostWeight(Integer.parseInt(lineTokens[1]));
                resourceList.add(resource);
                resourceId++;
            }
            machineReassignment.setResourceList(resourceList);
        }

        private void readMachineList() throws IOException {
            int machineListSize = readIntegerValue();
            List<MrNeighborhood> neighborhoodList = new ArrayList<>(machineListSize);
            Map<Long, MrNeighborhood> idToNeighborhoodMap = new HashMap<>(machineListSize);
            List<MrLocation> locationList = new ArrayList<>(machineListSize);
            Map<Long, MrLocation> idToLocationMap = new HashMap<>(machineListSize);
            machineList = new ArrayList<>(machineListSize);
            long machineId = 0L;
            List<MrMachineCapacity> machineCapacityList = new ArrayList<>(machineListSize * resourceListSize);
            long machineCapacityId = 0L;
            // 2 phases because service dependencies are not in low to high order
            for (int i = 0; i < machineListSize; i++) {
                MrMachine machine = new MrMachine();
                machine.setId(machineId);
                machineList.add(machine);
                machineId++;
            }
            for (int i = 0; i < machineListSize; i++) {
                MrMachine machine = machineList.get(i);
                String line = readStringValue();
                int moveCostOffset = 2 + (resourceListSize * 2);
                String[] lineTokens = splitBySpace(line, moveCostOffset + machineListSize);
                long neighborhoodId = Long.parseLong(lineTokens[0]);
                MrNeighborhood neighborhood = idToNeighborhoodMap.get(neighborhoodId);
                if (neighborhood == null) {
                    neighborhood = new MrNeighborhood();
                    neighborhood.setId(neighborhoodId);
                    neighborhoodList.add(neighborhood);
                    idToNeighborhoodMap.put(neighborhoodId, neighborhood);
                }
                machine.setNeighborhood(neighborhood);
                long locationId = Long.parseLong(lineTokens[1]);
                MrLocation location = idToLocationMap.get(locationId);
                if (location == null) {
                    location = new MrLocation();
                    location.setId(locationId);
                    locationList.add(location);
                    idToLocationMap.put(locationId, location);
                }
                machine.setLocation(location);
                List<MrMachineCapacity> machineCapacityListOfMachine = new ArrayList<>(resourceListSize);
                for (int j = 0; j < resourceListSize; j++) {
                    MrMachineCapacity machineCapacity = new MrMachineCapacity();
                    machineCapacity.setId(machineCapacityId);
                    machineCapacity.setMachine(machine);
                    machineCapacity.setResource(resourceList.get(j));
                    machineCapacity.setMaximumCapacity(Long.parseLong(lineTokens[2 + j]));
                    machineCapacity.setSafetyCapacity(Long.parseLong(lineTokens[2 + resourceListSize + j]));
                    machineCapacityList.add(machineCapacity);
                    machineCapacityListOfMachine.add(machineCapacity);
                    machineCapacityId++;
                }
                machine.setMachineCapacityList(machineCapacityListOfMachine);
                Map<MrMachine, Integer> machineMoveCostMap = new HashMap<>(machineListSize);
                for (int j = 0; j < machineListSize; j++) {
                    MrMachine toMachine = machineList.get(j);
                    int moveCost = Integer.parseInt(lineTokens[moveCostOffset + j]);
                    machineMoveCostMap.put(toMachine, moveCost);
                }
                machine.setMachineMoveCostMap(machineMoveCostMap);
            }
            machineReassignment.setNeighborhoodList(neighborhoodList);
            machineReassignment.setLocationList(locationList);
            machineReassignment.setMachineList(machineList);
            machineReassignment.setMachineCapacityList(machineCapacityList);
        }

        private void readServiceList() throws IOException {
            int serviceListSize = readIntegerValue();
            serviceList = new ArrayList<>(serviceListSize);
            long serviceId = 0L;
            // 2 phases because service dependencies are not in low to high order
            for (int i = 0; i < serviceListSize; i++) {
                MrService service = new MrService();
                service.setId(serviceId);
                service.setFromDependencyServiceList(new ArrayList<>(5));
                serviceList.add(service);
                serviceId++;
            }
            for (int i = 0; i < serviceListSize; i++) {
                MrService service = serviceList.get(i);
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line);
                service.setLocationSpread(Integer.parseInt(lineTokens[0]));
                int serviceDependencyListSize = Integer.parseInt(lineTokens[1]);
                List<MrService> toDependencyServiceList = new ArrayList<>(serviceDependencyListSize);
                for (int j = 0; j < serviceDependencyListSize; j++) {
                    int toServiceIndex = Integer.parseInt(lineTokens[2 + j]);
                    if (toServiceIndex >= serviceList.size()) {
                        throw new IllegalArgumentException("Service with id (" + serviceId
                                + ") has a non existing toServiceIndex (" + toServiceIndex + ").");
                    }
                    MrService toService = serviceList.get(toServiceIndex);
                    if (toService.equals(service)) {
                        throw new IllegalStateException("The toService (" + toService
                                + ") cannot be equal to the service (" + service + ").");
                    }
                    toDependencyServiceList.add(toService);
                    toService.getFromDependencyServiceList().add(service);
                }
                service.setToDependencyServiceList(toDependencyServiceList);
                int numberOfTokens = 2 + serviceDependencyListSize;
                if (lineTokens.length != numberOfTokens) {
                    throw new IllegalArgumentException("Read line (" + line + ") has " + lineTokens.length
                            + " tokens but is expected to contain " + numberOfTokens + " tokens separated by space.");
                }
            }
            machineReassignment.setServiceList(serviceList);
        }

        private void readProcessList() throws IOException {
            processListSize = readIntegerValue();
            processList = new ArrayList<>(processListSize);
            long processId = 0L;
            long processRequirementId = 0L;
            for (int i = 0; i < processListSize; i++) {
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line, 2 + resourceListSize);
                MrProcess process = new MrProcess();
                process.setId(processId);
                int serviceIndex = Integer.parseInt(lineTokens[0]);
                if (serviceIndex >= serviceList.size()) {
                    throw new IllegalArgumentException("Process with id (" + processId
                            + ") has a non existing serviceIndex (" + serviceIndex + ").");
                }
                MrService service = serviceList.get(serviceIndex);
                process.setService(service);
                List<MrProcessRequirement> processRequirementList = new ArrayList<>(resourceListSize);
                for (int j = 0; j < resourceListSize; j++) {
                    MrProcessRequirement processRequirement = new MrProcessRequirement();
                    processRequirement.setId(processRequirementId);
                    processRequirement.setProcess(process);
                    processRequirement.setResource(resourceList.get(j));
                    processRequirement.setUsage(Integer.parseInt(lineTokens[1 + j]));
                    processRequirementList.add(processRequirement);
                    processRequirementId++;
                }
                process.setProcessRequirementList(processRequirementList);
                process.setMoveCost(Integer.parseInt(lineTokens[1 + resourceListSize]));
                processList.add(process);
                processId++;
            }
            machineReassignment.setProcessList(processList);
        }

        private void readBalancePenaltyList() throws IOException {
            int balancePenaltyListSize = readIntegerValue();
            List<MrBalancePenalty> balancePenaltyList = new ArrayList<>(balancePenaltyListSize);
            long balancePenaltyId = 0L;
            for (int i = 0; i < balancePenaltyListSize; i++) {
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line, 3);
                MrBalancePenalty balancePenalty = new MrBalancePenalty();
                balancePenalty.setId(balancePenaltyId);
                int originResourceIndex = Integer.parseInt(lineTokens[0]);
                if (originResourceIndex >= resourceListSize) {
                    throw new IllegalArgumentException("BalancePenalty with id (" + balancePenaltyId
                            + ") has a non existing originResourceIndex (" + originResourceIndex + ").");
                }
                balancePenalty.setOriginResource(resourceList.get(originResourceIndex));
                int targetResourceIndex = Integer.parseInt(lineTokens[1]);
                if (targetResourceIndex >= resourceListSize) {
                    throw new IllegalArgumentException("BalancePenalty with id (" + balancePenaltyId
                            + ") has a non existing targetResourceIndex (" + targetResourceIndex + ").");
                }
                balancePenalty.setTargetResource(resourceList.get(targetResourceIndex));
                balancePenalty.setMultiplicand(Integer.parseInt(lineTokens[2]));
                // Read a new line (weird in the input definition)
                balancePenalty.setWeight(readIntegerValue());
                balancePenaltyList.add(balancePenalty);
                balancePenaltyId++;
            }
            machineReassignment.setBalancePenaltyList(balancePenaltyList);
        }

        private void readGlobalPenaltyInfo() throws IOException {
            MrGlobalPenaltyInfo globalPenaltyInfo = new MrGlobalPenaltyInfo();
            globalPenaltyInfo.setId(0L);
            String line = readStringValue();
            String[] lineTokens = splitBySpace(line, 3);
            globalPenaltyInfo.setProcessMoveCostWeight(Integer.parseInt(lineTokens[0]));
            globalPenaltyInfo.setServiceMoveCostWeight(Integer.parseInt(lineTokens[1]));
            globalPenaltyInfo.setMachineMoveCostWeight(Integer.parseInt(lineTokens[2]));
            machineReassignment.setGlobalPenaltyInfo(globalPenaltyInfo);
        }

        private void readProcessAssignmentList() {
            String line = readOriginalProcessAssignmentLine();
            String[] lineTokens = splitBySpace(line, processListSize);
            List<MrProcessAssignment> processAssignmentList = new ArrayList<>(processListSize);
            long processAssignmentId = 0L;
            for (int i = 0; i < processListSize; i++) {
                MrProcessAssignment processAssignment = new MrProcessAssignment();
                processAssignment.setId(processAssignmentId);
                processAssignment.setProcess(processList.get(i));
                int machineIndex = Integer.parseInt(lineTokens[i]);
                if (machineIndex >= machineList.size()) {
                    throw new IllegalArgumentException("ProcessAssignment with id (" + processAssignmentId
                            + ") has a non existing machineIndex (" + machineIndex + ").");
                }
                processAssignment.setOriginalMachine(machineList.get(machineIndex));
                // Notice that we leave the PlanningVariable properties on null
                processAssignmentList.add(processAssignment);
                processAssignmentId++;
            }
            machineReassignment.setProcessAssignmentList(processAssignmentList);
        }

        private String readOriginalProcessAssignmentLine() {
            String inputFileName = inputFile.getName();
            String inputFilePrefix = "model_";
            if (!inputFileName.startsWith(inputFilePrefix)) {
                throw new IllegalArgumentException("The inputFile (" + inputFile
                        + ") is expected to start with \"" + inputFilePrefix + "\".");
            }
            File assignmentInputFile = new File(inputFile.getParent(),
                    inputFileName.replaceFirst(inputFilePrefix, "assignment_").replaceAll("\\.txt$", ".sol"));
            try (BufferedReader assignmentBufferedReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(assignmentInputFile), "UTF-8"))) {
                return assignmentBufferedReader.readLine();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Exception in assignmentInputFile ("
                        + assignmentInputFile + ")", e);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Exception in assignmentInputFile ("
                        + assignmentInputFile + ")", e);
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not read the file (" + assignmentInputFile.getName() + ").", e);
            }
        }

    }

}
