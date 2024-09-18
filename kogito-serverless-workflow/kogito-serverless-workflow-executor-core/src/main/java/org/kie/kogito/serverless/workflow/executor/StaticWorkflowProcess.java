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

import java.util.Collection;

import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstancesFactory;
import org.kie.kogito.process.impl.AbstractProcess;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;

class StaticWorkflowProcess extends AbstractProcess<JsonNodeModel> {

    private final KogitoWorkflowProcess process;

    public StaticWorkflowProcess(Application app, Collection<KogitoWorkItemHandler> handlers, ProcessInstancesFactory processInstanceFactory, KogitoWorkflowProcess process) {
        super(app, handlers, null, processInstanceFactory);
        this.process = process;
        activate();
    }

    @Override
    public ProcessInstance<JsonNodeModel> createInstance(JsonNodeModel model) {
        return new StaticWorkflowProcessInstance(this, model, this.createProcessRuntime());
    }

    @Override
    public ProcessInstance<JsonNodeModel> createInstance(String businessKey, CompositeCorrelation correlation,
            JsonNodeModel model) {
        return new StaticWorkflowProcessInstance(this, model, this.createProcessRuntime(), businessKey, correlation);
    }

    @Override
    public ProcessInstance<? extends Model> createInstance(Model m) {
        return createInstance((JsonNodeModel) m);
    }

    @Override
    public JsonNodeModel createModel() {
        return new JsonNodeModel();
    }

    @Override
    public ProcessInstance<JsonNodeModel> createInstance(WorkflowProcessInstance wpi) {
        return new StaticWorkflowProcessInstance(this, this.createModel(), this.createProcessRuntime(), wpi);
    }

    @Override
    public ProcessInstance<JsonNodeModel> createReadOnlyInstance(WorkflowProcessInstance wpi) {
        return new StaticWorkflowProcessInstance(this, this.createModel(), wpi);
    }

    @Override
    protected Process process() {
        return process;
    }
}
