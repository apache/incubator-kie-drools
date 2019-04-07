/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.assignment.impl.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jbpm.services.task.assignment.impl.AssignmentImpl;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.task.TaskContext;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.assignment.Assignment;
import org.kie.internal.task.api.assignment.AssignmentStrategy;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Assignment strategy that assigns tasks to least occupied user from potential owners.
 * Least occupied is based on number of active tasks given user is assigned to - user is actual owner of the task.
 *
 * This strategy requires <code>GroupResolver</code> to be able to transform group potential owners into users list only.
 */
public class PotentialOwnerBusynessAssignmentStrategy implements AssignmentStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(PotentialOwnerBusynessAssignmentStrategy.class);
    
    public static final String IDENTIFIER = "PotentialOwnerBusyness";

    private static final String DEFAULT_QUERY = 
            "select new org.jbpm.services.task.assignment.impl.AssignmentImpl(t.taskData.actualOwner.id, count(t)) "
            + "from TaskImpl t "
            + "where t.taskData.actualOwner.id in (:owners) and t.taskData.status in ('Reserved', 'InProgress', 'Suspended') "
            + "group by t.taskData.actualOwner "
            + "order by count(t) asc, t.taskData.actualOwner.id asc";
    
    public PotentialOwnerBusynessAssignmentStrategy() {
    }

    @Override
    public Assignment apply(Task task, TaskContext context, String excludedUser) {
        if (task.getPeopleAssignments().getPotentialOwners().isEmpty()) {
            logger.debug("No potential owners in the task {} can't auto assign", task);
            return null;
        }
        List<OrganizationalEntity> potentialOwners = new ArrayList<>(task.getPeopleAssignments().getPotentialOwners());
        Set<String> resolvedUsers = new TreeSet<>(Collections.reverseOrder());
        List<OrganizationalEntity> excludedOwners = ((InternalPeopleAssignments)task.getPeopleAssignments()).getExcludedOwners();
        
        potentialOwners.stream().filter(po -> po instanceof User && !excludedOwners.contains(po)).forEach(po -> resolvedUsers.add(po.getId()));
       
        UserInfo userInfo = (UserInfo) ((org.jbpm.services.task.commands.TaskContext)context).get(EnvironmentName.TASK_USER_INFO);
        if (userInfo != null) {
            logger.debug("Groups going to be resolved by {}", userInfo);
            potentialOwners.stream().filter(po -> po instanceof Group && !excludedOwners.contains(po)).forEach(po -> {
                Iterator<OrganizationalEntity> usersOfGroup = userInfo.getMembersForGroup((Group)po);
                if (usersOfGroup != null) {
                    
                    while(usersOfGroup.hasNext()) {
                        OrganizationalEntity entity = usersOfGroup.next();
                        if (!excludedOwners.contains(entity)) {
                            resolvedUsers.add(entity.getId());
                        }
                    }
                }
            });            
        }
        logger.debug("Resolved users eligible for task {} assignments are {}", task, resolvedUsers);
        
        if (excludedUser != null) {
            logger.debug("Removing excluded user {} from the list of eligible users", excludedUser);
            resolvedUsers.remove(excludedUser);
        }
        
        TaskPersistenceContext persistenceContext = ((org.jbpm.services.task.commands.TaskContext)context).getPersistenceContext();
        Map<String, Object> params = new HashMap<>();
        params.put("owners", resolvedUsers);
        
        logger.debug("DB query to be used for finding assignments :: '{}'", getQuery());
        List<Assignment> assignments = persistenceContext.queryStringWithParametersInTransaction(getQuery(), params, ClassUtil.<List<Assignment>>castClass(List.class));
       
        if (assignments.size() < resolvedUsers.size()) {
            logger.debug("Not all eligible users found in db, adding missing bits (eligible {}, found in db {})", resolvedUsers, assignments);
            // in case not all users have already assigned tasks added them to the list so can get the tasks
            resolvedUsers.forEach(user -> {
                Assignment assignment = new AssignmentImpl(user);
                if (!assignments.contains(assignment)) {
                    // always add missing users to the top of the list so they get assigned first
                    assignments.add(0, assignment);
                }
            });
        }
        
        if (assignments.isEmpty()) {
            logger.debug("No assignments found for task {}", task);
            return null;
        }
        logger.debug("Following assignments {} were found for task {}", assignments, task);
        // select first from the top of the list as it has the least assigned tasks
        Assignment selected = assignments.get(0);
        logger.debug("Retruning first of found assignments {}", selected);
        
        return new Assignment(selected.getUser());
    }
    
    protected String getQuery() {
        return DEFAULT_QUERY;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }
    
    @Override
    public String toString() {
        return "AssignmentStrategy:: " + IDENTIFIER;
    }

}
