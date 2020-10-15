/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.mongodb.marshalling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;

import org.drools.core.impl.EnvironmentImpl;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.jbpm.marshalling.impl.KogitoMarshallerReaderContext;
import org.jbpm.marshalling.impl.KogitoProcessMarshallerWriteContext;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.jbpm.marshalling.impl.JBPMMessages;
import org.jbpm.marshalling.impl.ProcessMarshallerRegistry;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.kogito.mongodb.model.ProcessInstanceDocument;
import org.kie.kogito.mongodb.utils.ProcessInstanceDocumentMapper;
import org.kie.kogito.mongodb.utils.ProcessInstanceMessageMapper;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.AbstractProcess;
import org.kie.kogito.process.impl.AbstractProcessInstance;

import static org.kie.kogito.mongodb.utils.DocumentConstants.DOCUMENT_MARSHALLING_ERROR_MSG;
import static org.kie.kogito.mongodb.utils.DocumentConstants.DOCUMENT_UNMARSHALLING_ERROR_MSG;

public class DocumentProcessInstanceMarshaller {

    private Environment env = new EnvironmentImpl();

    public DocumentProcessInstanceMarshaller(ObjectMarshallingStrategy... strategies) {
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

    public ProcessInstanceDocument marshalProcessInstance(ProcessInstance<?> processInstance) {
        try {
            WorkflowProcessInstance pi = ((AbstractProcessInstance<?>) processInstance).internalGetProcessInstance();
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                KogitoProcessMarshallerWriteContext context = new KogitoProcessMarshallerWriteContext(baos, null, null, null, null, env);
                org.jbpm.marshalling.impl.ProcessInstanceMarshaller marshaller = ProcessMarshallerRegistry.INSTANCE.getMarshaller(pi.getProcess().getType());
                JBPMMessages.ProcessInstance instance = (JBPMMessages.ProcessInstance) marshaller.writeProcessInstance(context, pi);
                ProcessInstanceDocument document = new ProcessInstanceDocumentMapper().apply(context, instance);
                pi.disconnect();
                return document;
            }
        } catch (Exception e) {
            throw new DocumentMarshallingException(processInstance.id(), e, DOCUMENT_MARSHALLING_ERROR_MSG);
        }
    }

    public WorkflowProcessInstance unmarshallWorkflowProcessInstance(ProcessInstanceDocument doc, Process<?> process) {

        try (ByteArrayInputStream bais = new ByteArrayInputStream(getDummyByteArray())) {
            MarshallerReaderContext context = new KogitoMarshallerReaderContext(bais,
                                                                                Collections.singletonMap(process.id(), ((AbstractProcess<?>) process).process()),
                                                                                null, null, null, env);
            JBPMMessages.ProcessInstance instance = new ProcessInstanceMessageMapper().apply(context, doc);
            context.setParameterObject( instance );
            org.jbpm.marshalling.impl.ProcessInstanceMarshaller marshaller = ProcessMarshallerRegistry.INSTANCE.getMarshaller(instance.getProcessType());
            return (WorkflowProcessInstance) marshaller.readProcessInstance(context);
        } catch (Exception e) {
            throw new DocumentUnmarshallingException(process.id(), e, DOCUMENT_UNMARSHALLING_ERROR_MSG);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ProcessInstance<T> unmarshallProcessInstance(ProcessInstanceDocument doc, Process<?> process) {
        return (ProcessInstance<T>) ((AbstractProcess<?>) process).createInstance(unmarshallWorkflowProcessInstance(doc, process));
    }

    @SuppressWarnings("unchecked")
    public <T> ProcessInstance<T> unmarshallReadOnlyProcessInstance(ProcessInstanceDocument doc, Process<?> process) {
        return (ProcessInstance<T>) ((AbstractProcess<?>) process).createReadOnlyInstance(unmarshallWorkflowProcessInstance(doc, process));
    }

    //This is to get dummy byte arrays to create context using existing marshaling framework
    private byte[] getDummyByteArray() {
        String dummy = "";
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeUTF(dummy);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new DocumentMarshallingException(e);
        }
    }
}
