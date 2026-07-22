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

import java.util.Map;

import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.events.UserTaskDeadlineEvent;

public class UserTaskDeadlineEventImpl extends UserTaskEventImpl implements UserTaskDeadlineEvent {

    private static final long serialVersionUID = 510l;

    private Map<String, Object> notification;
    private DeadlineType type;

    public UserTaskDeadlineEventImpl(UserTaskInstance userTaskInstance, Map<String, Object> notification, DeadlineType type, String user) {
        super(userTaskInstance, user);
        this.notification = notification;
        this.type = type;
    }

    @Override
    public Map<String, Object> getNotification() {
        return notification;
    }

    @Override
    public DeadlineType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "HumanTaskDeadlineEventImpl [notification=" + notification + ", type=" + type + "]";
    }

}
