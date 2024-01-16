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

import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "Task_Instance_Deadline_Log",
        indexes = {
                @Index(name = "ix_utdl_utid", columnList = "user_task_instance_id"),
                @Index(name = "ix_utdl_pid", columnList = "process_instance_id"),
                @Index(name = "ix_utdl_key", columnList = "business_key"),
                @Index(name = "ix_utdl_event_date", columnList = "event_date")
        })
@SequenceGenerator(name = "taskInstanceDeadlineLogIdSeq", sequenceName = "TASK_INSTANCE_DEADLINE_LOG_ID_SEQ")
public class UserTaskInstanceDeadlineLog extends AbstractUserTaskInstanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "taskInstanceDeadlineLogIdSeq")
    private Long id;

    @ElementCollection
    @JoinTable(name = "Task_Instance_Deadline_Notification_Log", joinColumns = @JoinColumn(name = "task_instance_deadline_log_id"), foreignKey = @ForeignKey(name = "fk_task_instance_deadline_tid"))
    @MapKeyColumn(name = "property_name")
    @Column(name = "property_value")
    private Map<String, String> notification;

    @Column(name = "event_type")
    private String eventType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, String> getNotification() {
        return notification;
    }

    public void setNotification(Map<String, String> notification) {
        this.notification = notification;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

}
