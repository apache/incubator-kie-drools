/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.integrationtests.marshalling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.marshalling.impl.MarshallingConfigurationImpl;
import org.drools.core.marshalling.impl.RuleBaseNodes;
import org.drools.serialization.protobuf.ProtobufMarshaller;
import org.drools.serialization.protobuf.ProtobufMarshallerReaderContext;
import org.drools.serialization.protobuf.ProtobufMarshallerWriteContext;
import org.jbpm.marshalling.impl.ProcessInstanceResolverStrategy;
import org.jbpm.process.instance.ProcessInstanceManager;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessInstanceResolverStrategyTest extends AbstractBaseTest {

    private final static String PROCESS_NAME = "simpleProcess.xml";

    @Test
    public void testAccept() {
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        WorkflowProcessImpl process = new WorkflowProcessImpl();

        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setState(KogitoProcessInstance.STATE_ACTIVE);
        processInstance.setProcess(process);
        processInstance.setKnowledgeRuntime((InternalKnowledgeRuntime) kruntime.getKieSession());

        ProcessInstanceResolverStrategy strategy = new ProcessInstanceResolverStrategy();

        assertTrue(strategy.accept(processInstance));
        Object object = new Object();
        assertFalse(strategy.accept(object));
    }

    @Test
    public void testProcessInstanceResolverStrategy() throws Exception {
        // Setup
        builder.add(new ClassPathResource(PROCESS_NAME, this.getClass()), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        KogitoProcessInstance processInstance = kruntime.createProcessInstance("process name", new HashMap<String, Object>());
        kruntime.getKieSession().insert(processInstance);

        // strategy setup
        ProcessInstanceResolverStrategy strategy = new ProcessInstanceResolverStrategy();
        ObjectMarshallingStrategy[] strategies = {
                strategy,
                MarshallerFactory.newSerializeMarshallingStrategy()
        };

        // Test strategy.write
        org.kie.api.marshalling.MarshallingConfiguration marshallingConfig = new MarshallingConfigurationImpl(strategies, true, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ProtobufMarshallerWriteContext writerContext = new ProtobufMarshallerWriteContext(baos,
                ((InternalKnowledgeBase) kruntime.getKieSession().getKieBase()),
                (InternalWorkingMemory) kruntime.getKieSession(),
                RuleBaseNodes.getNodeMap(((InternalKnowledgeBase) kruntime.getKieSession().getKieBase())),
                marshallingConfig.getObjectMarshallingStrategyStore(),
                marshallingConfig.isMarshallProcessInstances(),
                marshallingConfig.isMarshallWorkItems(), kruntime.getKieRuntime().getEnvironment());

        strategy.write(writerContext, processInstance);
        baos.close();
        writerContext.close();
        byte[] bytes = baos.toByteArray();
        int numCorrectBytes = calculateNumBytesForLong(processInstance.getStringId());
        assertEquals(numCorrectBytes, bytes.length, "Expected " + numCorrectBytes + " bytes, not " + bytes.length);

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        String serializedProcessInstanceId = ois.readUTF();
        ois.close();
        assertEquals(serializedProcessInstanceId, processInstance.getStringId(), "Expected " + processInstance.getStringId() + ", not " + serializedProcessInstanceId);

        // Test other strategy stuff
        ProcessInstanceManager pim = ProcessInstanceResolverStrategy.retrieveProcessInstanceManager(writerContext);
        assertNotNull(pim);
        assertNotNull(ProcessInstanceResolverStrategy.retrieveKnowledgeRuntime(writerContext));
        assertEquals(pim.getProcessInstance(serializedProcessInstanceId), processInstance);
        bais.close();
        // Test strategy.read
        bais = new ByteArrayInputStream(bytes);
        ProtobufMarshallerReaderContext readerContext = new ProtobufMarshallerReaderContext(bais,
                ((KnowledgeBaseImpl) kruntime.getKieSession().getKieBase()),
                RuleBaseNodes.getNodeMap(((KnowledgeBaseImpl) kruntime.getKieSession().getKieBase())),
                marshallingConfig.getObjectMarshallingStrategyStore(),
                ProtobufMarshaller.TIMER_READERS,
                marshallingConfig.isMarshallProcessInstances(),
                marshallingConfig.isMarshallWorkItems(),
                EnvironmentFactory.newEnvironment());
        bais.close();
        readerContext.setWorkingMemory(((StatefulKnowledgeSessionImpl) kruntime.getKieSession()).getInternalWorkingMemory());
        Object procInstObject = strategy.read(readerContext);
        assertTrue(procInstObject instanceof KogitoProcessInstance);
        assertSame(processInstance, procInstObject);
    }

    private int calculateNumBytesForLong(String longVal) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeUTF(longVal);
        baos.close();
        oos.close();
        return baos.toByteArray().length;
    }

}
