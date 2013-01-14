package org.jbpm.task.service.test.impl;

import java.util.List;

import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Task;

public class AssignmentService {

    public void assignTask(Task task) {
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        potentialOwners.clear();
        
        potentialOwners.add(new Group("Crusaders"));
    }
}
