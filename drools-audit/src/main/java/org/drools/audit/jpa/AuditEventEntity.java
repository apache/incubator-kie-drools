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
package org.drools.audit.jpa;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * JPA entity mapping for persisting audit events to a relational database.
 * Designed for compliance workloads with indexed queries by session, rule,
 * time range, and event type.
 */
@Entity
@Table(name = "DROOLS_AUDIT_EVENT", indexes = {
        @Index(name = "IDX_AUDIT_SESSION", columnList = "sessionId"),
        @Index(name = "IDX_AUDIT_RULE", columnList = "ruleName"),
        @Index(name = "IDX_AUDIT_TIMESTAMP", columnList = "eventTimestamp"),
        @Index(name = "IDX_AUDIT_TYPE", columnList = "eventType")
})
public class AuditEventEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 50)
    private String eventType;

    @Column(nullable = false, length = 20)
    private String eventCategory;

    @Column(nullable = false)
    private Instant eventTimestamp;

    @Column(nullable = false, length = 255)
    private String sessionId;

    @Column(nullable = false)
    private long sequenceNumber;

    @Column(length = 512)
    private String ruleName;

    @Column(length = 512)
    private String packageName;

    @Column(length = 256)
    private String objectClassName;

    private Long factHandleId;

    private int salience;

    @Lob
    private String eventPayload;

    public AuditEventEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Instant eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getObjectClassName() {
        return objectClassName;
    }

    public void setObjectClassName(String objectClassName) {
        this.objectClassName = objectClassName;
    }

    public Long getFactHandleId() {
        return factHandleId;
    }

    public void setFactHandleId(Long factHandleId) {
        this.factHandleId = factHandleId;
    }

    public int getSalience() {
        return salience;
    }

    public void setSalience(int salience) {
        this.salience = salience;
    }

    public String getEventPayload() {
        return eventPayload;
    }

    public void setEventPayload(String eventPayload) {
        this.eventPayload = eventPayload;
    }
}
