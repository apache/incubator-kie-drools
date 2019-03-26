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
package org.jbpm.services.task.assignment.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jbpm.services.task.assignment.UserTaskLoad;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.task.TaskContext;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskCountLoadCalculator extends AbstractLoadCalculator {
	private static final String IDENTIFIER = "TaskCountLoadCalculator";
	private static final Logger logger = LoggerFactory.getLogger(TaskCountLoadCalculator.class);
    private static final String SINGLE_USER_QUERY = 
            "select new Integer(count(t)) "
            + "from TaskImpl t "
            + "where t.taskData.actualOwner.id = :owner and t.taskData.status in ('Reserved', 'InProgress', 'Suspended') ";
    private static final String MULTI_USER_QUERY = 
            "select new org.jbpm.services.task.assignment.impl.AssignmentImpl(t.taskData.actualOwner.id, count(t)) "
            + "from TaskImpl t "
            + "where t.taskData.actualOwner.id in (:owners) and t.taskData.status in ('Reserved', 'InProgress', 'Suspended') "
            + "group by t.taskData.actualOwner "
            + "order by count(t) asc, t.taskData.actualOwner.id asc";
    
    
    public TaskCountLoadCalculator() {
    	super(IDENTIFIER);
    }

    Function<AssignmentImpl, String> assignKey = (assignment) -> { return assignment.getUser(); };
    
    @Override
	public UserTaskLoad getUserTaskLoad(User user, TaskContext context) {
		UserTaskLoad load = new UserTaskLoad(getIdentifier(), user);
        TaskPersistenceContext persistenceContext = ((org.jbpm.services.task.commands.TaskContext)context).getPersistenceContext();
        Map<String, Object> params = new HashMap<>();
        params.put("owner", user.getId());
        
        logger.debug("DB query to be used for finding assignments :: '{}'", getSingleUserQuery());
        List<Integer> assignmentCounts = persistenceContext.queryStringWithParametersInTransaction(getSingleUserQuery(), params, ClassUtil.<List<Integer>>castClass(List.class));
        if (assignmentCounts != null && !assignmentCounts.isEmpty()) {
        	load.setCalculatedLoad(new Double(assignmentCounts.get(0)));
        } else {
        	load.setCalculatedLoad(new Double(0));
        }
		return load;
	}
    

	@Override
	public Collection<UserTaskLoad> getUserTaskLoads(List<User> users, TaskContext context) {
		Collection<UserTaskLoad> userTaskLoads = new ArrayList<>();
		List<String> userIds = users.stream().map(user -> {return user.getId();}).collect(Collectors.toList());
		
        TaskPersistenceContext persistenceContext = ((org.jbpm.services.task.commands.TaskContext)context).getPersistenceContext();
        Map<String, Object> params = new HashMap<>();
        params.put("owners", userIds);
        
        logger.debug("DB query to be used for finding assignments :: '{}'", getMultiUserQuery());
        List<AssignmentImpl> assignments = persistenceContext.queryStringWithParametersInTransaction(getMultiUserQuery(), params, ClassUtil.<List<AssignmentImpl>>castClass(List.class));
        Map<String,AssignmentImpl> assignmentMap = assignments.stream().collect(Collectors.toMap(assignKey,(assign)->assign));
        if (assignments != null && !assignments.isEmpty()) {
        	users.forEach(usr -> {
        		String uid = usr.getId();
        		if (assignmentMap.containsKey(uid)) {
        			Long loadValue = assignmentMap.get(uid).getCurrentlyAssigned();
        			userTaskLoads.add(new UserTaskLoad(getIdentifier(), usr, new Double(loadValue != null ? loadValue:0)));
        		} else {
        			userTaskLoads.add(new UserTaskLoad(getIdentifier(), usr, new Double(0)));
        		}
        	});
        } else {
        	users.forEach(u -> {
        		userTaskLoads.add(new UserTaskLoad(getIdentifier(),u,new Double(0)));
        	});
        }
		return userTaskLoads;
	}

	private String getSingleUserQuery() {
		return SINGLE_USER_QUERY;
	}
	
	private String getMultiUserQuery() {
		return MULTI_USER_QUERY;
	}

}
