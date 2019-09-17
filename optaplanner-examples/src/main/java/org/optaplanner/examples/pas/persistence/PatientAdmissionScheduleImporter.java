/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.pas.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.pas.app.PatientAdmissionScheduleApp;
import org.optaplanner.examples.pas.domain.AdmissionPart;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.Department;
import org.optaplanner.examples.pas.domain.DepartmentSpecialism;
import org.optaplanner.examples.pas.domain.Equipment;
import org.optaplanner.examples.pas.domain.Gender;
import org.optaplanner.examples.pas.domain.GenderLimitation;
import org.optaplanner.examples.pas.domain.Night;
import org.optaplanner.examples.pas.domain.Patient;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.domain.PreferredPatientEquipment;
import org.optaplanner.examples.pas.domain.RequiredPatientEquipment;
import org.optaplanner.examples.pas.domain.Room;
import org.optaplanner.examples.pas.domain.RoomEquipment;
import org.optaplanner.examples.pas.domain.RoomSpecialism;
import org.optaplanner.examples.pas.domain.Specialism;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;

public class PatientAdmissionScheduleImporter extends AbstractTxtSolutionImporter<PatientAdmissionSchedule> {

    public static void main(String[] args) {
        SolutionConverter<PatientAdmissionSchedule> converter = SolutionConverter.createImportConverter(
                PatientAdmissionScheduleApp.DATA_DIR_NAME, new PatientAdmissionScheduleImporter(), PatientAdmissionSchedule.class);
        converter.convertAll();
    }

    @Override
    public TxtInputBuilder<PatientAdmissionSchedule> createTxtInputBuilder() {
        return new PatientAdmissionScheduleInputBuilder();
    }

    public static class PatientAdmissionScheduleInputBuilder extends TxtInputBuilder<PatientAdmissionSchedule> {

        private static final Comparator<Room> ROOM_COMPARATOR =
                comparing(Room::getDepartment, comparingLong(Department::getId))
                        .thenComparingLong(Room::getId);
        private static final Comparator<Bed> BED_COMPARATOR = comparing(Bed::getRoom, ROOM_COMPARATOR)
                .thenComparingInt(Bed::getIndexInRoom)
                .thenComparingLong(Bed::getId);

        private PatientAdmissionSchedule patientAdmissionSchedule;

        private int specialismListSize;
        private int departmentListSize;
        private int equipmentListSize;
        private int roomListSize;
        private int bedListSize;
        private int nightListSize;
        private int patientListSize;

        private Map<Long, Specialism> idToSpecialismMap = null;
        private Map<Long, Department> idToDepartmentMap = null;
        private Map<Integer, Equipment> indexToEquipmentMap = null;
        private Map<Long, Room> idToRoomMap = null;
        private Map<Integer, Night> indexToNightMap = null;

