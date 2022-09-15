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

import com.fasterxml.jackson.databind.JsonNode;

public class ExpressionAction extends BaseExpressionAction {

    protected final String outputVar;

    public ExpressionAction(String lang, String expr, String inputVar, String outputVar) {
        super(lang, expr, inputVar);
        this.outputVar = outputVar;
    }

    @Override
    public void execute(KogitoProcessContext context) throws Exception {
        JsonNode result = evaluate(context, JsonNode.class);
        context.setVariable(outputVar, result);
    }
}
