package org.jbpm.services.task.internals.rule;

import java.util.List;

import org.jbpm.services.task.impl.model.GroupImpl;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;

public class AssignmentService {

    public void assignTask(Task task) {
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        potentialOwners.clear();
        
        potentialOwners.add(new GroupImpl("Crusaders"));
    }
}

