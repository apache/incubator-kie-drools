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

package org.kie.kogito.app.audit.graphql.type;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserTaskInstanceDeadlineTO {

    private String eventId;

    private OffsetDateTime eventDate;

    private String userTaskDefinitionId;

    private String userTaskInstanceId;

    private String processInstanceId;

    private String businessKey;

    private Map<String, Object> notification;

    private String eventType;

    public UserTaskInstanceDeadlineTO(String eventId, Date eventDate, String userTaskDefinitionId, String userTaskInstanceId, String processInstanceId, String businessKey, String eventType) {
        this();
        this.eventId = eventId;
        this.eventDate = OffsetDateTime.ofInstant(eventDate.toInstant(), ZoneId.of("UTC"));
        this.userTaskDefinitionId = userTaskDefinitionId;
        this.userTaskInstanceId = userTaskInstanceId;
        this.processInstanceId = processInstanceId;
        this.businessKey = businessKey;
        this.eventType = eventType;
    }

    public UserTaskInstanceDeadlineTO() {
        this.notification = new HashMap<>();
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public OffsetDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(OffsetDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getUserTaskDefinitionId() {
        return userTaskDefinitionId;
    }

    public void setUserTaskDefinitionId(String userTaskDefinitionId) {
        this.userTaskDefinitionId = userTaskDefinitionId;
    }

    public String getUserTaskInstanceId() {
        return userTaskInstanceId;
    }

    public void setUserTaskInstanceId(String userTaskInstanceId) {
        this.userTaskInstanceId = userTaskInstanceId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Map<String, Object> getNotification() {
        return notification;
    }

    public void setNotification(Map<String, Object> notification) {
        this.notification = notification;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void addNotification(String key, String value) {
        notification.put(key, value);
    }

}
