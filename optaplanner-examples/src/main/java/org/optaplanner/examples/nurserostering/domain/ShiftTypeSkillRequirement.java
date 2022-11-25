package org.optaplanner.examples.nurserostering.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class ShiftTypeSkillRequirement extends AbstractPersistable {

    private ShiftType shiftType;
    private Skill skill;

    public ShiftTypeSkillRequirement() {
    }

    public ShiftTypeSkillRequirement(long id, ShiftType shiftType, Skill skill) {
        super(id);
        this.shiftType = shiftType;
        this.skill = skill;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    @Override
    public String toString() {
        return shiftType + "-" + skill;
    }

}
