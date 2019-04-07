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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

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

public class RoundRobinAssignmentStrategy implements AssignmentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(RoundRobinAssignmentStrategy.class);
    private static final String IDENTIFIER = "RoundRobin";

    private Map<String, CircularQueue<OrganizationalEntity>> circularQueueMap = new ConcurrentHashMap<>();

    private class CircularQueue<T> extends LinkedBlockingQueue<T> {

        @Override
        public synchronized T take() {
            T headValue = null;
            try {
                headValue = super.take();
                super.offer(headValue);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted during the 'take' from a circular queue in the " +
                        "RoundRobinAssignmentStrategy", e);
            }
            return headValue;
        }
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Assignment apply(Task task, TaskContext taskContext, String excludedUser) {
        UserInfo userInfo = (UserInfo) ((org.jbpm.services.task.commands.TaskContext) taskContext).get(EnvironmentName.TASK_USER_INFO);
        List<OrganizationalEntity> excluded = getExcludedEntities(task, userInfo);

        // Get the the users from the task's the potential owners
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners().stream()
                .filter(oe -> oe instanceof User && !excluded.contains(oe))
                .collect(Collectors.toList());

        // Get the users belonging to groups that are potential owners
        task.getPeopleAssignments().getPotentialOwners().stream().filter(oe -> oe instanceof Group)
                .forEach(oe -> {
                    Iterator<OrganizationalEntity> groupUsers = userInfo.getMembersForGroup((Group) oe);
                    if (groupUsers != null) {
                        groupUsers.forEachRemaining(user -> {
                            if (user != null && !excluded.contains(user) && !potentialOwners.contains(user)) {
                                potentialOwners.add(user);
                            }
                        });
                    }
                });

        if (excludedUser != null) {
            logger.debug("Removing excluded user {} from the list of eligible users", excludedUser);
            potentialOwners.removeIf(entity -> entity.getId().equals(excludedUser));
        }

        String queueName = getQueueName(task);
        CircularQueue<OrganizationalEntity> mappedQueue = synchronizedQueue(queueName, potentialOwners);
        OrganizationalEntity owner = mappedQueue.take();
        return new Assignment(owner.getId());
    }

    /**
     * Synchronizes the {@code OrganizationalEntity} objects contained in the {@code CircularQueue} and the list of
     * potential owners
     * @param queueName The name of the queue to be synchronized.
     * @param potentialOwners This list of potential owners of the task
     * @return The CircularQueue that contains all potential owners
     */
    private synchronized CircularQueue<OrganizationalEntity> synchronizedQueue(String queueName,
                                                                               List<OrganizationalEntity> potentialOwners) {
        CircularQueue<OrganizationalEntity> existingQueue = (queueName == null || queueName.trim().length() == 0) ? null : circularQueueMap.get(queueName);
        // If the queue does not exist then a new CircularQueue should be created
        final CircularQueue<OrganizationalEntity> workingQueue = existingQueue != null ? existingQueue : new CircularQueue();
        potentialOwners.forEach(po -> {
            if (!queueContainsUser(workingQueue, po)) {
                workingQueue.add(po);
            }
        });
        workingQueue.removeIf(oe -> !potentialOwners.contains(oe));
        circularQueueMap.put(queueName, workingQueue);
        return workingQueue;
    }

    protected boolean queueContainsUser(CircularQueue<OrganizationalEntity> queue, OrganizationalEntity oe) {
        return queue.contains(oe);
    }

    /**
     * Generates a queue name that is based on data retrieved from the task. The form of the generated queue name is:
     * Process ID + "_" + Deployment ID + " " + Task Name
     * @param task Source of the data used to generate the queue name
     * @return The generated queue name
     */
    protected String getQueueName(Task task) {
        return task.getTaskData().getProcessId() + "_" + task.getTaskData().getDeploymentId() + "_" + task.getName();
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
