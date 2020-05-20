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

package org.optaplanner.examples.pas.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoft.HardMediumSoftScoreXStreamConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@PlanningSolution
@XStreamAlias("PatientAdmissionSchedule")
public class PatientAdmissionSchedule extends AbstractPersistable {

    private List<Specialism> specialismList;
    private List<Equipment> equipmentList;
    private List<Department> departmentList;
    private List<DepartmentSpecialism> departmentSpecialismList;
    private List<Room> roomList;
    private List<RoomSpecialism> roomSpecialismList;
    private List<RoomEquipment> roomEquipmentList;
    private List<Bed> bedList;
    private List<Night> nightList;
    private List<Patient> patientList;
    private List<AdmissionPart> admissionPartList;
    private List<RequiredPatientEquipment> requiredPatientEquipmentList;
    private List<PreferredPatientEquipment> preferredPatientEquipmentList;

    private List<BedDesignation> bedDesignationList;

    @XStreamConverter(HardMediumSoftScoreXStreamConverter.class)
    private HardMediumSoftScore score;

    @ProblemFactCollectionProperty
    public List<Specialism> getSpecialismList() {
        return specialismList;
    }

    public void setSpecialismList(List<Specialism> specialismList) {
        this.specialismList = specialismList;
    }

    @ProblemFactCollectionProperty
    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    @ProblemFactCollectionProperty
    public List<Department> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<Department> departmentList) {
        this.departmentList = departmentList;
    }

    @ProblemFactCollectionProperty
    public List<DepartmentSpecialism> getDepartmentSpecialismList() {
        return departmentSpecialismList;
    }

    public void setDepartmentSpecialismList(List<DepartmentSpecialism> departmentSpecialismList) {
        this.departmentSpecialismList = departmentSpecialismList;
    }

    @ProblemFactCollectionProperty
    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    @ProblemFactCollectionProperty
    public List<RoomSpecialism> getRoomSpecialismList() {
        return roomSpecialismList;
    }

    public void setRoomSpecialismList(List<RoomSpecialism> roomSpecialismList) {
        this.roomSpecialismList = roomSpecialismList;
    }

    @ProblemFactCollectionProperty
    public List<RoomEquipment> getRoomEquipmentList() {
        return roomEquipmentList;
    }

    public void setRoomEquipmentList(List<RoomEquipment> roomEquipmentList) {
        this.roomEquipmentList = roomEquipmentList;
    }

    @ValueRangeProvider(id = "bedRange")
    @ProblemFactCollectionProperty
    public List<Bed> getBedList() {
        return bedList;
    }

    public void setBedList(List<Bed> bedList) {
        this.bedList = bedList;
    }

    @ProblemFactCollectionProperty
    public List<Night> getNightList() {
        return nightList;
    }

    public void setNightList(List<Night> nightList) {
        this.nightList = nightList;
    }

    @ProblemFactCollectionProperty
    public List<Patient> getPatientList() {
        return patientList;
    }

    public void setPatientList(List<Patient> patientList) {
        this.patientList = patientList;
    }

    @ProblemFactCollectionProperty
    public List<AdmissionPart> getAdmissionPartList() {
        return admissionPartList;
    }

    public void setAdmissionPartList(List<AdmissionPart> admissionPartList) {
        this.admissionPartList = admissionPartList;
    }

    @ProblemFactCollectionProperty
    public List<RequiredPatientEquipment> getRequiredPatientEquipmentList() {
        return requiredPatientEquipmentList;
    }

    public void setRequiredPatientEquipmentList(List<RequiredPatientEquipment> requiredPatientEquipmentList) {
        this.requiredPatientEquipmentList = requiredPatientEquipmentList;
    }

    @ProblemFactCollectionProperty
    public List<PreferredPatientEquipment> getPreferredPatientEquipmentList() {
        return preferredPatientEquipmentList;
    }

    public void setPreferredPatientEquipmentList(List<PreferredPatientEquipment> preferredPatientEquipmentList) {
        this.preferredPatientEquipmentList = preferredPatientEquipmentList;
    }

    @PlanningEntityCollectionProperty
    public List<BedDesignation> getBedDesignationList() {
        return bedDesignationList;
    }

    public void setBedDesignationList(List<BedDesignation> bedDesignationList) {
        this.bedDesignationList = bedDesignationList;
    }

    @PlanningScore
    public HardMediumSoftScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
