package org.optaplanner.examples.nurserostering.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

public class SkillProficiency extends AbstractPersistableJackson {

    private Employee employee;
    private Skill skill;

    public SkillProficiency() { // For Jackson.
    }

    public SkillProficiency(long id, Employee employee, Skill skill) {
        super(id);
        this.employee = employee;
        this.skill = skill;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    @Override
    public String toString() {
        return employee + "-" + skill;
    }

}
