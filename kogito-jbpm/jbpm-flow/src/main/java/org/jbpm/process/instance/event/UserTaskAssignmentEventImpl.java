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
package org.jbpm.process.instance.event;

import java.util.Set;

import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.kie.api.event.usertask.UserTaskAssignmentEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;

public class UserTaskAssignmentEventImpl extends UserTaskEventImpl implements UserTaskAssignmentEvent {

    private static final long serialVersionUID = 1964525156416025043L;
    private String assignmentType;
    private String[] oldUsersId;
    private String[] newUsersId;

    public UserTaskAssignmentEventImpl(ProcessInstance instance, HumanTaskNodeInstance nodeInstance, KieRuntime kruntime, String identity) {
        super(instance, nodeInstance, kruntime, identity);
    }

    @Override
    public String getUserTaskId() {
        return getHumanTaskNodeInstance().getWorkItemId();
    }

    public void setAssignmentType(String name) {
        this.assignmentType = name;

    }

    public void setOldUsersId(Set<String> oldUsersId) {
        this.oldUsersId = oldUsersId.toArray(String[]::new);

    }

    public void setNewUsersId(Set<String> newUsersId) {
        this.newUsersId = newUsersId.toArray(String[]::new);
    }

    @Override
    public String getAssignmentType() {
        return assignmentType;
    }

    @Override
    public String[] getOldUsersId() {
        return oldUsersId;
    }

    @Override
    public String[] getNewUsersId() {
        return newUsersId;
    }
}
