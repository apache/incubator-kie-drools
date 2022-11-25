package org.optaplanner.examples.taskassigning.domain;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class TaskType extends AbstractPersistable implements Labeled {

    private String code;
    private String title;
    private int baseDuration; // In minutes

    private List<Skill> requiredSkillList;

    public TaskType() {
    }

    public TaskType(long id, String code, String title, int baseDuration) {
        super(id);
        this.code = code;
        this.title = title;
        this.baseDuration = baseDuration;
        requiredSkillList = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBaseDuration() {
        return baseDuration;
    }

    public void setBaseDuration(int baseDuration) {
        this.baseDuration = baseDuration;
    }

    public List<Skill> getRequiredSkillList() {
        return requiredSkillList;
    }

    public void setRequiredSkillList(List<Skill> requiredSkillList) {
        this.requiredSkillList = requiredSkillList;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public String getLabel() {
        return title;
    }

    @Override
    public String toString() {
        return code;
    }

}
