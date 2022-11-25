package org.optaplanner.examples.taskassigning.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.swingui.components.Labeled;
import org.optaplanner.examples.taskassigning.domain.solver.StartTimeUpdatingVariableListener;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningEntity
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Task extends AbstractPersistable implements Labeled {

    private TaskType taskType;
    private int indexInTaskType;
    private Customer customer;
    private int readyTime;
    private Priority priority;

    // Shadow variables
    @InverseRelationShadowVariable(sourceVariableName = "tasks")
    private Employee employee;
    @ShadowVariable(variableListenerClass = StartTimeUpdatingVariableListener.class,
            sourceEntityClass = Employee.class, sourceVariableName = "tasks")
    private Integer startTime; // In minutes

    public Task() {
    }

    public Task(long id, TaskType taskType, int indexInTaskType, Customer customer, int readyTime, Priority priority) {
        super(id);
        this.taskType = taskType;
        this.indexInTaskType = indexInTaskType;
        this.customer = customer;
        this.readyTime = readyTime;
        this.priority = priority;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public int getIndexInTaskType() {
        return indexInTaskType;
    }

    public void setIndexInTaskType(int indexInTaskType) {
        this.indexInTaskType = indexInTaskType;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(int readyTime) {
        this.readyTime = readyTime;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @JsonIgnore
    public int getMissingSkillCount() {
        if (employee == null) {
            return 0;
        }
        int count = 0;
        for (Skill skill : taskType.getRequiredSkillList()) {
            if (!employee.getSkillSet().contains(skill)) {
                count++;
            }
        }
        return count;
    }

    /**
     * In minutes
     *
     * @return at least 1 minute
     */
    @JsonIgnore
    public int getDuration() {
        Affinity affinity = getAffinity();
        return taskType.getBaseDuration() * affinity.getDurationMultiplier();
    }

    @JsonIgnore
    public Affinity getAffinity() {
        return (employee == null) ? Affinity.NONE : employee.getAffinity(customer);
    }

    @JsonIgnore
    public Integer getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime + getDuration();
    }

    @JsonIgnore
    public String getCode() {
        return taskType + "-" + indexInTaskType;
    }

    @JsonIgnore
    public String getTitle() {
        return taskType.getTitle();
    }

    @Override
    public String getLabel() {
        return getCode() + ": " + taskType.getTitle();
    }

    @JsonIgnore
    public String getToolText() {
        StringBuilder toolText = new StringBuilder();
        toolText.append("<html><center><b>").append(getLabel()).append("</b><br/>")
                .append(priority.getLabel()).append("<br/><br/>");
        toolText.append("Required skills:<br/>");
        for (Skill skill : taskType.getRequiredSkillList()) {
            toolText.append(skill.getLabel()).append("<br/>");
        }
        toolText.append("<br/>Customer:<br/>").append(customer.getName()).append("<br/>(")
                .append(getAffinity().getLabel()).append(")<br/>");
        toolText.append("</center></html>");
        return toolText.toString();
    }

    @Override
    public String toString() {
        return getCode();
    }

}
