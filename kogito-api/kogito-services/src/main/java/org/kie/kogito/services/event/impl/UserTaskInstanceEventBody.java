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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserTaskInstanceEventBody {

    public static final String UT_ID_META_DATA = "kogito.usertaskinstance.id";
    public static final String UT_STATE_META_DATA = "kogito.usertaskinstance.state";

    private String id;
    private String taskName;
    private String taskDescription;
    private String taskPriority;
    private String referenceName;
    private Date startDate;
    private Date completeDate;

    private String state;

    private String actualOwner;
    private Set<String> potentialUsers;
    private Set<String> potentialGroups;
    private Set<String> excludedUsers;
    private Set<String> adminUsers;
    private Set<String> adminGroups;

    private Map<String, Object> inputs;
    private Map<String, Object> outputs;

    private String processInstanceId;
    private String rootProcessInstanceId;
    private String processId;
    private String rootProcessId;

    private UserTaskInstanceEventBody() {

    }

    public String getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }
    
    public String getTaskPriority() {
        return taskPriority;
    }
    
    public String getReferenceName() {
        return referenceName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public String getState() {
        return state;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public Set<String> getPotentialUsers() {
        return potentialUsers;
    }

    public Set<String> getPotentialGroups() {
        return potentialGroups;
    }

    public Set<String> getExcludedUsers() {
        return excludedUsers;
    }

    public Set<String> getAdminUsers() {
        return adminUsers;
    }

    public Set<String> getAdminGroups() {
        return adminGroups;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public String getRootProcessId() {
        return rootProcessId;
    }
    
    public Map<String, String> metaData() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(UT_ID_META_DATA, id);
        metadata.put(ProcessInstanceEventBody.ID_META_DATA, processInstanceId);
        metadata.put(ProcessInstanceEventBody.ROOT_ID_META_DATA, rootProcessInstanceId);
        metadata.put(ProcessInstanceEventBody.PROCESS_ID_META_DATA, processId);
        metadata.put(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA, rootProcessId);
        metadata.put(UT_STATE_META_DATA, state);
        return metadata;
    }

    @Override
    public String toString() {
        return "UserTaskInstanceEventBody [id=" + id + ", taskName=" + taskName + ", taskDescription=" + taskDescription + ", startDate=" + startDate + ", completeDate=" + completeDate + ", state=" + state +
               ", actualOwner=" + actualOwner + "]";
    }

    public static Builder create() {
        return new Builder(new UserTaskInstanceEventBody());
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
        UserTaskInstanceEventBody other = (UserTaskInstanceEventBody) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    static class Builder {

        private UserTaskInstanceEventBody instance;

        private Builder(UserTaskInstanceEventBody instance) {
            this.instance = instance;
        }

        public Builder id(String id) {
            instance.id = id;
            return this;
        }

        public Builder taskName(String taskName) {
            instance.taskName = taskName;
            return this;
        }

        public Builder taskDescription(String taskDescription) {
            instance.taskDescription = taskDescription;
            return this;
        }
        
        public Builder taskPriority(String taskPriority) {
            instance.taskPriority = taskPriority;
            return this;
        }
        
        public Builder referenceName(String referenceName) {
            instance.referenceName = referenceName;
            return this;
        }

        public Builder state(String state) {
            instance.state = state;
            return this;
        }

        public Builder actualOwner(String actualOwner) {
            instance.actualOwner = actualOwner;
            return this;
        }

        public UserTaskInstanceEventBody build() {
            return instance;
        }

        public Builder startDate(Date startDate) {
            instance.startDate = startDate;
            return this;
        }

        public Builder completeDate(Date completeDate) {
            instance.completeDate = completeDate;
            return this;
        }

        public Builder potentialUsers(Set<String> potentialUsers) {
            instance.potentialUsers = potentialUsers;
            return this;
        }

        public Builder potentialGroups(Set<String> potentialGroups) {
            instance.potentialGroups = potentialGroups;
            return this;
        }

        public Builder excludedUsers(Set<String> excludedUsers) {
            instance.excludedUsers = excludedUsers;
            return this;
        }

        public Builder adminUsers(Set<String> adminUsers) {
            instance.adminUsers = adminUsers;
            return this;
        }

        public Builder adminGroups(Set<String> adminGroups) {
            instance.adminGroups = adminGroups;
            return this;
        }

        public Builder inputs(Map<String, Object> inputs) {
            instance.inputs = inputs;
            return this;
        }

        public Builder outputs(Map<String, Object> outputs) {
            instance.outputs = outputs;
            return this;
        }
        
        public Builder processInstanceId(String processInstanceId) {
            instance.processInstanceId = processInstanceId;
            return this;
        }
        
        public Builder rootProcessInstanceId(String rootProcessInstanceId) {
            instance.rootProcessInstanceId = rootProcessInstanceId;
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
    }
}
