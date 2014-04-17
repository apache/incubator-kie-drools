package org.jbpm.test.util;

import org.drools.core.impl.KnowledgeBaseImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBaseTest {
    
    private static final Logger log = LoggerFactory.getLogger(AbstractBaseTest.class);

    protected boolean useLocking;
   
    public KieBase createKieBase(Process... process) { 
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        for( Process processToAdd : process ) {
            ((KnowledgeBaseImpl) kbase).addProcess(processToAdd);
        }
        return kbase;
    }
    
    @BeforeClass
    public static void configure() { 
        BpmnDebugPrintStream.interceptSysOutSysErr();
    }
    
    @AfterClass
    public static void reset() { 
        BpmnDebugPrintStream.resetInterceptSysOutSysErr();
    }
}
