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

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public class UserTaskInstanceAttachmentTO {

    private String eventId;

    private OffsetDateTime eventDate;

    private String eventUser;

    private String userTaskDefinitionId;

    private String userTaskInstanceId;

    private String processInstanceId;

    private String businessKey;

    private String attachmentId;

    private String attachmentName;

    private URI attachmentURI;

    private int eventType;

    public UserTaskInstanceAttachmentTO(String eventId, Date eventDate, String eventUser, String userTaskDefinitionId, String userTaskInstanceId, String processInstanceId, String businessKey,
            String attachmentId, String attachmentName, String attachmentURI, Integer eventType) {
        this.eventId = eventId;
        this.eventDate = OffsetDateTime.ofInstant(eventDate.toInstant(), ZoneId.of("UTC"));
        this.eventUser = eventUser;
        this.userTaskDefinitionId = userTaskDefinitionId;
        this.userTaskInstanceId = userTaskInstanceId;
        this.processInstanceId = processInstanceId;
        this.businessKey = businessKey;
        this.attachmentId = attachmentId;
        this.attachmentName = attachmentName;
        if (attachmentURI != null) {
            this.attachmentURI = URI.create(new String(attachmentURI));
        }
        this.eventType = eventType;
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

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public URI getAttachmentURI() {
        return attachmentURI;
    }

    public void setAttachmentURI(URI attachmentURI) {
        this.attachmentURI = attachmentURI;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getEventUser() {
        return eventUser;
    }

    public void setEventUser(String eventUser) {
        this.eventUser = eventUser;
    }
}
