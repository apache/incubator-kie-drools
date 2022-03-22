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
package org.kie.kogito.serverless.workflow.rest;

import java.util.Map;

import org.kogito.workitem.rest.bodybuilders.DefaultWorkItemHandlerBodyBuilder;

import static org.kie.kogito.serverless.workflow.SWFConstants.MODEL_WORKFLOW_VAR;

public class ParamsRestWorkItemHandlerBodyBuilder extends DefaultWorkItemHandlerBodyBuilder {

    @Override
    protected Object buildFromParams(Map<String, Object> parameters) {
        Object inputModel = parameters.remove(MODEL_WORKFLOW_VAR);
        return parameters.isEmpty() ? inputModel : parameters;
    }
}
