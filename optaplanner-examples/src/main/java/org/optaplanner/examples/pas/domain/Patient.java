package org.optaplanner.examples.pas.domain;

import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Patient.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Patient extends AbstractPersistable {

    private String name;
    private Gender gender;
    private int age;
    private Integer preferredMaximumRoomCapacity;

    public Patient() {
    }

    public Patient(long id, String name, Gender gender, int age, Integer preferredMaximumRoomCapacity) {
        super(id);
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.preferredMaximumRoomCapacity = preferredMaximumRoomCapacity;
    }

    private List<RequiredPatientEquipment> requiredPatientEquipmentList;
    private List<PreferredPatientEquipment> preferredPatientEquipmentList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getPreferredMaximumRoomCapacity() {
        return preferredMaximumRoomCapacity;
    }

    public void setPreferredMaximumRoomCapacity(Integer preferredMaximumRoomCapacity) {
        this.preferredMaximumRoomCapacity = preferredMaximumRoomCapacity;
    }

    public List<RequiredPatientEquipment> getRequiredPatientEquipmentList() {
        return requiredPatientEquipmentList;
    }

    public void setRequiredPatientEquipmentList(List<RequiredPatientEquipment> requiredPatientEquipmentList) {
        this.requiredPatientEquipmentList = requiredPatientEquipmentList;
    }

    public List<PreferredPatientEquipment> getPreferredPatientEquipmentList() {
        return preferredPatientEquipmentList;
    }

    public void setPreferredPatientEquipmentList(List<PreferredPatientEquipment> preferredPatientEquipmentList) {
        this.preferredPatientEquipmentList = preferredPatientEquipmentList;
    }

    @Override
    public String toString() {
        return name;
    }

}
