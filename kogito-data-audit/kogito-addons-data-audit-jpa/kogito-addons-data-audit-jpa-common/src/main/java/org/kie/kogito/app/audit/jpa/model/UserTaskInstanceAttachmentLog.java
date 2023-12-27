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

package org.kie.kogito.app.audit.jpa.model;

import java.net.URL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "Task_Instance_Attachment_Log")
@SequenceGenerator(name = "taskInstanceAttachmentLogIdSeq", sequenceName = "TASK_INSTANCE_ATTACHMENT_LOG_ID_SEQ")
public class UserTaskInstanceAttachmentLog extends AbstractUserTaskInstanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "taskInstanceAttachmentLogIdSeq")
    private Long id;

    @Column(name = "attachment_id")
    private String attachmentId;

    @Column(name = "attachment_name")
    private String attachmentName;

    @Column(name = "attachment_uri")
    private URL attachmentURI;

    @Column(name = "event_type")
    private int eventType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public URL getAttachmentURI() {
        return attachmentURI;
    }

    public void setAttachmentURI(URL attachmentURI) {
        this.attachmentURI = attachmentURI;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

}