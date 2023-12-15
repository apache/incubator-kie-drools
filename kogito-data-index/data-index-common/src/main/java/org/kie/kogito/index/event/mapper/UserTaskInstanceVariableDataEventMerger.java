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
package org.kie.kogito.index.event.mapper;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceVariableEventBody;
import org.kie.kogito.index.json.JsonUtils;
import org.kie.kogito.index.model.UserTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserTaskInstanceVariableDataEventMerger implements UserTaskInstanceEventMerger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInstanceVariableDataEventMerger.class);

    @Override
    public boolean accept(Object event) {
        return event instanceof UserTaskInstanceVariableDataEvent;
    }

    @Override
    public UserTaskInstance merge(UserTaskInstance userTaskInstance, UserTaskInstanceDataEvent<?> data) {
        UserTaskInstanceVariableDataEvent event = (UserTaskInstanceVariableDataEvent) data;
        try {
            UserTaskInstanceVariableEventBody body = event.getData();
            ObjectMapper mapper = JsonUtils.getObjectMapper();
            switch (body.getVariableType()) {
                case "INPUT":
                    Map<String, Object> inVars = toMap(mapper, userTaskInstance.getInputs());
                    inVars.put(body.getVariableName(), body.getVariableValue());
                    userTaskInstance.setInputs(mapper.valueToTree(inVars));
                    break;
                case "OUTPUT":
                    Map<String, Object> outVars = toMap(mapper, userTaskInstance.getOutputs());
                    outVars.put(body.getVariableName(), body.getVariableValue());
                    userTaskInstance.setOutputs(mapper.valueToTree(outVars));
            }

        } catch (JsonProcessingException e) {
            LOGGER.error("error during unmarshalling variable instance", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("error during merging variable instance event", e);
        }
        return userTaskInstance;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(ObjectMapper mapper, ObjectNode node) throws JsonProcessingException, IllegalArgumentException {

        Map<String, Object> variables = null;
        if (node == null) {
            variables = new HashMap<>();
        } else {
            variables = new HashMap<>(mapper.treeToValue(node, HashMap.class));

        }
        return variables;
    }

}
