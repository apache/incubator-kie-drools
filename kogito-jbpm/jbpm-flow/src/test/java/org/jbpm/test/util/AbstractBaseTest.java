package org.jbpm.test.util;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.impl.KnowledgeBaseImpl;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.jbpm.process.test.TestProcessEventListener;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.slf4j.Logger;

public abstract class AbstractBaseTest {

    protected Logger logger;

    @Rule
    public TestName name = new TestName();
    
    @Before 
    public void before() { 
        addLogger();
        logger.debug( "> " + name.getMethodName() );
    }
   
    public abstract void addLogger();
    
    protected static AtomicInteger uniqueIdGen = new AtomicInteger(0);

    public KieSession createKieSession(Process... process) {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        for (Process processToAdd : process) {
            ((KnowledgeBaseImpl) kbase).addProcess(processToAdd);
        }
        return kbase.newStatefulKnowledgeSession();
    }

    public void showEventHistory(KieSession ksession) {
        TestProcessEventListener procEventListener = (TestProcessEventListener) ksession.getProcessEventListeners().iterator().next();
        for (String event : procEventListener.getEventHistory()) {
            System.out.println("\"" + event + "\",");
        }
    }

    public void verifyEventHistory(String[] eventOrder, List<String> eventHistory) {
        int max = eventOrder.length > eventHistory.size() ? eventOrder.length : eventHistory.size();
        logger.debug("{} | {}", "EXPECTED", "TEST" );
        for (int i = 0; i < max; ++i) {
            String expected = "", real = "";
            if (i < eventOrder.length) {
                expected = eventOrder[i];
            }
            if (i < eventHistory.size()) {
                real = eventHistory.get(i);
            }
            logger.debug("{} | {}", expected, real);
            assertEquals("Mismatch in expected event", expected, real);
        }
        assertEquals("Mismatch in number of events expected.", eventOrder.length, eventHistory.size());
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
