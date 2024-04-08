/**
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
package org.drools.core.process;

import java.util.Map;

import org.kie.api.definition.process.WorkflowElementIdentifier;

public interface WorkItem extends org.kie.api.runtime.process.WorkItem {

    void setName(String name);

    void setParameter(String name, Object value);

    void setParameters(Map<String, Object> parameters);

    void setResults(Map<String, Object> results);

    void setState(int state);

    void setProcessInstanceId(String processInstanceId);

    void setDeploymentId(String deploymentId);

    void setNodeInstanceId(long deploymentId);

    void setNodeId(WorkflowElementIdentifier nodeIdentifier);

    String getDeploymentId();

    long getNodeInstanceId();

    WorkflowElementIdentifier getNodeId();
}
