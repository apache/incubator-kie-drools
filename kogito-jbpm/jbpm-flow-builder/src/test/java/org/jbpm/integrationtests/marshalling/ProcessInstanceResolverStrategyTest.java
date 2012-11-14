package org.jbpm.integrationtests.marshalling;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.impl.ClassPathResource;
import org.kie.marshalling.MarshallerFactory;
import org.kie.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.MarshallingConfigurationImpl;
import org.drools.marshalling.impl.ProtobufMarshaller;
import org.drools.marshalling.impl.RuleBaseNodes;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.jbpm.marshalling.impl.ProcessInstanceResolverStrategy;
import org.jbpm.process.instance.ProcessInstanceManager;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.junit.Test;

public class ProcessInstanceResolverStrategyTest {

    private final static String PROCESS_NAME = "simpleProcess.xml";
    
    @Test
    public void testAccept() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
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
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ProcessInstance processInstance = ksession.createProcessInstance("process name", new HashMap<String, Object>());
        ksession.insert(processInstance);

        // strategy setup
        ProcessInstanceResolverStrategy strategy = new ProcessInstanceResolverStrategy();
        ObjectMarshallingStrategy[] strategies = { 
                strategy,
                MarshallerFactory.newSerializeMarshallingStrategy() 
        };

        
        // Test strategy.write
        org.kie.marshalling.MarshallingConfiguration marshallingConfig = new MarshallingConfigurationImpl(strategies, true, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MarshallerWriteContext writerContext = new MarshallerWriteContext(baos,
                                                                    (InternalRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase(),
													                (InternalWorkingMemory) ((StatefulKnowledgeSessionImpl) ksession).session,
													                RuleBaseNodes.getNodeMap((InternalRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()),
													                marshallingConfig.getObjectMarshallingStrategyStore(), 
													                marshallingConfig.isMarshallProcessInstances(),
													                marshallingConfig.isMarshallWorkItems(), ksession.getEnvironment());

        strategy.write(writerContext, processInstance);
        baos.close();
        writerContext.close();
        byte[] bytes = baos.toByteArray();
        int numCorrectBytes = calculateNumBytesForLong(processInstance.getId());
        assertTrue("Expected " + numCorrectBytes + " bytes, not " + bytes.length, bytes.length == numCorrectBytes);

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        long serializedProcessInstanceId = ois.readLong();
        assertTrue("Expected " + processInstance.getId() + ", not " + serializedProcessInstanceId,
                processInstance.getId() == serializedProcessInstanceId);

        // Test other strategy stuff
        ProcessInstanceManager pim = ProcessInstanceResolverStrategy.retrieveProcessInstanceManager(writerContext);
        assertNotNull(pim);
        assertNotNull(ProcessInstanceResolverStrategy.retrieveKnowledgeRuntime(writerContext));
        assertTrue(processInstance == pim.getProcessInstance(serializedProcessInstanceId));
        
        // Test strategy.read
        bais = new ByteArrayInputStream(bytes);
        MarshallerReaderContext readerContext = new MarshallerReaderContext(bais,
                                                                            (InternalRuleBase) ((KnowledgeBaseImpl) kbase).ruleBase,
                                                                            RuleBaseNodes.getNodeMap( (InternalRuleBase) ((KnowledgeBaseImpl) kbase).ruleBase ),
                                                                            marshallingConfig.getObjectMarshallingStrategyStore(),
                                                                            ProtobufMarshaller.TIMER_READERS,
                                                                            marshallingConfig.isMarshallProcessInstances(),
                                                                            marshallingConfig.isMarshallWorkItems() ,
                                                                            EnvironmentFactory.newEnvironment());
        readerContext.wm = ((StatefulKnowledgeSessionImpl) ksession).getInternalWorkingMemory();
        Object procInstObject = strategy.read(readerContext); 
        assertTrue(procInstObject != null && procInstObject instanceof ProcessInstance );
        assertTrue(processInstance == procInstObject);
    }

    private int calculateNumBytesForLong(Long longVal) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeLong(longVal);
        baos.close();
        oos.close();
        return baos.toByteArray().length;
    }

}
