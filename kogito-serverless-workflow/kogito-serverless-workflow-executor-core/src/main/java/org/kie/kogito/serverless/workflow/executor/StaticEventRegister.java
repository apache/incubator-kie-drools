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
package org.kie.kogito.serverless.workflow.executor;

import org.jbpm.process.core.event.StaticMessageConsumer;
import org.jbpm.workflow.core.node.EventNode;
import org.kie.kogito.event.impl.EventFactoryUtils;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;

import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_MESSAGE;
import static org.jbpm.ruleflow.core.Metadata.TRIGGER_REF;

public class StaticEventRegister implements StaticProcessRegister {

    @Override
    public void register(StaticWorkflowApplication application, Workflow workflow, StaticWorkflowProcess process) {
        ((KogitoWorkflowProcess) process.get()).getNodesRecursively().stream().filter(EventNode.class::isInstance).map(EventNode.class::cast)
                .filter(node -> EVENT_TYPE_MESSAGE.equals(node.getMetaData(EVENT_TYPE)))
                .forEach(node -> StaticMessageConsumer.of(application, process, JsonNode.class, (String) node.getMetaData(TRIGGER_REF)).executor(application.executorService()).build());
        application.registerCloseable(EventFactoryUtils::cleanUp);
    }
}
