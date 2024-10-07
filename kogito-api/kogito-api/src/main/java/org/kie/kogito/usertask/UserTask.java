/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.usertask;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.kie.kogito.usertask.model.DeadlineInfo;
import org.kie.kogito.usertask.model.Reassignment;

/**
 * this is the definition of the user task. Properties are immutable
 */
public interface UserTask {

    UserTaskInstance createInstance();

    UserTaskInstances instances();

    String id();

    String name();

    /**
     * Returns name of the task
     * 
     * @return task name
     */
    String getTaskName();

    /**
     * Returns optional description of the task
     * 
     * @return task description if present
     */
    String getTaskDescription();

    /**
     * Returns optional priority of the task
     * 
     * @return task priority if present
     */
    Integer getTaskPriority();

    /**
     * Returns reference name of the task
     * 
     * @return task reference
     */
    String getReferenceName();

    /**
     * Returns potential users that can work on this task
     * 
     * @return potential users
     */
    Set<String> getPotentialUsers();

    /**
     * Returns potential groups that can work on this task
     * 
     * @return potential groups
     */
    Set<String> getPotentialGroups();

    /**
     * Returns admin users that can administer this task
     * 
     * @return admin users
     */
    Set<String> getAdminUsers();

    /**
     * Returns admin groups that can administer this task
     * 
     * @return admin groups
     */
    Set<String> getAdminGroups();

    /**
     * Returns excluded users that cannot work on this task
     * 
     * @return excluded users
     */
    Set<String> getExcludedUsers();

    Collection<DeadlineInfo<Map<String, Object>>> getNotStartedDeadlines();

    Collection<DeadlineInfo<Map<String, Object>>> getNotCompletedDeadlines();

    Collection<DeadlineInfo<Reassignment>> getNotStartedReassignments();

    Collection<DeadlineInfo<Reassignment>> getNotCompletedReassigments();

    UserTaskAssignmentStrategy getAssignmentStrategy();

}
