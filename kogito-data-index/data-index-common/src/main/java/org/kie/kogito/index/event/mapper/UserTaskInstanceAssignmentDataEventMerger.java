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
package org.kie.kogito.index.event.mapper;

import java.util.HashSet;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.index.model.UserTaskInstance;

@ApplicationScoped
public class UserTaskInstanceAssignmentDataEventMerger implements UserTaskInstanceEventMerger {

    @Override
    public boolean accept(UserTaskInstanceDataEvent<?> event) {
        return event instanceof UserTaskInstanceAssignmentDataEvent;
    }

    @Override
    public void merge(UserTaskInstance userTaskInstance, UserTaskInstanceDataEvent<?> data) {
        UserTaskInstanceAssignmentDataEvent event = (UserTaskInstanceAssignmentDataEvent) data;
        UserTaskInstanceAssignmentEventBody body = event.getData();

        switch (body.getAssignmentType()) {
            case "USER_OWNERS":
                userTaskInstance.setPotentialUsers(new HashSet<>(body.getUsers()));
                break;
            case "USER_GROUPS":
                userTaskInstance.setPotentialGroups(new HashSet<>(body.getUsers()));
                break;
            case "USERS_EXCLUDED":
                userTaskInstance.setExcludedUsers(new HashSet<>(body.getUsers()));
                break;
            case "ADMIN_GROUPS":
                userTaskInstance.setAdminGroups(new HashSet<>(body.getUsers()));
                break;
            case "ADMIN_USERS":
                userTaskInstance.setAdminUsers(new HashSet<>(body.getUsers()));
                break;
        }
    }

}
