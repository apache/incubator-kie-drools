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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jbpm.services.task.assignment.LoadCalculator;
import org.jbpm.services.task.assignment.UserTaskLoad;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.task.TaskContext;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.assignment.Assignment;
import org.kie.internal.task.api.assignment.AssignmentStrategy;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Assignment strategy that uses a plug-able approach for calculating
 * the load that potential task owners have. It then assigns the task
 * to the user with the lightest load.
 */
public class LoadBalanceAssignmentStrategy implements AssignmentStrategy {
	private static final Logger logger = LoggerFactory.getLogger(LoadBalanceAssignmentStrategy.class);
	private static final String IDENTIFIER = "LoadBalance";
	private LoadCalculator calculator;
	
	public LoadBalanceAssignmentStrategy() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String calculatorClass = System.getProperty("org.jbpm.task.assignment.loadbalance.calculator","org.jbpm.services.task.assignment.impl.TaskCountLoadCalculator");
		this.calculator = (LoadCalculator)Class.forName(calculatorClass).newInstance();
	}
	
	private Function<OrganizationalEntity, User> entityToUser = (oe) -> {
		return (User)oe;
	};
	

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public Assignment apply(Task task, TaskContext taskContext, String excludedUser) {
        UserInfo userInfo = (UserInfo) ((org.jbpm.services.task.commands.TaskContext)taskContext).get(EnvironmentName.TASK_USER_INFO);
		List<OrganizationalEntity> excluded = (getExcludedEntities(task, userInfo));

        // Get the the users from the task's the potential owners, making sure that excluded users are not included
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners().stream()
                .filter(oe -> oe instanceof User && !excluded.contains(oe) && !oe.getId().equals(excludedUser))
                .collect(Collectors.toList());

        // Get the users belonging to groups that are potential owners
        task.getPeopleAssignments().getPotentialOwners().stream().filter(oe -> oe instanceof Group)
                .forEach(oe -> {
                    Iterator<OrganizationalEntity> groupUsers = userInfo.getMembersForGroup((Group)oe);
                    if (groupUsers != null) {
                        groupUsers.forEachRemaining(user -> {
                            if (user != null && !excluded.contains(user) && !potentialOwners.contains(user) && !user.getId().equals(excludedUser)) {
                                potentialOwners.add(user);
                            }
                        });
                    }
                });
        logger.debug("Asking the load calculator [{}] for task loads for the users {}",calculator.getIdentifier(),potentialOwners);
        List<User> users = potentialOwners.stream().map(entityToUser).collect(Collectors.toList());
        Collection<UserTaskLoad> loads = calculator.getUserTaskLoads(users, taskContext);
        UserTaskLoad lightestLoad = loads.stream().min(UserTaskLoad::compareTo).orElse(null);
		return lightestLoad != null ? new Assignment(lightestLoad.getUser().getId()):null;
	}

	private static List<OrganizationalEntity> getExcludedEntities(Task task, UserInfo userInfo) {
		List<OrganizationalEntity> excluded = ((InternalPeopleAssignments) task.getPeopleAssignments()).getExcludedOwners();

		List<OrganizationalEntity> excludedUsers = new ArrayList<>();
		for (OrganizationalEntity entity : excluded) {
			if (entity instanceof Group) {
				userInfo.getMembersForGroup((Group) entity).forEachRemaining(excludedUsers::add);
			}
		}
		excluded.addAll(excludedUsers);

		return excluded;
	}

}
