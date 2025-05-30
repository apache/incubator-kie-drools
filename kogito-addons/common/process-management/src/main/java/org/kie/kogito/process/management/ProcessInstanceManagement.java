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
package org.kie.kogito.process.management;

public interface ProcessInstanceManagement<T> {

    T getProcesses();

    T getProcessInfo(String processId);

    T getProcessNodes(String processId);

    T getInstanceInError(String processId, String processInstanceId);

    T getWorkItemsInProcessInstance(String processId, String processInstanceId);

    T getProcessInstanceTimers(String processId, String processInstanceId);

    T retriggerInstanceInError(String processId, String processInstanceId);

    T skipInstanceInError(String processId, String processInstanceId);

    T triggerNodeInstanceId(String processId, String processInstanceId, String nodeId);

    T retriggerNodeInstanceId(String processId, String processInstanceId, String nodeInstanceId);

    T cancelNodeInstanceId(String processId, String processInstanceId, String nodeInstanceId);

    T getNodeInstanceTimers(String processId, String processInstanceId, String nodeInstanceId);

    T cancelProcessInstanceId(String processId, String processInstanceId);

    T migrateAllInstances(String processId, ProcessMigrationSpec migrationSpec);

    T migrateInstance(String processId, String processInstanceId, ProcessMigrationSpec migrationSpec);

    T updateNodeInstanceSla(String processId, String processInstanceId, String nodeInstanceId, SlaPayload SLAPayload);

    T updateProcessInstanceSla(String processId, String processInstanceId, SlaPayload SLAPayload);

}
