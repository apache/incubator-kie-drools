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

import java.util.Date;
import java.util.EventObject;

import org.kie.kogito.usertask.UserTask;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.events.UserTaskEvent;

public class UserTaskEventImpl extends EventObject implements UserTaskEvent {

    private static final long serialVersionUID = 5290126847252514783L;

    public UserTaskEventImpl(UserTaskInstance userTaskInstance, String user) {
        super(userTaskInstance);
        this.userTaskInstance = userTaskInstance;
        this.eventDate = new Date();
        this.eventUser = user;
    }

    private UserTaskInstance userTaskInstance;
    private final Date eventDate;
    private final String eventUser;

    @Override
    public UserTask getUserTask() {
        return userTaskInstance.getUserTask();
    }

    @Override
    public UserTaskInstance getSource() {
        return (UserTaskInstance) super.getSource();
    }

    @Override
    public UserTaskInstance getUserTaskInstance() {
        return userTaskInstance;
    }

    @Override
    public Date getEventDate() {
        return eventDate;
    }

    @Override
    public String getEventUser() {
        return eventUser;
    }

    @Override
    public String toString() {
        return "UserTaskEventImpl [userTaskInstance=" + userTaskInstance + ", eventDate=" + eventDate + ", eventUser=" + eventUser + "]";
    }

}
