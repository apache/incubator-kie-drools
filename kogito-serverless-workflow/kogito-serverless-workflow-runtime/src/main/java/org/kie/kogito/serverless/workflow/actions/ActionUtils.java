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

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonObjectUtils;

import com.fasterxml.jackson.databind.JsonNode;

import static org.kie.kogito.serverless.workflow.SWFConstants.DEFAULT_WORKFLOW_VAR;

class ActionUtils {

    private ActionUtils() {
    }

    protected static JsonNode getWorkflowData(KogitoProcessContext context) {
        return getJsonNode(context, DEFAULT_WORKFLOW_VAR);
    }

    protected static JsonNode getJsonNode(KogitoProcessContext context, String variableName) {
        return JsonObjectUtils.fromValue(context.getVariable(variableName));
    }
}
