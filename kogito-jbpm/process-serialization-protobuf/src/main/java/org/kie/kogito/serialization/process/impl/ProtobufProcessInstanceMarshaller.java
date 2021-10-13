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
package org.kie.kogito.serialization.process.impl;

import java.io.IOException;

import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.AbstractProcess;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.serialization.process.MarshallerContextName;
import org.kie.kogito.serialization.process.MarshallerReaderContext;
import org.kie.kogito.serialization.process.MarshallerWriterContext;
import org.kie.kogito.serialization.process.ProcessInstanceMarshaller;

/**
 * Marshaller class for RuleFlowProcessInstances
 */

public class ProtobufProcessInstanceMarshaller implements ProcessInstanceMarshaller {

    @Override
    public void writeProcessInstance(MarshallerWriterContext context, ProcessInstance<?> processInstance) throws IOException {
        RuleFlowProcessInstance pi = (RuleFlowProcessInstance) ((AbstractProcessInstance<?>) processInstance).internalGetProcessInstance();
        ProtobufProcessInstanceWriter writer = new ProtobufProcessInstanceWriter(context);
        writer.writeProcessInstance(pi, context.output());
    }

    @Override
    public ProcessInstance<?> readProcessInstance(MarshallerReaderContext context) throws IOException {
        ProtobufProcessInstanceReader reader = new ProtobufProcessInstanceReader(context);
        boolean readOnly = context.get(MarshallerContextName.MARSHALLER_INSTANCE_READ_ONLY);
        AbstractProcess<?> process = (AbstractProcess<?>) context.get(MarshallerContextName.MARSHALLER_PROCESS);
        return readOnly ? process.createReadOnlyInstance(reader.read(context.input())) : process.createInstance(reader.read(context.input()));
    }

    @Override
    public void reloadProcessInstance(MarshallerReaderContext context, ProcessInstance<?> processInstance) throws IOException {
        ProtobufProcessInstanceReader reader = new ProtobufProcessInstanceReader(context);
        ((AbstractProcessInstance<?>) processInstance).internalSetProcessInstance(reader.read(context.input()));
    }

}
