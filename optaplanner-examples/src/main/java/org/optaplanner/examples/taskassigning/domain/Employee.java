package org.optaplanner.examples.taskassigning.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.persistence.jackson.KeySerializer;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@PlanningEntity
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Employee extends AbstractPersistable implements Labeled {

    private String fullName;

    private Set<Skill> skillSet;
    private Map<Customer, Affinity> affinityMap;

    @PlanningListVariable
    private List<Task> tasks;

    // TODO pinning: https://issues.redhat.com/browse/PLANNER-2633.

    public Employee() {
    }

    public Employee(long id, String fullName) {
        super(id);
        this.fullName = fullName;
        skillSet = new LinkedHashSet<>();
        affinityMap = new LinkedHashMap<>();
        tasks = new ArrayList<>();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<Skill> getSkillSet() {
        return skillSet;
    }

    public void setSkillSet(Set<Skill> skillSet) {
        this.skillSet = skillSet;
    }

    public Map<Customer, Affinity> getAffinityMap() {
        return affinityMap;
    }

    @JsonSerialize(keyUsing = KeySerializer.class)
    @JsonDeserialize(keyUsing = CustomerKeyDeserializer.class)
    public void setAffinityMap(Map<Customer, Affinity> affinityMap) {
        this.affinityMap = affinityMap;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * @param customer never null
     * @return never null
     */
    @JsonIgnore
    public Affinity getAffinity(Customer customer) {
        Affinity affinity = affinityMap.get(customer);
        if (affinity == null) {
            affinity = Affinity.NONE;
        }
        return affinity;
    }

    @JsonIgnore
    public Integer getEndTime() {
        return tasks.isEmpty() ? 0 : tasks.get(tasks.size() - 1).getEndTime();
    }

    @Override
    public String getLabel() {
        return fullName;
    }

    @JsonIgnore
    public String getToolText() {
        StringBuilder toolText = new StringBuilder();
        toolText.append("<html><center><b>").append(fullName).append("</b><br/><br/>");
        toolText.append("Skills:<br/>");
        for (Skill skill : skillSet) {
            toolText.append(skill.getLabel()).append("<br/>");
        }
        toolText.append("</center></html>");
        return toolText.toString();
    }

    @Override
    public String toString() {
        return fullName;
    }

}
