package org.optaplanner.examples.pas.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("AdmissionPart")
public class AdmissionPart extends AbstractPersistable {

    private Patient patient;
    private Night firstNight;
    private Night lastNight;
    private Specialism specialism;

    public AdmissionPart() {
    }

    public AdmissionPart(Patient patient, Night firstNight, Night lastNight, Specialism specialism) {
        this.patient = patient;
        this.firstNight = firstNight;
        this.lastNight = lastNight;
        this.specialism = specialism;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Night getFirstNight() {
        return firstNight;
    }

    public void setFirstNight(Night firstNight) {
        this.firstNight = firstNight;
    }

    public Night getLastNight() {
        return lastNight;
    }

    public void setLastNight(Night lastNight) {
        this.lastNight = lastNight;
    }

    public Specialism getSpecialism() {
        return specialism;
    }

    public void setSpecialism(Specialism specialism) {
        this.specialism = specialism;
    }

    public int getNightCount() {
        return lastNight.getIndex() - firstNight.getIndex() + 1;
    }

    public int calculateSameNightCount(AdmissionPart other) {
        int firstNightIndex = Math.max(getFirstNight().getIndex(), other.getFirstNight().getIndex());
        int lastNightIndex = Math.min(getLastNight().getIndex(), other.getLastNight().getIndex());
        return Math.max(0, lastNightIndex - firstNightIndex + 1);
    }

    @Override
    public String toString() {
        return patient + "(" + firstNight + "-" + lastNight + ")";
    }

}
