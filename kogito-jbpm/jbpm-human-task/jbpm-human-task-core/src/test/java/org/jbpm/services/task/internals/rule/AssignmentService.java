package org.jbpm.services.task.internals.rule;

import java.util.List;

import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;

public class AssignmentService {

    public void assignTask(Task task) {
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        potentialOwners.clear();
        
        Group group = TaskModelProvider.getFactory().newGroup();
        ((InternalOrganizationalEntity) group).setId("Crusaders");
        potentialOwners.add(group);
    }
}

