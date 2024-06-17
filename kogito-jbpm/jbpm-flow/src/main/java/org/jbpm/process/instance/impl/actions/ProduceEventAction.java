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
package org.jbpm.process.instance.impl.actions;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Supplier;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.impl.NodeIoHelper;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.kogito.event.impl.MessageProducer;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

public class ProduceEventAction<T> implements Action, Serializable {

    private static final long serialVersionUID = 1L;

    private final String varName;
    private final String triggerName;
    private final Supplier<MessageProducer<T>> supplier;

    public ProduceEventAction(String triggerName, String varName, Supplier<MessageProducer<T>> supplier) {
        this.triggerName = triggerName;
        this.varName = varName;
        this.supplier = supplier;
    }

    @Override
    public void execute(KogitoProcessContext context) throws Exception {
        Map<String, Object> inputs = NodeIoHelper.processInputs((NodeInstanceImpl) context.getNodeInstance(), var -> context.getVariable(var));
        Object object = inputs.get(varName);
        KogitoProcessInstance pi = context.getProcessInstance();
        InternalKnowledgeRuntime runtime = (InternalKnowledgeRuntime) context.getKieRuntime();
        InternalProcessRuntime process = (InternalProcessRuntime) runtime.getProcessRuntime();
        process.getProcessEventSupport().fireOnMessage(pi, context.getNodeInstance(), runtime, triggerName, object);
        supplier.get().produce(pi, getObject(object, context));
    }

    protected T getObject(Object object, KogitoProcessContext context) {
        return (T) object;
    }
}
