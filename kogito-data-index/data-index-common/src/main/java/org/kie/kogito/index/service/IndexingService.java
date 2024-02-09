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
package org.kie.kogito.index.service;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.kie.kogito.event.process.ProcessDefinitionDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDeadlineDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableDataEvent;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.index.storage.ProcessInstanceStorage;
import org.kie.kogito.index.storage.UserTaskInstanceStorage;
import org.kie.kogito.persistence.api.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;
import static org.kie.kogito.index.storage.Constants.ID;
import static org.kie.kogito.index.storage.Constants.KOGITO_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.storage.Constants.LAST_UPDATE;
import static org.kie.kogito.index.storage.Constants.PROCESS_ID;
import static org.kie.kogito.index.storage.Constants.PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.storage.Constants.USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE;

@ApplicationScoped
public class IndexingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingService.class);

    @Inject
    DataIndexStorageService manager;

    //retry in case of rare but possible race condition during the insert for the first registry
    @Retry(maxRetries = 3, delay = 300, jitter = 100, retryOn = ConcurrentModificationException.class)
    public void indexProcessInstanceEvent(ProcessInstanceDataEvent<?> event) {
        ProcessInstanceStorage storage = manager.getProcessInstanceStorage();
        if (event instanceof ProcessInstanceErrorDataEvent) {
            storage.indexError((ProcessInstanceErrorDataEvent) event);
        } else if (event instanceof ProcessInstanceNodeDataEvent) {
            storage.indexNode((ProcessInstanceNodeDataEvent) event);
        } else if (event instanceof ProcessInstanceSLADataEvent) {
            storage.indexSLA((ProcessInstanceSLADataEvent) event);
        } else if (event instanceof ProcessInstanceStateDataEvent) {
            storage.indexState((ProcessInstanceStateDataEvent) event);
        } else if (event instanceof ProcessInstanceVariableDataEvent) {
            storage.indexVariable((ProcessInstanceVariableDataEvent) event);
        }
    }

    //retry in case of rare but possible race condition during the insert for the first registry
    @Retry(maxRetries = 3, delay = 300, jitter = 100, retryOn = ConcurrentModificationException.class)
    public void indexProcessDefinition(ProcessDefinitionDataEvent definitionDataEvent) {
        ProcessDefinitionKey key = new ProcessDefinitionKey(definitionDataEvent.getKogitoProcessId(), definitionDataEvent.getData().getVersion());
        manager.getProcessDefinitionStorage().put(key, ProcessDefinitionHelper.merge(manager.getProcessDefinitionStorage().get(key), definitionDataEvent));
    }

    //retry in case of rare but possible race condition during the insert for the first registry
    @Retry(maxRetries = 3, delay = 300, jitter = 100, retryOn = ConcurrentModificationException.class)
    public <T> void indexUserTaskInstanceEvent(UserTaskInstanceDataEvent<T> event) {
        UserTaskInstanceStorage storage = manager.getUserTaskInstanceStorage();
        if (event instanceof UserTaskInstanceAssignmentDataEvent) {
            storage.indexAssignment((UserTaskInstanceAssignmentDataEvent) event);
        } else if (event instanceof UserTaskInstanceAttachmentDataEvent) {
            storage.indexAttachment((UserTaskInstanceAttachmentDataEvent) event);
        } else if (event instanceof UserTaskInstanceDeadlineDataEvent) {
            storage.indexDeadline((UserTaskInstanceDeadlineDataEvent) event);
        } else if (event instanceof UserTaskInstanceStateDataEvent) {
            storage.indexState((UserTaskInstanceStateDataEvent) event);
        } else if (event instanceof UserTaskInstanceCommentDataEvent) {
            storage.indexComment((UserTaskInstanceCommentDataEvent) event);
        } else if (event instanceof UserTaskInstanceVariableDataEvent) {
            storage.indexVariable((UserTaskInstanceVariableDataEvent) event);
        }
    }

    public void indexJob(Job job) {
        manager.getJobsStorage().put(job.getId(), job);
    }

    public void indexModel(ObjectNode updateData) {
        String processId = updateData.remove(PROCESS_ID).asText();
        Storage<String, ObjectNode> cache = manager.getDomainModelCache(processId);

        if (cache == null) {
            LOGGER.debug("Ignoring Kogito cloud event for unknown process: {}", processId);
            return;
        }

        String processInstanceId = updateData.get(ID).asText();
        String type = cache.getRootType();
        ObjectNode persistedModel = Optional.ofNullable(cache.get(processInstanceId)).orElse(getObjectMapper().createObjectNode());

        LOGGER.debug("About to update model \n{}\n with data {}", persistedModel, updateData);
        ObjectNode newModel = merge(processId, type, processInstanceId, persistedModel, updateData);

        LOGGER.debug("Merged model\n{}\n for {} and id {}", newModel, processId, processInstanceId);
        cache.put(processInstanceId, newModel);
    }

    private ObjectNode merge(String processId, String type, String processInstanceId, ObjectNode persistedModel, ObjectNode updateData) {
        ObjectNode newModel = getObjectMapper().createObjectNode();
        newModel.put("_type", type);
        newModel.setAll(persistedModel);
        newModel.put(ID, processInstanceId);
        // copy variables
        copyFieldsExcept(newModel, updateData, ID, PROCESS_ID, KOGITO_DOMAIN_ATTRIBUTE);

        // now merge metadata
        mergeMetadata(newModel, updateData);

        return newModel;
    }

    private void mergeMetadata(ObjectNode newModel, ObjectNode updateData) {
        if (!updateData.has(KOGITO_DOMAIN_ATTRIBUTE)) {
            // nothing to merge
            return;
        }

        if (!newModel.has(KOGITO_DOMAIN_ATTRIBUTE)) {
            newModel.set(KOGITO_DOMAIN_ATTRIBUTE, updateData.get(KOGITO_DOMAIN_ATTRIBUTE));
            return;
        }

        mergeProcessInstance((ObjectNode) newModel.get(KOGITO_DOMAIN_ATTRIBUTE), (ObjectNode) updateData.get(KOGITO_DOMAIN_ATTRIBUTE));
        mergeUserTaskInstance((ObjectNode) newModel.get(KOGITO_DOMAIN_ATTRIBUTE), (ObjectNode) updateData.get(KOGITO_DOMAIN_ATTRIBUTE));

    }

    private void copyFieldsExcept(ObjectNode newModel, ObjectNode updateData, String... exceptions) {
        List<String> nonVars = Arrays.asList(exceptions);
        Iterator<Map.Entry<String, JsonNode>> iterator = updateData.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> element = iterator.next();
            JsonNode node = element.getValue();
            String key = element.getKey();
            if (!nonVars.contains(key)) {
                newModel.set(key, node);
            }
        }
    }

    private void mergeUserTaskInstance(ObjectNode newModel, ObjectNode updateData) {
        if (!updateData.has(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE)) {
            return;
        }

        if (!newModel.has(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE)) {
            newModel.set(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE, updateData.get(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE));
            return;
        }
        newModel.set(LAST_UPDATE, updateData.get(LAST_UPDATE));

        ArrayNode currentUserTaskModel = (ArrayNode) newModel.get(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE);
        ArrayNode updateUserTasks = (ArrayNode) updateData.get(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE);

        ArrayNode newArrayNode = getObjectMapper().createArrayNode();
        newArrayNode.addAll(currentUserTaskModel);
        for (int i = 0; i < updateUserTasks.size(); i++) {
            String indexId = updateUserTasks.get(i).get(ID).asText();
            boolean found = false;
            for (int j = 0; j < currentUserTaskModel.size(); j++) {
                String currentIndexId = currentUserTaskModel.get(j).get(ID).asText();
                if (indexId.equals(currentIndexId)) {
                    ObjectNode currentNode = (ObjectNode) newArrayNode.get(j);
                    ObjectNode updateNode = ((ObjectNode) updateUserTasks.get(i));
                    copyFieldsExcept(currentNode, updateNode, "comments", "attachments");
                    mergeFieldArray("comments", currentNode, updateNode);
                    mergeFieldArray("attachments", currentNode, updateNode);
                    found = true;
                    break;
                }
            }
            if (!found) {
                newArrayNode.add(updateUserTasks.get(i));
            }
        }

        newModel.set(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE, newArrayNode);
        return;
    }

    private void mergeFieldArray(String field, ObjectNode newModel, ObjectNode updateData) {
        if (!updateData.has(field) || (updateData.has(field) && updateData.get(field).isNull())) {
            return;
        }

        if (!newModel.has(field) || (newModel.has(field) && newModel.get(field).isNull())) {
            newModel.set(field, updateData.get(field));
            return;
        }

        newModel.set(field, mergeArray((ArrayNode) newModel.get(field), (ArrayNode) updateData.get(field)));

    }

    private ObjectNode mergeProcessInstance(ObjectNode newModel, ObjectNode updateData) {
        if (!updateData.has(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE)) {
            return newModel;
        }

        if (!newModel.has(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE)) {
            newModel.set(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE, updateData.get(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE));
            return newModel;
        }

        newModel.set(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE, mergeArray((ArrayNode) newModel.get(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE), (ArrayNode) updateData.get(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE)));
        return newModel;
    }

    private ArrayNode mergeArray(ArrayNode newModel, ArrayNode updateData) {
        ArrayNode newArrayNode = getObjectMapper().createArrayNode();
        newArrayNode.addAll(newModel);
        for (int i = 0; i < updateData.size(); i++) {
            String indexId = updateData.get(i).get(ID).asText();
            boolean found = false;
            for (int j = 0; j < newModel.size(); j++) {
                String currentIndexId = newModel.get(j).get(ID).asText();
                if (indexId.equals(currentIndexId)) {
                    ((ObjectNode) newArrayNode.get(j)).setAll((ObjectNode) updateData.get(i));
                    found = true;
                    break;
                }
            }
            if (!found) {
                newArrayNode.add(updateData.get(i));
            }
        }

        Iterator<JsonNode> iterator = newArrayNode.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().has("remove")) {
                iterator.remove();
            }
        }

        return newArrayNode;
    }
}
