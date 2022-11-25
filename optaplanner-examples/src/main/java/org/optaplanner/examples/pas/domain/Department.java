package org.optaplanner.examples.pas.domain;

import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = Department.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Department extends AbstractPersistable implements Labeled {

    private String name;
    private Integer minimumAge = null;
    private Integer maximumAge = null;

    private List<Room> roomList;

    public Department() {
    }

    public Department(long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Integer getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(Integer maximumAge) {
        this.maximumAge = maximumAge;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    public int countHardDisallowedAdmissionPart(AdmissionPart admissionPart) {
        return countDisallowedPatientAge(admissionPart.getPatient());
    }

    public int countDisallowedPatientAge(Patient patient) {
        int count = 0;
        if (minimumAge != null && patient.getAge() < minimumAge) {
            count += 100;
        }
        if (maximumAge != null && patient.getAge() > maximumAge) {
            count += 100;
        }
        return count;
    }

    @Override
    public String getLabel() {
        String label = name;
        if (minimumAge != null) {
            label += "(≥" + minimumAge + ")";
        }
        if (maximumAge != null) {
            label += "(≤" + maximumAge + ")";
        }
        return label;
    }

    @Override
    public String toString() {
        return name;
    }

}
