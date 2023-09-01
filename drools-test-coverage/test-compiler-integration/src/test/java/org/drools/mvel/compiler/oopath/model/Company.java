package org.drools.mvel.compiler.oopath.model;


import org.drools.core.phreak.AbstractReactiveObject;

public class Company extends AbstractReactiveObject {

    private String name;

    private Employee[] employees;

    public Company(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
        notifyModification();
    }

    public Employee[] getEmployees() {
        return  employees;
    }

    public void setEmployees(final Employee[] employees) {
        this.employees = employees;
        notifyModification();
    }

    @Override
    public String toString() {
        return ("Company: " + name);
    }
}
