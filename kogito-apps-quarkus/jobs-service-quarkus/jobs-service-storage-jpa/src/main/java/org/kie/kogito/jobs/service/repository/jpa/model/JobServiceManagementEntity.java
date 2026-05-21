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

package org.kie.kogito.jobs.service.repository.jpa.model;

import java.time.OffsetDateTime;

import jakarta.persistence.*;

@Entity
@NamedQuery(name = "JobServiceManagementEntity.GetServiceByIdAndToken",
        query = "select service " +
                "from JobServiceManagementEntity service " +
                "where service.id = :id and service.token = :token")
@Table(name = "job_service_management")
public class JobServiceManagementEntity {

    @Id
    private String id;

    @Column(name = "last_heartbeat")
    @Temporal(TemporalType.TIMESTAMP)
    private OffsetDateTime lastHeartBeat;

    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OffsetDateTime getLastHeartBeat() {
        return lastHeartBeat;
    }

    public void setLastHeartBeat(OffsetDateTime lastHeartBeat) {
        this.lastHeartBeat = lastHeartBeat;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
