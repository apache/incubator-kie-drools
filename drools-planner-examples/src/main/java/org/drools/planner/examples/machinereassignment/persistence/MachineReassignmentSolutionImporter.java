/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.machinereassignment.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.drools.planner.examples.machinereassignment.domain.MachineReassignment;
import org.drools.planner.examples.machinereassignment.domain.MrBalancePenalty;
import org.drools.planner.examples.machinereassignment.domain.MrMachineCapacity;
import org.drools.planner.examples.machinereassignment.domain.MrGlobalPenaltyInfo;
import org.drools.planner.examples.machinereassignment.domain.MrLocation;
import org.drools.planner.examples.machinereassignment.domain.MrMachine;
import org.drools.planner.examples.machinereassignment.domain.MrNeighborhood;
import org.drools.planner.examples.machinereassignment.domain.MrProcess;
import org.drools.planner.examples.machinereassignment.domain.MrProcessRequirement;
import org.drools.planner.examples.machinereassignment.domain.MrResource;
import org.drools.planner.examples.machinereassignment.domain.MrService;
import org.drools.planner.examples.machinereassignment.domain.MrServiceDependency;

public class MachineReassignmentSolutionImporter extends AbstractTxtSolutionImporter {

    public static void main(String[] args) {
        new MachineReassignmentSolutionImporter().convertAll();
    }

    public MachineReassignmentSolutionImporter() {
        super(new MachineReassignmentDaoImpl());
    }

    @Override
    public boolean acceptInputFile(File inputFile) {
        return super.acceptInputFile(inputFile) && inputFile.getName().startsWith("model_");
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new MachineReassignmentInputBuilder();
    }

    public class MachineReassignmentInputBuilder extends TxtInputBuilder {

        private MachineReassignment machineReassignment;

        private int resourceListSize;
        private List<MrResource> resourceList;
        private List<MrService> serviceList;

        public Solution readSolution() throws IOException {
            machineReassignment = new MachineReassignment();
            machineReassignment.setId(0L);
            readResourceList();
            readMachineList();
            readServiceList();
            readProcessList();
            readBalancePenaltyList();
            readGlobalPenaltyInfo();
//            createBedDesignationList();
            logger.info("MachineReassignment with {} resources.",
                    new Object[]{machineReassignment.getResourceList().size()});
//            BigInteger possibleSolutionSize = BigInteger.valueOf(machineReassignment.getBedList().size()).pow(
//                    machineReassignment.getAdmissionPartList().size());
//            String flooredPossibleSolutionSize = "10^" + (possibleSolutionSize.toString().length() - 1);
//            logger.info("MachineReassignment with flooredPossibleSolutionSize ({}) and possibleSolutionSize({}).",
//                    flooredPossibleSolutionSize, possibleSolutionSize);
            return machineReassignment;
        }

        private void readResourceList() throws IOException {
            resourceListSize = readIntegerValue();
            resourceList = new ArrayList<MrResource>(resourceListSize);
            long resourceId = 0L;
            for (int i = 0; i < resourceListSize; i++) {
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line, 2);
                MrResource resource = new MrResource();
                resource.setId(resourceId);
                resource.setTransientlyConsumed(parseBooleanFromNumber(lineTokens[0]));
                resource.setWeight(Integer.parseInt(lineTokens[1]));
                resourceList.add(resource);
                resourceId++;
            }
            machineReassignment.setResourceList(resourceList);
        }

