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
import java.util.List;
import java.util.Map;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.drools.planner.examples.machinereassignment.domain.MachineReassignment;
import org.drools.planner.examples.machinereassignment.domain.MrBalancePenalty;
import org.drools.planner.examples.machinereassignment.domain.MrGlobalPenaltyInfo;
import org.drools.planner.examples.machinereassignment.domain.MrMachine;
import org.drools.planner.examples.machinereassignment.domain.MrProcess;
import org.drools.planner.examples.machinereassignment.domain.MrResource;
import org.drools.planner.examples.machinereassignment.domain.MrService;
import org.drools.planner.examples.pas.domain.Specialism;

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

        private Map<Long, Specialism> idToSpecialismMap = null;

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
            int resourceListSize = readIntegerValue();
            List<MrResource> resourceList = new ArrayList<MrResource>(resourceListSize);
            long resourceId = 0L;
            for (int i = 0; i < resourceListSize; i++) {
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line, 2);
                MrResource resource = new MrResource();
                resource.setId(resourceId);
                resourceId++;
                resource.setTransientlyConsumed(parseBooleanFromNumber(lineTokens[0]));
                resource.setWeight(Integer.parseInt(lineTokens[1]));
                resourceList.add(resource);
            }
            machineReassignment.setResourceList(resourceList);
        }

        private void readMachineList() throws IOException {
            int machineListSize = readIntegerValue();
            List<MrMachine> machineList = new ArrayList<MrMachine>(machineListSize);
            long machineId = 0L;
            for (int i = 0; i < machineListSize; i++) {
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line);
                MrMachine machine = new MrMachine();
                machine.setId(machineId);
                machineId++;
//                machine.setTransientlyConsumed(parseBooleanFromNumber(lineTokens[0]));
//                machine.setWeight(Integer.parseInt(lineTokens[1]));
                machineList.add(machine);
            }
            machineReassignment.setMachineList(machineList);
        }

        private void readServiceList() throws IOException {
            int serviceListSize = readIntegerValue();
            List<MrService> serviceList = new ArrayList<MrService>(serviceListSize);
            long serviceId = 0L;
            for (int i = 0; i < serviceListSize; i++) {
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line);
                MrService service = new MrService();
                service.setId(serviceId);
                serviceId++;
//                machine.setTransientlyConsumed(parseBooleanFromNumber(lineTokens[0]));
//                machine.setWeight(Integer.parseInt(lineTokens[1]));
                serviceList.add(service);
            }
            machineReassignment.setServiceList(serviceList);
        }

        private void readProcessList() throws IOException {
            int processListSize = readIntegerValue();
            List<MrProcess> processList = new ArrayList<MrProcess>(processListSize);
            long processId = 0L;
            for (int i = 0; i < processListSize; i++) {
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line);
                MrProcess process = new MrProcess();
                process.setId(processId);
                processId++;
//                machine.setTransientlyConsumed(parseBooleanFromNumber(lineTokens[0]));
//                machine.setWeight(Integer.parseInt(lineTokens[1]));
                processList.add(process);
            }
            machineReassignment.setProcessList(processList);
        }

        private void readBalancePenaltyList() throws IOException {
            int balancePenaltyListSize = readIntegerValue();
            List<MrBalancePenalty> balancePenaltyList = new ArrayList<MrBalancePenalty>(balancePenaltyListSize);
            long balancePenaltyId = 0L;
            for (int i = 0; i < balancePenaltyListSize; i++) {
                String line = readStringValue();
                String[] lineTokens = splitBySpace(line);
                MrBalancePenalty balancePenalty = new MrBalancePenalty();
                balancePenalty.setId(balancePenaltyId);
                balancePenaltyId++;
//                machine.setTransientlyConsumed(parseBooleanFromNumber(lineTokens[0]));
//                machine.setWeight(Integer.parseInt(lineTokens[1]));
                int weight = readIntegerValue(); // TODO
                balancePenaltyList.add(balancePenalty);
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