        @Override
        public PatientAdmissionSchedule readSolution() throws IOException {
            patientAdmissionSchedule = new PatientAdmissionSchedule();
            patientAdmissionSchedule.setId(0L);
            readSizes();
            readEmptyLine();
            readEmptyLine();
            readSpecialismList();
            readEmptyLine();
            readDepartmentListAndDepartmentSpecialismList();
            readEmptyLine();
            readEquipmentList();
            readEmptyLine();
            readRoomListAndRoomSpecialismListAndRoomEquipmentList();
            readEmptyLine();
            readBedList();
            readEmptyLine();
            generateNightList();
            readPatientListAndAdmissionPartListAndRequiredPatientEquipmentListAndPreferredPatientEquipmentList();
            readEmptyLine();
            readConstantLine("END\\.");
            createBedDesignationList();
            // TODO not all nights are planned, only the "planning horizon" nights are planned
            BigInteger possibleSolutionSize = BigInteger.valueOf(patientAdmissionSchedule.getBedList().size()).pow(
                    patientAdmissionSchedule.getAdmissionPartList().size());
            logger.info("PatientAdmissionSchedule {} has {} specialisms, {} equipments, {} departments, {} rooms, "
                            + "{} beds, {} nights, {} patients and {} admissions with a search space of {}.",
                    getInputId(),
                    patientAdmissionSchedule.getSpecialismList().size(),
                    patientAdmissionSchedule.getEquipmentList().size(),
                    patientAdmissionSchedule.getDepartmentList().size(),
                    patientAdmissionSchedule.getRoomList().size(),
                    patientAdmissionSchedule.getBedList().size(),
                    patientAdmissionSchedule.getNightList().size(),
                    patientAdmissionSchedule.getPatientList().size(),
                    patientAdmissionSchedule.getAdmissionPartList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return patientAdmissionSchedule;
        }

        private void readSizes() throws IOException {
            readConstantLine("ARTICLE BENCHMARK DATA SET");
            roomListSize = readIntegerValue("Rooms:");
            equipmentListSize = readIntegerValue("Roomproperties:");
            bedListSize = readIntegerValue("Beds:");
            departmentListSize = readIntegerValue("Departments:");
            specialismListSize = readIntegerValue("Specialisms:");
            patientListSize = readIntegerValue("Patients:");
            nightListSize = readIntegerValue("Planning horizon:");
        }

        private void readSpecialismList() throws IOException {
            readConstantLine("SPECIALISMS:");
            List<Specialism> specialismList = new ArrayList<>(specialismListSize);
            idToSpecialismMap = new HashMap<>(specialismListSize);
            for (int i = 0; i < specialismListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line, 2);
                Specialism specialism = new Specialism();
                specialism.setId(Long.parseLong(lineTokens[0]));
                specialism.setName(lineTokens[1]);
                specialismList.add(specialism);
                idToSpecialismMap.put(specialism.getId(), specialism);
            }
            patientAdmissionSchedule.setSpecialismList(specialismList);
        }

