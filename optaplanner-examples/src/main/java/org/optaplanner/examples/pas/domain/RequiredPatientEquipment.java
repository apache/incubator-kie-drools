package org.optaplanner.examples.pas.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = RequiredPatientEquipment.class, generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class RequiredPatientEquipment extends AbstractPersistable {

    private Patient patient;
    private Equipment equipment;

    public RequiredPatientEquipment() {
    }

    public RequiredPatientEquipment(long id, Patient patient, Equipment equipment) {
        super(id);
        this.patient = patient;
        this.equipment = equipment;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return patient + "-" + equipment;
    }

}
