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
package org.kie.kogito.jobs.descriptors;

import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.JobDescription;

public class UserTaskInstanceJobDescription implements JobDescription {

    private String id;
    private ExpirationTime expirationTime;
    private Integer priority = ProcessInstanceJobDescription.DEFAULT_PRIORITY;
    private String userTaskInstanceId;
    private String processId;
    private String processInstanceId;
    private String nodeInstanceId;
    private String rootProcessInstanceId;
    private String rootProcessId;

    public UserTaskInstanceJobDescription() {
        // do nothing
    }

    public UserTaskInstanceJobDescription(
            String id,
            ExpirationTime expirationTime,
            Integer priority,
            String userTaskInstanceId,
            String processId,
            String processInstanceId,
            String nodeInstanceId,
            String rootProcessInstanceId,
            String rootProcessId) {
        this.id = id;
        this.expirationTime = expirationTime;
        this.priority = priority;
        this.userTaskInstanceId = userTaskInstanceId;
        this.processId = processId;
        this.processInstanceId = processInstanceId;
        this.nodeInstanceId = nodeInstanceId;
        this.rootProcessInstanceId = rootProcessInstanceId;
        this.rootProcessId = rootProcessId;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public ExpirationTime expirationTime() {
        return expirationTime;
    }

    @Override
    public Integer priority() {
        return priority;
    }

    @Override
    public String path() {
        return null;
    }

    public String userTaskInstanceId() {
        return userTaskInstanceId;
    }

    public String processId() {
        return processId;
    }

    public String processInstanceId() {
        return processInstanceId;
    }

    public String nodeInstanceId() {
        return nodeInstanceId;
    }

    public String rootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public String rootProcessId() {
        return rootProcessId;
    }

    public static UserTaskInstanceJobDescriptionBuilder newUserTaskInstanceJobDescriptionBuilder() {
        return new UserTaskInstanceJobDescriptionBuilder();
    }
}
