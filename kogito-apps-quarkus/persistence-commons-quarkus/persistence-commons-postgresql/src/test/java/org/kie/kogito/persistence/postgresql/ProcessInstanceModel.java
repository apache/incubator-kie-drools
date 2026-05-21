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
package org.kie.kogito.persistence.postgresql;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.singleton;

public class ProcessInstanceModel {

    private String id;
    private String processId;
    private String processName;
    private Integer state;
    private String businessKey;
    private String endpoint;
    private Set<String> roles;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private String rootProcessInstanceId;
    private String rootProcessId;
    private String parentProcessInstanceId;
    private ZonedDateTime lastUpdate;
    private Set<String> addons;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public void setRootProcessInstanceId(String rootProcessInstanceId) {
        this.rootProcessInstanceId = rootProcessInstanceId;
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public void setRootProcessId(String rootProcessId) {
        this.rootProcessId = rootProcessId;
    }

    public String getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(String parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Set<String> getAddons() {
        return addons;
    }

    public void setAddons(Set<String> addons) {
        this.addons = addons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProcessInstanceModel that = (ProcessInstanceModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProcessInstanceEntity{" +
                "id='" + id + '\'' +
                ", processId='" + processId + '\'' +
                ", processName='" + processName + '\'' +
                ", state=" + state +
                ", businessKey='" + businessKey + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", roles=" + roles +
                ", start=" + start +
                ", end=" + end +
                ", rootProcessInstanceId='" + rootProcessInstanceId + '\'' +
                ", rootProcessId='" + rootProcessId + '\'' +
                ", parentProcessInstanceId='" + parentProcessInstanceId + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", addons=" + addons +
                '}';
    }

    public static ProcessInstanceModel newModel() {
        return newModel(UUID.randomUUID().toString());
    }

    public static ProcessInstanceModel newModel(String id) {
        ProcessInstanceModel pi = new ProcessInstanceModel();
        pi.setId(id);
        pi.setProcessId("testProcessId");
        pi.setRoles(singleton("testRoles"));
        pi.setEndpoint("testEndpoint");
        pi.setState(2);
        pi.setStart(ZonedDateTime.now());
        pi.setEnd(ZonedDateTime.now());
        pi.setRootProcessId("testRootProcessId");
        pi.setRootProcessInstanceId("testRootProcessInstanceId");
        pi.setParentProcessInstanceId("testParentProcessInstanceId");
        pi.setProcessName("testProcessName");
        pi.setAddons(singleton("testAddons"));
        pi.setLastUpdate(ZonedDateTime.now());
        pi.setBusinessKey("testBusinessKey");
        return pi;
    }
}
