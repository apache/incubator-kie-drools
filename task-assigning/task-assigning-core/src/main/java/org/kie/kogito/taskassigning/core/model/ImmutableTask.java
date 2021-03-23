/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.core.model;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ImmutableTask extends Task {

    public ImmutableTask() {
        // required for marshaling and FieldAccessingSolutionCloner purposes.
    }

    public ImmutableTask(String id, String name, String state, String description, String referenceName, String priority,
            String processInstanceId, String processId, String rootProcessInstanceId, String rootProcessId,
            ZonedDateTime started, ZonedDateTime completed, ZonedDateTime lastUpdate,
            String endpoint) {
        super.setId(id);
        super.setName(name);
        super.setState(state);
        super.setDescription(description);
        super.setReferenceName(referenceName);
        super.setPriority(priority);
        super.setProcessInstanceId(processInstanceId);
        super.setProcessId(processId);
        super.setRootProcessInstanceId(rootProcessInstanceId);
        super.setRootProcessId(rootProcessId);
        super.setStarted(started);
        super.setCompleted(completed);
        super.setLastUpdate(lastUpdate);
        super.setEndpoint(endpoint);
        super.setPotentialUsers(Collections.emptySet());
        super.setPotentialGroups(Collections.emptySet());
        super.setAdminUsers(Collections.emptySet());
        super.setAdminGroups(Collections.emptySet());
        super.setExcludedUsers(Collections.emptySet());
        super.setInputData(Collections.emptyMap());
        super.setAttributes(Collections.emptyMap());
    }

    @Override
    public void setId(String id) {
        throwImmutableException();
    }

    @Override
    public void setName(String name) {
        throwImmutableException();
    }

    @Override
    public void setState(String state) {
        throwImmutableException();
    }

    @Override
    public void setDescription(String description) {
        throwImmutableException();
    }

    @Override
    public void setReferenceName(String referenceName) {
        throwImmutableException();
    }

    @Override
    public void setPriority(String priority) {
        throwImmutableException();
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        throwImmutableException();
    }

    @Override
    public void setProcessId(String processId) {
        throwImmutableException();
    }

    @Override
    public void setRootProcessInstanceId(String rootProcessInstanceId) {
        throwImmutableException();
    }

    @Override
    public void setRootProcessId(String rootProcessId) {
        throwImmutableException();
    }

    @Override
    public void setPotentialUsers(Set<String> potentialUsers) {
        throwImmutableException();
    }

    @Override
    public void setPotentialGroups(Set<String> potentialGroups) {
        throwImmutableException();
    }

    @Override
    public void setAdminUsers(Set<String> adminUsers) {
        throwImmutableException();
    }

    @Override
    public void setAdminGroups(Set<String> adminGroups) {
        throwImmutableException();
    }

    @Override
    public void setExcludedUsers(Set<String> excludedUsers) {
        throwImmutableException();
    }

    @Override
    public void setStarted(ZonedDateTime started) {
        throwImmutableException();
    }

    @Override
    public void setCompleted(ZonedDateTime completed) {
        throwImmutableException();
    }

    @Override
    public void setLastUpdate(ZonedDateTime lastUpdate) {
        throwImmutableException();
    }

    @Override
    public void setEndpoint(String endpoint) {
        throwImmutableException();
    }

    @Override
    public void setInputData(Map<String, Object> inputData) {
        throwImmutableException();
    }

    @Override
    public void setAttributes(Map<String, Object> attributes) {
        throwImmutableException();
    }

    private void throwImmutableException() {
        throw new UnsupportedOperationException("ImmutableTask: " + this.getId() + " object can not be modified.");
    }
}
