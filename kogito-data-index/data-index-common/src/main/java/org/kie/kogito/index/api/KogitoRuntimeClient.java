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
package org.kie.kogito.index.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;

public interface KogitoRuntimeClient {

    CompletableFuture<String> executeProcessIntance(ProcessDefinition definition, ExecuteArgs args);

    CompletableFuture<String> abortProcessInstance(String serviceURL, ProcessInstance processInstance);

    CompletableFuture<String> retryProcessInstance(String serviceURL, ProcessInstance processInstance);

    CompletableFuture<String> skipProcessInstance(String serviceURL, ProcessInstance processInstance);

    CompletableFuture<String> updateProcessInstanceVariables(String serviceURL, ProcessInstance processInstance, String variables);

    CompletableFuture<String> getProcessInstanceDiagram(String serviceURL, ProcessInstance processInstance);

    CompletableFuture<String> getProcessDefinitionSourceFileContent(String serviceURL, String processId);

    CompletableFuture<List<Node>> getProcessDefinitionNodes(String serviceURL, String processId);

    CompletableFuture<String> triggerNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeDefinitionId);

    CompletableFuture<String> retriggerNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeInstanceId);

    CompletableFuture<String> cancelNodeInstance(String serviceURL, ProcessInstance processInstance, String nodeInstanceId);

    CompletableFuture<String> cancelJob(String serviceURL, Job job);

    CompletableFuture<String> rescheduleJob(String serviceURL, Job job, String newJobData);

    CompletableFuture<String> getUserTaskSchema(String serviceURL, UserTaskInstance userTaskInstance, String user,
            List<String> groups);

    CompletableFuture<String> updateUserTaskInstance(String serviceURL, UserTaskInstance userTaskInstance, String user,
            List<String> groups, Map taskInfo);

    CompletableFuture<String> createUserTaskInstanceComment(String serviceURL, UserTaskInstance userTaskInstance,
            String user, List<String> groups, String commentInfo);

    CompletableFuture<String> createUserTaskInstanceAttachment(String serviceURL, UserTaskInstance userTaskInstance,
            String user, List<String> groups, String name, String uri);

    CompletableFuture<String> updateUserTaskInstanceComment(String serviceURL, UserTaskInstance userTaskInstance, String user,
            List<String> groups, String commentId, String commentInfo);

    CompletableFuture<String> deleteUserTaskInstanceComment(String serviceURL, UserTaskInstance userTaskInstance, String user,
            List<String> groups, String commentId);

    CompletableFuture<String> updateUserTaskInstanceAttachment(String serviceURL, UserTaskInstance userTaskInstance, String user,
            List<String> groups, String attachmentId, String name, String uri);

    CompletableFuture<String> deleteUserTaskInstanceAttachment(String serviceURL, UserTaskInstance userTaskInstance, String user,
            List<String> groups, String attachmentId);
}
