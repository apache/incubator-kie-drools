/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs;

import static java.util.Objects.requireNonNull;

import java.util.UUID;

public class ProcessInstanceJobDescription implements JobDescription {

    public final static Integer DEFAULT_PRIORITY = 5;
    
    private final String id;

    private final ExpirationTime expirationTime;    

    private final Integer priority;

    private final String processInstanceId;
    private final String rootProcessInstanceId;
    private final String processId;
    private final String rootProcessId;

    private ProcessInstanceJobDescription(long timerId,
                                          ExpirationTime expirationTime,
                                          Integer priority, 
                                          String processInstanceId, 
                                          String rootProcessInstanceId, 
                                          String processId, 
                                          String rootProcessId) {
        this.id = UUID.randomUUID().toString() + "_" + timerId;
        this.expirationTime = requireNonNull(expirationTime);
        this.priority = requireNonNull(priority);
        this.processInstanceId = requireNonNull(processInstanceId);
        this.rootProcessInstanceId = rootProcessInstanceId;
        this.processId = processId;
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

    public String processInstanceId() {
        return processInstanceId;
    }

    public String rootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public String processId() {
        return processId;
    }

    public String rootProcessId() {
        return rootProcessId;
    }
    
    public static ProcessInstanceJobDescription of(long timerId,
                                                   ExpirationTime expirationTime,
                                                   String processInstanceId,
                                                   String processId) {
        return of(timerId, expirationTime, processInstanceId, null, processId, null);
    }
    
    public static ProcessInstanceJobDescription of(long timerId,
                                                   ExpirationTime expirationTime,
                                                   String processInstanceId,
                                                   String rootProcessInstanceId,
                                                   String processId,
                                                   String rootProcessId) {
        return of(timerId, expirationTime, DEFAULT_PRIORITY, processInstanceId, rootProcessInstanceId, processId, rootProcessId);
    }

    public static ProcessInstanceJobDescription of(long timerId,
                                                   ExpirationTime expirationTime,
                                                   Integer priority,
                                                   String processInstanceId,
                                                   String rootProcessInstanceId,
                                                   String processId,
                                                   String rootProcessId) {

        return new ProcessInstanceJobDescription(timerId, expirationTime, priority, processInstanceId, rootProcessInstanceId, processId, rootProcessId);
    }

}
