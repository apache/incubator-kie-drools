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
package org.kie.kogito.workflows.services;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.functions.ActionFunctionNamespace;
import org.kie.kogito.serverless.workflow.parser.VariableInfo;
import org.kie.kogito.serverless.workflow.suppliers.InjectActionSupplier;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionRef;

public class DummyAnsibleCustomFunction extends ActionFunctionNamespace {

    @Override
    public String namespace() {
        return "ansible";
    }

    @Override
    protected <T extends RuleFlowNodeContainerFactory<T, ?>> ActionNodeFactory<T> fillAction(Workflow workflow,
            ActionNodeFactory<T> node,
            FunctionRef functionRef,
            VariableInfo varInfo) {
        return node.action(new InjectActionSupplier(ObjectMapperFactory.get().createObjectNode().put("name", "John")));
    }
}
