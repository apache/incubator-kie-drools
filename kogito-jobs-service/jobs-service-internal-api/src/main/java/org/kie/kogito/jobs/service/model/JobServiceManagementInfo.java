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
package org.kie.kogito.jobs.service.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.StringJoiner;

public class JobServiceManagementInfo {

    private String id;
    private OffsetDateTime lastHeartbeat;
    private String token;

    public JobServiceManagementInfo(String id, String token, OffsetDateTime heartbeat) {
        this.id = id;
        this.token = token;
        this.lastHeartbeat = heartbeat;
    }

    public JobServiceManagementInfo() {
    }

    public String getId() {
        return id;
    }

    public OffsetDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public String getToken() {
        return token;
    }

    public void setLastHeartbeat(OffsetDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JobServiceManagementInfo.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("lastHeartbeat=" + lastHeartbeat)
                .add("token=" + token)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobServiceManagementInfo that = (JobServiceManagementInfo) o;
        return Objects.equals(id, that.id) && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token);
    }
}
