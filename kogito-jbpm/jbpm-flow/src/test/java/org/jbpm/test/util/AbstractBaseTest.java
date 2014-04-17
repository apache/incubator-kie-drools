package org.jbpm.test.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.impl.KnowledgeBaseImpl;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;

public abstract class AbstractBaseTest {
    
    protected static AtomicInteger uniqueIdGen = new AtomicInteger(0);
   
    public KieSession createKieSession(Process... process) { 
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        for( Process processToAdd : process ) {
            ((KnowledgeBaseImpl) kbase).addProcess(processToAdd);
        }
        return kbase.newStatefulKnowledgeSession(); 
    }
    
    @BeforeClass
    public static void configure() { 
        LoggingPrintStream.interceptSysOutSysErr();
    }
    
    @AfterClass
    public static void reset() { 
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }
}
