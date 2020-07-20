/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process.impl.marshalling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.util.Collections;

import org.drools.core.impl.EnvironmentImpl;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProcessMarshallerWriteContext;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.jbpm.marshalling.impl.JBPMMessages;
import org.jbpm.marshalling.impl.ProcessMarshallerRegistry;
import org.jbpm.marshalling.impl.ProtobufRuleFlowProcessInstanceMarshaller;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.AbstractProcess;
import org.kie.kogito.process.impl.AbstractProcessInstance;

public class ProcessInstanceMarshaller {

    private Environment env = new EnvironmentImpl();

    public ProcessInstanceMarshaller(ObjectMarshallingStrategy... strategies) {
        ObjectMarshallingStrategy[] strats = null;
        if (strategies == null) {
            strats = new ObjectMarshallingStrategy[]{new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)};
        } else {
            strats = new ObjectMarshallingStrategy[strategies.length + 1];
            int i = 0;
            for (ObjectMarshallingStrategy strategy : strategies) {
                strats[i] = strategy;
                i++;
            }
            strats[i] = new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);
        }

        env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, strats);
    }

    public byte[] marshallProcessInstance(ProcessInstance<?> processInstance) {

        WorkflowProcessInstance pi = ((AbstractProcessInstance<?>) processInstance).internalGetProcessInstance();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            ProcessMarshallerWriteContext context = new ProcessMarshallerWriteContext(baos,
                                                                                      null,
                                                                                      null,
                                                                                      null,
                                                                                      null,
                                                                                      this.env);
            context.setProcessInstanceId(pi.getId());
            context.setState(pi.getState());

            String processType = pi.getProcess().getType();
            context.stream.writeUTF(processType);

            org.jbpm.marshalling.impl.ProcessInstanceMarshaller marshaller = ProcessMarshallerRegistry.INSTANCE.getMarshaller(processType);

            Object result = marshaller.writeProcessInstance(context, pi);
            if (marshaller instanceof ProtobufRuleFlowProcessInstanceMarshaller && result != null) {
                JBPMMessages.ProcessInstance _instance = (JBPMMessages.ProcessInstance) result;
                PersisterHelper.writeToStreamWithHeader(context, _instance);
            }
            context.close();
            pi.disconnect();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error while marshalling process instance", e);
        }
    }

    public WorkflowProcessInstance unmarshallWorkflowProcessInstance(byte[] data, Process<?> process) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            MarshallerReaderContext context = new MarshallerReaderContext(bais,
                                                                          Collections.singletonMap(process.id(), ((AbstractProcess<?>) process).process()),
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          this.env
            );
            ObjectInputStream stream = context.stream;
            String processInstanceType = stream.readUTF();

            org.jbpm.marshalling.impl.ProcessInstanceMarshaller marshaller = ProcessMarshallerRegistry.INSTANCE.getMarshaller(processInstanceType);

            WorkflowProcessInstance pi = (WorkflowProcessInstance) marshaller.readProcessInstance(context);

            context.close();
            return pi;
        } catch (Exception e) {
            throw new RuntimeException("Error while unmarshalling process instance", e);
        }
    }

    public ProcessInstance<?> unmarshallProcessInstance(byte[] data, Process<?> process) {
        return ((AbstractProcess<?>) process).createInstance(unmarshallWorkflowProcessInstance(data, process));
    }
}
