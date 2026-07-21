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
package org.kie.kogito.usertask.impl.events;

import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.events.UserTaskStateEvent;
import org.kie.kogito.usertask.lifecycle.UserTaskState;

public class UserTaskStateEventImpl extends UserTaskEventImpl implements UserTaskStateEvent {

    private static final long serialVersionUID = 4556236095420836309L;
    private UserTaskState oldStatus;
    private UserTaskState newStatus;

    public UserTaskStateEventImpl(UserTaskInstance userTaskInstance, UserTaskState oldStatus, UserTaskState newStatus, String user) {
        super(userTaskInstance, user);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public void setOldStatus(UserTaskState oldStatus) {
        this.oldStatus = oldStatus;

    }

    public void setNewStatus(UserTaskState newStatus) {
        this.newStatus = newStatus;

    }

    @Override
    public UserTaskState getNewStatus() {
        return newStatus;
    }

    @Override
    public UserTaskState getOldStatus() {
        return oldStatus;
    }

    @Override
    public String toString() {
        return "UserTaskStateEventImpl [oldStatus=" + oldStatus + ", newStatus=" + newStatus + "]";
    }

}
