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
package org.jbpm.process.instance.impl.actions;

import java.io.Serializable;
import java.util.function.Supplier;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.impl.Action;
import org.kie.kogito.event.impl.AbstractMessageProducer;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

public class ProduceEventAction<T> implements Action, Serializable {

    private static final long serialVersionUID = 1L;

    private final String varName;
    private final String triggerName;
    private final Supplier<AbstractMessageProducer<T>> supplier;

    public ProduceEventAction(String triggerName, String varName, Supplier<AbstractMessageProducer<T>> supplier) {
        this.triggerName = triggerName;
        this.varName = varName;
        this.supplier = supplier;
    }

    @Override
    public void execute(KogitoProcessContext context) throws Exception {
        Object object = context.getVariable(varName);
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
