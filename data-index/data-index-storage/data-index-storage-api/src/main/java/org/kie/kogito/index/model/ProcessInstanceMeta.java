/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.index.model;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;

import javax.json.bind.annotation.JsonbProperty;

public class ProcessInstanceMeta {

    private String id;
    private String processId;
    private Integer state;
    private String endpoint;
    private Set<String> roles;
    @JsonbProperty("startDate")
    private ZonedDateTime start;
    @JsonbProperty("endDate")
    private ZonedDateTime end;
    @JsonbProperty("rootInstanceId")
    private String rootProcessInstanceId;
    private String rootProcessId;
    @JsonbProperty("parentInstanceId")
    private String parentProcessInstanceId;

    public ProcessInstanceMeta() {
    }

    public ProcessInstanceMeta(String id, String processId) {
        this.id = id;
        this.processId = processId;
    }

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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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

    public Date getStartDate() {
        return start == null ? null : new Date(start.toInstant().toEpochMilli());
    }

    public void setStartDate(Date start) {
        if (start != null) {
            this.start = ZonedDateTime.ofInstant(start.toInstant(), ZoneOffset.UTC);
        }
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public Date getEndDate() {
        return end == null ? null : new Date(end.toInstant().toEpochMilli());
    }

    public void setEndDate(Date end) {
        if (end != null) {
            this.end = ZonedDateTime.ofInstant(end.toInstant(), ZoneOffset.UTC);
        }
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

    @Override
    public String toString() {
        return "ProcessInstanceMeta{" +
                "id='" + id + '\'' +
                ", processId='" + processId + '\'' +
                ", state=" + state +
                ", endpoint='" + endpoint + '\'' +
                ", roles=" + roles +
                ", start=" + start +
                ", end=" + end +
                ", rootProcessInstanceId='" + rootProcessInstanceId + '\'' +
                ", rootProcessId='" + rootProcessId + '\'' +
                ", parentProcessInstanceId='" + parentProcessInstanceId + '\'' +
                '}';
    }
}
