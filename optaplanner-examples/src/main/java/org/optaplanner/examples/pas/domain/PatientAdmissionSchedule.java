/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.impl.score.buildin.hardmediumsoft.HardMediumSoftScoreDefinition;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.persistence.xstream.impl.score.XStreamScoreConverter;

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

    @XStreamConverter(value = XStreamScoreConverter.class, types = {HardMediumSoftScoreDefinition.class})
    private HardMediumSoftScore score;

    @PlanningFactCollectionProperty
    public List<Specialism> getSpecialismList() {
        return specialismList;
    }

    public void setSpecialismList(List<Specialism> specialismList) {
        this.specialismList = specialismList;
    }

    @PlanningFactCollectionProperty
    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    @PlanningFactCollectionProperty
    public List<Department> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<Department> departmentList) {
        this.departmentList = departmentList;
    }

    @PlanningFactCollectionProperty
    public List<DepartmentSpecialism> getDepartmentSpecialismList() {
        return departmentSpecialismList;
    }

    public void setDepartmentSpecialismList(List<DepartmentSpecialism> departmentSpecialismList) {
        this.departmentSpecialismList = departmentSpecialismList;
    }

    @PlanningFactCollectionProperty
    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    @PlanningFactCollectionProperty
    public List<RoomSpecialism> getRoomSpecialismList() {
        return roomSpecialismList;
    }

    public void setRoomSpecialismList(List<RoomSpecialism> roomSpecialismList) {
        this.roomSpecialismList = roomSpecialismList;
    }

    @PlanningFactCollectionProperty
    public List<RoomEquipment> getRoomEquipmentList() {
        return roomEquipmentList;
    }

    public void setRoomEquipmentList(List<RoomEquipment> roomEquipmentList) {
        this.roomEquipmentList = roomEquipmentList;
    }

    @ValueRangeProvider(id = "bedRange")
    @PlanningFactCollectionProperty
    public List<Bed> getBedList() {
        return bedList;
    }

    public void setBedList(List<Bed> bedList) {
        this.bedList = bedList;
    }

    @PlanningFactCollectionProperty
    public List<Night> getNightList() {
        return nightList;
    }

    public void setNightList(List<Night> nightList) {
        this.nightList = nightList;
    }

    @PlanningFactCollectionProperty
    public List<Patient> getPatientList() {
        return patientList;
    }

    public void setPatientList(List<Patient> patientList) {
        this.patientList = patientList;
    }

    @PlanningFactCollectionProperty
    public List<AdmissionPart> getAdmissionPartList() {
        return admissionPartList;
    }

    public void setAdmissionPartList(List<AdmissionPart> admissionPartList) {
        this.admissionPartList = admissionPartList;
    }

    @PlanningFactCollectionProperty
    public List<RequiredPatientEquipment> getRequiredPatientEquipmentList() {
        return requiredPatientEquipmentList;
    }

    public void setRequiredPatientEquipmentList(List<RequiredPatientEquipment> requiredPatientEquipmentList) {
        this.requiredPatientEquipmentList = requiredPatientEquipmentList;
    }

    @PlanningFactCollectionProperty
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
