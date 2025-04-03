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
package org.kie.kogito.internal.process.runtime;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.internal.process.event.KogitoEventListener;

public interface KogitoProcessInstance extends ProcessInstance, KogitoEventListener {

    int STATE_PENDING = 0;
    int STATE_ACTIVE = 1;
    int STATE_COMPLETED = 2;
    int STATE_ABORTED = 3;
    int STATE_SUSPENDED = 4;
    int STATE_ERROR = 5;

    int SLA_NA = 0;
    int SLA_PENDING = 1;
    int SLA_MET = 2;
    int SLA_VIOLATED = 3;
    int SLA_ABORTED = 4;

    String getStringId();

    /**
     * Returns root process instance id if this process instance has a root process instance
     * 
     * @return the unique id of root process instance, null if this process instance doesn't have a root or is a root itself
     */
    String getRootProcessInstanceId();

    /**
     * The id of the root process definition that is related to this process instance.
     * 
     * @return the id of the root process definition that is related to this process instance
     */
    String getRootProcessId();

    /**
     * Returns current snapshot of process instance variables
     * 
     * @return non empty map of process instance variables
     */
    Map<String, Object> getVariables();

    /**
     * Returns optional reference id this process instance was triggered by
     * 
     * @return reference id or null if not set
     */
    String getReferenceId();

    /**
     * The description of the current process instance
     * 
     * @return the process instance description
     */
    String getDescription();

    /**
     * Returns optional business key of the process instance
     * 
     * @return the business key or null if not set
     */
    String getBusinessKey();

    Date getStartDate();

    /**
     * Returns optional header of the process instance
     * 
     * @return map with headers
     */
    Map<String, List<String>> getHeaders();

    void wrap(org.kie.kogito.process.ProcessInstance<?> kogitoProcessInstance);

    org.kie.kogito.process.ProcessInstance<?> unwrap();
}
