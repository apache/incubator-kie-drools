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
package org.kie.kogito.services.event.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcessInstanceEventBody {

    public static final String ID_META_DATA = "kogito.processinstance.id";
    public static final String PARENT_ID_META_DATA = "kogito.processinstance.parentInstanceId";
    public static final String ROOT_ID_META_DATA = "kogito.processinstance.rootInstanceId";
    public static final String PROCESS_ID_META_DATA = "kogito.processinstance.processId";
    public static final String ROOT_PROCESS_ID_META_DATA = "kogito.processinstance.rootProcessId";
    public static final String STATE_META_DATA = "kogito.processinstance.state";

    private String id;
    private String parentInstanceId;
    private String rootInstanceId;
    private String processId;
    private String rootProcessId;
    private String processName;
    private Date startDate;
    private Date endDate;

    private Integer state;

    private String businessKey;

    private Set<NodeInstanceEventBody> nodeInstances = new LinkedHashSet<>();

    private Map<String, Object> variables;

    private ProcessErrorEventBody error;

    private List<String> roles;

    private Set<MilestoneEventBody> milestones = Collections.emptySet();

    private ProcessInstanceEventBody() {
    }

    public String getId() {
        return id;
    }

    public String getParentInstanceId() {
        return parentInstanceId;
    }

    public String getRootInstanceId() {
        return rootInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public String getProcessName() {
        return processName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Integer getState() {
        return state;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public ProcessErrorEventBody getError() {
        return error;
    }

    public Set<NodeInstanceEventBody> getNodeInstances() {
        return nodeInstances;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public List<String> getRoles() {
        return roles;
    }

    public Set<MilestoneEventBody> getMilestones() {
        return milestones;
    }

    public Builder update() {
        return new Builder(this);
    }

    public Map<String, String> metaData() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(ID_META_DATA, id);
        metadata.put(PARENT_ID_META_DATA, parentInstanceId);
        metadata.put(ROOT_ID_META_DATA, rootInstanceId);
        metadata.put(PROCESS_ID_META_DATA, processId);
        metadata.put(ROOT_PROCESS_ID_META_DATA, rootProcessId);
        metadata.put(STATE_META_DATA, String.valueOf(state));
        return metadata;
    }

    @Override
    public String toString() {
        return "ProcessInstanceEventBody [id=" + id + ", parentInstanceId=" + parentInstanceId + ", rootInstanceId=" + rootInstanceId + ", processId=" + processId + ", rootProcessId=" + rootProcessId
                + ", processName=" +
                processName + ", startDate=" + startDate + ", endDate=" + endDate + ", state=" + state + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessInstanceEventBody other = (ProcessInstanceEventBody) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public static Builder create() {
        return new Builder(new ProcessInstanceEventBody());
    }

    public static class Builder {

        private ProcessInstanceEventBody instance;

        public Builder(ProcessInstanceEventBody instance) {
            this.instance = instance;
        }

        public Builder id(String id) {
            instance.id = id;
            return this;
        }

        public Builder parentInstanceId(String parentInstanceId) {
            instance.parentInstanceId = parentInstanceId;
            return this;
        }

        public Builder rootInstanceId(String rootInstanceId) {
            instance.rootInstanceId = rootInstanceId;
            return this;
        }

        public Builder processId(String processId) {
            instance.processId = processId;
            return this;
        }

        public Builder rootProcessId(String rootProcessId) {
            instance.rootProcessId = rootProcessId;
            return this;
        }

        public Builder processName(String processName) {
            instance.processName = processName;
            return this;
        }

        public Builder startDate(Date date) {
            instance.startDate = date;
            return this;
        }

        public Builder endDate(Date date) {
            instance.endDate = date;
            return this;
        }

        public Builder state(Integer state) {
            instance.state = state;
            return this;
        }

        public Builder businessKey(String businessKey) {
            instance.businessKey = businessKey;
            return this;
        }

        public Builder nodeInstance(NodeInstanceEventBody nodeInstance) {
            instance.nodeInstances.add(nodeInstance);
            return this;
        }

        public Builder variables(Map<String, Object> variables) {
            instance.variables = variables;
            return this;
        }

        public Builder error(ProcessErrorEventBody error) {
            instance.error = error;
            return this;
        }

        public Builder roles(String... roles) {
            instance.roles = Arrays.asList(roles);
            return this;
        }

        public Builder milestones(Set<MilestoneEventBody> milestones) {
            instance.milestones = milestones;
            return this;
        }

        public ProcessInstanceEventBody build() {
            return instance;
        }
    }
}
