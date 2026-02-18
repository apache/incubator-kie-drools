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
package org.kie.kogito.app.jobs.jpa.model;

import java.time.OffsetDateTime;

import org.kie.kogito.app.jobs.jpa.converter.JsonBinaryConverter;

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "job_details",
        indexes = {
                @Index(name = "job_details_fire_time_idx", columnList = "fire_time"),
                @Index(name = "job_details_created_idx", columnList = "created")
        })
public class JobDetailsEntity {

    @Id
    private String id;

    @Column(name = "correlation_id")
    private String correlationId;

    private String status;

    @Column(name = "last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private OffsetDateTime lastUpdate;

    private Integer retries;

    @Column(name = "execution_counter")
    private Integer executionCounter;

    @Column(name = "scheduled_id")
    private String scheduledId;

    private Integer priority;

    @Convert(converter = JsonBinaryConverter.class)
    private ObjectNode recipient;

    @Convert(converter = JsonBinaryConverter.class)
    private ObjectNode trigger;

    @Column(name = "fire_time")
    @Temporal(TemporalType.TIMESTAMP)
    private OffsetDateTime fireTime;

    @Column(name = "execution_timeout")
    private Long executionTimeout;

    @Column(name = "execution_timeout_unit")
    private String executionTimeoutUnit;

    @Temporal(TemporalType.TIMESTAMP)
    private OffsetDateTime created;

    @Column(name = "exception_message")
    private String exceptionMessage;

    @Column(name = "exception_details", columnDefinition = "TEXT")
    private String exceptionDetails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getExecutionCounter() {
        return executionCounter;
    }

    public void setExecutionCounter(Integer executionCounter) {
        this.executionCounter = executionCounter;
    }

    public String getScheduledId() {
        return scheduledId;
    }

    public void setScheduledId(String scheduledId) {
        this.scheduledId = scheduledId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public ObjectNode getRecipient() {
        return recipient;
    }

    public void setRecipient(ObjectNode recipient) {
        this.recipient = recipient;
    }

    public ObjectNode getTrigger() {
        return trigger;
    }

    public void setTrigger(ObjectNode trigger) {
        this.trigger = trigger;
    }

    public OffsetDateTime getFireTime() {
        return fireTime;
    }

    public void setFireTime(OffsetDateTime fireTime) {
        this.fireTime = fireTime;
    }

    public Long getExecutionTimeout() {
        return executionTimeout;
    }

    public void setExecutionTimeout(Long executionTimeout) {
        this.executionTimeout = executionTimeout;
    }

    public String getExecutionTimeoutUnit() {
        return executionTimeoutUnit;
    }

    public void setExecutionTimeoutUnit(String executionTimeoutUnit) {
        this.executionTimeoutUnit = executionTimeoutUnit;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionDetails() {
        return exceptionDetails;
    }

    public void setExceptionDetails(String exceptionDetails) {
        this.exceptionDetails = exceptionDetails;
    }
}