        private void readMachineList() throws IOException {
            int machineListSize = readIntegerValue();
            List<MrNeighborhood> neighborhoodList = new ArrayList<MrNeighborhood>(machineListSize);
            Map<String, MrNeighborhood> idToNeighborhoodMap = new HashMap<String, MrNeighborhood>(machineListSize);
            List<MrLocation> locationList = new ArrayList<MrLocation>(machineListSize);
            Map<String, MrLocation> idToLocationMap = new HashMap<String, MrLocation>(machineListSize);
            List<MrMachine> machineList = new ArrayList<MrMachine>(machineListSize);
            long machineId = 0L;
            List<MrMachineCapacity> machineCapacityList = new ArrayList<MrMachineCapacity>(machineListSize * resourceListSize);
            long machineCapacityId = 0L;
            for (int i = 0; i < machineListSize; i++) {
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line, 2 + (resourceListSize * 2) + machineListSize);
                MrMachine machine = new MrMachine();
                machine.setId(machineId);
                long neighborhoodId = Long.parseLong(lineTokens[0]);
                MrNeighborhood neighborhood = idToNeighborhoodMap.get(neighborhoodId);
                if (neighborhood == null) {
                    neighborhood = new MrNeighborhood();
                    neighborhood.setId(neighborhoodId);
                }
                machine.setNeighborhood(neighborhood);
                long locationId = Long.parseLong(lineTokens[1]);
                MrLocation location = idToLocationMap.get(locationId);
                if (location == null) {
                    location = new MrLocation();
                    location.setId(locationId);
                }
                machine.setLocation(location);
                for (int j = 0; j < resourceListSize; j++) {
                    MrMachineCapacity machineCapacity = new MrMachineCapacity();
                    machineCapacity.setId(machineCapacityId);
                    machineCapacity.setMachine(machine);
                    machineCapacity.setResource(resourceList.get(j));
                    machineCapacity.setMaximumCapacity(Integer.parseInt(lineTokens[2 + j]));
                    machineCapacity.setSafetyCapacity(Integer.parseInt(lineTokens[2 + resourceListSize + j]));
                    machineCapacityList.add(machineCapacity);
                    machineCapacityId++;
                }
                // TODO moving costs

                machineList.add(machine);
                machineId++;
            }
            machineReassignment.setNeighborhoodList(neighborhoodList);
            machineReassignment.setLocationList(locationList);
            machineReassignment.setMachineList(machineList);
            machineReassignment.setMachineCapacityList(machineCapacityList);
        }

        private void readServiceList() throws IOException {
            int serviceListSize = readIntegerValue();
            serviceList = new ArrayList<MrService>(serviceListSize);
            long serviceId = 0L;
            // 2 phases because service dependencies are not in low to high order
            for (int i = 0; i < serviceListSize; i++) {
                MrService service = new MrService();
                service.setId(serviceId);
                serviceList.add(service);
                serviceId++;
            }
            List<MrServiceDependency> serviceDependencyList = new ArrayList<MrServiceDependency>(serviceListSize * 5);
            long serviceDependencyId = 0L;
            for (int i = 0; i < serviceListSize; i++) {
                MrService service = serviceList.get(i);
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line);
                service.setLocationSpread(Integer.parseInt(lineTokens[0]));
                int serviceDependencyListSize = Integer.parseInt(lineTokens[1]);
                for (int j = 0; j < serviceDependencyListSize; j++) {
                    MrServiceDependency serviceDependency = new MrServiceDependency();
                    serviceDependency.setId(serviceDependencyId);
                    serviceDependency.setFromService(service);
                    int toServiceIndex = Integer.parseInt(lineTokens[2 + j]);
                    if (toServiceIndex >= serviceList.size()) {
                        throw new IllegalArgumentException("Service with id (" + serviceId
                                + ") has a non existing toServiceIndex (" + toServiceIndex + ").");
                    }
                    MrService toService = serviceList.get(toServiceIndex);
                    serviceDependency.setToService(toService);
                    serviceDependencyList.add(serviceDependency);
                    serviceDependencyId++;
                }
                int numberOfTokens = 2 + serviceDependencyListSize;
                if (lineTokens.length != numberOfTokens) {
                    throw new IllegalArgumentException("Read line (" + line + ") has " + lineTokens.length
                            + " tokens but is expected to contain " + numberOfTokens + " tokens separated by space.");
                }
            }
            machineReassignment.setServiceList(serviceList);
            machineReassignment.setServiceDependencyList(serviceDependencyList);
        }

        private void readProcessList() throws IOException {
            int processListSize = readIntegerValue();
            List<MrProcess> processList = new ArrayList<MrProcess>(processListSize);
            long processId = 0L;
            List<MrProcessRequirement> processRequirementList = new ArrayList<MrProcessRequirement>(processListSize * resourceListSize);
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
                for (int j = 0; j < resourceListSize; j++) {
                    MrProcessRequirement processRequirement = new MrProcessRequirement();
                    processRequirement.setId(processRequirementId);
                    processRequirement.setProcess(process);
                    processRequirement.setResource(resourceList.get(j));
                    processRequirement.setUsage(Integer.parseInt(lineTokens[1 + j]));
                    processRequirementList.add(processRequirement);
                    processRequirementId++;
                }
                process.setMoveCost(Integer.parseInt(lineTokens[1 + resourceListSize]));
                processList.add(process);
                processId++;
            }
            machineReassignment.setProcessList(processList);
            machineReassignment.setProcessRequirementList(processRequirementList);
        }

        private void readBalancePenaltyList() throws IOException {
            int balancePenaltyListSize = readIntegerValue();
            List<MrBalancePenalty> balancePenaltyList = new ArrayList<MrBalancePenalty>(balancePenaltyListSize);
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
            globalPenaltyInfo.setProcessMoveCost(Integer.parseInt(lineTokens[0]));
            globalPenaltyInfo.setServiceMoveCost(Integer.parseInt(lineTokens[1]));
            globalPenaltyInfo.setMachineMoveCost(Integer.parseInt(lineTokens[2]));
            machineReassignment.setGlobalPenaltyInfo(globalPenaltyInfo);
        }

//        private void createBedDesignationList() {
//            List<AdmissionPart> admissionPartList = machineReassignment.getAdmissionPartList();
//            List<BedDesignation> bedDesignationList = new ArrayList<BedDesignation>(admissionPartList.size());
//            long id = 0L;
//            for (AdmissionPart admissionPart : admissionPartList) {
//                BedDesignation bedDesignation = new BedDesignation();
//                bedDesignation.setId(id);
//                id++;
//                bedDesignation.setAdmissionPart(admissionPart);
//                // Notice that we leave the PlanningVariable properties on null
//                bedDesignationList.add(bedDesignation);
//            }
//            machineReassignment.setBedDesignationList(bedDesignationList);
//        }

    }

}
