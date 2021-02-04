/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.drools.core.impl.KnowledgeBaseFactory;
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
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessInstanceResolverStrategyTest extends AbstractBaseTest {

    private final static String PROCESS_NAME = "simpleProcess.xml";
    
    @Test
    public void testAccept() {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        WorkflowProcessImpl process = new WorkflowProcessImpl();

        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setState(ProcessInstance.STATE_ACTIVE);
        processInstance.setProcess(process);
        processInstance.setKnowledgeRuntime((InternalKnowledgeRuntime) ksession);

        ProcessInstanceResolverStrategy strategy = new ProcessInstanceResolverStrategy();
        
        assertTrue( strategy.accept(processInstance) );
        Object object = new Object();
        assertTrue( ! strategy.accept(object) );
    }
    

    @Test
    public void testProcessInstanceResolverStrategy() throws Exception {
        // Setup
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource(PROCESS_NAME, this.getClass()), ResourceType.DRF);
        KieBase kbase = kbuilder.newKieBase();
        KieSession ksession = kbase.newKieSession();
        KogitoProcessInstance processInstance = ( KogitoProcessInstance ) ksession.createProcessInstance("process name", new HashMap<String, Object>());
        ksession.insert(processInstance);

        // strategy setup
        ProcessInstanceResolverStrategy strategy = new ProcessInstanceResolverStrategy();
        ObjectMarshallingStrategy[] strategies = { 
                strategy,
                MarshallerFactory.newSerializeMarshallingStrategy() 
        };

        
        // Test strategy.write
        org.kie.api.marshalling.MarshallingConfiguration marshallingConfig = new MarshallingConfigurationImpl(strategies, true, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ProtobufMarshallerWriteContext writerContext
            = new ProtobufMarshallerWriteContext(baos,
                                         ((InternalKnowledgeBase) kbase),
										 (InternalWorkingMemory) ((StatefulKnowledgeSessionImpl) ksession),
										 RuleBaseNodes.getNodeMap(((InternalKnowledgeBase) kbase)),
										 marshallingConfig.getObjectMarshallingStrategyStore(), 
										 marshallingConfig.isMarshallProcessInstances(),
										 marshallingConfig.isMarshallWorkItems(), ksession.getEnvironment());

        strategy.write(writerContext, processInstance);
        baos.close();
        writerContext.close();
        byte[] bytes = baos.toByteArray();
        int numCorrectBytes = calculateNumBytesForLong(processInstance.getStringId());
        assertTrue(bytes.length == numCorrectBytes, "Expected " + numCorrectBytes + " bytes, not " + bytes.length);

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        String serializedProcessInstanceId = ois.readUTF();
        ois.close();
        assertTrue(processInstance.getStringId().equals(serializedProcessInstanceId),
                   "Expected " + processInstance.getStringId() + ", not " + serializedProcessInstanceId);

        // Test other strategy stuff
        ProcessInstanceManager pim = ProcessInstanceResolverStrategy.retrieveProcessInstanceManager(writerContext);
        assertNotNull(pim);
        assertNotNull(ProcessInstanceResolverStrategy.retrieveKnowledgeRuntime(writerContext));
        assertTrue(processInstance.equals(pim.getProcessInstance(serializedProcessInstanceId)));
        bais.close();
        // Test strategy.read
        bais = new ByteArrayInputStream(bytes);
        ProtobufMarshallerReaderContext readerContext = new ProtobufMarshallerReaderContext(bais,
                                                                            ((KnowledgeBaseImpl) kbase),
                                                                            RuleBaseNodes.getNodeMap( ((KnowledgeBaseImpl) kbase)),
                                                                            marshallingConfig.getObjectMarshallingStrategyStore(),
                                                                            ProtobufMarshaller.TIMER_READERS,
                                                                            marshallingConfig.isMarshallProcessInstances(),
                                                                            marshallingConfig.isMarshallWorkItems() ,
                                                                            EnvironmentFactory.newEnvironment());
        bais.close();
        readerContext.setWorkingMemory( ((StatefulKnowledgeSessionImpl) ksession).getInternalWorkingMemory() );
        Object procInstObject = strategy.read(readerContext); 
        assertTrue(procInstObject != null && procInstObject instanceof ProcessInstance );
        assertTrue(processInstance == procInstObject);
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
