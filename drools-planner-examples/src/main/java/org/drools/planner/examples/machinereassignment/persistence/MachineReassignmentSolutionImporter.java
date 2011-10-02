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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.drools.planner.examples.machinereassignment.domain.MachineReassignment;
import org.drools.planner.examples.machinereassignment.domain.MrMachine;
import org.drools.planner.examples.machinereassignment.domain.MrResource;
import org.drools.planner.examples.pas.domain.AdmissionPart;
import org.drools.planner.examples.pas.domain.Bed;
import org.drools.planner.examples.pas.domain.BedDesignation;
import org.drools.planner.examples.pas.domain.Department;
import org.drools.planner.examples.pas.domain.DepartmentSpecialism;
import org.drools.planner.examples.pas.domain.Equipment;
import org.drools.planner.examples.pas.domain.Gender;
import org.drools.planner.examples.pas.domain.GenderLimitation;
import org.drools.planner.examples.pas.domain.Night;
import org.drools.planner.examples.pas.domain.Patient;
import org.drools.planner.examples.pas.domain.PatientAdmissionSchedule;
import org.drools.planner.examples.pas.domain.PreferredPatientEquipment;
import org.drools.planner.examples.pas.domain.RequiredPatientEquipment;
import org.drools.planner.examples.pas.domain.Room;
import org.drools.planner.examples.pas.domain.RoomEquipment;
import org.drools.planner.examples.pas.domain.RoomSpecialism;
import org.drools.planner.examples.pas.domain.Specialism;
import sun.security.util.BigInt;

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
                String line = bufferedReader.readLine();
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
                String line = bufferedReader.readLine();
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
