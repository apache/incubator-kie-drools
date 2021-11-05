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
package org.kie.kogito.serverless.workflow.actions;

import java.util.Iterator;

import org.jbpm.process.instance.impl.Action;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.kie.kogito.serverless.workflow.actions.ActionUtils.getWorkflowData;
import static org.kie.kogito.serverless.workflow.actions.ActionUtils.setWorkflowData;

public class InjectAction implements Action {

    protected JsonNode node;

    public InjectAction(String json) {
        this(readObject(json));
    }

    public InjectAction(JsonNode node) {
        this.node = node;
    }

    private static JsonNode readObject(String json) {
        try {
            return ObjectMapperFactory.get().readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void execute(KogitoProcessContext context) throws Exception {
        ObjectNode mainNode = getWorkflowData(context);
        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            mainNode.set(fieldName, node.get(fieldName));
        }
        setWorkflowData(context, mainNode);
    }
}
