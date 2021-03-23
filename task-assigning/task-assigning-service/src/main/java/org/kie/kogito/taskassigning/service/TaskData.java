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

package org.kie.kogito.taskassigning.service;

import java.time.ZonedDateTime;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public interface TaskData {
    String getId();

    String getName();

    String getState();

    String getDescription();

    String getReferenceName();

    String getPriority();

    String getProcessInstanceId();

    String getProcessId();

    String getRootProcessInstanceId();

    String getRootProcessId();

    String getActualOwner();

    Set<String> getPotentialUsers();

    Set<String> getPotentialGroups();

    Set<String> getAdminUsers();

    Set<String> getAdminGroups();

    Set<String> getExcludedUsers();

    ZonedDateTime getStarted();

    ZonedDateTime getCompleted();

    ZonedDateTime getLastUpdate();

    JsonNode getInputs();

    String getEndpoint();
}
