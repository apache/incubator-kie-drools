package org.optaplanner.examples.pas.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class DepartmentSpecialism extends AbstractPersistable {

    private Department department;
    private Specialism specialism;

    private int priority; // AKA choice

    public DepartmentSpecialism() {
    }

    public DepartmentSpecialism(long id, Department department, Specialism specialism, int priority) {
        super(id);
        this.department = department;
        this.specialism = specialism;
        this.priority = priority;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Specialism getSpecialism() {
        return specialism;
    }

    public void setSpecialism(Specialism specialism) {
        this.specialism = specialism;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return department + "-" + specialism;
    }

}
