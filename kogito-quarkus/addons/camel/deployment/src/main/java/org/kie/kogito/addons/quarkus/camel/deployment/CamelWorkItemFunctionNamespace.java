/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.quarkus.camel.deployment;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.addons.quarkus.camel.runtime.CamelConstants;
import org.kie.kogito.serverless.workflow.functions.WorkItemFunctionNamespace;
import org.kie.kogito.serverless.workflow.parser.ParserContext;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionRef;

import static org.kie.kogito.addons.quarkus.camel.runtime.CamelCustomWorkItemHandler.NAME;
import static org.kie.kogito.addons.quarkus.camel.runtime.CamelCustomWorkItemHandler.OPERATION;
import static org.kie.kogito.serverless.workflow.parser.FunctionNamespaceFactory.getFunctionName;

/**
 * Implementation of the custom Camel Namespace Function Reference
 */
public class CamelWorkItemFunctionNamespace extends WorkItemFunctionNamespace {

    @Override
    protected <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> fillWorkItemHandler(Workflow workflow, ParserContext parserContext, WorkItemNodeFactory<T> workItemNodeFactory,
            FunctionRef functionRef) {
        return workItemNodeFactory.workName(NAME).metaData(OPERATION, getFunctionName(functionRef));
    }

    @Override
    protected void validateArgs(FunctionRef ref) {
        super.validateArgs(ref);
    }

    @Override
    public String namespace() {
        return CamelConstants.NAMESPACE;
    }
}