        private void readDepartmentListAndDepartmentSpecialismList() throws IOException {
            readConstantLine("DEPARTMENTS:");
            List<Department> departmentList = new ArrayList<>(departmentListSize);
            idToDepartmentMap = new HashMap<>(departmentListSize);
            List<DepartmentSpecialism> departmentSpecialismList = new ArrayList<>(
                    departmentListSize * 5);
            long departmentSpecialismId = 0L;
            for (int i = 0; i < departmentListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitByPipelineAndTrim(line, 2);

                String[] departmentTokens = splitBySpace(lineTokens[0], 4);
                Department department = new Department();
                department.setId(Long.parseLong(departmentTokens[0]));
                department.setName(departmentTokens[1]);
                department.setRoomList(new ArrayList<>());
                int minimumAge = Integer.parseInt(departmentTokens[2]);
                if (minimumAge != 0) {
                    department.setMinimumAge(Integer.valueOf(minimumAge));
                }
                int maximumAge = Integer.parseInt(departmentTokens[3]);
                if (maximumAge != 0) {
                    department.setMaximumAge(Integer.valueOf(maximumAge));
                }
                departmentList.add(department);
                idToDepartmentMap.put(department.getId(), department);

                String[] departmentSpecialismTokens = splitBySpace(lineTokens[1]);
                if (departmentSpecialismTokens.length % 2 != 0) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") is expected to contain even number of tokens (" + departmentSpecialismTokens.length
                            + ") after 1st pipeline (|) separated by a space ( ).");
                }
                for (int j = 0; j < departmentSpecialismTokens.length; j += 2) {
                    long specialismId = Long.parseLong(departmentSpecialismTokens[j + 1]);
                    if (specialismId != 0) {
                        DepartmentSpecialism departmentSpecialism = new DepartmentSpecialism();
                        departmentSpecialism.setId(departmentSpecialismId);
                        departmentSpecialism.setDepartment(department);
                        departmentSpecialism.setPriority(Integer.parseInt(departmentSpecialismTokens[j]));
                        Specialism specialism = idToSpecialismMap.get(specialismId);
                        if (specialism == null) {
                            throw new IllegalArgumentException("Read line (" + line
                                    + ") has a non existing specialismId (" + specialismId + ").");
                        }
                        departmentSpecialism.setSpecialism(specialism);
                        departmentSpecialismList.add(departmentSpecialism);
                        departmentSpecialismId++;
                    }
                }
            }
            Collections.sort(departmentList, comparingLong(Department::getId));
            patientAdmissionSchedule.setDepartmentList(departmentList);
            patientAdmissionSchedule.setDepartmentSpecialismList(departmentSpecialismList);
        }

        private void readEquipmentList() throws IOException {
            readConstantLine("ROOMPROPERTIES:");
            List<Equipment> equipmentList = new ArrayList<>(equipmentListSize);
            indexToEquipmentMap = new HashMap<>(equipmentListSize);
            for (int i = 0; i < equipmentListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line, 2);
                Equipment equipment = new Equipment();
                equipment.setId(Long.parseLong(lineTokens[0]));
                equipment.setName(lineTokens[1]);
                equipmentList.add(equipment);
                indexToEquipmentMap.put(i, equipment);
            }
            patientAdmissionSchedule.setEquipmentList(equipmentList);
        }

        private void readRoomListAndRoomSpecialismListAndRoomEquipmentList() throws IOException {
            readConstantLine("ROOMS:");
            List<Room> roomList = new ArrayList<>(roomListSize);
            idToRoomMap = new HashMap<>(roomListSize);
            List<RoomSpecialism> roomSpecialismList = new ArrayList<>(roomListSize * 5);
            List<RoomEquipment> roomEquipmentList = new ArrayList<>(roomListSize * 2);
            long roomSpecialismId = 0L;
            long roomEquipmentId = 0L;
            for (int i = 0; i < roomListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitByPipelineAndTrim(line, 6);

                String[] roomTokens = splitBySpace(lineTokens[0], 2);
                Room room = new Room();
                room.setId(Long.parseLong(roomTokens[0]));
                room.setName(roomTokens[1]);
                room.setCapacity(Integer.parseInt(lineTokens[1]));
                Department department = idToDepartmentMap.get(
                        Long.parseLong(lineTokens[2]));
                room.setDepartment(department);
                room.setGenderLimitation(GenderLimitation.valueOfCode(lineTokens[3]));
                room.setBedList(new ArrayList<>());
                roomList.add(room);
                idToRoomMap.put(room.getId(), room);
                department.getRoomList().add(room);

                String[] roomSpecialismTokens = splitBySpace(lineTokens[4]);
                if (roomSpecialismTokens.length % 2 != 0) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") is expected to contain even number of tokens (" + roomSpecialismTokens.length
                            + ") after 4th pipeline (|) separated by a space ( ).");
                }
                List<RoomSpecialism> roomSpecialismListOfRoom = new ArrayList<>(roomSpecialismTokens.length / 2);
                for (int j = 0; j < roomSpecialismTokens.length; j += 2) {
                    int priority = Integer.parseInt(roomSpecialismTokens[j]);
                    long specialismId = Long.parseLong(roomSpecialismTokens[j + 1]);
                    if (specialismId != 0) {
                        RoomSpecialism roomSpecialism = new RoomSpecialism();
                        roomSpecialism.setId(roomSpecialismId);
                        roomSpecialism.setRoom(room);
                        Specialism specialism = idToSpecialismMap.get(specialismId);
                        if (specialism == null) {
                            throw new IllegalArgumentException("Read line (" + line
                                    + ") has a non existing specialismId (" + specialismId + ").");
                        }
                        roomSpecialism.setSpecialism(specialism);
                        roomSpecialism.setPriority(priority);
                        roomSpecialismListOfRoom.add(roomSpecialism);
                        roomSpecialismList.add(roomSpecialism);
                        roomSpecialismId++;
                    }
                }
                room.setRoomSpecialismList(roomSpecialismListOfRoom);

                List<RoomEquipment> roomEquipmentListOfRoom = new ArrayList<>(equipmentListSize);
                String[] roomEquipmentTokens = splitBySpace(lineTokens[5]);
                if (roomEquipmentTokens.length != equipmentListSize) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") is expected to contain equal number of tokens (" + roomEquipmentTokens.length
                            + ") as equipmentListSize (" + equipmentListSize + ") after 5th pipeline (|).");
                }
                for (int j = 0; j < roomEquipmentTokens.length; j++) {
                    int hasEquipment = Integer.parseInt(roomEquipmentTokens[j]);
                    if (hasEquipment == 1) {
                        RoomEquipment roomEquipment = new RoomEquipment();
                        roomEquipment.setId(roomEquipmentId);
                        roomEquipment.setRoom(room);
                        roomEquipment.setEquipment(indexToEquipmentMap.get(j));
                        roomEquipmentListOfRoom.add(roomEquipment);
                        roomEquipmentList.add(roomEquipment);
                        roomEquipmentId++;
                    } else if (hasEquipment != 0) {
                        throw new IllegalArgumentException("Read line (" + line
                                + ") is expected to have 0 or 1 hasEquipment (" + hasEquipment + ").");
                    }
                }
                room.setRoomEquipmentList(roomEquipmentListOfRoom);
            }
            Collections.sort(roomList, ROOM_COMPARATOR);
            patientAdmissionSchedule.setRoomList(roomList);
            patientAdmissionSchedule.setRoomSpecialismList(roomSpecialismList);
            patientAdmissionSchedule.setRoomEquipmentList(roomEquipmentList);
        }

        private void readBedList() throws IOException {
            readConstantLine("BEDS:");
            List<Bed> bedList = new ArrayList<>(bedListSize);
            Map<Room, Integer> roomToLastIndexInRoomMap = new HashMap<>(roomListSize);
            for (int i = 0; i < bedListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line, 2);
                Bed bed = new Bed();
                bed.setId(Long.parseLong(lineTokens[0]));
                Room room = idToRoomMap.get(Long.parseLong(lineTokens[1]));
                bed.setRoom(room);
                Integer indexInRoom = roomToLastIndexInRoomMap.get(room);
                if (indexInRoom == null) {
                    indexInRoom = 0;
                } else {
                    indexInRoom++;
                }
                bed.setIndexInRoom(indexInRoom);
                roomToLastIndexInRoomMap.put(room, indexInRoom);
                bedList.add(bed);
                room.getBedList().add(bed);
            }
            Collections.sort(bedList, BED_COMPARATOR);
            patientAdmissionSchedule.setBedList(bedList);
        }

        private void generateNightList() {
            List<Night> nightList = new ArrayList<>(nightListSize);
            indexToNightMap = new HashMap<>(nightListSize);
            long nightId = 0L;
            for (int i = 0; i < nightListSize; i++) {
                Night night = new Night();
                night.setId(nightId);
                night.setIndex(i);
                nightList.add(night);
                indexToNightMap.put(i, night);
                nightId++;
            }
            patientAdmissionSchedule.setNightList(nightList);
        }

        private void readPatientListAndAdmissionPartListAndRequiredPatientEquipmentListAndPreferredPatientEquipmentList() throws IOException {
            readConstantLine("PATIENTS:");
            List<Patient> patientList = new ArrayList<>(patientListSize);
            List<AdmissionPart> admissionPartList = new ArrayList<>(patientListSize);
            List<RequiredPatientEquipment> requiredPatientEquipmentList = new ArrayList<>(patientListSize * equipmentListSize);
            List<PreferredPatientEquipment> preferredPatientEquipmentList = new ArrayList<>(patientListSize * equipmentListSize);
            long admissionPartId = 0L;
            long requiredPatientEquipmentId = 0L;
            long preferredPatientEquipmentId = 0L;
            for (int i = 0; i < patientListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitByPipelineAndTrim(line, 6);

                String[] nightTokens = splitBySpace(lineTokens[1], 2);
                int firstNightIndex = Integer.parseInt(nightTokens[0]);
                int lastNightIndex = Integer.parseInt(nightTokens[1]);
                int patientNightListSize = lastNightIndex - firstNightIndex;
                // A patient with no nights in the planning horizon or no nights at all is ignored
                if (firstNightIndex >= nightListSize || patientNightListSize == 0) {
                    continue;
                }

                String[] patientTokens = splitBySpace(lineTokens[0], 4);
                Patient patient = new Patient();
                patient.setId(Long.parseLong(patientTokens[0]));
                patient.setName(patientTokens[1]);
                patient.setAge(Integer.parseInt(patientTokens[2]));
                patient.setGender(Gender.valueOfCode(patientTokens[3]));
                int preferredMaximumRoomCapacity = Integer.parseInt(lineTokens[3]);
                patient.setPreferredMaximumRoomCapacity(preferredMaximumRoomCapacity == 0
                        ? null : preferredMaximumRoomCapacity);
                patientList.add(patient);

                String[] admissionPartTokens = splitBySpace(lineTokens[2]);
                int patientAdmissionPartListSize = Integer.parseInt(admissionPartTokens[0]);
                if (admissionPartTokens.length != ((patientAdmissionPartListSize * 2) + 1)) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") is expected to contain " + ((patientAdmissionPartListSize * 2) + 1)
                            + " number of tokens after 2th pipeline (|).");
                }
                int nextFirstNightIndex = firstNightIndex;
                for (int j = 1; j < admissionPartTokens.length; j += 2) {
                    long specialismId = Long.parseLong(admissionPartTokens[j]);
                    int admissionPartNightListSize = Integer.parseInt(admissionPartTokens[j + 1]);
                    if (nextFirstNightIndex >= nightListSize || admissionPartNightListSize == 0) {
                        nextFirstNightIndex += admissionPartNightListSize;
                        continue;
                    }
                    AdmissionPart admissionPart = new AdmissionPart();
                    admissionPart.setId(admissionPartId);
                    admissionPart.setPatient(patient);
                    Specialism specialism = (specialismId == 0) ? null : idToSpecialismMap.get(specialismId);
                    if (specialism == null) {
                        throw new IllegalArgumentException("Read line (" + line
                                + ") has a non existing specialismId (" + specialismId + ").");
                    }
                    admissionPart.setSpecialism(specialism);
                    int admissionPartFirstNightIndex = nextFirstNightIndex;
                    Night admissionPartFirstNight = indexToNightMap.get(admissionPartFirstNightIndex);
                    if (admissionPartFirstNight == null) {
                        throw new IllegalStateException(
                                "The admissionPartFirstNight was not found for admissionPartFirstNightIndex("
                                        + admissionPartFirstNightIndex + ").");
                    }
                    admissionPart.setFirstNight(admissionPartFirstNight);
                    int admissionPartLastNightIndex = nextFirstNightIndex + admissionPartNightListSize - 1;
                    // TODO Instead of ensureEnoughNights(lastNightIndex);
                    // the official score function ignores any broken constraints after the planning horizon
                    if (admissionPartLastNightIndex >= nightListSize) {
                        admissionPartLastNightIndex = nightListSize - 1;
                    }
                    Night admissionPartLastNight = indexToNightMap.get(admissionPartLastNightIndex);
                    if (admissionPartLastNight == null) {
                        throw new IllegalStateException(
                                "The admissionPartLastNight was not found for admissionPartLastNightIndex("
                                        + admissionPartLastNightIndex + ").");
                    }
                    admissionPart.setLastNight(admissionPartLastNight);
                    admissionPartList.add(admissionPart);
                    admissionPartId++;
                    nextFirstNightIndex += admissionPartNightListSize;
                }
                int admissionPartNightListSizeSum = nextFirstNightIndex - firstNightIndex;
                if (patientNightListSize != admissionPartNightListSizeSum) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") has patientNightListSize (" + patientNightListSize
                            + ") different from admissionPartNightListSizeSum(" + admissionPartNightListSizeSum + ")");
                }

                String[] requiredPatientEquipmentTokens = splitBySpace(lineTokens[4]);
                if (requiredPatientEquipmentTokens.length != equipmentListSize) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") is expected to contain equal number of tokens ("
                            + requiredPatientEquipmentTokens.length
                            + ") as equipmentListSize (" + equipmentListSize + ") after 4th pipeline (|).");
                }
                List<RequiredPatientEquipment> requiredPatientEquipmentOfPatientList
                        = new ArrayList<>(equipmentListSize);
                for (int j = 0; j < requiredPatientEquipmentTokens.length; j++) {
                    int hasEquipment = Integer.parseInt(requiredPatientEquipmentTokens[j]);
                    if (hasEquipment == 1) {
                        RequiredPatientEquipment requiredPatientEquipment = new RequiredPatientEquipment();
                        requiredPatientEquipment.setId(requiredPatientEquipmentId);
                        requiredPatientEquipment.setPatient(patient);
                        requiredPatientEquipment.setEquipment(indexToEquipmentMap.get(j));
                        requiredPatientEquipmentOfPatientList.add(requiredPatientEquipment);
                        requiredPatientEquipmentList.add(requiredPatientEquipment);
                        requiredPatientEquipmentId++;
                    } else if (hasEquipment != 0) {
                        throw new IllegalArgumentException("Read line (" + line
                                + ") is expected to have 0 or 1 hasEquipment (" + hasEquipment + ").");
                    }
                }
                patient.setRequiredPatientEquipmentList(requiredPatientEquipmentOfPatientList);

                String[] preferredPatientEquipmentTokens = splitBySpace(lineTokens[5]);
                if (preferredPatientEquipmentTokens.length != equipmentListSize) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") is expected to contain equal number of tokens ("
                            + preferredPatientEquipmentTokens.length
                            + ") as equipmentListSize (" + equipmentListSize + ") after 5th pipeline (|).");
                }
                List<PreferredPatientEquipment> preferredPatientEquipmentOfPatientList
                        = new ArrayList<>(equipmentListSize);
                for (int j = 0; j < preferredPatientEquipmentTokens.length; j++) {
                    int hasEquipment = Integer.parseInt(preferredPatientEquipmentTokens[j]);
                    if (hasEquipment == 1) {
                        boolean alreadyRequired = (Integer.parseInt(requiredPatientEquipmentTokens[j]) == 1);
                        // Official spec: if equipment is required
                        // then a duplicate preffered constraint should be ignored
                        if (!alreadyRequired) {
                            PreferredPatientEquipment preferredPatientEquipment = new PreferredPatientEquipment();
                            preferredPatientEquipment.setId(preferredPatientEquipmentId);
                            preferredPatientEquipment.setPatient(patient);
                            preferredPatientEquipment.setEquipment(indexToEquipmentMap.get(j));
                            preferredPatientEquipmentOfPatientList.add(preferredPatientEquipment);
                            preferredPatientEquipmentList.add(preferredPatientEquipment);
                            preferredPatientEquipmentId++;
                        }
                    } else if (hasEquipment != 0) {
                        throw new IllegalArgumentException("Read line (" + line
                                + ") is expected to have 0 or 1 hasEquipment (" + hasEquipment + ").");
                    }
                }
                patient.setPreferredPatientEquipmentList(preferredPatientEquipmentOfPatientList);
            }
            patientAdmissionSchedule.setPatientList(patientList);
            patientAdmissionSchedule.setAdmissionPartList(admissionPartList);
            patientAdmissionSchedule.setRequiredPatientEquipmentList(requiredPatientEquipmentList);
            patientAdmissionSchedule.setPreferredPatientEquipmentList(preferredPatientEquipmentList);
        }

//        /**
//         * hack to make sure there are enough nights
//         * @param lastNightIndex {@code >= 0}
//         */
//        private void ensureEnoughNights(int lastNightIndex) {
//            List<Night> nightList = patientAdmissionSchedule.getNightList();
//            if (lastNightIndex >= nightList.size()) {
//                long nightId = nightList.size();
//                for (int j = nightList.size(); j <= lastNightIndex; j++) {
//                    Night night = new Night();
//                    night.setId(nightId);
//                    night.setIndex(j);
//                    nightList.add(night);
//                    indexToNightMap.put(j, night);
//                    nightId++;
//                }
//            }
//        }

        private void createBedDesignationList() {
            List<AdmissionPart> admissionPartList = patientAdmissionSchedule.getAdmissionPartList();
            List<BedDesignation> bedDesignationList = new ArrayList<>(admissionPartList.size());
            long id = 0L;
            for (AdmissionPart admissionPart : admissionPartList) {
                BedDesignation bedDesignation = new BedDesignation();
                bedDesignation.setId(id);
                id++;
                bedDesignation.setAdmissionPart(admissionPart);
                // Notice that we leave the PlanningVariable properties on null
                bedDesignationList.add(bedDesignation);
            }
            patientAdmissionSchedule.setBedDesignationList(bedDesignationList);
        }
    }
}
